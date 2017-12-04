package com.fit.run;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.fit.run.bean.Rank;
import com.fit.run.bean.RefreshStepEvent;
import com.fit.run.event.RefreshFriendEvent;
import com.fit.run.pedometer.service.StepService;
import com.fit.run.ui.activity.LoginActivity;
import com.fit.run.ui.base.BaseActivity;
import com.fit.run.ui.fragment.FriendFragment;
import com.fit.run.ui.fragment.RankFragment;
import com.fit.run.ui.fragment.RunFragment;
import com.fit.run.utils.RunLog;
import com.fit.run.utils.SpUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ss.bottomnavigation.BottomNavigation;
import com.ss.bottomnavigation.TabItem;
import com.ss.bottomnavigation.events.OnSelectedItemChangeListener;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.fit.run.Config.MSG_FROM_CLIENT;
import static com.fit.run.Config.MSG_FROM_SERVER;
import static com.fit.run.Config.REQUEST_SERVER;


public class MainActivity extends BaseActivity implements Handler.Callback {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tab_run)
    TabItem mTabRun;
    @BindView(R.id.tab_friend)
    TabItem mTabFriend;
    @BindView(R.id.tab_rank)
    TabItem mTabRank;
    @BindView(R.id.bottom_navigation)
    BottomNavigation mBottomNavigation;


    private RunFragment mRunFragment;
    private FriendFragment mFriendFragment;
    private RankFragment mRankFragment;

    private Fragment mCurrentFragment;


    private DatabaseReference mDatabaseReference;

    private final String TAG = RunFragment.class.getSimpleName();
    private long TIME_INTERVAL = 500;
    private Messenger messenger;
    private Messenger mGetReplyMessenger = new Messenger(new Handler(this));
    private Handler delayHandler;
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                messenger = new Messenger(service);
                Message msg = Message.obtain(null, MSG_FROM_CLIENT);
                msg.replyTo = mGetReplyMessenger;
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    private Rank mRank;
    private FirebaseAuth mFirebaseAuth;
    RefreshStepEvent mRefreshStepEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mRank = new Rank();
        mRefreshStepEvent = new RefreshStepEvent();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("ranks");
        mFirebaseAuth = FirebaseAuth.getInstance();
        mToolbar.setSubtitleTextColor(ContextCompat.getColor(mContext, android.R.color.white));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mFirebaseAuth.signOut();
                SpUtils.clear(mContext, SpUtils.FILE_NAME_USER);
                startActivity(new Intent(mContext, LoginActivity.class));
                finish();
            }
        });
        mToolbar.setSubtitle(mFirebaseAuth.getCurrentUser().getEmail());
        Log.i(MainActivity.class.getSimpleName(), stringFromJNI());


        delayHandler = new Handler(this);
        startServiceForStrategy();


        mRankFragment = new RankFragment();
        mFriendFragment = new FriendFragment();
        mRunFragment = new RunFragment();

        mBottomNavigation.setOnSelectedItemChangeListener(new OnSelectedItemChangeListener() {
            @Override
            public void onSelectedItemChanged(int id) {
                switch (id) {
                    case R.id.tab_run:
                        switchContent(mRunFragment);
                        break;

                    case R.id.tab_friend:
                        switchContent(mFriendFragment);
                        break;

                    case R.id.tab_rank:
                        switchContent(mRankFragment);
                        break;
                    default:
                        break;
                }
            }
        });

    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();


    public void switchContent(Fragment to) {
        Fragment from = mCurrentFragment;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mCurrentFragment == null) {
            transaction.add(R.id.container, to).commit();
            mCurrentFragment = to;
            return;
        }
        if (mCurrentFragment != to) {
            mCurrentFragment = to;
            if (!to.isAdded()) {
                if (from.isAdded()) {
                    transaction.hide(from).add(R.id.container, to).commit();
                } else {
                    transaction.add(R.id.container, to).commit();
                }
            } else {
                transaction.hide(from).show(to).commit();
            }
        }
    }


    private Map<String, Object> mMap = new HashMap<>();

    @Override
    public boolean handleMessage(final Message msg) {

        switch (msg.what) {
            case MSG_FROM_SERVER:
                // refresh step
                FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                String lover = (String) SpUtils.get(mContext, SpUtils.FILE_NAME_USER, SpUtils.FILE_NAME_USER_KEY_LOVER, "");
                int point = (int) SpUtils.get(mContext, SpUtils.FILE_NAME_USER, SpUtils.FILE_NAME_USER_KEY_POINT, 0);
                String uid = (String) SpUtils.get(mContext, SpUtils.FILE_NAME_USER, SpUtils.FILE_NAME_USER_KEY_UID, "");


                int aStep = msg.getData().getInt(Config.STEP);

                RunLog.e("after step "+aStep);
                mRefreshStepEvent.setStep(aStep);
                EventBus.getDefault().post(mRefreshStepEvent);
                delayHandler.sendEmptyMessageDelayed(REQUEST_SERVER, TIME_INTERVAL);


                if (firebaseUser != null && point != 0) {

                    int bStep = (int) SpUtils.get(mContext, SpUtils.FILE_NAME_USER, SpUtils.FILE_NAME_USER_KEY_STEP, 0);
                    RunLog.e("before step "+bStep);
                    if (aStep != bStep) {
                        SpUtils.put(mContext, SpUtils.FILE_NAME_USER, SpUtils.FILE_NAME_USER_KEY_STEP, aStep);
                        mRank.setStep(aStep);
                        mRank.setAccount(firebaseUser.getEmail());
                        mRank.setLover(lover);
                        mRank.setPoint(point);
                        mRank.setUid(uid);
                        mMap.clear();
                        mMap.put(firebaseUser.getUid(), mRank);
                        RunLog.e("更新Step： " + firebaseUser.getUid() + "-" + point + "-" + firebaseUser.getEmail() + "-" + bStep + "-" + lover);

                        mDatabaseReference.updateChildren(mMap);
                    }

                }


                break;
            case REQUEST_SERVER:
                try {
                    Message msg1 = Message.obtain(null, MSG_FROM_CLIENT);
                    msg1.replyTo = mGetReplyMessenger;
                    messenger.send(msg1);
                } catch (RemoteException e) {
                    Log.e(TAG, "request server error: " + e.getMessage());
                }

                break;
            default:
                break;
        }
        return false;
    }

    /**
     * start service
     */
    private void startServiceForStrategy() {
        if (!isServiceWork(this, StepService.class.getName())) {
            setupService(true);
        } else {
            setupService(false);
        }
    }

    /**
     * start service
     *
     * @param flag true:bind and start; false:just bind;
     */
    private void setupService(boolean flag) {
        Intent intent = new Intent(this, StepService.class);

        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        if (flag) {
            startService(intent);
        }
    }


    /**
     * judge service is work or not
     *
     * @param mContext
     * @param serviceName eg：xx.xxx.xxxx.TestService）
     * @return
     */
    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }

    @OnClick(R.id.iv_sign)
    public void onViewClicked() {

        String bDate = (String) SpUtils.get(mContext, SpUtils.FILE_NAME_LOGIN, mFirebaseAuth.getCurrentUser().getUid(), "");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date();
        String aDate = sdf.format(d);
        if (!aDate.equals(bDate)) {
            SpUtils.put(mContext, SpUtils.FILE_NAME_LOGIN, mFirebaseAuth.getCurrentUser().getUid(), aDate);
            //如果当天第一次签到：首先查询当前积分，其次在当前积分上加上10分
            final ProgressDialog progressDialog = new ProgressDialog(mContext);
            progressDialog.setTitle("signing...");
            progressDialog.show();
            mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    final Rank rank = dataSnapshot.getValue(Rank.class);
                    if (rank == null) {
                        return;
                    }
                    final int point = rank.getPoint() + 10;
                    rank.setPoint(point);
                    RunLog.e("更新Point: " + dataSnapshot.getKey() + "-" + rank.getPoint() + "-" + rank.getAccount() + "-" + rank.getStep() + "-" + rank.getLover());
                    mMap.clear();
                    mMap.put(mFirebaseAuth.getCurrentUser().getUid(), rank);
                    SpUtils.put(mContext, SpUtils.FILE_NAME_USER, SpUtils.FILE_NAME_USER_KEY_POINT, point);
                    SpUtils.put(mContext, SpUtils.FILE_NAME_USER, SpUtils.FILE_NAME_USER_KEY_ACCOUNT, mFirebaseAuth.getCurrentUser().getEmail());
                    SpUtils.put(mContext, SpUtils.FILE_NAME_USER, SpUtils.FILE_NAME_USER_KEY_STEP, rank.getStep());
                    SpUtils.put(mContext, SpUtils.FILE_NAME_USER, SpUtils.FILE_NAME_USER_KEY_LOVER, rank.getLover());

                    mDatabaseReference.updateChildren(mMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                toast("sign success, point add 10.");
                            }
                            EventBus.getDefault().post(new RefreshFriendEvent());
                        }
                    });


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {

            toast("you have signed today");
        }
    }
}
