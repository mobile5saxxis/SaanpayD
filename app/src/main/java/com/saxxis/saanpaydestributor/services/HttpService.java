package com.saxxis.saanpaydestributor.services;

import android.app.IntentService;
import android.content.Intent;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.saxxis.saanpaydestributor.activities.main.LoginActivity;
import com.saxxis.saanpaydestributor.activities.main.MainActivity;
import com.saxxis.saanpaydestributor.app.AppConstants;
import com.saxxis.saanpaydestributor.app.MixCartApplication;

import org.json.JSONObject;

/**
 * Created by saxxis25 on 5/3/2017.
 */

public class HttpService extends IntentService {

    private static String TAG = HttpService.class.getSimpleName();

    public HttpService() {
        super(HttpService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String otp = intent.getStringExtra("otp");
            String mn = intent.getStringExtra("mobilenumber");
            String sessionid = intent.getStringExtra("sessionid");
            if(sessionid!=null){
                verifyOtpLogin(sessionid,otp);
            }else{
                verifyOtp(mn,otp);
            }

        }
    }

    private void verifyOtp(String mn, String otp) {

        String url = AppConstants.SUBMIT_OTP_URL + "&mobile="+mn+"&otp="+otp;
        System.out.println(url);
        StringRequest str = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if(status.equals("ok")){
                        Intent intent = new Intent(HttpService.this, LoginActivity.class);
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
    private void verifyOtpLogin(String sessionid, String otp) {

        String url = AppConstants.SUBMIT_OTP_LOGIN_URL + "&sessionid="+sessionid+"&otp="+otp;
        System.out.println(url);
        StringRequest str = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if(status.equals("ok")){
                        Intent intent = new Intent(HttpService.this, MainActivity.class);
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


}
