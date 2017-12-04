package com.fit.run.ui.activity;

import android.app.ProgressDialog;
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
import android.widget.EditText;

import com.fit.run.MainActivity;
import com.fit.run.R;
import com.fit.run.bean.Rank;
import com.fit.run.ui.base.BaseActivity;
import com.fit.run.utils.SpUtils;
import com.fit.run.utils.StringUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class RegisterActivity extends BaseActivity {
    private int PASSWORD_LENGTH = 6;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.edt_account)
    EditText mEdtAccount;
    @BindView(R.id.edt_password)
    EditText mEdtPassword;
    @BindView(R.id.edt_repeat)
    EditText mEdtRepeat;
    @BindView(R.id.acs_lover)
    AppCompatSpinner mAcsLover;
    private String[] lovers = new String[]{"yoga lover", "cardio lover", "muscle lover"};
    private String lover = lovers[0];

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        mToolbar.setSubtitleTextColor(ContextCompat.getColor(mContext, android.R.color.white));
        mAcsLover.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                lover = lovers[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
    }


    @OnClick({R.id.btn_register, R.id.btn_to_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_register:

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

                if (password.length() < PASSWORD_LENGTH) {
                    toast("password's length must greater than 6");
                    return;
                }


                String repeat = mEdtRepeat.getText().toString().trim();
                if (TextUtils.isEmpty(repeat)) {
                    toast("please repeat password");
                    return;
                }

                if (!password.equals(repeat)) {
                    toast("please input the same password");
                    return;
                }

                if (TextUtils.isEmpty(lover)) {
                    toast("please select lover");
                    return;
                }

                //TODO judge account, password and repeat password

                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("loading...");
                progressDialog.show();
                mFirebaseAuth.createUserWithEmailAndPassword(account, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    final FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                                    final Rank rank = new Rank();
                                    rank.setAccount(account);
                                    rank.setLover(lover);
                                    rank.setPoint(100);
                                    rank.setStep(0);
                                    rank.setUid(firebaseUser.getUid());
                                    mDatabaseReference.child("ranks").child(firebaseUser.getUid()).setValue(rank);
                                    SpUtils.put(mContext,SpUtils.FILE_NAME_USER,SpUtils.FILE_NAME_USER_KEY_UID,firebaseUser.getUid());
                                    SpUtils.put(mContext,SpUtils.FILE_NAME_USER,SpUtils.FILE_NAME_USER_KEY_ACCOUNT,firebaseUser.getEmail());
                                    SpUtils.put(mContext,SpUtils.FILE_NAME_USER,SpUtils.FILE_NAME_USER_KEY_POINT,100);
                                    SpUtils.put(mContext,SpUtils.FILE_NAME_USER,SpUtils.FILE_NAME_USER_KEY_STEP,0);
                                    SpUtils.put(mContext,SpUtils.FILE_NAME_USER,SpUtils.FILE_NAME_USER_KEY_LOVER,lover);
                                    startActivity(new Intent(mContext, MainActivity.class));
                                    finish();
                                    toast("sign success");
                                } else {
                                    // If sign in fails, display a message to the user.
                                    toast("sign fail: " + task.getException().getMessage());
                                }
                            }
                        });


                break;
            case R.id.btn_to_login:
                startActivity(new Intent(mContext, LoginActivity.class));
                break;
            default:
                break;
        }
    }


}
