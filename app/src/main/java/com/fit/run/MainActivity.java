package com.fit.run;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.fit.run.ui.base.BaseActivity;
import com.fit.run.ui.fragment.FriendFragment;
import com.fit.run.ui.fragment.RankFragment;
import com.fit.run.ui.fragment.RunFragment;
import com.ss.bottomnavigation.BottomNavigation;
import com.ss.bottomnavigation.TabItem;
import com.ss.bottomnavigation.events.OnSelectedItemChangeListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author
 */
public class MainActivity extends BaseActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mToolbar.setSubtitleTextColor(ContextCompat.getColor(mContext, android.R.color.white));
        Log.i(MainActivity.class.getSimpleName(), stringFromJNI());


        mRankFragment = new RankFragment();
        mFriendFragment = new FriendFragment();
        mRunFragment = new RunFragment();

        mBottomNavigation.setOnSelectedItemChangeListener(new OnSelectedItemChangeListener() {
            @Override
            public void onSelectedItemChanged(int id) {
                switch (id) {
                    case R.id.tab_run:
                        mToolbar.setSubtitle("Run");
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


    /**
     *
     * @param to
     */
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
}
