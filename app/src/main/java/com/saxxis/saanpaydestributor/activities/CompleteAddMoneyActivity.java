package com.saxxis.saanpaydestributor.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.atom.mobilepaymentsdk.PayActivity;
import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.activities.payment.PassBookActivity;
import com.saxxis.saanpaydestributor.app.AppConstants;
import com.saxxis.saanpaydestributor.app.MixCartApplication;
import com.saxxis.saanpaydestributor.app.UserPref;
import com.saxxis.saanpaydestributor.helpers.AppHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.saxxis.saanpaydestributor.R.string.userid;

public class CompleteAddMoneyActivity extends AppCompatActivity {

    @BindView(R.id.paytoaddmoneytoolbar)
    Toolbar toolbar;

    @BindView(R.id.addmoney_creditpay)TextView payCredit;
    @BindView(R.id.addmoney_debitcard)TextView payDebit;
    @BindView(R.id.addmoney_netbanking)TextView payNetBanking;

    private UserPref mUser;

    Intent newPayIntent ;
    Resources res;

    String amount="";
    String orderID="";
    String paymentMode="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_add_money);

        ButterKnife.bind(this);
        mUser = new UserPref(CompleteAddMoneyActivity.this);
        setSupportActionBar(toolbar);
        res=this.getResources();

        toolbar.setTitle("Add Money to your SaanPay Wallet");
        toolbar.setNavigationIcon(R.drawable.arrow);
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            amount = extras.getString("amount");
            orderID = extras.getString("ordernumber");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            super.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @OnClick(R.id.addmoney_creditpay)
    public void goToAddCreditMoney(){
        final String[] list={"MASTER","VISA","MAESTRO"};
        new AlertDialog.Builder(CompleteAddMoneyActivity.this)
                .setTitle("select Card Type")
                .setCancelable(true)
                .setItems(list, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        paymentMode="CC";
                        String finalurl="";
                        callAtomKit("CC",list[which]);
                        dialog.dismiss();
                    }
                }).create().show();

    }

    @OnClick(R.id.addmoney_debitcard)
    public void goToAddDebitMoney(){
        final String[] list={"MASTER","VISA","MAESTRO"};
        new AlertDialog.Builder(CompleteAddMoneyActivity.this)
                .setTitle("select Card Type")
                .setCancelable(true)
                .setItems(list, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        paymentMode="DC";
                        String finalurl="";
                        callAtomKit("DC",list[which]);
                        dialog.dismiss();
                    }
                }).create().show();
    }

    @OnClick(R.id.addmoney_netbanking)
    public void goToAddNetBankingMoney(){

        //paymentamount,ordernumber
        paymentMode="NB";
        // User ID, Password and Product ID m
        newPayIntent = new Intent(CompleteAddMoneyActivity.this, PayActivity.class);
        newPayIntent.putExtra("merchantId", res.getString(userid));
        newPayIntent.putExtra("txnscamt", "0"); //Fixed. Must be 0
        newPayIntent.putExtra("loginid", res.getString(userid));
        newPayIntent.putExtra("password", res.getString(R.string.merchantpassword));
        newPayIntent.putExtra("prodid", res.getString(R.string.productid));
        newPayIntent.putExtra("txncurr", "INR"); //Fixed. Must be ?INR?
        newPayIntent.putExtra("clientcode", "007");
        newPayIntent.putExtra("custacc", res.getString(R.string.accno));
        newPayIntent.putExtra("amt",amount);//Should be 3 decimal number i.e 51.000
        newPayIntent.putExtra("txnid",orderID);
        newPayIntent.putExtra("date", new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.ENGLISH).format(new Date()));//Should be in same format

        //        newPayIntent.putExtra("bankid", “2001”); //Should be valid bank id // Optional
        newPayIntent.putExtra("discriminator", "NB"); // NB or IMPS or All ONLY (value should be same as commented)
        //use below Production url only with Production "Library-MobilePaymentSDK", Located inside PROD folder
        newPayIntent.putExtra("ru","https://payment.atomtech.in/mobilesdk/param"); //ru FOR Production

        //use below UAT url only with UAT "Library-MobilePaymentSDK", Located inside UAT folder
//        newPayIntent.putExtra("ru", "https://paynetzuat.atomtech.in/mobilesdk/param"); // FOR UAT (Testing)

        //Optinal Parameters
