package com.saxxis.saanpaydestributor.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.activities.main.MainActivity;
import com.saxxis.saanpaydestributor.app.AppConstants;
import com.saxxis.saanpaydestributor.app.MixCartApplication;
import com.saxxis.saanpaydestributor.app.UserPref;
import com.saxxis.saanpaydestributor.helpers.AppHelper;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CompleteInfoActivity extends AppCompatActivity {

    @BindView(R.id.response)
    TextView txtResponse;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private UserPref userPref;

    private String response=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_info);
        ButterKnife.bind(this);
        userPref=new UserPref(CompleteInfoActivity.this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setTitle("Payment Info");
        mToolbar.setNavigationIcon(R.drawable.arrow);


        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            response= extras.getString("response");
            Log.e("response",response);
            try {
                JSONObject jsonObject=new JSONObject(extras.getString("response"));
                JSONObject dataObject=jsonObject.getJSONObject("data");

//                txtResponse.setText(dataObject.getString("ordernumber"));
//                txtResponse.setTextColor(Color.RED);

                if (dataObject.getString("paymentamount").equals("0")){
//                    rechargeCorrect(dataObject.getString("ordernumber"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

//        txtResponse.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(CompleteInfoActivity.this, MainActivity.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(i);
//            }
//        });
    }

    private void rechargeCorrect(final String ordernumber) {

        String finalurl = AppConstants.RECHARGEAPI+userPref.getUserId()+"&ordernumber="+ordernumber;
        Log.e("response",finalurl);

        StringRequest request=new StringRequest(finalurl,new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                AppHelper.hideDialog();
                try {
                    AppHelper.logout(CompleteInfoActivity.this,response);
                    Log.e("response",response);
                    AppHelper.showDialog(CompleteInfoActivity.this,ordernumber+" is Connecting \n Loading Please Wait...",false);
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if(status.equals("ok")){
                        AppHelper.hideDialog();
                        new AlertDialog.Builder(CompleteInfoActivity.this)
                                .setTitle("Recharge Successfull")
                                .setMessage(jsonObject.getString("message"))
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        Intent i = new Intent(CompleteInfoActivity.this, MainActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        finish();
                                    }
                                }).create().show();
                    }if (status.equals("ko")){

                        AppHelper.hideDialog();
                        new AlertDialog.Builder(CompleteInfoActivity.this)
                                .setTitle("Recharge Failed")
                                .setMessage(jsonObject.getString("message"))
                                .setPositiveButton("Try Again ", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        Intent i = new Intent(CompleteInfoActivity.this, MainActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        finish();
                                    }
                                }).create().show();
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

        MixCartApplication.getInstance().addToRequestQueue(request);

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(CompleteInfoActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        if (id==android.R.id.home){
            Intent i = new Intent(CompleteInfoActivity.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
