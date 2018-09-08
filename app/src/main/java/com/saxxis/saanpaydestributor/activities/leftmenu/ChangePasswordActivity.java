package com.saxxis.saanpaydestributor.activities.leftmenu;

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
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.activities.main.LoginActivity;
import com.saxxis.saanpaydestributor.app.AppConstants;
import com.saxxis.saanpaydestributor.app.MixCartApplication;
import com.saxxis.saanpaydestributor.app.UserPref;
import com.saxxis.saanpaydestributor.helpers.AppHelper;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangePasswordActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.tipl_oldpassword)
    TextInputLayout txtlayOldPassword;

    @BindView(R.id.tipl_newpassword)
    TextInputLayout txtlayNewPassword;

    @BindView(R.id.tipl_confnewpassword)
    TextInputLayout txtlayConfNewPassword;

    @BindView(R.id.old_password)
    TextInputEditText edt_oldPassword;

    @BindView(R.id.new_password)
    TextInputEditText edtNewPAssword;

    @BindView(R.id.confirmnew_password)
    TextInputEditText confNewPassword;

    @BindView(R.id.changepassword)
    TextView changePasswword;

    @BindView(R.id.coordinatelay)
    CoordinatorLayout coordinatorLayout;

    private UserPref userPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        ButterKnife.bind(this);

        userPref = new UserPref(ChangePasswordActivity.this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setTitle("Change Password");
        mToolbar.setNavigationIcon(R.drawable.arrow);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            super.onBackPressed();
            return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.changepassword)
    void changePassword(){
           String oldPAssword=edt_oldPassword.getText().toString();
           String newPassword=edtNewPAssword.getText().toString();
           String confnewPassword=confNewPassword.getText().toString();

        if (valid(oldPAssword,newPassword,confnewPassword)){
            AppHelper.showDialog(ChangePasswordActivity.this,"Loading Please Wait...");
            if(newPassword.equals(confnewPassword)){
                String url=AppConstants.CHANGE_PASSWORD+userPref.getUserId()+
                        "&password="+oldPAssword+"&newpassword="+newPassword+"&conformpassword="+confnewPassword;

                Log.e("respponse",url);
                StringRequest request=new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.logout(ChangePasswordActivity.this,response);
                        Log.e("response",response);
                        AppHelper.hideDialog();
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            if (status.equals("ok")){
                                new AlertDialog.Builder(ChangePasswordActivity.this)
                                        .setMessage(jsonObject.getString("Statuss"))
                                        .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            }
                                        }).create().show();
                            }

                            /*if(status.equals("ko")) {
                                new AlertDialog.Builder(ChangePasswordActivity.this)
                                        .setMessage(jsonObject.getString("message"))
                                        .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).create().show();
                            }*/
                        }catch (Exception ignored){

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AppHelper.Snackbar(ChangePasswordActivity.this,coordinatorLayout,"UnKnown Error occured",R.color.colorRedDark,R.color.white);

                    }
                });

                MixCartApplication.getInstance().addToRequestQueue(request);
            }
        }

    }

    private boolean valid(String oldPAssword, String newPassword, String confnewPassword) {

        if (oldPAssword.isEmpty()){
            AppHelper.Snackbar(ChangePasswordActivity.this,coordinatorLayout,"Please Enter Old Password",R.color.colorchange,R.color.white);
            return false;
        }

        if (newPassword.isEmpty()){
            AppHelper.Snackbar(ChangePasswordActivity.this,coordinatorLayout,"Please Enter New Password",R.color.colorchange,R.color.white);
            return false;
        }

        if (confnewPassword.isEmpty()){
            AppHelper.Snackbar(ChangePasswordActivity.this,coordinatorLayout,"Please Enter Confirm Password",R.color.colorchange,R.color.white);
            return false;
        }

        if (!newPassword.equals(confnewPassword)){
            AppHelper.Snackbar(ChangePasswordActivity.this,coordinatorLayout,"Please Confirm Password is not Matched",R.color.colorchange,R.color.white);
            return false;
        }

        return true;
    }

}
