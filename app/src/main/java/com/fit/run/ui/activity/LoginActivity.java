package com.fit.run.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.fit.run.MainActivity;
import com.fit.run.R;
import com.fit.run.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created on 17/11/13 11:50
 *
 * @author
 */

public class LoginActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.edt_account)
    EditText mEdtAccount;
    @BindView(R.id.edt_password)
    EditText mEdtPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mToolbar.setSubtitleTextColor(ContextCompat.getColor(mContext, android.R.color.white));
    }


    @OnClick({R.id.btn_login, R.id.btn_to_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:

                String account = mEdtAccount.getText().toString().trim();
                if (TextUtils.isEmpty(account)){
                    toast("please input account");
                    return;
                }



                String password = mEdtPassword.getText().toString().trim();
                if (TextUtils.isEmpty(password)){
                    toast("please input password");
                    return;
                }

                //TODO judge account and password

                startActivity(new Intent(mContext, MainActivity.class));


                break;
            case R.id.btn_to_register:
                startActivity(new Intent(mContext, RegisterActivity.class));
                break;
            default:
                break;
        }
    }
}
