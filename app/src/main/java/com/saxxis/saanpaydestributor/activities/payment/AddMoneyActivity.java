package com.saxxis.saanpaydestributor.activities.payment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.activities.CompleteAddMoneyActivity;
import com.saxxis.saanpaydestributor.app.AppConstants;
import com.saxxis.saanpaydestributor.app.MixCartApplication;
import com.saxxis.saanpaydestributor.app.UserPref;
import com.saxxis.saanpaydestributor.helpers.AppHelper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddMoneyActivity extends AppCompatActivity {

    @BindView(R.id.tinplay_amount)
    TextInputLayout textLayAmount;


    @BindView(R.id.add_money_et_money)
    TextInputEditText etMoney;

    @BindView(R.id.txt_walletbalance)
    TextView txtWalletBalance;

    @BindView(R.id.txt_1000)
    TextView txt1000;

    @BindView(R.id.txt_500)
    TextView txt500;

    @BindView(R.id.txt_100)
    TextView txt100;

    @BindView(R.id.add_money_btn)
    AppCompatButton btnAddMoney;

    @BindView(R.id.addmoneytoolbar)
    Toolbar toolbar;


    String finalmoney="0";

    private UserPref mUser;

    // Mandatory
    private static String HOST_NAME = "";

    ArrayList<HashMap<String, String>> custom_post_parameters;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);
        ButterKnife.bind(this);
        mUser = new UserPref(AddMoneyActivity.this);
        setSupportActionBar(toolbar);
        txtWalletBalance.setText(getResources().getString(R.string.walletbalanceout)+mUser.getWalletAmount());

        toolbar.setTitle("Add Money to your SaanPay Wallet");
        toolbar.setNavigationIcon(R.drawable.arrow);
        refreshWallet();

//        String walletbalance=getResources().getString(R.string.walletbalanceout)+mUser.getWalletAmount();
//        txtWalletBalance.setText(walletbalance);

        HOST_NAME = getResources().getString(R.string.hostname);
    }

    @OnClick(R.id.txt_1000)
    void add1000(){
        etMoney.setText("1000");
        finalmoney="1000";
    }

    @OnClick(R.id.txt_500)
    void add500(){
        etMoney.setText("500");
        finalmoney="500";
    }

    @OnClick(R.id.txt_100)
    void add100(){
        etMoney.setText("100");
        finalmoney="100";
    }

    @OnClick(R.id.add_money_btn)
   public void addmoney(){

        finalmoney = etMoney.getText().toString().trim();

        if(!finalmoney.equals("0")){
            //rediectToPaymentGateway(finalmoney);
            checkLimit(finalmoney);
        }else{
            if(finalmoney.isEmpty()) {
                new AlertDialog.Builder(AddMoneyActivity.this)
                        .setMessage("Enter Amount")
                        .setPositiveButton("close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }else{
              //  rediectToPaymentGateway(AddMoneyActivity.this,amount);
            }
        }


    }

    private void refreshWallet() {
//        mProgress.setVisibility(View.VISIBLE);
        Log.e("response",AppConstants.WALLET_URL+"&userid="+mUser.getUserId());
        StringRequest request=new StringRequest(Request.Method.GET, AppConstants.WALLET_URL+
                "&userid="+mUser.getUserId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response",response);

                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
//                    mProgress.setVisibility(View.GONE);
                            if (status.equals("ok")){
                                JSONObject dataobject=jsonObject.getJSONObject("data");
                                mUser.setWalletAmount(Float.parseFloat(dataobject.getString("walletamount")));
                                txtWalletBalance.setText(getResources().getString(R.string.walletbalanceout)+dataobject.getString("walletamount"));
                            }
//                            if(status.equals("ko")) {
//                                new AlertDialog.Builder(AddMoneyActivity.this)
//                                        .setMessage("User Session Timed Out...Login Again")
//                                        .setPositiveButton("Login", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                mUser.logoutUser();
//                                                Intent intent = new Intent(AddMoneyActivity.this, LoginActivity.class);
//                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
//                                                startActivity(intent);
//                                                Toast.makeText(AddMoneyActivity.this,"Logout Successfull",Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//                            }
                        }catch (Exception ignored){

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        MixCartApplication.getInstance().addToRequestQueue(request);
    }


    private void rediectToPaymentGateway(final String finalmoney) {

        String url = AppConstants.MONEY_ORDERID_REQ+"&userid="+mUser.getUserId()+"&amount="+finalmoney+"&desc=RequestToAddMoney";

        AppHelper.showDialog(AddMoneyActivity.this,"Loading Your Details Please Wait...");
        StringRequest str = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                AppHelper.logout(AddMoneyActivity.this,s);
                try{
                    AppHelper.hideDialog();
                    JSONObject resp = new JSONObject(s);
                    String status = resp.getString("status");
                    if(status.equals("ok")){
                        String amount = resp.getString("amount");
                        String orderId = resp.getString("ordernumber");
                        startActivity(new Intent(AddMoneyActivity.this, CompleteAddMoneyActivity.class)
                                .putExtra("amount",amount).putExtra("ordernumber",orderId));
                    }
                }catch (Exception e){
                    AppHelper.hideDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                AppHelper.hideDialog();
            }
        });

        str.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MixCartApplication.getInstance().addToRequestQueue(str);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            super.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void checkLimit(final String finalmoney){
        String url = AppConstants.LIMIT_CHECK+mUser.getUserId();

        AppHelper.showDialog(AddMoneyActivity.this,"Checking transaction limit. Please Wait...");
        StringRequest str = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                AppHelper.logout(AddMoneyActivity.this,s);
                try{
                    AppHelper.hideDialog();
                    JSONObject resp = new JSONObject(s);
                    String status = resp.getString("status");
                    if(status.equals("ok")){
                        final String usertype = resp.getString("usertype");
                        String atomwallet = resp.getString("atomwallet");
                        String walletamt = resp.getString("walletamt");
                        String atomwalletremainingamount = resp.getString("atomwalletremainingamount");
                        String remainingwalletamount = resp.getString("remainingwalletamount");
                        double remainingwalletamountInt = 0;
                        if(remainingwalletamount!=null){
                            remainingwalletamountInt = Double.parseDouble(remainingwalletamount);
                        }
                        if(Integer.parseInt(finalmoney)<=remainingwalletamountInt){
                            rediectToPaymentGateway(finalmoney);
                        }else{
                            new AlertDialog.Builder(AddMoneyActivity.this)
                                    .setMessage("Available limit is "+remainingwalletamountInt+" Please re enter amount")
                                    .setPositiveButton("close", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                          //  if(usertype.equals("NonkycUser")){
                                         //       showKYCAlert();
                                         //   }

                                        }
                                    }).show();
                        }

                    }
                }catch (Exception e){
                    AppHelper.hideDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                AppHelper.hideDialog();
            }
        });

        str.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MixCartApplication.getInstance().addToRequestQueue(str);

    }
    public void showKYCAlert(){
        new AlertDialog.Builder(AddMoneyActivity.this)
                .setMessage("Verify KYC to increase limit")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivity(new Intent(AddMoneyActivity.this, KYCUploadActivity.class));
                    }
                }).show();
    }

}
