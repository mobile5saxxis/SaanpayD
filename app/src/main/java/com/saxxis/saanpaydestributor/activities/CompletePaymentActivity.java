package com.saxxis.saanpaydestributor.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
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
import com.saxxis.saanpaydestributor.app.FetchWalletAmount;
import com.saxxis.saanpaydestributor.app.MixCartApplication;
import com.saxxis.saanpaydestributor.app.UserPref;
import com.saxxis.saanpaydestributor.helpers.AppHelper;
import com.saxxis.saanpaydestributor.pattern.PatternLockUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

import static com.saxxis.saanpaydestributor.R.string.amount;


public class CompletePaymentActivity extends AppCompatActivity implements PatternLockUtils.OnConfirmPatternResultListener{

    @BindView(R.id.to_be_paid)
    TextView txtRechargeAmount;

    @BindView(R.id.current_balance)
    TextView txtCurrentBalance;

    @BindView(R.id.use_wallet_amount)
    TextView txtWalletAmount;


    @BindView(R.id.remaining_amount)
    TextView txtRemainingAmount;


    @BindView(R.id.complete_payment)
    Button btnCompletePayment;

    @BindView(R.id.addamount)
    Button btnAddAmount;

    @BindView(R.id.cl_comp_pay)
    CoordinatorLayout clLayout;

    @BindView(R.id.chck_wallet)
    CheckBox chckWallet;

    @BindView(R.id.walletlayout)
    LinearLayout walletLayoutchecked;

    @BindView(R.id.other_payment_options)
    LinearLayout otherPaymentOptions;

    String generatedOrderNumber="";
    String paymentMode="";


    private String operator,payamount=null,number,type,operatortype;
    private UserPref mUser;

    private boolean wallet=false;

    private Float walletBalance,balance;

    // Mandatory
    private static String HOST_NAME = "";

    ArrayList<HashMap<String, String>> custom_post_parameters;

    Intent newPayIntent ;
    Resources res;
    int walletValue=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_payment);
        ButterKnife.bind(this);
        mUser = new UserPref(this);

        if (mUser.getKeyUserType().equals("U")){
         otherPaymentOptions.setVisibility(View.VISIBLE);
        }



        if (mUser.getKeyUserType().equals("D")){
            otherPaymentOptions.setVisibility(View.GONE);
        }


        FetchWalletAmount.getAmount(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow);
        toolbar.setTitle("Complete Payment");
        Bundle extras = getIntent().getExtras();

        if(extras!=null){
            operator = extras.getString("operator");
            operatortype=extras.getString("operatortype");
            number = extras.getString("number");
            payamount = extras.getString("amount");
            type = extras.getString("type");
        }
        res = CompletePaymentActivity.this.getResources();

        txtRechargeAmount.setText(getResources().getString(R.string.rupee_display,payamount));
        txtRemainingAmount.setText(getResources().getString(R.string.rupee_display,payamount));
        btnAddAmount.setVisibility(View.GONE);
        btnCompletePayment.setVisibility(View.VISIBLE);

        Float temp = mUser.getWalletAmount();
        if (temp == -1){
            walletBalance = Float.valueOf(0);
        }else {
            walletBalance = temp;
        }

        chckWallet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    btnCompletePayment.setVisibility(View.VISIBLE);
                }
                if (!isChecked){
                    btnCompletePayment.setVisibility(View.GONE);
                }
            }
        });

        txtCurrentBalance.setText("(Your Current Balance is Rs"+ walletBalance+")");
        refreshWallet();

        if (mUser.getWalletAmount()<Integer.parseInt(payamount)){
            btnCompletePayment.setVisibility(View.GONE);
            walletLayoutchecked.setVisibility(View.GONE);
        }
        if (mUser.getWalletAmount()>=Integer.parseInt(payamount)){
            walletLayoutchecked.setVisibility(View.VISIBLE);
            btnCompletePayment.setVisibility(View.VISIBLE);
        }
//        rechargeAmount = Integer.parseInt(payamount);

        HOST_NAME = getResources().getString(R.string.hostname);
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
                        AppHelper.logout(CompletePaymentActivity.this,response);

                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
