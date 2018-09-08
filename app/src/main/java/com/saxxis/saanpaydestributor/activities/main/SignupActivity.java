package com.saxxis.saanpaydestributor.activities.main;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
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
import com.saxxis.saanpaydestributor.utils.MyViewPager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignupActivity extends AppCompatActivity implements NetworkListener {


    private static final String TAG = SignupActivity.class.getSimpleName() ;
    @BindView(R.id.tipl_signup_name)
    TextInputLayout txtSignUpName;

    @BindView(R.id.tipl_signup_phoneno)
    TextInputLayout txtSignUpPhone;

    @BindView(R.id.tipl_signup_email)
    TextInputLayout txtSignUpEmail;

    @BindView(R.id.tipl_signup_newpasswrd)
    TextInputLayout txtSignUpNewPassword;

    @BindView(R.id.tipl_signup_confpass)
    TextInputLayout txtConfirmPAssword;




    @BindView(R.id.signup_et_name)
    TextInputEditText etName;
    @BindView(R.id.signup_et_email)
    TextInputEditText etEmail;
    @BindView(R.id.signup_et_mobile)
    TextInputEditText etMobile;
    @BindView(R.id.signup_et_password)
    TextInputEditText etPassword;
    @BindView(R.id.signup_et_conf_password)
    TextInputEditText etConfPassword;

    @BindView(R.id.signup_et_rcode)
    EditText etRefferal;


    @BindView(R.id.alreadyhaveacc)
    TextView alreadyhaveacc;

    @BindView(R.id.terms_conditions_reg)
    CheckBox regtermaandconds;

    @BindView(R.id.inputOtp)
    EditText etOTP;


    @BindView(R.id.signup_chck_terms)
    CheckBox chckTerms;
    @BindView(R.id.signup_btn_verify)
    TextView verify;

    @BindView(R.id.cl_signup)
    CoordinatorLayout clSignUp;

    @BindView(R.id.terms_condtns)
    TextView termsCondtions;
    @BindView(R.id.privacy_plcy)
    TextView privacyPolicy;

    @BindView(R.id.viewPagerVertical)
    MyViewPager viewPager;

    @BindView(R.id.signup_layout)
    LinearLayout signup_layout;

    @BindView(R.id.otp_layout)
    LinearLayout otp_layout;

    @BindView(R.id.progrs_vrfy)
    ProgressBar progrs_vrfy;



    private UserPref mUser;

    private static final int PERMISSION_REQUEST_ID = 100;
    IncomingSms IncomingSms;
    private final String  BROADCAST_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private IntentFilter intentFilter;
    SmsListener smsListener;
    Boolean isPermissionGranted = false;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        IncomingSms = new IncomingSms();
        intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_ACTION);
        requestRuntimePermissions(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS);
        ButterKnife.bind(this);
        mUser = new UserPref(this);
        setupActionbar();

        getSupportActionBar().setTitle("Sign Up");
        String text1 = "<font color=#607D8B></font>" +
                " <font color=#0ad7af>Terms & Conditions</font> ";

        String text2 = "<font color=#607D8B>and</font> " +
                "<font color=#0ad7af>Privacy Policy</font>";

        termsCondtions.setText(Html.fromHtml(text1), TextView.BufferType.SPANNABLE);
        privacyPolicy.setText(Html.fromHtml(text2), TextView.BufferType.SPANNABLE);

