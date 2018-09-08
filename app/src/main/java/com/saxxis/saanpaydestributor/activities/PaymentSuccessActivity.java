package com.saxxis.saanpaydestributor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.activities.main.MainActivity;
import com.saxxis.saanpaydestributor.app.AppConstants;
import com.saxxis.saanpaydestributor.app.MixCartApplication;
import com.saxxis.saanpaydestributor.app.UserPref;
import com.saxxis.saanpaydestributor.helpers.AppHelper;

import org.json.JSONObject;

import java.net.URLEncoder;

public class PaymentSuccessActivity extends AppCompatActivity {

    String payment_id;
    String PaymentId;
    String TransactionId;
    String AccountId;
    String MerchantRefNo;
    String Amount;
    String DateCreated;
    String Description;
    String Mode;
    String IsFlagged;
    String BillingName;
    String BillingAddress;
    String BillingCity;
    String BillingState;
    String BillingPostalCode;
    String BillingCountry;
    String BillingPhone;
    String BillingEmail;
    String DeliveryName;
    String DeliveryAddress;
    String DeliveryCity;
    String DeliveryState;
    String DeliveryPostalCode;
    String DeliveryCountry;
    String DeliveryPhone;
    String PaymentStatus;
    String PaymentMode;
    String ResponseMessage;
    String SecureHash;

    private UserPref mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);
        mUser = new UserPref(this);
        Intent intent = getIntent();

        payment_id = intent.getStringExtra("payment_id");
        System.out.println("Success and Failure response to merchant app..." + " " + payment_id);
        Log.e("Response","Success and Failure response to merchant app..." + " " + payment_id);

        getJsonReport(payment_id);

    }

    private void getJsonReport(String payment_id) {
        try{
            JSONObject  jObject = new JSONObject(payment_id);
            System.out.println("called1");
            PaymentId = jObject.getString("PaymentId");
            TransactionId = jObject.getString("TransactionId");
            ResponseMessage = jObject.getString("ResponseMessage");
            AccountId = jObject.getString("AccountId");
            MerchantRefNo = jObject.getString("MerchantRefNo");
            Amount = jObject.getString("Amount");
            DateCreated = jObject.getString("DateCreated");
            Description = jObject.getString("Description");
            Mode = jObject.getString("Mode");
            IsFlagged = jObject.getString("IsFlagged");
            BillingName = jObject.getString("BillingName");
            BillingAddress = jObject
                    .getString("BillingAddress");
            BillingCity = jObject.getString("BillingCity");
            BillingState = jObject.getString("BillingState");
            BillingPostalCode = jObject
                    .getString("BillingPostalCode");
            BillingCountry = jObject
                    .getString("BillingCountry");
            BillingPhone = jObject.getString("BillingPhone");
            BillingEmail = jObject.getString("BillingEmail");
            DeliveryName = jObject.getString("DeliveryName");
            DeliveryAddress = jObject
                    .getString("DeliveryAddress");
            DeliveryCity = jObject.getString("DeliveryCity");
            DeliveryState = jObject.getString("DeliveryState");
            DeliveryPostalCode = jObject
                    .getString("DeliveryPostalCode");
            DeliveryCountry = jObject
                    .getString("DeliveryCountry");
            DeliveryPhone = jObject.getString("DeliveryPhone");
            PaymentStatus = jObject.getString("PaymentStatus");
            PaymentMode = jObject.getString("PaymentMode");
            SecureHash = jObject.getString("SecureHash");

            ResponseMessage = URLEncoder.encode(ResponseMessage,"utf-8");
            PaymentMode = URLEncoder.encode(PaymentMode,"utf-8");

            if(PaymentStatus.equalsIgnoreCase("failed")){

            }else{

            }
            System.out.println("called");
            updateWallet();

        }catch (Exception e){
            Log.e("error",e.getMessage());
        }

    }

    private void updateWallet() {
        String url = AppConstants.UPDATE_MONEY_URL +  "&userid="+mUser.getUserId()+"&orderid="+MerchantRefNo+"&amount="+Amount
                +"&paymentid="+PaymentId+"&transationid="+TransactionId+"&responcemessage="+ResponseMessage+"&paymentmode="+PaymentMode+"&paymentstatus="+PaymentStatus;
        System.out.println(url);
        StringRequest str = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                AppHelper.logout(PaymentSuccessActivity.this,s);
                System.out.println(s);
                try{
                    JSONObject resp = new JSONObject(s);
                    String status = resp.getString("status");
                    if(status.equals("ok")){
                        Intent intent = new Intent(PaymentSuccessActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        PaymentSuccessActivity.this.finish();
                    }
                }catch (Exception e){

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        MixCartApplication.getInstance().addToRequestQueue(str);
    }
}