//        newPayIntent.putExtra("customerName",mUser.getName()); //Only for Name
//        newPayIntent.putExtra("customerEmailID",mUser.getEmail());//Only for Email ID
//        newPayIntent.putExtra("customerMobileNo", mUser.getMobileNumber());//Only for Mobile Number
//        newPayIntent.putExtra("billingAddress", mUser.getKeyAddress());//Only for Address
//        newPayIntent.putExtra("optionalUdf9", "OPTIONAL DATA 1");// Can pass any data
//        newPayIntent.putExtra("mprod", mprod); // Pass data in XML format, only for Multi product
        startActivityForResult(newPayIntent, 1);

    }


    private void callAtomKit(String cardType,String cardSignatureType) {
        /**
         * Set Parameters Before Initializing the EBS Gateway, All mandatory
         * values must be provided
         */
        newPayIntent = new Intent(CompleteAddMoneyActivity.this, PayActivity.class);
        /** Payment Amount Details */
        // Total Amount
        newPayIntent.putExtra("merchantId",res.getString(userid));
        newPayIntent.putExtra("txnscamt", "0"); //Fixed. Must be 0
        newPayIntent.putExtra("loginid",  res.getString(userid));
        newPayIntent.putExtra("password", res.getString(R.string.merchantpassword));
        newPayIntent.putExtra("prodid",  res.getString(R.string.productid));
        newPayIntent.putExtra("txncurr", "INR"); //Fixed. Must be INR
        newPayIntent.putExtra("clientcode", "007");
        newPayIntent.putExtra("custacc", res.getString(R.string.accno));
        newPayIntent.putExtra("channelid", "INT");
        newPayIntent.putExtra("amt", amount);//Should be 3 decimal number i.e 100.000
        newPayIntent.putExtra("txnid",orderID);
        newPayIntent.putExtra("date",new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.ENGLISH).format(new Date()));//"30/12/2015 18:31:00");//Should be in same format
        newPayIntent.putExtra("cardtype", cardType);//strPaymentMode);// CC or DC ONLY (value should be same as commented)
        newPayIntent.putExtra("cardAssociate", cardSignatureType);//strCardType);// for VISA and MASTER. MAESTRO ONLY (value should be same as commented)
        newPayIntent.putExtra("surcharge", "NO");// Should be passed YES if surcharge is applicable else pass NO
//        //use below Production url only with Production "Library-MobilePaymentSDK", Located inside PROD folder
        newPayIntent.putExtra("ru","https://payment.atomtech.in/mobilesdk/param"); //ru FOR Production
//        use below UAT url only with UAT "Library-MobilePaymentSDK", Located inside UAT folder
//        newPayIntent.putExtra("ru", "https://paynetzuat.atomtech.in/mobilesdk/param"); // FOR UAT (Testing)

        //Optinal Parameters
