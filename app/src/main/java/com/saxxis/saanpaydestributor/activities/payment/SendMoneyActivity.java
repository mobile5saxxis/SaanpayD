package com.saxxis.saanpaydestributor.activities.payment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;

import com.saxxis.saanpaydestributor.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SendMoneyActivity extends AppCompatActivity {

    @BindView(R.id.btn_money)
    Button sendMoney;

    @BindView(R.id.bank_id)
    TextView txtBankId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Transfer Money");

    }
}
