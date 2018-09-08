package com.saxxis.saanpaydestributor.activities.leftmenu;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.app.AppConstants;
import com.saxxis.saanpaydestributor.app.MixCartApplication;
import com.saxxis.saanpaydestributor.app.UserPref;
import com.saxxis.saanpaydestributor.helpers.AppHelper;
import com.saxxis.saanpaydestributor.utils.ui.LinedEditText;

import org.json.JSONObject;

import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.saxxis.saanpaydestributor.R.drawable.dob;

public class AddressActivity extends AppCompatActivity {


    @BindView(R.id.toolbar)
    Toolbar addresstoolbar;

    @BindView(R.id.edt_address)
    LinedEditText edtAddress;

    @BindView(R.id.save_address)
    Button saveAddress;

    private UserPref userPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        ButterKnife.bind(this);

        userPref = new UserPref(AddressActivity.this);

        setSupportActionBar(addresstoolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addresstoolbar.setTitle("Address");
        addresstoolbar.setNavigationIcon(R.drawable.arrow);
        edtAddress.setText(userPref.getKeyAddress());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            super.onBackPressed();
            return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.save_address)
    void saveAddress(){
        userPref.setKeyAddress(edtAddress.getText().toString().replaceAll(" ","%20"));
      try {
            String fName = userPref.getName();
//            String lName = etLName.;
            String email = userPref.getEmail();
            String mn = userPref.getMobileNumber();
            String pc=userPref.getPan();
            String address =userPref.getKeyAddress();
            String gender = edtAddress.getText().toString().replaceAll(" ","%20");

            String url = AppConstants.UPDATE_PROFILE + "&email=" + email + "&mobile=" + mn + "&firstname=" + URLEncoder.encode(fName,"utf-8")
                    + "&lastname=" + URLEncoder.encode("","utf-8") + "&gender=" +
                    gender + "&dob=" + dob + "&pancard=" + pc+ "&address=" + URLEncoder.encode(address,"utf-8") + "&id=" + userPref.getUserId();
            System.out.println(url);
          AppHelper.showDialog(AddressActivity.this,"Updating Address Please Wait...");
            final StringRequest str = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    AppHelper.logout(AddressActivity.this,response);
                    try {
                        AppHelper.hideDialog();
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        if (status.equals("ok")) {

                            new AlertDialog.Builder(AddressActivity.this)
                                    .setTitle("Updated Address SuccessFully")
                                    .setPositiveButton("ok ", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            finish();
                                        }
                                    }).create().show();
                        }
                        if (status.equals("ko")) {

                            new AlertDialog.Builder(AddressActivity.this)
                                    .setTitle("Address Failed To Update")
                                    .setPositiveButton("Try Again ", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            finish();
                                        }
                                    }).create().show();
                        }
                    } catch (Exception ignored) {

                    }
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            getActivity().finish();
//                        }
//                    }, 3000);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            MixCartApplication.getInstance().addToRequestQueue(str);
            //Submit To server
        }catch (Exception e){

        }
    }

}
