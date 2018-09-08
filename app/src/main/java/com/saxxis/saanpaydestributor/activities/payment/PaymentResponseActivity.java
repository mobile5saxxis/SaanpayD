package com.saxxis.saanpaydestributor.activities.payment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.app.AppConstants;
import com.saxxis.saanpaydestributor.app.MixCartApplication;
import com.saxxis.saanpaydestributor.app.UserPref;
import com.saxxis.saanpaydestributor.helpers.AppHelper;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PaymentResponseActivity extends AppCompatActivity {

    @BindView(R.id.toolbarotp)
    Toolbar toolbar;

    @BindView(R.id.txtresend)
    Button btnResend;

    @BindView(R.id.txtsubmit)
    Button btnSubmit;

    @BindView(R.id.txt_otp)
    TextInputEditText txtOtp;

    UserPref userPref;
    String beneficaryId="";
    String senderId="";
    String url_imps="";
    String otpVerified="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_response);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userPref = new UserPref(PaymentResponseActivity.this);
        toolbar.setTitle("Payment Response");
        toolbar.setNavigationIcon(R.drawable.arrow);

        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            beneficaryId = extras.getString("benefId");
            senderId = extras.getString("senderId");
            url_imps = extras.getString("moneyurl");
            otpVerified = extras.getString("otpverified");

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


    @OnClick(R.id.txtsubmit)
    void getSubmitResponse(){
        AppHelper.showDialog(PaymentResponseActivity.this,"Loading Please Wait....");
        //1064&senderid=5827&beneficiaryid=42179&otp=1248
        Log.e("response",AppConstants.SUBMIT_BANK_PAYMENT_OTP +userPref.getUserId()+
                "&senderid="+senderId+"&beneficiaryid="+beneficaryId+"&otp="+txtOtp.getText().toString());
        StringRequest request = new StringRequest(AppConstants.SUBMIT_BANK_PAYMENT_OTP +userPref.getUserId()+
                "&senderid="+senderId+"&beneficiaryid="+beneficaryId+"&otp="+txtOtp.getText().toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.logout(PaymentResponseActivity.this,response);
                        Log.e("response",response);
//                        {"result":{"status":0,"beneficiaryid":"42390","message":"Transaction Successful"},"status":"ko"}
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status").equals("ok")){
                                   setAllImpsTransfer(jsonObject.getJSONObject("result").getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MixCartApplication.getInstance().addToRequestQueue(request);
    }

    private void setAllImpsTransfer(final String message) {
        StringRequest request = new StringRequest(url_imps+"&otpverified="+otpVerified,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.hideDialog();
                        Log.e("response",response);
                        new AlertDialog.Builder(PaymentResponseActivity.this)
                                .setMessage(message)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                        AppHelper.LaunchActivity(PaymentResponseActivity.this,PassBookActivity.class);
                                    }
                                }).create().show();

                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MixCartApplication.getInstance().addToRequestQueue(request);
    }


    @OnClick(R.id.txtresend)
    void getOtpByResend(){

        Log.e("response",AppConstants.RESEND_OTP+userPref.getUserId()+
                "&senderid="+senderId+"&beneficiaryid="+beneficaryId);

        StringRequest request = new StringRequest(AppConstants.RESEND_OTP+userPref.getUserId()+
                "&senderid="+senderId+"&beneficiaryid="+senderId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.logout(PaymentResponseActivity.this,response);
                        AppHelper.hideDialog();
                        Log.e("response",response);
                        Toast.makeText(PaymentResponseActivity.this, "OTP Sent to your Mobile", Toast.LENGTH_SHORT).show();
                    }
        },new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });

        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MixCartApplication.getInstance().addToRequestQueue(request);
    }

}
