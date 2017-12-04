package com.fit.run.pedometer.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.fit.run.MainActivity;
import com.fit.run.R;
import com.fit.run.bean.Challenge;
import com.fit.run.bean.Rank;
import com.fit.run.pedometer.StepMode;
import com.fit.run.pedometer.callback.StepCallBack;
import com.fit.run.pedometer.pojo.StepData;
import com.fit.run.utils.CountDownTimer;
import com.fit.run.utils.DbUtils;
import com.fit.run.utils.RunLog;
import com.fit.run.utils.SpUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fit.run.Config.MSG_FROM_CLIENT;
import static com.fit.run.Config.MSG_FROM_SERVER;

@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class StepService extends Service implements /*SensorEventListener,*/ StepCallBack {
    private static final long SCREEN_OFF_RECEIVER_DELAY = 500L;
    private final String TAG = "StepService";
    //默认为30秒进行一次存储
    private static int duration = 30000;
    private NotificationManager nm;
    private NotificationCompat.Builder builder;
    private Messenger messenger = new Messenger(new MessenerHandler());
    private BroadcastReceiver mBatInfoReceiver;
    private WakeLock mWakeLock;
    private TimeCount time;
    //当天的日期
    private String CURRENTDATE = "";

    private static class MessenerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FROM_CLIENT:
                    try {
                        Messenger messenger = msg.replyTo;
                        Message replyMsg = Message.obtain(null, MSG_FROM_SERVER);
                        Bundle bundle = new Bundle();
                        bundle.putInt("step", StepMode.CURRENT_SETP);
                        RunLog.e("step " + StepMode.CURRENT_SETP);
                        replyMsg.setData(bundle);
                        messenger.send(replyMsg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "onCreate");
        FirebaseApp.initializeApp(getApplicationContext());
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        initBroadcastReceiver();
        startStep();
        startTimeCount();
    }

    private void initBroadcastReceiver() {
        Log.v(TAG, "initBroadcastReceiver");
        final IntentFilter filter = new IntentFilter();
        // 屏幕灭屏广播
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        //日期修改
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        //关机广播
        filter.addAction(Intent.ACTION_SHUTDOWN);
        // 屏幕亮屏广播
        filter.addAction(Intent.ACTION_SCREEN_ON);
        // 屏幕解锁广播
        filter.addAction(Intent.ACTION_USER_PRESENT);
        // 当长按电源键弹出“关机”对话或者锁屏时系统会发出这个广播
        // example：有时候会用到系统对话框，权限可能很高，会覆盖在锁屏界面或者“关机”对话框之上，
        // 所以监听这个广播，当收到时就隐藏自己的对话，如点击pad右下角部分弹出的对话框
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

        mBatInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                String action = intent.getAction();

                if (Intent.ACTION_SCREEN_ON.equals(action)) {
                    Log.v(TAG, "screen on");
                } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                    Log.v(TAG, "screen off");
                    //改为60秒一存储
                    duration = 60000;
                    //解决某些厂商的rom在锁屏后收不到sensor的回调
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            startStep();
                        }
                    };
                    new Handler().postDelayed(runnable, SCREEN_OFF_RECEIVER_DELAY);
                } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                    Log.v(TAG, "screen unlock");
                    save();
                    //改为30秒一存储
                    duration = 30000;
                } else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
                    Log.v(TAG, " receive Intent.ACTION_CLOSE_SYSTEM_DIALOGS");
                    //保存一次
                    save();
                } else if (Intent.ACTION_SHUTDOWN.equals(intent.getAction())) {
                    Log.v(TAG, " receive ACTION_SHUTDOWN");
                    save();
                } else if (Intent.ACTION_DATE_CHANGED.equals(intent.getAction())) {
                    Log.v(TAG, " receive ACTION_DATE_CHANGED");
                    initTodayData();
                    clearStepData();
                    Log.v(TAG, "归零数据：" + StepMode.CURRENT_SETP);
                    Step(StepMode.CURRENT_SETP);
                }
            }
        };
        registerReceiver(mBatInfoReceiver, filter);
    }

    private void startStep() {
        StepMode mode = new StepInPedometer(this, this);
        boolean isAvailable = mode.getStep();
        if (!isAvailable) {
            mode = new StepInAcceleration(this, this);
            isAvailable = mode.getStep();
            if (isAvailable) {
                Log.v(TAG, "acceleration can execute!");
            }
        }
    }

    private void startTimeCount() {
        time = new TimeCount(duration, 1000);
        time.start();
    }

    @Override
    public void Step(int stepNum) {
        StepMode.CURRENT_SETP = stepNum;
        RunLog.e("Step:" + stepNum);
        updateNotification("今日步数：" + stepNum + " 步");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initTodayData();
        updateNotification("今日步数：" + StepMode.CURRENT_SETP + " 步");
        return START_STICKY;
    }

    private void initTodayData() {
        CURRENTDATE = getTodayDate();
        //获取当天的数据，用于展示
        List<StepData> list = DbUtils.getQueryByWhere(StepData.class, "today", new String[]{CURRENTDATE});
        if (list.size() == 0 || list.isEmpty()) {
            StepMode.CURRENT_SETP = 0;
        } else if (list.size() == 1) {
            StepMode.CURRENT_SETP = Integer.parseInt(list.get(0).getStep());
        } else {
            Log.v(TAG, "It's wrong！");
        }
    }

    private String getTodayDate() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }


    /**
     * update notification
     */
    private void updateNotification(String content) {
        builder = new NotificationCompat.Builder(this);
        builder.setPriority(Notification.PRIORITY_MIN);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        builder.setContentIntent(contentIntent);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setTicker("Run");
        builder.setContentTitle("Run");
        //设置不可清除
        builder.setOngoing(true);
        builder.setContentText(content);
        Notification notification = builder.build();

        startForeground(0, notification);
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(R.string.app_name, notification);
    }


    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            // 如果计时器正常结束，则开始计步
            time.cancel();
            save();
            startTimeCount();
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }
    }

    private void save() {
        int tempStep = StepMode.CURRENT_SETP;
        List<StepData> list = DbUtils.getQueryByWhere(StepData.class, "today", new String[]{CURRENTDATE});
        if (list.size() == 0 || list.isEmpty()) {
            StepData data = new StepData();
            data.setToday(CURRENTDATE);
            data.setStep(tempStep + "");
            DbUtils.insert(data);
            RunLog.e("重置数据库为当天数据");
            //结算challenge；清除challenge记录；
            settlement();
        } else if (list.size() == 1) {
            StepData data = list.get(0);
            data.setStep(tempStep + "");
            DbUtils.update(data);
            RunLog.e("update:" + CURRENTDATE);
        }
    }

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;

    private FirebaseUser mFirebaseUser;

    private List<Challenge> mChallenges = new ArrayList<>();

    private Map<String, Object> mMap = new HashMap<>();

    private void settlement() {
        Query query = mDatabaseReference.child("challenges").child(mFirebaseUser.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mChallenges.clear();
                final int myStep = (int) SpUtils.get(getApplicationContext(), SpUtils.FILE_NAME_USER, SpUtils.FILE_NAME_USER_KEY_STEP, 0);
                if (dataSnapshot.getChildrenCount() == 0) {
                    final int[] index = {0};
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        final Challenge challenge = snapshot.getValue(Challenge.class);
                        mDatabaseReference.child("ranks").child(challenge.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final Rank user = dataSnapshot.getValue(Rank.class);
                                int step = user.getStep();
                                if (myStep != step) {
                                    int point = (int) SpUtils.get(getApplicationContext(), SpUtils.FILE_NAME_USER, SpUtils.FILE_NAME_USER_KEY_POINT, 0);
                                    RunLog.e("结算前分数：" + point);
                                    final int result;
                                    if (myStep > step) {
                                        //本用户赢：自己加上设置的分数
                                        result = point + challenge.getPoint();
                                    } else {
                                        //对手赢：自己减去设置的分数
                                        result = point - challenge.getPoint();
                                    }
                                    RunLog.e("结算后分数：" + result);
                                    String lover = (String) SpUtils.get(getApplicationContext(), SpUtils.FILE_NAME_USER, SpUtils.FILE_NAME_USER_KEY_LOVER, "");

                                    Rank rank = new Rank();
                                    rank.setPoint(result);
                                    rank.setStep(myStep);
                                    rank.setUid(mFirebaseUser.getUid());
                                    rank.setLover(lover);
                                    rank.setAccount(mFirebaseUser.getEmail());
                                    RunLog.e("更新Point: " + dataSnapshot.getKey() + "-" + rank.getPoint() + "-" + rank.getAccount() + "-" + rank.getStep() + "-" + rank.getLover());
                                    mMap.clear();
                                    mMap.put(mFirebaseUser.getUid(), rank);
                                    SpUtils.put(getApplicationContext(), SpUtils.FILE_NAME_USER, SpUtils.FILE_NAME_USER_KEY_POINT, result);

                                    mDatabaseReference.child("ranks").updateChildren(mMap).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {


                                            RunLog.e("结算：" + result);
                                            //结算成功后，删除该条挑战记录
                                            mDatabaseReference.child("challenges").child(mFirebaseUser.getUid()).child(challenge.getUid()).removeValue(new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                    RunLog.e("已删除该条挑战记录：" + mFirebaseUser.getUid() + "  -  " + challenge.getUid());

                                                }
                                            });


                                        }
                                    });
                                } else {

                                    //结算成功后，删除该条挑战记录
                                    mDatabaseReference.child("challenges").child(mFirebaseUser.getUid()).child(challenge.getUid()).removeValue(new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            RunLog.e("已删除该条挑战记录：" + mFirebaseUser.getUid() + "  -  " + challenge.getUid());
                                        }
                                    });
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                //结算成功后，删除该条挑战记录
                                mDatabaseReference.child("challenges").child(mFirebaseUser.getUid()).child(challenge.getUid()).removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        RunLog.e("已删除该条挑战记录：" + mFirebaseUser.getUid() + "  -  " + challenge.getUid());
                                    }
                                });
                            }
                        });
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void clearStepData() {
        StepMode.CURRENT_SETP = 0;
    }

    @Override
    public void onDestroy() {
        //取消前台进程
        stopForeground(true);
        DbUtils.closeDb();
        unregisterReceiver(mBatInfoReceiver);
        Intent intent = new Intent(this, StepService.class);
        startService(intent);
        super.onDestroy();
    }


//    private  void unlock(){
//        setLockPatternEnabled(android.provider.Settings.Secure.LOCK_PATTERN_ENABLED,false);
//    }
//
//    private void setLockPatternEnabled(String systemSettingKey, boolean enabled) {
//        //推荐使用
//        android.provider.Settings.Secure.putInt(getContentResolver(), systemSettingKey,enabled ? 1 : 0);
//    }

    synchronized private WakeLock getLock(Context context) {
        if (mWakeLock != null) {
            if (mWakeLock.isHeld()) {
                mWakeLock.release();
            }
            mWakeLock = null;
        }

        if (mWakeLock == null) {
            PowerManager mgr = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            mWakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    StepService.class.getName());
            mWakeLock.setReferenceCounted(true);
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis());
            int hour = c.get(Calendar.HOUR_OF_DAY);
            if (hour >= 23 || hour <= 6) {
                mWakeLock.acquire(5000);
            } else {
                mWakeLock.acquire(300000);
            }
        }
        return (mWakeLock);
    }
}
