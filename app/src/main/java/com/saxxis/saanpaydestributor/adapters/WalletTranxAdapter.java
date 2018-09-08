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
import com.saxxis.saanpaydestributor.models.WalletTranxs;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by saxxis25 on 8/21/2017.
 */

public class WalletTranxAdapter extends RecyclerView.Adapter<WalletTranxAdapter.WalletTranxHolder> {

    private Context context;
    private ArrayList<WalletTranxs> mData;

    public WalletTranxAdapter(Context context, ArrayList<WalletTranxs> mData) {
        this.context = context;
        this.mData = mData;
    }

    @Override
    public WalletTranxHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
        return new WalletTranxHolder(view);
    }

    @Override
    public void onBindViewHolder(WalletTranxHolder holder, int position) {
        if (!mData.get(position).getOrder_number().isEmpty()) {
            holder.orderIdRefNumber.setText(mData.get(position).getOrder_number());
        }

        if (mData.get(position).getOrder_number().isEmpty()) {
//            holder.orderIdRefNumber.setText("");
            holder.orderIdRefNumber.setText(mData.get(position).getOrder_number());
            holder.orderTitle.setVisibility(View.GONE);
        }

        holder.orderAmount.setText(context.getResources().getString(R.string.currency) + " " + mData.get(position).getAmount());
        holder.orderGenerateTime.setText(AppHelper.spanDateFormater(mData.get(position).getDate()));

        if (mData.get(position).getStatus().equals("2")) {
            holder.orderStatus.setText("Pending");
            holder.orderStatus.setTextColor(Color.parseColor("#FF9800"));
        }
        if (mData.get(position).getStatus().equals("1")) {
            holder.orderStatus.setText("Success");
            holder.orderStatus.setTextColor(Color.parseColor("#0EC654"));
        }
        if (mData.get(position).getStatus().equals("0")) {
            if (mData.get(position).getWallet_type().equals("1")) {
                holder.orderStatus.setText("Credited");
                holder.orderStatus.setTextColor(Color.parseColor("#E00034"));
            }
            if (mData.get(position).getWallet_type().equals("2")) {
                holder.orderStatus.setText("Debited");
                holder.orderStatus.setTextColor(Color.parseColor("#E00034"));
            }
        }

        if (!mData.get(position).getMobile().isEmpty()) {
            holder.orderPhoneNumber.setText(mData.get(position).getMobile());
            holder.operator.setText(mData.get(position).getOperatorname());
        } else if (mData.get(position).getMobile().isEmpty()) {
            holder.orderPhoneNumber.setText(mData.get(position).getWallet_description().replaceAll("[-()]", ""));
            holder.operator.setText("");
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class WalletTranxHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.order_item_value)
        TextView orderIdRefNumber;

        @BindView(R.id.order_item_amount)
        TextView orderAmount;

        @BindView(R.id.order_item_time)
        TextView orderGenerateTime;

        @BindView(R.id.order_item_number)
        TextView orderPhoneNumber;

        @BindView(R.id.order_item_status)
        TextView orderStatus;

        @BindView(R.id.order_item_Operator)
        TextView operator;

        @BindView(R.id.order_item_title)
        TextView orderTitle;
        TextView help;

        public WalletTranxHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            help = (TextView) itemView.findViewById(R.id.help);
            help.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == help.getId()) {
                //Toast.makeText(v.getContext(), "ITEM PRESSED = " + txtOrderNumber.getText().toString(), Toast.LENGTH_SHORT).show();
                Intent mIntent = new Intent(context, HelpContactActivity.class);
                mIntent.putExtra("ordernumber", orderIdRefNumber.getText().toString());
                context.startActivity(mIntent);
            }
        }
    }

}