//        newPayIntent.putExtra("customerName",mUser.getName());//Only for Name
//        newPayIntent.putExtra("customerEmailID", mUser.getEmail());//Only for Email ID
//        newPayIntent.putExtra("customerMobileNo",mUser.getMobileNumber());//Only for Mobile Number
//        newPayIntent.putExtra("billingAddress", mUser.getKeyAddress());//Only for Address
//        newPayIntent.putExtra("optionalUdf9", "OPTIONAL DATA 2");// Can pass any data
//        newPayIntent.putExtra("mprod", mprod); // Pass data in XML format, only for Multi product
        startActivityForResult(newPayIntent, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
        {
            if (data != null)
            {
                String message = data.getStringExtra("status");
                String[] resKey = data.getStringArrayExtra("responseKeyArray");
                String[] resValue = data.getStringArrayExtra("responseValueArray");

                if(resKey!=null && resValue!=null){
                    JSONObject jsonObject=new JSONObject();
                    for(int i=0; i<resKey.length; i++) {
                        System.out.println(" " + i + " resKey : " + resKey[i] + " resValue : " + resValue[i]);
                        try {
                            jsonObject.put(resKey[i], resValue[i]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                Log.e("response",jsonObject.toString());
//                        if (i==resKey.length-1){
//                            if (resValue[i].contains("success_00")){
                                String finalAddWallet="";
                                if (paymentMode.equals("DC")||paymentMode.equals("CC")){
                                    finalAddWallet = AppConstants.SERVER_URL+"index.php?option=com_jbackend&view=request&action=get&module=user&resource=atompayment" +
                                            "&userid="+mUser.getUserId()+"&ordernumber="+orderID+"&Amount=" +amount+
                                            "&transationid="+resValue[20]+"&ResponseCode="+message.replaceAll(" ","")+"&ResponseMessage="+jsonObject.toString();
                                }
                                if (paymentMode.equals("NB")||paymentMode.equals("IMPS")){
                                    finalAddWallet = AppConstants.SERVER_URL+"index.php?option=com_jbackend&view=request&action=get&module=user&resource=atompayment" +
                                            "&userid="+mUser.getUserId()+"&ordernumber="+orderID+"&Amount=" +amount+
                                            "&transationid="+resValue[9]+"&ResponseCode="+message.replaceAll(" ","")+"&ResponseMessage="+jsonObject.toString();
                                }

                                Log.e("response",finalAddWallet);
                                StringRequest request=new StringRequest(Request.Method.GET, finalAddWallet.replace(" ","%20"),
                                        new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(final String response) {
                                        AppHelper.logout(CompleteAddMoneyActivity.this,response);
                                        Log.e("response",response);
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            String status = jsonObject.getString("status");
//                                            gotoDetailedString();

                                            if(status.equals("ok")){
                                                new AlertDialog.Builder(CompleteAddMoneyActivity.this)
                                                        .setMessage(jsonObject.getString("message"))
                                                        .setCancelable(false)
                                                        .create().show();

                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        AppHelper.LaunchActivity(CompleteAddMoneyActivity.this, PassBookActivity.class);
                                                        finish();
                                                    }
                                                }, 2000);
                                            }

                                            if(status.equals("ko")){
                                                new AlertDialog.Builder(CompleteAddMoneyActivity.this)
                                                        .setMessage(jsonObject.getString("message"))
                                                        .setCancelable(false)
//                                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
//                                                            @Override
//                                                            public void onClick(DialogInterface dialog, int which) {
//                                                                finish();
//                                                                AppHelper.LaunchActivity(CompleteAddMoneyActivity.this, PassBookActivity.class);
//                                                            }
//                                                        })
                                                        .create().show();

                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        AppHelper.LaunchActivity(CompleteAddMoneyActivity.this, PassBookActivity.class);
                                                        finish();
                                                    }
                                                }, 2000);

                                            }

                                        }catch (JSONException ignored){
                                            AppHelper.hideDialog();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        AppHelper.hideDialog();
                                    }
                                });

                                MixCartApplication.getInstance().addToRequestQueue(request);
//                            }
//                            if (resValue[i].contains("F")){
//                                new AlertDialog.Builder(CompleteAddMoneyActivity.this)
//                                        .setMessage(message+" :: recharge Failed")
//                                        .setCancelable(false)
//                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                finish();
//                                            }
//                                        })
//                                        .create().show();
//                            }
//                            if (resValue[i].contains("C")){
//                                new AlertDialog.Builder(CompleteAddMoneyActivity.this)
//                                        .setMessage(message+" :: recharge cancelled")
//                                        .setCancelable(false)
//                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                finish();
//                                            }
//                                        })
//                                        .create().show();
//
//                            }
//                        }
                    }

                    if (message.equalsIgnoreCase("Transaction Successful!")){
                        Log.e("response",resValue[19]);
                    }else{
                        new AlertDialog.Builder(CompleteAddMoneyActivity.this)
                                .setMessage(message)
                                .setCancelable(false)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                                .create().show();
                    }

                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void gotoDetailedString() {
        Log.e("response", AppConstants.ORDER_DETAILS+mUser.getUserId()+"&ordernumber="+orderID);
       AppHelper.showDialog(CompleteAddMoneyActivity.this,"Loading Please Wait...",false);
        StringRequest request = new StringRequest(Request.Method.GET, AppConstants.ORDER_DETAILS+mUser.getUserId()+"&ordernumber="+orderID,
                new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                Log.e("response",response);
                AppHelper.hideDialog();
                AppHelper.logout(CompleteAddMoneyActivity.this,response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if(status.equals("ok")){
                        new AlertDialog.Builder(CompleteAddMoneyActivity.this)
                                .setMessage(jsonObject.getString("message"))
                                .setCancelable(false)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        AppHelper.LaunchActivity(CompleteAddMoneyActivity.this, PassBookActivity.class);
                                    }
                                })
                                .create().show();
                    }
                    if(status.equals("ko")){
                        new AlertDialog.Builder(CompleteAddMoneyActivity.this)
                                .setMessage(jsonObject.getString("message"))
                                .setCancelable(false)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        AppHelper.LaunchActivity(CompleteAddMoneyActivity.this, PassBookActivity.class);
                                        finish();
                                    }
                                })
                                .create().show();
                    }

                }catch (JSONException ignored){
                    AppHelper.hideDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AppHelper.hideDialog();
            }
        });

        MixCartApplication.getInstance().addToRequestQueue(request);
    }
}
