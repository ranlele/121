package com.fit.run.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fit.run.R;
import com.fit.run.pedometer.pojo.StepData;
import com.fit.run.ui.base.BaseActivity;
import com.fit.run.utils.DbUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created on 17/11/13 14:22
 *
 */

public class InfoActivity extends BaseActivity {
    @BindView(R.id.tv_account)
    TextView mTvAccount;
    @BindView(R.id.btn_add_friend)
    Button mBtnAddFriend;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.btn_challenge)
    Button mBtnChallenge;
    @BindView(R.id.tv_step)
    TextView mTvStep;
    @BindView(R.id.tv_lover)
    TextView mTvLover;
    @BindView(R.id.tv_integral)
    TextView mTvIntegral;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        ButterKnife.bind(this);
        mToolbar.setSubtitleTextColor(ContextCompat.getColor(mContext, android.R.color.white));
        //TODO judge is friend or not
        Intent intent = getIntent();
        mTvAccount.setText("Account: " + intent.getStringExtra(Config.ACCOUNT));
        mTvLover.setText("Interest: " + intent.getStringExtra(Config.LOVER));
        mTvIntegral.setText("Total integral: " + intent.getIntExtra(Config.INTEGRAL, 0));

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        List<StepData> list = DbUtils.getQueryByWhere(StepData.class, "today", new String[]{getTodayDate()});
        if (list == null || list.size() == 0) {
            mTvStep.setText("Today's step: " + 0);
        } else {
            mTvStep.setText("Today's step: " + list.get(0).getStep());
        }
    }


    @OnClick({R.id.btn_add_friend, R.id.btn_challenge})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_add_friend:
                //TODO add friend
                break;
            case R.id.btn_challenge:
                //TODO challenge sb
                break;
            default:
                break;
        }
    }

    private String getTodayDate() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
}
