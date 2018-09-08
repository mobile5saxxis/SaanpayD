package com.saxxis.saanpaydestributor.activities.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.activities.PrivacyPolicyActivity;
import com.saxxis.saanpaydestributor.activities.TermsConditionsActivity;
import com.saxxis.saanpaydestributor.app.AppConstants;
import com.saxxis.saanpaydestributor.app.MixCartApplication;
import com.saxxis.saanpaydestributor.app.UserPref;
import com.saxxis.saanpaydestributor.helpers.AppHelper;
import com.saxxis.saanpaydestributor.interfaces.NetworkListener;
import com.saxxis.saanpaydestributor.interfaces.SmsListener;
import com.saxxis.saanpaydestributor.receivers.IncomingSms;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity implements NetworkListener {
    @BindView(R.id.regsterd_mbno)
    TextInputEditText userRegNum;

    @BindView(R.id.password)
    TextInputEditText userpassword;

    @BindView(R.id.terms_conditions)
    CheckBox termsCondions;

    @BindView(R.id.forget_pswd)
    TextView forgotpasswordButton;

    @BindView(R.id.login)
    TextView loginBtn;

    @BindView(R.id.cl_login)
    CoordinatorLayout clLogin;

    @BindView(R.id.l_terms_condtns)
    TextView termsCondtions;
    @BindView(R.id.l_privacy_plcy)
    TextView privacyPolicy;
    @BindView(R.id.inputOtp)
    EditText etOTP;
    @BindView(R.id.progrs_vrfy)
    ProgressBar progrs_vrfy;
    @BindView(R.id.otp_layout)
    LinearLayout otp_layout;
    @BindView(R.id.login_layout)
    LinearLayout login_layout;
    @BindView(R.id.btn_verify_otp)
    Button btn_verify_otp;
    private UserPref mUser;
    private String finishActivity = "nofinish";

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mUser = new UserPref(LoginActivity.this);
        mUser.logoutUser();
        MixCartApplication.getInstance().setupActionbar(this, "Login");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            finishActivity = getIntent().getExtras().getString("finish");
        }

        String text1 = "<font color=#607D8B></font>" +
                " <font color=#0ad7af>Terms & Conditions</font> ";

        String text2 = "<font color=#607D8B>and</font> " +
                "<font color=#0ad7af>Privacy Policy</font>";

        termsCondtions.setText(Html.fromHtml(text1), TextView.BufferType.SPANNABLE);
        privacyPolicy.setText(Html.fromHtml(text2), TextView.BufferType.SPANNABLE);

        IncomingSms.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                etOTP.setText(messageText);
                progrs_vrfy.setVisibility(View.GONE);
                btn_verify_otp.performClick();
            }
        });

    }


    @OnClick(R.id.l_terms_condtns)
    void trmscon() {
        AppHelper.LaunchActivity(LoginActivity.this, TermsConditionsActivity.class);
    }

    @OnClick(R.id.l_privacy_plcy)
    void prcypolicy() {
        AppHelper.LaunchActivity(LoginActivity.this, PrivacyPolicyActivity.class);
    }

    /*@OnClick(R.id.create_act)
    void RedirectToSignUp() {
        AppHelper.LaunchActivity(LoginActivity.this, SignupActivity.class);
    }*/

    @OnClick(R.id.forget_pswd)
    void forgotPassword() {
        AppHelper.LaunchActivity(LoginActivity.this, ForgotPassActivity.class);
    }

    @OnClick(R.id.login)
    void userLogin() {

        if (termsCondions.isChecked()) {
            AppHelper.hideKeyboard(this);

            String email = userRegNum.getText().toString().trim();
            String password = userpassword.getText().toString().trim();

            if (!validate(password, email)) {
                String url = null;
                try {
                    url = AppConstants.LOGIN_URL
                            + "&username=" + URLEncoder.encode(email, "utf-8")
                            + "&password=" + URLEncoder.encode(password, "utf-8");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                Log.e("response", url);
                AppHelper.showDialog(this, "Please Relax..while we set up your account...");
                StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        handleresponse(response);
                        AppHelper.hideDialog();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AppHelper.hideDialog();
                        AppHelper.Snackbar(LoginActivity.this, clLogin,
                                getString(R.string.connection_is_not_available),
                                AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);

                    }
                });
//            {
//                @Override
//                protected Response<String> parseNetworkResponse(NetworkResponse response) {
//                    try {
//                        JSONObject jsonObject = JsonUtils.mapToJson(response.headers);
//                        String response1 = jsonObject.getString("Set-Cookie");
//                        String[] parts = response1.split(";");
//                        mUser.setSessionId(parts[0]);
//                    }catch (Exception ignored){}
//                    return super.parseNetworkResponse(response);
//                }
//            };

                MixCartApplication.getInstance().addToRequestQueue(request);
            }
        } else {
            Toast.makeText(LoginActivity.this, "Please Agree the Terms & Conditions", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validate(String password, String email) {
//        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
        if (email.isEmpty()) {
            AppHelper.Snackbar(LoginActivity.this, clLogin, "Please Enter Valid EmailId/UserName", AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
            return true;
        } else if (password.isEmpty()) {
            AppHelper.Snackbar(LoginActivity.this, clLogin, "Please Enter Password", AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
            return true;
        }
        return false;
    }

    private void handleresponse(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            System.out.print(response);
            Log.e("response", response);
            String status = jsonObject.getString("status");
            if (status.equals("ok")) {
                login_layout.setVisibility(View.GONE);
                otp_layout.setVisibility(View.VISIBLE);
                //AppHelper.Snackbar(LoginActivity.this,clLogin,"Login Success", AppConstants.MESSAGE_COLOR_SUCCESS,AppConstants.TEXT_COLOR);
                /*String userid = jsonObject.getString("userid");
                String name = jsonObject.getString("name");
                String ReferCode = jsonObject.getString("Refercode");
                String email = jsonObject.getString("email");
                String mobile = jsonObject.getString("mobile");
                String uniqueid = jsonObject.getString("uniquecode");
                String userType =jsonObject.getString("usertype");*/
                String session_id = jsonObject.getString("session_id");

                /*mUser.setMobileNumber(mobile);
                mUser.setReferralId(ReferCode);
                mUser.setUniqueId(uniqueid);
                mUser.setName(name);
                mUser.setUserDetails(userid);
                mUser.setKeyUserType(userType);
                mUser.setLoggedIn();*/
                mUser.setSessionId(session_id);

                /*if (finishActivity.equals("finish")){
                    finish();
                }else {
                    AppHelper.LaunchActivity(LoginActivity.this, MainActivity.class);
                }

                finish();*/
            } else {
                //message
                new AlertDialog.Builder(LoginActivity.this)
                        .setMessage("Login Failed Please try Again")
                        .setCancelable(false)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
//                AppHelper.Snackbar(LoginActivity.this,clLogin,"Login Failed Please try Again", AppConstants.MESSAGE_COLOR_ERROR,AppConstants.TEXT_COLOR);
            }
        } catch (Exception ignored) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MixCartApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnecting, boolean isConnected) {
        if (!isConnecting && !isConnected) {
            AppHelper.Snackbar(this, clLogin, getString(R.string.connection_is_not_available), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
        } else if (isConnecting && isConnected) {
            AppHelper.Snackbar(this, clLogin, getString(R.string.connection_is_available), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
        } else {
            AppHelper.Snackbar(this, clLogin, getString(R.string.waiting_for_network), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);
        }
    }

    @OnClick(R.id.btn_verify_otp)
    void setupOTP() {
        String otp = etOTP.getText().toString().trim();
        if (otp.isEmpty()) {
            AppHelper.Snackbar(LoginActivity.this, clLogin, "Please Enter OTP", AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
        } else {
            /*Intent hhtpIntent = new Intent(LoginActivity.this, HttpService.class);
            hhtpIntent.putExtra("otp", otp);
            hhtpIntent.putExtra("sessionid",mUser.getSessionId());
            startService(hhtpIntent);*/
            verifyOtpLogin(mUser.getSessionId(), otp);
        }

    }

    private void verifyOtpLogin(String sessionid, String otp) {

        String url = AppConstants.SUBMIT_OTP_LOGIN_URL + "&sessionid=" + sessionid + "&otp=" + otp;
        System.out.println(url);
        StringRequest str = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                AppHelper.logout(LoginActivity.this, response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equals("ok")) {
                        //AppHelper.Snackbar(LoginActivity.this,clLogin,"Login Success", AppConstants.MESSAGE_COLOR_SUCCESS,AppConstants.TEXT_COLOR);
                        mUser.setLoggedIn();

                        //String username = jsonObject.getString("username");
                        String userType = jsonObject.getString("usertype");
                        if (!userType.equals("D")) {
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setTitle("Login Failed")
                                    .setMessage("Please login with your distributor details")
                                    .setCancelable(false)
                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).create().show();
                            return;
                        }
                        String userid = jsonObject.getString("userid");
                        String name = jsonObject.getString("name");
                        String ReferCode = jsonObject.getString("Refercode");
                        String email = jsonObject.getString("email");
                        String mobile = jsonObject.getString("mobile");
                        String uniqueid = jsonObject.getString("uniquecode");

                        mUser.setMobileNumber(mobile);
                        mUser.setReferralId(ReferCode);
                        mUser.setUniqueId(uniqueid);
                        mUser.setName(name);
                        mUser.setUserDetails(userid);
                        mUser.setKeyUserType(userType);
                        mUser.setEmail(email);

                        Toast.makeText(LoginActivity.this, "Login Successfull", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                } catch (Exception e) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MixCartApplication.getInstance().addToRequestQueue(str);


    }

}