//                    mProgress.setVisibility(View.GONE);
                            if (status.equals("ok")){
                                JSONObject dataobject=jsonObject.getJSONObject("data");
                                mUser.setWalletAmount(Float.parseFloat(dataobject.getString("walletamount")));
                                txtCurrentBalance.setText("(Your Current Balance is Rs"+ dataobject.getString("walletamount")+")");
                                if (mUser.getWalletAmount()<Integer.parseInt(payamount)){
                                    btnCompletePayment.setVisibility(View.GONE);
                                }
                            }

                            /*if(status.equals("ko")) {
                                new AlertDialog.Builder(CompletePaymentActivity.this)
                                        .setMessage("User Session Timed Out...Login Again")
                                        .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                mUser.logoutUser();
                                                Intent intent = new Intent(CompletePaymentActivity.this, LoginActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                Toast.makeText(CompletePaymentActivity.this,"Logout Successfull",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }*/
                        }catch (Exception ignored){

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                mProgress.setVisibility(View.GONE);
            }
        });

        MixCartApplication.getInstance().addToRequestQueue(request);

    }

    @OnCheckedChanged(R.id.chck_wallet)
    void checkListener(){
        if(chckWallet.isChecked()){
//
//
//            txtWalletAmount.setVisibility(View.VISIBLE);
//            btnAddAmount.setVisibility(View.GONE);
//            btnCompletePayment.setVisibility(View.VISIBLE);
//            if(Integer.parseInt(payamount)<walletBalance){
//                wallet=true;
//                txtWalletAmount.setText(getResources().getString(R.string.negative_rupee_display,String.valueOf(Integer.parseInt(payamount))));
//                txtRemainingAmount.setText(getResources().getString(R.string.rupee_display,"0"));
//            }else{
//                wallet=false;
//                balance = Integer.parseInt(payamount)-walletBalance;
//                txtRemainingAmount.setText(getResources().getString(R.string.rupee_display,String.valueOf(balance)));
//                txtWalletAmount.setText(getResources().getString(R.string.negative_rupee_display,String.valueOf(walletBalance)));
//            }
        }

        if(!chckWallet.isChecked()){
            wallet=true;
            txtWalletAmount.setVisibility(View.GONE);
            txtRemainingAmount.setText(getResources().getString(R.string.rupee_display,payamount));
            btnAddAmount.setVisibility(View.VISIBLE);
            btnCompletePayment.setVisibility(View.GONE);
        }
    }


    @OnClick(R.id.complete_payment)
    void completePayment() {

        if (chckWallet.isChecked()){
            walletValue=1;
        }

        if (!chckWallet.isChecked()){
            walletValue=0;

        }

        String finalurl;
        switch (type) {
            case "mobile":
                finalurl = AppConstants.RECHARGE_URL+"&userid="+mUser.getUserId()+"&operatortype="+operatortype+"&op_id=" + operator + "&mobile=" + number + "&amount=" +payamount+"&wallet="+walletValue;
                submitToServer(finalurl, "Recharge Successfull");
                break;
            case "dth":
                finalurl = AppConstants.DTH_RECHARGE_URL+"&userid="+mUser.getUserId()+ "&operatortype="+operatortype+"&op_id=" + operator + "&mobile=" + number + "&amount=" + payamount+"&wallet="+walletValue;
                submitToServer(finalurl, "DTH Payment Successfull");
                break;
            case "dc":
                finalurl = AppConstants.DC_RECHARGE_URL +"&userid="+mUser.getUserId()+"&operatortype="+operatortype+ "&op_id=" + operator+"&amount=" + payamount + "&mobile=" + number +"&wallet="+walletValue;
                submitToServer(finalurl, "DataCard Payment Successfull");
                break;
            case "electricity":
                finalurl = AppConstants.GENERATE_ORDER+"&userid="+mUser.getUserId()+"&operatortype="+operatortype+ "&op_id=" + operator+"&amount=" + payamount + "&mobile=" + number +"&wallet="+walletValue;;
                submitToServer(finalurl,"Electricity Bill Payment SuccessFull");
                break;
            case "shopping":
                finalurl = "http://sastabankproperty.in.md-38.webhostbox.net/recharge/index.php?option=com_jbackend&view=request&action=get&module=user&resource=ordersgenarate&userid="+mUser.getUserId()+"&prodid="+number;
                submitToServer(finalurl, "Order Placed Successfully");
                break;
        }
//        if (!wallet) {
//            new AlertDialog.Builder(CompletePaymentActivity.this).setMessage("No Sufficient Funds..Add Money To Wallet and Recharge").setPositiveButton("close", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//
//                    CompletePaymentActivity.this.finish();
//                }
//            }).show();
//
//            rediectToPaymentGateway(CompletePaymentActivity.this,payamount);

//        }else {
//            if(PatternLockUtils.hasPattern(CompletePaymentActivity.this)){
//                PatternLockUtils.confirmPattern(CompletePaymentActivity.this);
//            }
//            rediectToPaymentGateway(CompletePaymentActivity.this,payamount);
//        }
    }

    @OnClick(R.id.addamount)
    void AddMoney() {
//        AppHelper.LaunchActivity(CompletePaymentActivity.this, AddMoneyActivity.class);
    }

    private void submitToServer(String finalurl,final String message) {
                AppHelper.showDialog(CompletePaymentActivity.this,"Recharge in Process Please wait",false);

        Log.e("response   ",finalurl);
        StringRequest request = new StringRequest(Request.Method.GET, finalurl, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                AppHelper.hideDialog();
                AppHelper.logout(CompletePaymentActivity.this,response);
                try {

                    Log.e("response",response);
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if(status.equals("ok")){
//                       AppHelper.Snackbar(CompletePaymentActivity.this,clLayout,message,AppConstants.MESSAGE_COLOR_SUCCESS,AppConstants.TEXT_COLOR);
//                       new Handler().postDelayed(new Runnable() {
//                           @Override
//                           public void run() {
//                               Intent intent = new Intent(getApplicationContext(), CompleteInfoActivity.class);
//                               intent.putExtra("response",response);
//                               startActivity(intent);
//                               CompletePaymentActivity.this.finish();
//                           }
//                       },500);

                  JSONObject dataObject=jsonObject.getJSONObject("data");
//                txtResponse.setText(dataObject.getString("ordernumber"));
//                txtResponse.setTextColor(Color.RED);
                            if (dataObject.getString("paymentamount").equals("0")){
                                if (walletValue==1){
                                    generatedOrderNumber=dataObject.getString("ordernumber");
                                    rechargeCorrect(dataObject.getString("ordernumber"));
                                }
//                                if (walletValue==0){
//                                    callEbsKit(dataObject.getString("ordernumber"),payamount);
//                                }
                            }
//                        if (!dataObject.getString("paymentamount").equals("0")){
//                            if (walletValue==1){
//                                generatedOrderNumber=dataObject.getString("ordernumber");
//                                payamount=    dataObject.getString("paymentamount");
//                            }

//                                if (walletValue==0){
//                                    callEbsKit(dataObject.getString("ordernumber"),payamount);
//                                }
//                        }

//                            else{
//                                if (walletValue==1){
//                                    rechargeCorrect(dataObject.getString("ordernumber"));
//                                }
//                                if (walletValue==0){
//                                   callEbsKit(dataObject.getString("ordernumber"),payamount);
//                                }
//                            }
                    }

                    /*if (status.equals("ko")){
                        new AlertDialog.Builder(CompletePaymentActivity.this)
                                .setTitle("Recharge UnSuccessfull")
                                .setMessage("Error Occured")
                                .setPositiveButton("Try Again ", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();
                    }*/
                }catch (JSONException ignored){

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        if(Integer.parseInt(payamount)>mUser.getWalletAmount()&&chckWallet.isChecked()){
            AppHelper.hideDialog();
            new AlertDialog.Builder(CompletePaymentActivity.this)
                    .setTitle("Recharge UnSuccessfull")
                    .setMessage("You have InSufficient Balance ")
                    .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }

//        if (Integer.parseInt(payamount)<=mUser.getWalletAmount()){
            MixCartApplication.getInstance().addToRequestQueue(request);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        walletBalance = mUser.getWalletAmount();
    }


    private void rechargeCorrect(final String ordernumber) {
        String finalurl = AppConstants.RECHARGEAPI+mUser.getUserId()+"&ordernumber="+ordernumber;
        Log.e("response",finalurl);

       class MyTask extends AsyncTask<String,Void,String>{

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                AppHelper.showDialog(CompletePaymentActivity.this," Your OrderNumber is: "+ordernumber+" is Connecting \n Loading Please Wait...",false);
            }

            @Override
            protected String doInBackground(String[] params) {
                String data = "";
                InputStream iStream = null;
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL(params[0]);

                    // Creating an http connection to communicate with url
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoOutput(true);

                    // Connecting to url
                    urlConnection.connect();

                    // Reading data from url
                    iStream = urlConnection.getInputStream();

                    BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                    StringBuffer sb = new StringBuffer();

                    String line = "";
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }

                    data = sb.toString();
                    Log.d("downloadUrl", data.toString());
                    br.close();

                } catch (Exception e) {
                    Log.d("Exception", e.toString());
                } finally {
                    try {
                        iStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    urlConnection.disconnect();
                }
                return data;
            }


            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                AppHelper.logout(CompletePaymentActivity.this,response);
                try {
                    Log.e("response",response);
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    AppHelper.hideDialog();

//                    if (jsonObject.getString("message").equals(null)){
                        new AlertDialog.Builder(CompletePaymentActivity.this)
                                .setMessage(jsonObject.getString("message"))
                                .setCancelable(false)
                                .create().show();
//                    }
//                    if (jsonObject.getString("message").equals("pending")){
//                        new AlertDialog.Builder(CompletePaymentActivity.this)
//                                .setMessage("Pending ")
//                                .create().show();
//
//                    }
//                    if (jsonObject.getString("message").equals("success")){
//                        new AlertDialog.Builder(CompletePaymentActivity.this)
//                                .setMessage("Recharge Success")
//                                .create().show();
//
//                    }
//                    else {
//                        new AlertDialog.Builder(CompletePaymentActivity.this)
//                                .setMessage(jsonObject.getString("message"))
//                                .create().show();
//                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            AppHelper.LaunchActivity(CompletePaymentActivity.this, PassBookActivity.class);
                            finish();
                        }
                    }, 2000);

