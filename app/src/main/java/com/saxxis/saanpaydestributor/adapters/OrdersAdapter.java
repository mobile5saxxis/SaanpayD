package com.saxxis.saanpaydestributor.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.activities.leftmenu.HelpContactActivity;
import com.saxxis.saanpaydestributor.helpers.AppHelper;
import com.saxxis.saanpaydestributor.models.Order;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by saxxis25 on 4/5/2017.
 */

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrdersHolder> {

    private Context mContext;
    private ArrayList<Order> mData;

    public OrdersAdapter(Context cont,ArrayList<Order> data){
        this.mContext = cont;
        this.mData = data;
    }

    @Override
    public OrdersHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.order_item,parent,false);
        return new OrdersHolder(view);
    }

    @Override
    public void onBindViewHolder(OrdersHolder holder, int position) {
        Order order = mData.get(position);

        holder.txtOrderNumber.setText(order.getOrder_id());
        holder.txtOrderAmount.setText(mContext.getResources().getString(R.string.rupee_display,order.getAmount()));
        holder.txtOrderTime.setText(AppHelper.spanDateFormater(order.getPay_date()));
        if (!order.getOperatorname().equals("null")){
            holder.txtOperator.setText(order.getOperatorname());
        }else {
            holder.txtOperator.setText("");
        }

        holder.txtNumber.setText(order.getMobileno());

        if (order.getPay_status().equals("2")) {
            holder.txtOrderStatus.setText("Pending");
            holder.txtOrderStatus.setTextColor(Color.parseColor("#FF9800"));
        }
        if (order.getPay_status().equals("1")) {
            holder.txtOrderStatus.setText("Success");
            holder.txtOrderStatus.setTextColor(Color.parseColor("#0EC654"));
        }
        if (order.getPay_status().equals("0")) {
            holder.txtOrderStatus.setText("Failed");
            holder.txtOrderStatus.setTextColor(Color.parseColor("#E00034"));
        }
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }


    class OrdersHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.order_item_value)
        TextView txtOrderNumber;
        @BindView(R.id.order_item_amount)
        TextView txtOrderAmount;
        @BindView(R.id.order_item_time)
        TextView txtOrderTime;
        @BindView(R.id.order_item_status)
        TextView txtOrderStatus;
        @BindView(R.id.order_item_Operator)
        TextView txtOperator;
        @BindView(R.id.order_item_number)
        TextView txtNumber;

        TextView help;

        public OrdersHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            help = (TextView)itemView.findViewById(R.id.help);
            help.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId()==help.getId()){
                //Toast.makeText(v.getContext(), "ITEM PRESSED = " + txtOrderNumber.getText().toString(), Toast.LENGTH_SHORT).show();
                Intent mIntent = new Intent(mContext, HelpContactActivity.class);
                mIntent.putExtra("ordernumber",txtOrderNumber.getText().toString());
                mContext.startActivity(mIntent);
            }
        }

    }
}
