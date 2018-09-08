package com.saxxis.saanpaydestributor.activities.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.helpers.AppHelper;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_login)
    void gotoLogin(){
        AppHelper.LaunchActivity(LoginMainActivity.this,LoginActivity.class);
    }

    @OnClick(R.id.btn_signup)
    void gotoSignUP(){
        AppHelper.LaunchActivity(LoginMainActivity.this,SignupActivity.class);
    }
}
