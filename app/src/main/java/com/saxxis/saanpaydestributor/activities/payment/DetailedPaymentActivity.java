package com.saxxis.saanpaydestributor.activities.payment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.helpers.AppHelper;
import com.saxxis.saanpaydestributor.models.WalletTranxs;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailedPaymentActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.trax_amount)
    TextView tranxAmount;

    @BindView(R.id.tranx_id_number)
    TextView trankIdNumber;

    @BindView(R.id.message)
    TextView description;

    @BindView(R.id.order_time)
    TextView orderTime;

    private WalletTranxs walletTranxs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_payment);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.arrow);
        toolbar.setTitle("Payment Details");

        Bundle extras=getIntent().getExtras();
        if (extras!=null){
//            if (extras.getString("idarray").equals("wallet"))
            walletTranxs=extras.getParcelable("ordernumber");
            orderTime.setText(AppHelper.spanDateFormater(walletTranxs.getDate()));
            tranxAmount.setText(getResources().getString(R.string.currency)+" "+walletTranxs.getAmount());
            description.setText(walletTranxs.getWallet_description());
            if (!walletTranxs.getOrder_number().isEmpty()){
                trankIdNumber.setText("Wallet Transaction ID: "+walletTranxs.getOrder_number());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id=item.getItemId();
        if (id==android.R.id.home){
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
