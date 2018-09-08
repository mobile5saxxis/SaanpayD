package com.saxxis.saanpaydestributor.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.helpers.AppHelper;
import com.saxxis.saanpaydestributor.models.Order;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderDertailsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.order_amount)
    TextView tranxAmount;

    @BindView(R.id.order_tranx_id_number)
    TextView trankIdNumber;

    @BindView(R.id.order_message)
    TextView description;

    @BindView(R.id.order_time)
    TextView orderTime;

    @BindView(R.id.mobile_number)
    TextView orderMobileNumber;

    @BindView(R.id.mobileopetor)
    TextView operator;

    Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_dertails);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.arrow);
        toolbar.setTitle("Order Details");

        Bundle extras=getIntent().getExtras();
        if (extras!=null){
            order=extras.getParcelable("ordereddata");
            orderTime.setText(AppHelper.spanDateFormater(order.getPay_date()));
            orderMobileNumber.setText("Recharge for :"+order.getMobileno());
            tranxAmount.setText(getResources().getString(R.string.currency)+" "+order.getAmount());
            operator.setText("Operator :"+order.getOperatorname());

            if (order.getPay_status().equals("2")) {
                description.setText("Order is Pending");
                description.setTextColor(Color.parseColor("#FF9800"));
            }

            if (order.getPay_status().equals("1")) {
                description.setText("Order is Success");
                description.setTextColor(Color.parseColor("#0EC654"));
            }
            if (order.getPay_status().equals("0")) {
                description.setText("Order is Failed");
                description.setTextColor(Color.parseColor("#E00034"));
            }


            if (!order.getOrder_id().isEmpty()){
                trankIdNumber.setText("Transaction ID: "+order.getOrder_id());
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();
        if (id==android.R.id.home){
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
