package com.saxxis.saanpaydestributor.activities.payment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.widget.TextView;

import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.app.UserPref;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReceiveMoneyActivity extends AppCompatActivity {

    @BindView(R.id.qrcode_img)
    WebView ivQrCode;
    @BindView(R.id.unique_id)
    TextView txtUnique;
    @BindView(R.id.mn)
    TextView txtMN;

    private UserPref mUser;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_money);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mUser = new UserPref(this);
        url = "http://topcharging.com/plugins/jbackend/user/vennelaqrcode/parameter.php?data="+mUser.getUserId();
        ivQrCode.setInitialScale(1);
        ivQrCode.getSettings().setJavaScriptEnabled(true);
        ivQrCode.getSettings().setLoadWithOverviewMode(true);
        ivQrCode.getSettings().setUseWideViewPort(true);
        ivQrCode.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        ivQrCode.setScrollbarFadingEnabled(true);
        ivQrCode.loadUrl(url);

        txtUnique.setText(mUser.getUniqueId());
        txtMN.setText(mUser.getMobileNumber());
    }

    @OnClick(R.id.txt_share)
    void shareIntent(){

        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, "Scan this code to send money");
        share.putExtra(Intent.EXTRA_TEXT, url);
        startActivity(Intent.createChooser(share, "Share QRCode!"));

    }

}
