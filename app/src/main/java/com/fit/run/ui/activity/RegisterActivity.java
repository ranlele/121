package com.fit.run.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.fit.run.R;
import com.fit.run.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created on 17/11/13 11:53
 *
 */

public class RegisterActivity extends BaseActivity {
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
    }


    @OnClick({R.id.btn_register, R.id.btn_to_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_register:

                String account = mEdtAccount.getText().toString().trim();
                if (TextUtils.isEmpty(account)) {
                    toast("please input account");
                    return;
                }


                String password = mEdtPassword.getText().toString().trim();
                if (TextUtils.isEmpty(password)) {
                    toast("please input password");
                    return;
                }


                String repeat = mEdtRepeat.getText().toString().trim();
                if (TextUtils.isEmpty(repeat)) {
                    toast("please repeat password");
                    return;
                }

                if (TextUtils.isEmpty(lover)) {
                    toast("please select lover");
                    return;
                }

                //TODO judge account password and repeat password

                startActivity(new Intent(mContext, MainActivity.class));

                finish();
                break;
            case R.id.btn_to_login:
                startActivity(new Intent(mContext, LoginActivity.class));
                break;
            default:
                break;
        }
    }
}
