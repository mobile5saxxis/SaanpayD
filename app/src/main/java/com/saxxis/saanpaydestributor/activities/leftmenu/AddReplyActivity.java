package com.saxxis.saanpaydestributor.activities.leftmenu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;

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

public class AddReplyActivity extends AppCompatActivity {
    private UserPref userPref;
    @BindView(R.id.reply_message)
    TextInputEditText reply_message;
    @BindView(R.id.submit_reply)
    Button submit_reply;
    String ticket = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reply);
        ButterKnife.bind(this);

        userPref=new UserPref(AddReplyActivity.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.arrow);

        Intent intent = getIntent();
        ticket = intent.getStringExtra("ticket");


    }
    @OnClick(R.id.submit_reply)
    void reply(){
        AppHelper.showDialog(AddReplyActivity.this,"Loading Please Wait...");
        String url = AppConstants.HELP_REPLY+userPref.getUserId()
                +"&ticket="+ticket+
                "&message="+reply_message.getText().toString().replaceAll(" ","%20");

        Log.e("reponse",url);

        StringRequest stringRequest = new StringRequest(url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppHelper.logout(AddReplyActivity.this,response);
                Log.e("reponse",response);
                try {
                    AppHelper.hideDialog();
                    JSONObject jsonObject=new JSONObject(response);
                    if (jsonObject.getString("status").equals("ok")){
                        new AlertDialog.Builder(AddReplyActivity.this).setMessage("Your Query Submited Successfully")
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                })
                                .create().show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                AppHelper.hideDialog();
            }
        });

        MixCartApplication.getInstance().addToRequestQueue(stringRequest);
    }
    }

