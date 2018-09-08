package com.saxxis.saanpaydestributor.activities.leftmenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.app.UserPref;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReferralActivity extends AppCompatActivity {

    @BindView(R.id.referraL_id)
    TextView txtID;

    private UserPref mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mUser = new UserPref(this);
        txtID.setText(mUser.getReferralId());

    }

    @OnClick(R.id.btn_share)
    void ShareIntent(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "click The link: https://play.google.com/store/apps/details?id=com.topcharging.android&hl=en \n" +
                "  Refer Code Is: " + mUser.getReferralId());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

}
