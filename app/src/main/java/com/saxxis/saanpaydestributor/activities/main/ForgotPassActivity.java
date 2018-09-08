package com.saxxis.saanpaydestributor.activities.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.app.AppConstants;
import com.saxxis.saanpaydestributor.app.MixCartApplication;
import com.saxxis.saanpaydestributor.app.UserPref;
import com.saxxis.saanpaydestributor.helpers.AppHelper;
import com.saxxis.saanpaydestributor.interfaces.SmsListener;
import com.saxxis.saanpaydestributor.receivers.IncomingSms;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgotPassActivity extends AppCompatActivity {

    @BindView(R.id.tipl_regdemailid)
    TextInputLayout txtlay;

    @BindView(R.id.emailforforgotpsd)
    TextInputEditText edt_email;

    @BindView(R.id.tipl_regdmobile)
    TextInputLayout txtlay_mobile;

    @BindView(R.id.mobileforforgotpsd)
    TextInputEditText edt_mobile;

    @BindView(R.id.forgotpasswordtoolbar)
    Toolbar mToolbar;

    @BindView(R.id.coord_forgotpassword)
    CoordinatorLayout coordPassword;

    @BindView(R.id.emailsubmit)
    Button emailSubmit;

    @BindView(R.id.forgotpassword)
    LinearLayout forgorPassword;

    @BindView(R.id.otplayout)
    LinearLayout otpLayout;

    @BindView(R.id.resetpassword)
    Button resetPassword;

    @BindView(R.id.tipl_otpnumber)
    TextInputLayout txtlayOtpNum;

    @BindView(R.id.tipl_newpassword)
    TextInputLayout txtlay_newpassword;

    @BindView(R.id.tipl_conf_newpassword)
    TextInputLayout txtlayConfnewPassword ;

    @BindView(R.id.new_otp)
    TextInputEditText edtNewOtp;

    @BindView(R.id.new_password)
    TextInputEditText edtnewPassword;

    @BindView(R.id.confirmnew_password)
    TextInputEditText edtConfNewPassword;

    UserPref userPref;
    String sendemailId="";
    SmsListener smsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        ButterKnife.bind(this);

        userPref=new UserPref(ForgotPassActivity.this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitle("Forgot Password ?");
        mToolbar.setNavigationIcon(R.drawable.arrow);
        edt_email.setText(userPref.getEmail());
        edt_mobile.setText(userPref.getMobileNumber());
        smsListener = new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                edtNewOtp.setText(messageText);
                AppHelper.hideDialog();
            }
        };
        IncomingSms.bindListener(smsListener);
    }

    @OnClick(R.id.emailsubmit)
    void emailSubmit(){

        final String email=edt_email.getText().toString();
        final String mobile=edt_mobile.getText().toString();


        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            AppHelper.Snackbar(ForgotPassActivity.this,coordPassword,"Please Enter Valid Email ID",R.color.colorRedDark,R.color.white);
            edt_email.setError("Please Enter Valid Email ID");
            return;
        }

        AppHelper.showDialog(ForgotPassActivity.this,"Loading Please Wait...");
        StringRequest request=new StringRequest(Request.Method.GET, AppConstants.FORGOTPASSWORD+email+"&mobile="+mobile,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppHelper.logout(ForgotPassActivity.this,response);
                try{
                    JSONObject jsonObject = new JSONObject(response);

                    AppHelper.hideDialog();
                    String status = jsonObject.getString("status");
                    if (status.equals("ok")){

                        sendemailId=email;
                        forgorPassword.setVisibility(View.GONE);
                        otpLayout.setVisibility(View.VISIBLE);
                        AppHelper.showDialog(ForgotPassActivity.this, "Reading OTP. Please enter the OTP in case if we fail to detect the SMS automatically");
//                        new AlertDialog.Builder(ForgotPassActivity.this)
//                                .setMessage(jsonObject.getString("For OTP Check Your Email"))
//                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                        Intent intent = new Intent(ForgotPassActivity.this, LoginActivity.class);
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        startActivity(intent);
//                                    }
//                                }).create().show();
                    }
                    if(status.equals("ko")) {
                        new AlertDialog.Builder(ForgotPassActivity.this)
                                .setMessage("Failed to send email")
                                .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
//                                        Intent intent = new Intent(ForgotPassActivity.this, LoginActivity.class);
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        startActivity(intent);
//                                        Toast.makeText(ForgotPassActivity.this,"Logout Successfull",Toast.LENGTH_SHORT).show();
                                    }
                                }).create().show();
                    }
                }catch (Exception ignored){

        }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AppHelper.Snackbar(ForgotPassActivity.this,coordPassword,"UnKnown Error occured",R.color.colorRedDark,R.color.white);
            }
        });

        MixCartApplication.getInstance().addToRequestQueue(request);
    }


    @OnClick(R.id.resetpassword)
    void resetPassword(){
//        &email=developer12saxxis@gmail.com&password=789
        String otp=edtNewOtp.getText().toString();
        String newPassword=edtnewPassword.getText().toString();
        String confnewPassword=edtConfNewPassword.getText().toString();

        if (valid(otp,newPassword,confnewPassword)){
            AppHelper.showDialog(ForgotPassActivity.this,"Loading Please Wait...");
            if(newPassword.equals(confnewPassword)){
                String url=AppConstants.RESETPASSWORD_OTP+otp+"&email="+edt_email.getText().toString()+"&password="+newPassword;

                Log.e("RESETPASSWORD_OTP",url);
                StringRequest request=new StringRequest(Request.Method.GET,url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                AppHelper.logout(ForgotPassActivity.this,response);
                                Log.e("response",response);
                                AppHelper.hideDialog();
                                try{
                                    JSONObject jsonObject = new JSONObject(response);
                                    String status = jsonObject.getString("status");
                                    if (status.equals("ok")){
                                        new AlertDialog.Builder(ForgotPassActivity.this)
                                                .setMessage(jsonObject.getString("message"))
                                                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent intent = new Intent(ForgotPassActivity.this, LoginActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                    }
                                                }).create().show();
                                    }

                                    if(status.equals("ko")) {
                                        new AlertDialog.Builder(ForgotPassActivity.this)
                                                .setMessage(jsonObject.getString("message"))
                                                .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                }).create().show();
                                    }
                                }catch (Exception ignored){

                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AppHelper.Snackbar(ForgotPassActivity.this,coordPassword,"UnKnown Error occured",R.color.colorRedDark,R.color.white);
                    }
                });

                MixCartApplication.getInstance().addToRequestQueue(request);
            }
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id==android.R.id.home){
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private boolean valid(String otp, String newPassword, String confnewPassword) {

        if (otp.isEmpty()){
            AppHelper.Snackbar(ForgotPassActivity.this,coordPassword,"Please Enter Otp ",R.color.colorchange,R.color.white);
            return false;
        }

        if (newPassword.isEmpty()){
            AppHelper.Snackbar(ForgotPassActivity.this,coordPassword,"Please Enter New Password",R.color.colorchange,R.color.white);
            return false;
        }

        if (confnewPassword.isEmpty()){
            AppHelper.Snackbar(ForgotPassActivity.this,coordPassword,"Please Enter Confirm Password",R.color.colorchange,R.color.white);
            return false;
        }

        if (!newPassword.equals(confnewPassword)){
            AppHelper.Snackbar(ForgotPassActivity.this,coordPassword,"Please Confirm Password is not Matched",R.color.colorchange,R.color.white);
            return false;
        }

        return true;
    }
}
