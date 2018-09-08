package com.saxxis.saanpaydestributor.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by saxxis25 on 4/5/2017.
 */

public class Order implements Parcelable {

    private String mobileno;
    private String order_id;
    private String amount;
    private String pay_status;
    private String pay_date;
    private String optype_name;
    private String operatorname;

    public Order(){

    }

    Order(String mobileno,String order_id,String amount,String pay_status,String pay_date,String optype_name,String operatorname){
        this.mobileno = mobileno;
        this.order_id = order_id;
        this.amount = amount;
        this.pay_status = pay_status;
        this.pay_date = pay_date;
        this.optype_name = optype_name;
        this.operatorname=operatorname;
    }


    protected Order(Parcel in) {
        mobileno = in.readString();
        order_id = in.readString();
        amount = in.readString();
        pay_status = in.readString();
        pay_date = in.readString();
        optype_name = in.readString();
        operatorname = in.readString();
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    public String getOrder_id() {
        return order_id;
    }

    public String getMobileno() {
        return mobileno;
    }

    public String getAmount() {
        return amount;
    }

    public String getPay_status() {
        return pay_status;
    }

    public String getPay_date() {
        return pay_date;
    }

    public String getOptype_name() {
        return optype_name;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public void setOptype_name(String optype_name) {
        this.optype_name = optype_name;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public void setPay_date(String pay_date) {
        this.pay_date = pay_date;
    }

    public void setPay_status(String pay_status) {
        this.pay_status = pay_status;
    }

    public String getOperatorname() {
        return operatorname;
    }

    public void setOperatorname(String operatorname) {
        this.operatorname = operatorname;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mobileno);
        dest.writeString(order_id);
        dest.writeString(amount);
        dest.writeString(pay_status);
        dest.writeString(pay_date);
        dest.writeString(optype_name);
        dest.writeString(operatorname);
    }
}