//        http://www.saxxishrsolution.com.md-33.webhostbox.net/saanpay/index.php?option=com_jbackend&view=request&module=user&action=post&resource=register&name=vanamali&email=developer1222%40gmail.com&password=123456&mobile=9989541608

        viewPager.setAdapter(new ViewPagerAdapter());
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        /*IncomingSms.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                if(isPermissionGranted){
                    etOTP.setText(messageText);
                    progrs_vrfy.setVisibility(View.GONE);
                }
            }
        });*/
        smsListener = new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                if(isPermissionGranted){
                    etOTP.setText(messageText);
                    progrs_vrfy.setVisibility(View.GONE);
                    setupOTP();
                }
            }
        };
        IncomingSms.bindListener(smsListener);
    }

    private void setupActionbar() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setCustomView(R.layout.ab_title_layout);
        View view =getSupportActionBar().getCustomView();
        TextView textviewTitle = (TextView) view.findViewById(R.id.mytext);
        textviewTitle.setText("Create Account");
        ImageView backarr=(ImageView)view.findViewById(R.id.backarr);

        backarr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignupActivity.super.onBackPressed();
            }
        });
    }

    @OnClick(R.id.alreadyhaveacc)
    void alreadyHaveAcc(){
        Intent intent=new Intent(SignupActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    private void requestforOTp(String s) {
        AppHelper.showDialog(SignupActivity.this,"Fetching OTP...",false);
        StringRequest str = new StringRequest(AppConstants.FETCH_OTP_URL+s, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppHelper.logout(SignupActivity.this,response);
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if(status.equals("ok")){
                       AppHelper.hideDialog();
                    }
                }catch (Exception ignored){

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AppHelper.hideDialog();
            }
        });
        MixCartApplication.getInstance().addToRequestQueue(str);
    }

    @OnClick(R.id.btn_verify_otp)
    void setupOTP(){
        String otp = etOTP.getText().toString().trim();
        if (otp.isEmpty()){
            AppHelper.Snackbar(SignupActivity.this,clSignUp,"Please Enter OTP", AppConstants.MESSAGE_COLOR_ERROR,AppConstants.TEXT_COLOR);
        }else{
            /*Intent hhtpIntent = new Intent(SignupActivity.this, HttpService.class);
            hhtpIntent.putExtra("otp", otp);
            hhtpIntent.putExtra("mobilenumber",mUser.getMobileNumber());
            startService(hhtpIntent);*/
            verifyOtp(mUser.getMobileNumber(),otp);
        }

    }

    @OnClick(R.id.skip)
    void skip(){
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.terms_condtns)
    void trmscon()
    {
        AppHelper.LaunchActivity(SignupActivity.this,TermsConditionsActivity.class);
    }
    @OnClick(R.id.privacy_plcy)
    void prcypolicy()
    {
        AppHelper.LaunchActivity(SignupActivity.this,PrivacyPolicyActivity.class);
    }


    @OnClick(R.id.signup_btn_verify)
    void createUser(){
        if(!chckTerms.isChecked()){
            AppHelper.Snackbar(SignupActivity.this,clSignUp,"Please Agree to the Terms & Conditions", AppConstants.MESSAGE_COLOR_ERROR,AppConstants.TEXT_COLOR);
            return;
        }

        String password = etPassword.getText().toString().trim();
        String confpassword=etConfPassword.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String mobilenumber = etMobile.getText().toString().trim();
        String refferalid=etRefferal.getText().toString().trim();

        if(!validate(password,confpassword, name, email, mobilenumber)){
            submitToServer(password, name, email, mobilenumber,refferalid);
        }

    }

    private void submitToServer(String password, String name, String email, final String mobilenumber, String refferalid) {
        AppHelper.showDialog(this,"Registering Please wait...");
        String url="";
        if(refferalid.isEmpty()){
            try {
                url = AppConstants.REGISTER_URL +"&name="+ URLEncoder.encode(name,"utf-8")
                        +"&email="+URLEncoder.encode(email,"utf-8")+"&password="+URLEncoder.encode(password,"utf-8")
                        +"&mobile="+URLEncoder.encode(mobilenumber,"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }else{
            try {
                url = AppConstants.REGISTER_URL +"&name="+URLEncoder.encode(name,"utf-8")
                        +"&email="+URLEncoder.encode(email,"utf-8")
                        +"&password="+URLEncoder.encode(password,"utf-8")
                        +"&mobile="+URLEncoder.encode(mobilenumber,"utf-8")
                        +"&code="+URLEncoder.encode(refferalid,"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        System.out.println(url);
        StringRequest str = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                AppHelper.hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = null;
                        status = jsonObject.getString("status");
                    if (status.equals("ok")){
                        //AppHelper.Snackbar(SignupActivity.this,clSignUp,"Registered Successfully", AppConstants.MESSAGE_COLOR_SUCCESS,AppConstants.TEXT_COLOR);
                        mUser.setMobileNumber(mobilenumber);
                        signup_layout.setVisibility(View.GONE);
                        otp_layout.setVisibility(View.VISIBLE);
                        //viewPager.setCurrentItem(1);
                        /*requestforOTp(mobilenumber);
                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);*/
                    }
                    if (status.equals("ko")){
                        //message
                        new AlertDialog.Builder(SignupActivity.this)
                                .setMessage(jsonObject.optString("message")+jsonObject.optString("error_description"))
                                .setCancelable(false)
                                .setPositiveButton("change", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();
//                        AppHelper.Snackbar(SignupActivity.this,clSignUp,jsonObject.getString("message"), AppConstants.MESSAGE_COLOR_ERROR,AppConstants.TEXT_COLOR);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                    else{
//                        AppHelper.Snackbar(SignupActivity.this,clSignUp,jsonObject.getString("error_description")+jsonObject.getString("error_description"), AppConstants.MESSAGE_COLOR_ERROR,AppConstants.TEXT_COLOR);
//                        finish();
//                    }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AppHelper.hideDialog();
                AppHelper.Snackbar(SignupActivity.this,clSignUp,"Unknown Error,Please try later ", AppConstants.MESSAGE_COLOR_ERROR,AppConstants.TEXT_COLOR);
                //finish();
            }
        });

        str.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MixCartApplication.getInstance().addToRequestQueue(str);
    }

    private boolean validate(String password,String confpassword, String name, String email, String mobilenumber) {
        if (name.isEmpty()){
            AppHelper.Snackbar(SignupActivity.this,clSignUp,"Please Enter Name", AppConstants.MESSAGE_COLOR_ERROR,AppConstants.TEXT_COLOR);
            return true;
        }else  if (mobilenumber.length() != 10){
            AppHelper.Snackbar(SignupActivity.this,clSignUp,"Please Enter 10-Digit Mobile Number", AppConstants.MESSAGE_COLOR_ERROR,AppConstants.TEXT_COLOR);
            return true;
        }else  if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            AppHelper.Snackbar(SignupActivity.this,clSignUp,"Please Enter Valid Email Id", AppConstants.MESSAGE_COLOR_ERROR,AppConstants.TEXT_COLOR);
            return true;
        }else if (password.isEmpty()){
            AppHelper.Snackbar(SignupActivity.this,clSignUp,"Please Enter Password", AppConstants.MESSAGE_COLOR_ERROR,AppConstants.TEXT_COLOR);
            return true;
        }else if (confpassword.isEmpty()){
            AppHelper.Snackbar(SignupActivity.this,clSignUp,"Please Confirm Password", AppConstants.MESSAGE_COLOR_ERROR,AppConstants.TEXT_COLOR);
            return true;
        }else if (!confpassword.equals(password)){
            AppHelper.Snackbar(SignupActivity.this,clSignUp,"Confirm Password Not Matched", AppConstants.MESSAGE_COLOR_ERROR,AppConstants.TEXT_COLOR);
            return true;
        }

        return false;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnecting, boolean isConnected) {

        if (!isConnecting && !isConnected) {
            AppHelper.Snackbar(this, clSignUp, getString(R.string.connection_is_not_available), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
        } else if (isConnecting && isConnected) {
            AppHelper.Snackbar(this, clSignUp, getString(R.string.connection_is_available), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
        } else {
            AppHelper.Snackbar(this, clSignUp, getString(R.string.waiting_for_network), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);
        }

    }


    class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }

        @SuppressWarnings("deprecation")
        public Object instantiateItem(View collection, int position) {

            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.id.signup_layout;
                    break;
                case 1:
                    resId = R.id.otp_layout;
                    break;
            }
            return findViewById(resId);
        }
    }
    private void verifyOtp(String mn, String otp) {

        String url = AppConstants.SUBMIT_OTP_URL + "&mobile="+mn+"&otp="+otp;
        System.out.println(url);
        StringRequest str = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                AppHelper.logout(SignupActivity.this,response);
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if(status.equals("ok")){
                        //AppHelper.Snackbar(LoginActivity.this,clLogin,"Login Success", AppConstants.MESSAGE_COLOR_SUCCESS,AppConstants.TEXT_COLOR);
                        mUser.setLoggedIn();

                        //String username = jsonObject.getString("username");
                        String userid = jsonObject.getString("userid");
                        String name = jsonObject.getString("name");
                        String ReferCode = jsonObject.getString("Refercode");
                        String email = jsonObject.getString("email");
                        String mobile = jsonObject.getString("mobile");
                        String uniqueid = jsonObject.getString("uniquecode");
                        String userType = jsonObject.getString("usertype");

                        mUser.setMobileNumber(mobile);
                        mUser.setReferralId(ReferCode);
                        mUser.setUniqueId(uniqueid);
                        mUser.setName(name);
                        mUser.setUserDetails(userid);
                        mUser.setKeyUserType(userType);
                        mUser.setEmail(email);

                        Toast.makeText(SignupActivity.this,"Registedred Successfully",Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                }catch (Exception e){

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MixCartApplication.getInstance().addToRequestQueue(str);


    }
    private void requestRuntimePermissions(String... permissions) {
        for (String perm : permissions) {

            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{perm}, PERMISSION_REQUEST_ID);

            }else{
                isPermissionGranted = true;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSION_REQUEST_ID){

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted
                IncomingSms.bindListener(new SmsListener() {
                    @Override
                    public void messageReceived(String messageText) {
                        etOTP.setText(messageText);
                        progrs_vrfy.setVisibility(View.GONE);
                        setupOTP();
                    }
                });


            } else {
                Log.e(TAG, "Permission not granted");

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(IncomingSms, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(IncomingSms);
    }
}