//                    gotoDetailedString();
                    if(status.equals("ok")){

                        if (jsonObject.getString("message").equals("null")){
                            Log.e("response","contains null");

//                            new AlertDialog.Builder(CompletePaymentActivity.this)
//                                    .setTitle("Recharge Failed")
//                                    .setMessage(jsonObject.getString("message"))
//                                    .setPositiveButton("Try Again ", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            dialog.dismiss();
//                                        Intent i = new Intent(CompletePaymentActivity.this, PassBookActivity.class);
//                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        startActivity(i);
//                                        finish();
//                                        }
//                                    }).create().show();
                        }

//                        new AlertDialog.Builder(CompletePaymentActivity.this)
//                                .setMessage(jsonObject.getString("message"))
//                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                    }
//                                }).create().show();
                    }if (status.equals("ko")){
                        AppHelper.hideDialog();
                        new AlertDialog.Builder(CompletePaymentActivity.this)
                                .setTitle("Recharge Failed")
                                .setMessage(jsonObject.getString("message"))
                                .setPositiveButton("Try Again ", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
//                                        Intent i = new Intent(CompletePaymentActivity.this, MainActivity.class);
//                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        startActivity(i);
//                                        finish();
                                    }
                                }).create().show();
                    }
                }catch (Exception ignored) {

                }
            }
        }

        MyTask myTask = new MyTask();
        myTask.execute(finalurl);
    }


    @Override
    public void onConfirmPatternResult(boolean successful) {
        if(successful){
            int walletValue=0;
            if (chckWallet.isChecked()){
                walletValue=1;
            }
            if (!chckWallet.isChecked()){
                walletValue=0;
            }

            String finalurl;
//            switch (type) {
//                case "mobile":
                    finalurl = AppConstants.RECHARGE_URL+"&userid="+mUser.getUserId()+"&operatortype="+operatortype+"&op_id=" + operator + "&mobile=" + number + "&amount=" + String.valueOf(payamount)+"&wallet="+walletValue;
                    submitToServer(finalurl, "Recharge Successfull");
//                    break;
//                case "dth":
//                    finalurl = AppConstants.DTH_RECHARGE_URL+"&userid="+mUser.getUserId()+ "&operatortype="+operatortype+"&op_id=" + operator + "&cid=" + number + "&amount=" + payamount+"&wallet="+walletValue;
//                    submitToServer(finalurl, "DTH Payment Successfull");
//                    break;
//                case "dc":
//                    finalurl = AppConstants.DC_RECHARGE_URL +"&userid="+mUser.getUserId()+"&operatortype="+operatortype+"&amount=" + payamount + "&cardno=" + number + "&op_id=" + operator+"&wallet="+walletValue;
//                    submitToServer(finalurl, "DataCard Payment Successfull");
//                    break;
//                case "shopping":
//                    finalurl = "http://sastabankproperty.in.md-38.webhostbox.net/recharge/index.php?option=com_jbackend&view=request&action=get&module=user&resource=ordersgenarate&userid="+mUser.getUserId()+"&prodid="+number;
//                    submitToServer(finalurl, "Order Placed Successfully");
//                    break;
//            }
        }
    }


    private void callAtomKit(String orderId, String finalmoney,String cardType,String cardTypeName) {
        /**
         * Set Parameters Before Initializing the ATOM Gateway, All mandatory
         * values must be provided
         */
        paymentMode=cardType;
        generatedOrderNumber=orderId;

        newPayIntent = new Intent(CompletePaymentActivity.this, PayActivity.class);
        /** Payment Amount Details */
        // Total Amount
        newPayIntent.putExtra("merchantId",res.getString(R.string.userid));
        newPayIntent.putExtra("txnscamt", "0"); //Fixed. Must be 0
        newPayIntent.putExtra("loginid",  res.getString(R.string.userid));
        newPayIntent.putExtra("password", res.getString(R.string.merchantpassword));
        newPayIntent.putExtra("prodid",  res.getString(R.string.productid));
        newPayIntent.putExtra("txncurr", "INR"); //Fixed. Must be INR
        newPayIntent.putExtra("clientcode",orderId);
        newPayIntent.putExtra("custacc", res.getString(R.string.accno));
        newPayIntent.putExtra("channelid", "INT");
        newPayIntent.putExtra("amt", finalmoney);//Should be 3 decimal number i.e 100.000
        newPayIntent.putExtra("txnid",orderId);
        newPayIntent.putExtra("date",new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.ENGLISH).format(new Date()));//"30/12/2015 18:31:00");//Should be in same format
        newPayIntent.putExtra("cardtype", cardType);//strPaymentMode);// CC or DC ONLY (value should be same as commented)
        newPayIntent.putExtra("cardAssociate", cardTypeName);//strCardType);// for VISA and MASTER. MAESTRO ONLY (value should be same as commented)
        newPayIntent.putExtra("surcharge", "NO");// Should be passed YES if surcharge is applicable else pass NO
        //use below Production url only with Production "Library-MobilePaymentSDK", Located inside PROD folder
        newPayIntent.putExtra("ru","https://payment.atomtech.in/mobilesdk/param"); //ru FOR Production
        //use below UAT url only with UAT "Library-MobilePaymentSDK", Located inside UAT folder
        //newPayIntent.putExtra("ru", "https://paynetzuat.atomtech.in/mobilesdk/param"); // FOR UAT (Testing)

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
    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();

        if (id==android.R.id.home){
            super.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.trx_debitcard)
    void debitcardPay(){

        final String[] list={"MASTER","VISA","MAESTRO"};
        new AlertDialog.Builder(CompletePaymentActivity.this)
                .setTitle("select Card Type")
                .setCancelable(true)
                .setItems(list, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String finalurl;
                        switch (type) {
                            case "mobile":
                                finalurl = AppConstants.RECHARGE_URL+"&userid="+mUser.getUserId()+"&operatortype="+operatortype+"&op_id=" + operator + "&mobile=" + number + "&amount=" + String.valueOf(payamount)+"&wallet="+walletValue;
                                getNoWalletOrderID(finalurl,payamount,"DC",list[which]);
                                break;
                            case "dth":
                                finalurl = AppConstants.DTH_RECHARGE_URL+"&userid="+mUser.getUserId()+ "&operatortype="+operatortype+"&op_id=" + operator + "&mobile=" + number + "&amount=" + payamount+"&wallet="+walletValue;
                                getNoWalletOrderID(finalurl,payamount,"DC",list[which]);
                                break;
                            case "dc":
                                finalurl = AppConstants.DC_RECHARGE_URL +"&userid="+mUser.getUserId()+"&operatortype="+operatortype+"&amount=" + payamount + "&mobile=" + number + "&op_id=" + operator+"&wallet="+walletValue;
                                getNoWalletOrderID(finalurl, payamount,"DC",list[which]);
                                break;
                            case "electricity":
                                finalurl = AppConstants.GENERATE_ORDER+"&userid="+mUser.getUserId()+"&operatortype="+operatortype+ "&op_id=" + operator+"&amount=" + payamount + "&mobile=" + number +"&wallet="+walletValue;;
                                getNoWalletOrderID(finalurl,payamount,"DC","Electricity Bill Payment SuccessFull");
                                break;
                            case "shopping":
                                finalurl = "http://sastabankproperty.in.md-38.webhostbox.net/recharge/index.php?option=com_jbackend&view=request&action=get&module=user&resource=ordersgenarate&userid="+mUser.getUserId()+"&prodid="+number;
                                getNoWalletOrderID(finalurl,payamount,"DC",list[which]);
                                break;
                        }
                        dialog.dismiss();
                    }
                }).create().show();
    }

    @OnClick(R.id.trx_creditpay)
    void creditcardPay(){
        final String[] list={"MASTER","VISA","MAESTRO"};
       new AlertDialog.Builder(CompletePaymentActivity.this)
               .setTitle("select Card Type")
               .setCancelable(true)
               .setItems(list, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       String finalurl;
                       switch (type) {
                           case "mobile":
                               finalurl = AppConstants.RECHARGE_URL+"&userid="+mUser.getUserId()+"&operatortype="+operatortype+"&op_id=" + operator + "&mobile=" + number + "&amount=" + String.valueOf(payamount)+"&wallet="+walletValue;
                               getNoWalletOrderID(finalurl,payamount,"CC",list[which] );
                               break;
                           case "dth":
                               finalurl = AppConstants.DTH_RECHARGE_URL+"&userid="+mUser.getUserId()+ "&operatortype="+operatortype+"&op_id=" + operator + "&mobile=" + number + "&amount=" + payamount+"&wallet="+walletValue;
                               getNoWalletOrderID(finalurl,payamount,"CC",list[which]);
                               break;
                           case "dc":
                               finalurl = AppConstants.DC_RECHARGE_URL +"&userid="+mUser.getUserId()+"&operatortype="+operatortype+"&amount=" + payamount + "&mobile=" + number + "&op_id=" + operator+"&wallet="+walletValue;
                               getNoWalletOrderID(finalurl, payamount,"CC",list[which]);
                               break;
                           case "electricity":
                               finalurl = AppConstants.GENERATE_ORDER+"&userid="+mUser.getUserId()+"&operatortype="+operatortype+ "&op_id=" + operator+"&amount=" + payamount + "&mobile=" + number +"&wallet="+walletValue;;
                               getNoWalletOrderID(finalurl,payamount,"CC","Electricity Bill Payment SuccessFull");
                               break;
                           case "shopping":
                               finalurl = "http://sastabankproperty.in.md-38.webhostbox.net/recharge/index.php?option=com_jbackend&view=request&action=get&module=user&resource=ordersgenarate&userid="+mUser.getUserId()+"&prodid="+number;
                               getNoWalletOrderID(finalurl,payamount,"CC",list[which]);
                               break;
                       }
                       dialog.dismiss();
                   }
               }).create().show();
    }

    @OnClick(R.id.trx_netbanking)
    void doNetBanking(){
        String finalurl = "";
        switch (type) {
            case "mobile":
                finalurl = AppConstants.RECHARGE_URL+"&userid="+mUser.getUserId()+"&operatortype="+operatortype+"&op_id=" + operator + "&mobile=" + number + "&amount=" + String.valueOf(payamount)+"&wallet="+walletValue;
                break;
            case "dth":
                finalurl = AppConstants.DTH_RECHARGE_URL+"&userid="+mUser.getUserId()+ "&operatortype="+operatortype+"&op_id=" + operator + "&mobile=" + number + "&amount=" + payamount+"&wallet="+walletValue;
                break;
            case "dc":
                finalurl = AppConstants.DC_RECHARGE_URL +"&userid="+mUser.getUserId()+"&operatortype="+operatortype+"&amount=" + payamount + "&mobile=" + number + "&op_id=" + operator+"&wallet="+walletValue;
                break;
            case "electricity":
                finalurl = AppConstants.GENERATE_ORDER+"&userid="+mUser.getUserId()+"&operatortype="+operatortype+ "&op_id=" + operator+"&amount=" + payamount + "&mobile=" + number +"&wallet="+walletValue;;
                break;
            case "shopping":
                finalurl = "http://sastabankproperty.in.md-38.webhostbox.net/recharge/index.php?option=com_jbackend&view=request&action=get&module=user&resource=ordersgenarate&userid="+mUser.getUserId()+"&prodid="+number;
                break;
        }

        Log.e("response   ",finalurl);
        AppHelper.showDialog(CompletePaymentActivity.this,"Loading Your Details Please Wait...");
        StringRequest request = new StringRequest(Request.Method.GET, finalurl, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {

                try {
                    AppHelper.logout(CompletePaymentActivity.this,response);
                    Log.e("response",response);
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if(status.equals("ok")){
                        final JSONObject dataObject=jsonObject.getJSONObject("data");
                        if (dataObject.getString("paymentamount").equals("0")) {
                            rechargeCorrect(dataObject.getString("ordernumber"));
                        }
                        if (!dataObject.getString("paymentamount").equals("0")){
                            //paymentamount,ordernumber

                            paymentMode="NB";
                            generatedOrderNumber=dataObject.getString("ordernumber");

                            // User ID, Password and Product ID m
                            newPayIntent = new Intent(CompletePaymentActivity.this, PayActivity.class);
                            newPayIntent.putExtra("merchantId", res.getString(R.string.userid));
                            newPayIntent.putExtra("txnscamt", "0"); //Fixed. Must be 0
                            newPayIntent.putExtra("loginid", res.getString(R.string.userid));
                            newPayIntent.putExtra("password", res.getString(R.string.merchantpassword));
                            newPayIntent.putExtra("prodid", res.getString(R.string.productid));
                            newPayIntent.putExtra("txncurr", "INR"); //Fixed. Must be ?INR?
                            newPayIntent.putExtra("clientcode", dataObject.getString("ordernumber"));
                            newPayIntent.putExtra("custacc", res.getString(R.string.accno));
                            newPayIntent.putExtra("amt",dataObject.getString("paymentamount"));//Should be 3 decimal number i.e 51.000
                            newPayIntent.putExtra("txnid",dataObject.getString("ordernumber"));
                            newPayIntent.putExtra("date", new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.ENGLISH).format(new Date()));//Should be in same format

                            //        newPayIntent.putExtra("bankid", “2001”); //Should be valid bank id // Optional
                            newPayIntent.putExtra("discriminator", "NB"); // NB or IMPS or All ONLY (value should be same as commented)
                            //use below Production url only with Production "Library-MobilePaymentSDK", Located inside PROD folder
                            newPayIntent.putExtra("ru","https://payment.atomtech.in/mobilesdk/param"); //ru FOR Production
                            //use below UAT url only with UAT "Library-MobilePaymentSDK", Located inside UAT folder
                            //newPayIntent.putExtra("ru", "https://paynetzuat.atomtech.in/mobilesdk/param"); // FOR UAT (Testing)

                            //Optinal Parameters
//                            newPayIntent.putExtra("customerName",mUser.getName()); //Only for Name
//                            newPayIntent.putExtra("customerEmailID",mUser.getEmail());//Only for Email ID
//                            if (!mUser.getMobileNumber().equals("empty")){
//                                newPayIntent.putExtra("customerMobileNo", mUser.getMobileNumber());//Only for Mobile Number
//                            }
//                            if (mUser.getMobileNumber().equals("empty")){
//                                newPayIntent.putExtra("customerMobileNo","9999999999");//Only for Mobile Number
//                            }
//                            newPayIntent.putExtra("billingAddress", mUser.getKeyAddress());//Only for Address
//        newPayIntent.putExtra("optionalUdf9", "OPTIONAL DATA 1");// Can pass any data
//        newPayIntent.putExtra("mprod", mprod); // Pass data in XML format, only for Multi product
                            startActivityForResult(newPayIntent, 1);
                            AppHelper.hideDialog();
                        }
                    }

                    if (status.equals("ko")){
                        new AlertDialog.Builder(CompletePaymentActivity.this)
                                .setTitle("Recharge UnSuccessfull")
                                .setMessage("Error Occured")
                                .setPositiveButton("Try Again ", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                }).create().show();
                    }
                }catch (JSONException ignored){

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        MixCartApplication.getInstance().addToRequestQueue(request);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
        {
            if (data != null)
            {
                final String message = data.getStringExtra("status");
                String[] resKey = data.getStringArrayExtra("responseKeyArray");
                final String[] resValue = data.getStringArrayExtra("responseValueArray");
//                if(resKey!=null && resValue!=null)
//                {
                    final JSONObject jsonObject=new JSONObject();
                    for(int i=0; i<resKey.length; i++){
                        System.out.println(" "+i+" resKey : "+resKey[i]+" resValue : "+resValue[i]);
                        try {
                            jsonObject.put(resKey[i], resValue[i]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                String finalAddWallet="";
                if (paymentMode.equals("DC")||paymentMode.equals("CC")){
                    finalAddWallet = AppConstants.SERVER_URL+"index.php?option=com_jbackend&view=request&action=get&module=user&resource=atompayment" +
                            "&userid="+mUser.getUserId()+"&ordernumber="+generatedOrderNumber+"&Amount=" +amount+
                            "&transationid="+jsonObject.optString("bank_txn")+"&ResponseCode="+message.replaceAll(" ","")+"&ResponseMessage="+jsonObject.toString();
                }

                if (paymentMode.equals("NB")||paymentMode.equals("IMPS")){
                    finalAddWallet = AppConstants.SERVER_URL+"index.php?option=com_jbackend&view=request&action=get&module=user&resource=atompayment" +
                            "&userid="+mUser.getUserId()+"&ordernumber="+generatedOrderNumber+"&Amount="+amount+
                            "&transationid="+jsonObject.optString("bank_txn")+"&ResponseCode="+message.replaceAll(" ","")+"&ResponseMessage="+jsonObject.toString();
                }

                if (jsonObject.optString("f_code").equalsIgnoreCase("F")){
                        Toast.makeText(CompletePaymentActivity.this, "Transaction Failed", Toast.LENGTH_SHORT).show();
                }

                if (jsonObject.optString("f_code").equalsIgnoreCase("Ok")){
                    Toast.makeText(CompletePaymentActivity.this, "Transaction SuccessFull!", Toast.LENGTH_SHORT).show();
                }

                Log.e("response",finalAddWallet);
                AppHelper.showDialog(CompletePaymentActivity.this,"Loading Please Wait..");
                StringRequest request=new StringRequest(Request.Method.GET, finalAddWallet.replace(" ","%20"),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(final String response) {
                                Log.e("response",response);

                                AppHelper.hideDialog();
                                try {
                                    JSONObject massjsonObject = new JSONObject(response);
                                    String status = massjsonObject.getString("status");
//                                            gotoDetailedString();

                                    if(status.equals("ok")){
                                        if (jsonObject.optString("f_code").equalsIgnoreCase("Ok")){
                                            Toast.makeText(CompletePaymentActivity.this, "Transaction SuccessFull!", Toast.LENGTH_SHORT).show();
                                            rechargeCorrect(generatedOrderNumber);
                                        }
                                    }
//
//                                    if(status.equals("ko")){
//                                        new AlertDialog.Builder(CompletePaymentActivity.this)
//                                                .setMessage(jsonObject.getString("message"))
//                                                .setCancelable(false)
//                                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(DialogInterface dialog, int which) {
////                                                        rechargeCorrect(generatedOrderNumber);
////                                                        finish();
////                                                        AppHelper.LaunchActivity(CompletePaymentActivity.this, PassBookActivity.class);
//                                                    }
//                                                })
//                                                .create().show();
//                                    }

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

           Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void  getNoWalletOrderID(String finalurl, String payamount, final String cardType, final String cardSignatureName){
        // callEbsKit("2365F315",payamount,"DC",list[which]);
        switch (type) {
            case "mobile":
                AppHelper.showDialog(CompletePaymentActivity.this,"Recharge in Process Please wait",false);
                break;
            case "dth":
                AppHelper.showDialog(CompletePaymentActivity.this,"Recharge in Process Please wait",false);
                break;
            case "dc":
                AppHelper.showDialog(CompletePaymentActivity.this,"Recharge in Process Please wait",false);
                break;
            case "electricity":
                AppHelper.showDialog(CompletePaymentActivity.this,"Paying Bill in Process Please wait",false);
                break;
            case "shopping":
                AppHelper.showDialog(CompletePaymentActivity.this,"Order Placing in process..Please wait",false);
                break;
        }

        Log.e("response",finalurl);
        StringRequest request=new StringRequest(Request.Method.GET, finalurl, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                AppHelper.logout(CompletePaymentActivity.this,response);
                try {
                    Log.e("response",response);
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if(status.equals("ok")){
                        final JSONObject dataObject=jsonObject.getJSONObject("data");
                        generatedOrderNumber=dataObject.getString("ordernumber");
                        if (dataObject.getString("paymentamount").equals("0")) {
                            rechargeCorrect(dataObject.getString("ordernumber"));
                        }
                        if (!dataObject.getString("paymentamount").equals("0")){
                            //paymentamount,ordernumber
                            Log.e("resposne",response);
                            callAtomKit(dataObject.getString("ordernumber"),
                                    dataObject.getString("paymentamount"),cardType,cardSignatureName);
                            AppHelper.hideDialog();
                        }
                    }
                    if (status.equals("ko")){
                        AppHelper.hideDialog();
                        new AlertDialog.Builder(CompletePaymentActivity.this)
                                .setTitle("Recharge UnSuccessfull")
                                .setMessage("Error Occured")
                                .setPositiveButton("Try Again ", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                }).create().show();
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

    private void gotoDetailedString() {
        AppHelper.showDialog(CompletePaymentActivity.this,"Loading Please Wait...",false);
        Log.e("response", AppConstants.ORDER_DETAILS+mUser.getUserId()+"&ordernumber="+generatedOrderNumber);
        StringRequest request = new StringRequest(Request.Method.GET, AppConstants.ORDER_DETAILS+mUser.getUserId()+"&ordernumber="+generatedOrderNumber,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        AppHelper.logout(CompletePaymentActivity.this,response);
                        AppHelper.hideDialog();
                        Log.e("response",response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            if(status.equals("ok")){
                                new AlertDialog.Builder(CompletePaymentActivity.this)
                                        .setMessage(jsonObject.getJSONObject("data").getString("order_remarks"))
                                        .setCancelable(false)
                                        .create().show();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        AppHelper.LaunchActivity(CompletePaymentActivity.this, PassBookActivity.class);
                                        finish();
                                    }
                                }, 2000);
                            }
                            if(status.equals("ko")){
                                new AlertDialog.Builder(CompletePaymentActivity.this)
                                        .setMessage(jsonObject.getString("message"))
                                        .setCancelable(false)
                                        .create().show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        AppHelper.LaunchActivity(CompletePaymentActivity.this, PassBookActivity.class);
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
    }
}
