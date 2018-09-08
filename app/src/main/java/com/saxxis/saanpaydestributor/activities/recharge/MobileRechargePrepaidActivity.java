package com.saxxis.saanpaydestributor.activities.recharge;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.activities.CompletePaymentActivity;
import com.saxxis.saanpaydestributor.activities.main.LoginActivity;
import com.saxxis.saanpaydestributor.adapters.TabAdapter;
import com.saxxis.saanpaydestributor.app.AppConstants;
import com.saxxis.saanpaydestributor.app.MixCartApplication;
import com.saxxis.saanpaydestributor.app.UserPref;
import com.saxxis.saanpaydestributor.fragments.OperatorFragment;
import com.saxxis.saanpaydestributor.helpers.AppHelper;
import com.saxxis.saanpaydestributor.helpers.PermissionHandler;
import com.saxxis.saanpaydestributor.helpers.parsers.JSONParser;
import com.saxxis.saanpaydestributor.models.Category;
import com.saxxis.saanpaydestributor.models.Operator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.saxxis.saanpaydestributor.app.AppConstants.PICK_CONTACT;
import static com.saxxis.saanpaydestributor.app.AppConstants.PLAN_REQUEST;

public class MobileRechargePrepaidActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.cl_p_mobile_recharge)
    CoordinatorLayout clLayout;

    @BindView(R.id.rgv_prepostpaid)
    RadioGroup radiopGrp;

    @BindView(R.id.rb_prepaid)
    RadioButton radioButtonPrepaid;

    @BindView(R.id.rb_postpaid)
    RadioButton radiobuttonPostPaid;

    @BindView(R.id.p_mobile_num)
    TextInputEditText mobileNum;
    @BindView(R.id.contacts)
    ImageView contacts;
    @BindView(R.id.p_operator)
    TextView mobileRchrg;
    @BindView(R.id.p_amount_mble_rchg)
    TextInputEditText amountMble;
    @BindView(R.id.p_proceed_to_pay_mble)
    TextView proceedPay;

    @BindView(R.id.p_btn_browse_plans)
    TextView mobilePlans;

//    @BindView(R.id.tab_layout)
//    TabLayout mTabLayout;

    String optiontype="1";

//    @BindView(R.id.tab_layout_top)
//    TabLayout mTabLayoutTop;

    private String mobilenumber,serviceid,servicename;

    private String operator="empty";
    private String amount="";

    private UserPref mUser;

    private boolean status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_recharge_prepaid);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras=getIntent().getExtras();
        if (!extras.isEmpty()){
            optiontype=extras.getString("checkoptiontype");
            if (optiontype.equals("1")){
                proceedPay.setText("Proceed To Recharge");
                radioButtonPrepaid.setChecked(true);
                mobilePlans.setVisibility(View.VISIBLE);
                operator="empty";
            }

            if (optiontype.equals("2")){
                operator="empty";
                proceedPay.setText("Proceed To Pay");
                radiobuttonPostPaid.setChecked(true);
                mobilePlans.setVisibility(View.GONE);
            }
        }

        radiopGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.rb_prepaid:
                        optiontype="1";
                        operator="empty";
                        proceedPay.setText("Proceed To Recharge");
                        mobileRchrg.setText("Select Operator");
                        radioButtonPrepaid.setChecked(true);
                        mobilePlans.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rb_postpaid:
                        optiontype="2";
                        operator="empty";
                        proceedPay.setText("Proceed To Pay");
                        mobileRchrg.setText("Select Operator");
                        radiobuttonPostPaid.setChecked(true);
                        mobilePlans.setVisibility(View.GONE);
                        break;
                }
            }
        });

        mToolbar.setTitle("Recharge");
        mToolbar.setNavigationIcon(R.drawable.arrow);

        mUser = new UserPref(this);

        mobileNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==4 || s.length()==8){
                   if(!status){
                       fetchOperator(s);
                       status= true;
                   }
                }else if(s.length()==0){
                    mobileRchrg.setText("Select Operator");
                    status=false;
                }
            }
        });

      //  initializeTabs();
    }

