package com.fit.run.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.fit.run.MainActivity;
import com.fit.run.R;
import com.fit.run.bean.Rank;
import com.fit.run.ui.base.BaseActivity;
import com.fit.run.utils.RunLog;
import com.fit.run.utils.SpUtils;
import com.fit.run.utils.StringUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;



public class LoginActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.edt_account)
    EditText mEdtAccount;
    @BindView(R.id.edt_password)
    EditText mEdtPassword;

    private Map<String, Object> mMap;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mMap = new HashMap<>();
        mToolbar.setSubtitleTextColor(ContextCompat.getColor(mContext, android.R.color.white));
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("ranks");
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            startActivity(new Intent(mContext, MainActivity.class));
            finish();
        }
    }


    @OnClick({R.id.btn_login, R.id.btn_to_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:

                final String account = mEdtAccount.getText().toString().trim();
                if (TextUtils.isEmpty(account)) {
                    toast("please input email");
                    return;
                }

                if (!StringUtils.checkEmaile(account)) {
                    toast("please input correct email");
                    return;
                }


                String password = mEdtPassword.getText().toString().trim();
                if (TextUtils.isEmpty(password)) {
                    toast("please input password");
                    return;
                }

                //TODO judge account and password

                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("loading...");
                progressDialog.show();
                mFirebaseAuth.signInWithEmailAndPassword(account, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information

                                    final String bDate = (String) SpUtils.get(mContext, SpUtils.FILE_NAME_LOGIN, account, "");
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                    Date d = new Date();
                                    final String aDate = sdf.format(d);

                                    final FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

                                    SpUtils.put(mContext, SpUtils.FILE_NAME_USER, SpUtils.FILE_NAME_USER_KEY_UID, firebaseUser.getUid());


                                    mDatabaseReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            final Rank rank = dataSnapshot.getValue(Rank.class);
                                            if (rank == null) {
                                                return;
                                            }

                                            SpUtils.put(mContext, SpUtils.FILE_NAME_USER, SpUtils.FILE_NAME_USER_KEY_POINT, rank.getPoint());
                                            SpUtils.put(mContext, SpUtils.FILE_NAME_USER, SpUtils.FILE_NAME_USER_KEY_ACCOUNT, firebaseUser.getEmail());
                                            SpUtils.put(mContext, SpUtils.FILE_NAME_USER, SpUtils.FILE_NAME_USER_KEY_STEP, rank.getStep());
                                            SpUtils.put(mContext, SpUtils.FILE_NAME_USER, SpUtils.FILE_NAME_USER_KEY_LOVER, rank.getLover());

                                            if (!aDate.equals(bDate)) {
                                                SpUtils.put(mContext, SpUtils.FILE_NAME_LOGIN, account, aDate);
                                                //如果当天第一次登录：首先查询当前积分，其次在当前积分上加上10分
                                                final int point = rank.getPoint() + 10;
                                                rank.setPoint(point);
                                                RunLog.e("更新Point: " + dataSnapshot.getKey() + "-" + rank.getPoint() + "-" + rank.getAccount() + "-" + rank.getStep() + "-" + rank.getLover());
                                                mMap.clear();
                                                mMap.put(firebaseUser.getUid(), rank);

                                                mDatabaseReference.updateChildren(mMap).addOnCompleteListener(new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task) {
                                                        SpUtils.put(mContext, SpUtils.FILE_NAME_USER, SpUtils.FILE_NAME_USER_KEY_POINT, rank.getPoint());
                                                        toast("point add 10");
                                                    }
                                                });
                                            }



                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    startActivity(new Intent(mContext, MainActivity.class));
                                    finish();
                                    toast("login success");
                                } else {
                                    // If sign in fails, display a message to the user.
                                    toast("login fail: " + task.getException().getMessage());
                                }
                            }
                        });

                break;
            case R.id.btn_to_register:
                startActivity(new Intent(mContext, RegisterActivity.class));
                break;
            default:
                break;
        }
    }
}
