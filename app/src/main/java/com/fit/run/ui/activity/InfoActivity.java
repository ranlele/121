package com.fit.run.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.fit.run.Config;
import com.fit.run.R;
import com.fit.run.bean.Challenge;
import com.fit.run.ui.base.BaseActivity;
import com.fit.run.utils.SpUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    @BindView(R.id.tv_point)
    TextView mTvPoint;
    @BindView(R.id.acs_challenge)
    AppCompatSpinner mAcsChallenge;


    private DatabaseReference mDatabaseReference;

    private String mCurrUid;
    private String mUserUid;
    private String mAccount;
    private String mLover;
    private int mPoint;
    private int mStep;
    private String mUserAccount;
    private String mUserLover;
    private int mUserPoint;
    private int mUserStep;
    private int[] points = new int[]{10, 20, 30};
    private int point = points[0];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        ButterKnife.bind(this);
        mToolbar.setSubtitleTextColor(ContextCompat.getColor(mContext, android.R.color.white));
        //TODO judge is friend or not
        Intent intent = getIntent();
        mCurrUid = intent.getStringExtra(Config.UID);
        mUserUid = (String) SpUtils.get(mContext, SpUtils.FILE_NAME_USER, SpUtils.FILE_NAME_USER_KEY_UID, "");
        mUserAccount = (String) SpUtils.get(mContext, SpUtils.FILE_NAME_USER, SpUtils.FILE_NAME_USER_KEY_ACCOUNT, "");
        mUserLover = (String) SpUtils.get(mContext, SpUtils.FILE_NAME_USER, SpUtils.FILE_NAME_USER_KEY_LOVER, "");
        mUserPoint = (int) SpUtils.get(mContext, SpUtils.FILE_NAME_USER, SpUtils.FILE_NAME_USER_KEY_POINT, 0);
        mUserStep = (int) SpUtils.get(mContext, SpUtils.FILE_NAME_USER, SpUtils.FILE_NAME_USER_KEY_STEP, 0);
        mAccount = intent.getStringExtra(Config.ACCOUNT);
        mLover = intent.getStringExtra(Config.LOVER);
        mPoint = intent.getIntExtra(Config.POINT, 0);
        mStep = intent.getIntExtra(Config.STEP, 0);
        mTvAccount.setText("User: " + mAccount);
        mTvLover.setText("Interest: " + mLover);
        mTvPoint.setText("Total point: " + mPoint);
        mTvStep.setText("Today's step: " + mStep);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();


        mAcsChallenge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                point = points[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if (mCurrUid != null && mUserUid != null && mCurrUid.equals(mUserUid)) {
            mBtnAddFriend.setVisibility(View.GONE);
            mBtnChallenge.setVisibility(View.GONE);
            mAcsChallenge.setVisibility(View.GONE);
        } else {
            mDatabaseReference.child("friends").child(mUserUid).child(mCurrUid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String uid = (String) dataSnapshot.getValue();
                    if (TextUtils.isEmpty(uid)) {
                        mBtnAddFriend.setVisibility(View.VISIBLE);
                    } else {
                        mBtnAddFriend.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            mDatabaseReference.child("challenges").child(mUserUid).child(mCurrUid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Challenge challenge =  dataSnapshot.getValue(Challenge.class);
                    if (challenge==null||TextUtils.isEmpty(challenge.getUid())) {
                        mBtnChallenge.setVisibility(View.VISIBLE);
                        mAcsChallenge.setVisibility(View.VISIBLE);
                    } else {
                        mBtnChallenge.setVisibility(View.GONE);
                        mAcsChallenge.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        mToolbar.setSubtitle(intent.getStringExtra(Config.ACCOUNT));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }


    @OnClick({R.id.btn_add_friend, R.id.btn_challenge})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_add_friend:
                //TODO add friend


                mDatabaseReference.child("friends").child(mUserUid).child(mCurrUid).setValue(mCurrUid).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            toast("add friend success");
                            mBtnAddFriend.setVisibility(View.GONE);
                        }
                    }
                });

                mDatabaseReference.child("friends").child(mCurrUid).child(mUserUid).setValue(mUserUid);


                break;
            case R.id.btn_challenge:
                //TODO challenge sb

                Challenge challenge = new Challenge();
                challenge.setUid(mCurrUid);
                challenge.setPoint(point);

                mDatabaseReference.child("challenges").child(mUserUid).child(mCurrUid).setValue(challenge).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            toast("start challenge success");
                            mBtnAddFriend.setVisibility(View.GONE);
                        }
                    }
                });

                Challenge challenge1 = new Challenge();
                challenge1.setUid(mUserUid);
                challenge1.setPoint(point);
                mDatabaseReference.child("challenges").child(mCurrUid).child(mUserUid).setValue(challenge1);



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