//    private void initializeTabs() {
//        TabAdapter pagerAdapter = new TabAdapter(getSupportFragmentManager(),this);
////        mTabLayoutTop.setVisibility(View.VISIBLE);
//
//        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
//            TabLayout.Tab tab = mTabLayout.getTabAt(i);
//            tab.setCustomView(pagerAdapter.getTabView(i));
//        }
////        mTabLayout.getTabAt(0).getCustomView().setSelected(true);
//    }

    private void fetchOperator(Editable s) {
        if(radiobuttonPostPaid.isChecked()) {
            Log.e("response", AppConstants.FETCH_OPERATOR_URL + s);
            StringRequest str = new StringRequest(AppConstants.FETCH_OPERATOR_URL + s,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            AppHelper.logout(MobileRechargePrepaidActivity.this,response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                Log.e("response", response);
                                String status = jsonObject.getString("status");
                                if (status.equals("ok")) {
//                        serviceid = jsonObject.getString("ServiceId");
                                    JSONArray jos1 = jsonObject.getJSONArray("data");
                                    JSONObject jos = jos1.getJSONObject(0);
                                    servicename = jos.getString("optype_name");
                                    mobileRchrg.setText(servicename);
                                    operator = jos.getString("op_id");
                                    serviceid = jos.getString("service");
                                    mobilePlans.setEnabled(true);
                                }
                                //else if (status.equals("ko")) {
//                        new AlertDialog.Builder(MobileRechargePrepaidActivity.this)
//                                .setMessage("Retry...")
//                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        mUser.logoutUser();
//                                        Intent intent = new Intent(MobileRechargePrepaidActivity.this, LoginActivity.class);
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                        startActivity(intent);
////                                        Toast.makeText(MobileRechargePrepaidActivity.this,"Logout Successfull",Toast.LENGTH_SHORT).show();
//                                    }
//                                });
                                //}
                            } catch (Exception ignored) {

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

    @OnClick(R.id.contacts)
    void pickContact(){
        if(!PermissionHandler.checkPermission(this, Manifest.permission.READ_CONTACTS)){
            PermissionHandler.requestPermission(this,Manifest.permission.READ_CONTACTS);
            return;
        }

        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }

    @OnClick(R.id.p_btn_browse_plans)
    void browseplans(){
        if(mobilePlans.isEnabled()) {
            ArrayList<Category> mPlans = AppConstants.getPlansCategories();

            Intent i = new Intent(MobileRechargePrepaidActivity.this, BrowsePlansActivity.class);
            i.putExtra("serviceid", serviceid);
            i.putExtra("servicename", servicename);
            i.putParcelableArrayListExtra("plans",mPlans);
            startActivityForResult(i, AppConstants.PLAN_REQUEST);
        } else{
            AlertDialog.Builder builder = new AlertDialog.Builder(MobileRechargePrepaidActivity.this);
            builder.setTitle("Select Operator to view Plans");
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }
    }


    @OnClick(R.id.p_operator)
    void selectOperator(){
        AppHelper.showDialog(MobileRechargePrepaidActivity.this,"Fetching Operators.......");

        String url="";

        if (optiontype.equals("1")){
            url=AppConstants.MOBILE_OPERATOR_URL+"1";
        }

        if (optiontype.equals("2")){
            url=AppConstants.MOBILE_OPERATOR_URL+"4";
        }

        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppHelper.logout(MobileRechargePrepaidActivity.this,response);

                Log.e("response",response);
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("ok")){
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        final ArrayList<Operator> mList = JSONParser.getOperators(jsonArray);
                        AppHelper.hideDialog();
                        showOperatorDialog(mList);

                    }
                    //else if(status.equals("ko")) {
                      //  AppHelper.hideDialog();
//                        new AlertDialog.Builder(MobileRechargePrepaidActivity.this)
//                                .setMessage("User Session Timed Out...Login Again")
//                                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        mUser.logoutUser();
//                                    }
//                                }).show();
                    //}
                }catch (Exception ignored){

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        MixCartApplication.getInstance().addToRequestQueue(request);
    }

    @OnClick(R.id.p_proceed_to_pay_mble)
    void proceedToPay(){
        mobilenumber = mobileNum.getText().toString().trim();
        amount = amountMble.getText().toString().trim();

        if(mobilenumber.isEmpty()){
            AppHelper.Snackbar(this,clLayout,"Enter or Select Mobile Number",AppConstants.MESSAGE_COLOR_ERROR,AppConstants.TEXT_COLOR);
            return;
        }else if(amount.isEmpty()){
            AppHelper.Snackbar(this,clLayout,"Enter the recharge amount",AppConstants.MESSAGE_COLOR_ERROR,AppConstants.TEXT_COLOR);
            return;
        }else if(operator.equals("empty")){
            AppHelper.Snackbar(this,clLayout,"Select Operator",AppConstants.MESSAGE_COLOR_ERROR,AppConstants.TEXT_COLOR);
            return;
        }else {
            if (mUser.isLoggedIn()){
                Intent i = new Intent(MobileRechargePrepaidActivity.this, CompletePaymentActivity.class);
                i.putExtra("operator", operator);
                i.putExtra("number", mobilenumber);
                if(optiontype.equals("1")){
                    i.putExtra("operatortype","1");
                }
                if(optiontype.equals("2")){
                    i.putExtra("operatortype","4");
                }
                i.putExtra("amount", amount);
                i.putExtra("type","mobile");
                startActivity(i);
            }
            if (!mUser.isLoggedIn()){
                startActivity(new Intent(MobileRechargePrepaidActivity.this, LoginActivity.class).putExtra("finish","finish"));
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            switch(requestCode) {
                case PICK_CONTACT :
                    Uri contactData = data.getData();
                    Cursor cursor = getContentResolver().query(contactData, null, null, null, null);
                    cursor.moveToFirst();
                    String hasPhone = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                    if (hasPhone.equals("1")) {
                        Cursor phones = getContentResolver().query
                                (ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                                + " = " + contactId, null, null);
                        while (phones.moveToNext()) {
                            mobilenumber = phones.getString(phones.getColumnIndex
                                    (ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("[-() ]", "");
                        }
                        phones.close();
                        mobileNum.setText(mobilenumber);
                    } else {
                        Toast.makeText(getApplicationContext(), "This contact has no phone number", Toast.LENGTH_LONG).show();
                    }
                    cursor.close();

                    break;
                case PLAN_REQUEST:
                    amount = bundle.getString("amount");
                    amountMble.setText(amount);
                    break;
            }
        }
    }

    private void showOperatorDialog(ArrayList<Operator> mList){
        OperatorFragment op = OperatorFragment.newInstance(mList,"prepaid");
        op.show(getSupportFragmentManager(),"op");
    }

    public void updateOperator(Operator op) {
        servicename = op.getOptype_name();
        mobileRchrg.setText(servicename);
        operator = op.getOp_id();
        serviceid = op.getId();
        mobilePlans.setEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            super.onBackPressed();
            return  true;
        }

        return super.onOptionsItemSelected(item);
    }
}
