package com.saxxis.saanpaydestributor.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by saxxis25 on 8/22/2017.
 */

public class WalletTranxs implements Parcelable {

    private String amount;
    private String order_number;
    private String wallet_description;
    private String date;
    private String mobile;
    private String operatorname;
    private String wallet_type;
    private String status;

        public WalletTranxs(String amount,String order_number,String wallet_description,String date,String mobile,String operatorname,String wallet_type,String status){
            this.amount=amount;
            this.order_number=order_number;
            this.wallet_description=wallet_description;
            this.date=date;
            this.mobile=mobile;
            this.operatorname=operatorname;
            this.wallet_type=wallet_type;
            this.status=status;
        }


    protected WalletTranxs(Parcel in) {
        amount = in.readString();
        order_number = in.readString();
        wallet_description = in.readString();
        date = in.readString();
        mobile = in.readString();
        operatorname = in.readString();
        wallet_type = in.readString();
        status = in.readString();
    }

    public static final Creator<WalletTranxs> CREATOR = new Creator<WalletTranxs>() {
        @Override
        public WalletTranxs createFromParcel(Parcel in) {
            return new WalletTranxs(in);
        }

        @Override
        public WalletTranxs[] newArray(int size) {
            return new WalletTranxs[size];
        }
    };

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getOrder_number() {
        return order_number;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }

    public String getWallet_description() {
        return wallet_description;
    }

    public void setWallet_description(String wallet_description) {
        this.wallet_description = wallet_description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOperatorname() {
        return operatorname;
    }

    public void setOperatorname(String operatorname) {
        this.operatorname = operatorname;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getWallet_type() {
        return wallet_type;
    }

    public void setWallet_type(String wallet_type) {
        this.wallet_type = wallet_type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(amount);
        dest.writeString(order_number);
        dest.writeString(wallet_description);
        dest.writeString(date);
        dest.writeString(mobile);
        dest.writeString(operatorname);
        dest.writeString(wallet_type);
        dest.writeString(status);
    }
}
