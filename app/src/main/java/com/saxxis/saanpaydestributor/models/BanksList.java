package com.saxxis.saanpaydestributor.models;

/**
 * Created by saxxis25 on 11/1/2017.
 */

public class BanksList {

    private String id;
    private String bank_name;
    private String bank_code;
    private String ifsc;


    public BanksList(String id, String bank_name, String bank_code, String ifsc) {
        this.id = id;
        this.bank_name = bank_name;
        this.bank_code = bank_code;
        this.ifsc = ifsc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getBank_code() {
        return bank_code;
    }

    public void setBank_code(String bank_code) {
        this.bank_code = bank_code;
    }

    public String getIfsc() {
        return ifsc;
    }

    public void setIfsc(String ifsc) {
        this.ifsc = ifsc;
    }
}
