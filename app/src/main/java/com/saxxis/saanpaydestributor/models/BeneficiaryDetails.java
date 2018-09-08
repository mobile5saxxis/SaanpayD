package com.saxxis.saanpaydestributor.models;

/**
 * Created by saxxis25 on 11/1/2017.
 */

public class BeneficiaryDetails {

    private String name;
    private String account;
    private String bankcode;
    private String ifsc;
    private String senderid;
    private String receiverid;
    private String otpverified;

    public BeneficiaryDetails(String name, String account, String bankcode, String ifsc, String senderid, String receiverid,String otpverified) {
        this.name = name;
        this.account = account;
        this.bankcode = bankcode;
        this.ifsc = ifsc;
        this.senderid = senderid;
        this.receiverid = receiverid;
        this.otpverified=otpverified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getBankcode() {
        return bankcode;
    }

    public void setBankcode(String bankcode) {
        this.bankcode = bankcode;
    }

    public String getIfsc() {
        return ifsc;
    }

    public void setIfsc(String ifsc) {
        this.ifsc = ifsc;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public String getReceiverid() {
        return receiverid;
    }

    public void setReceiverid(String receiverid) {
        this.receiverid = receiverid;
    }

    public String getOtpverified() {
        return otpverified;
    }

    public void setOtpverified(String otpverified) {
        this.otpverified = otpverified;
    }
}
