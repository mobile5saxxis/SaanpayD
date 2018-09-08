package com.saxxis.saanpaydestributor.activities.recharge;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
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
import com.saxxis.saanpaydestributor.models.Operator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.saxxis.saanpaydestributor.app.AppConstants.PICK_CONTACT;

public class MobileRechargePostpaidActivity extends AppCompatActivity {


    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.cl_mobile_recharge)
    CoordinatorLayout clLayout;

    @BindView(R.id.rgv_prepostpaid)
    RadioGroup radioGroupPostPaid;

    @BindView(R.id.rb_prepaid)
    RadioButton prepaid;
    @BindView(R.id.rb_postpaid)
    RadioButton postpaid;

    @BindView(R.id.tipl_recg_prep_mobile)
    TextInputLayout textInputLayout_prepmobile;

    @BindView(R.id.tipl_recg_prep_amount)
    TextInputLayout textImputLayout_amount;

    @BindView(R.id.mobile_num)
    TextInputEditText mobileNum;
    @BindView(R.id.contacts)
    ImageView contacts;
    @BindView(R.id.operator)
    TextView mobileRchrg;
    @BindView(R.id.amount_mble_rchg)
    TextInputEditText amountMble;
    @BindView(R.id.proceed_to_pay_mble)
    TextView proceedPay;


    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;

    private String operator,mobilenumber;
    private String amount="";

    private UserPref mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_recharge);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        postpaid.setChecked(true);
        mUser = new UserPref(this);


        mToolbar.setTitle("Postpaid");
        mToolbar.setNavigationIcon(R.drawable.arrow);

        initializeTabs();
    }

    private void initializeTabs() {
        TabAdapter pagerAdapter = new TabAdapter(getSupportFragmentManager(),this);
//        mTabLayoutTop.setVisibility(View.VISIBLE);

        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            tab.setCustomView(pagerAdapter.getTabView(i));
        }
//        mTabLayout.getTabAt(0).getCustomView().setSelected(true);
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

    @OnClick(R.id.operator)
    void selectOperator(){
        AppHelper.showDialog(MobileRechargePostpaidActivity.this,"Fetching Operators.......");
        StringRequest request=new StringRequest(Request.Method.GET, AppConstants.MOBILE_OPERATOR_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                AppHelper.logout(MobileRechargePostpaidActivity.this,response);
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("ok")){
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        final ArrayList<Operator> mList = JSONParser.getOperators(jsonArray);
                        AppHelper.hideDialog();
                        showOperatorDialog(mList);
//                        List<CharSequence> charSequences = new ArrayList<>();
//                        for (int i = 0; i<mList.size();i++){
//                            charSequences.add(mList.get(i).getOptype_name());
//                        }
//                        final CharSequence[] charSequenceArray = charSequences.toArray(new
//                                CharSequence[charSequences.size()]);
//                        AppHelper.hideDialog();
//                        AlertDialog.Builder builder = new AlertDialog.Builder(MobileRechargePostpaidActivity.this);
//                        builder.setTitle("Make your selection");
//                        builder.setItems(charSequenceArray, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int item) {
//                                // Do something with the selection
//                                mobileRchrg.setText(charSequenceArray[item]);
//                                operator = mList.get(item).getOp_id();
//                            }
//                        });
//                        AlertDialog alert = builder.create();
//                        alert.show();
                    }
                }catch (Exception ignored){

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Cookie",mUser.getSessionId());
                return headers;
            }
        };
        MixCartApplication.getInstance().addToRequestQueue(request);
    }


    @OnClick(R.id.proceed_to_pay_mble)
    void proceedToPay(){
        mobilenumber = mobileNum.getText().toString().trim();
        amount = amountMble.getText().toString().trim();

        if(mobilenumber.isEmpty()){
            AppHelper.Snackbar(this,clLayout,"Enter or Select Mobile Number",AppConstants.MESSAGE_COLOR_ERROR,AppConstants.TEXT_COLOR);
            return;
        }else if(amount.equals("")){
            AppHelper.Snackbar(this,clLayout,"Enter the recharge amount",AppConstants.MESSAGE_COLOR_ERROR,AppConstants.TEXT_COLOR);
            return;
        }else if(operator.isEmpty()){
            AppHelper.Snackbar(this,clLayout,"Select Operator",AppConstants.MESSAGE_COLOR_ERROR,AppConstants.TEXT_COLOR);
            return;
        }else {

            if (!mUser.isLoggedIn()){
                startActivity(new Intent(MobileRechargePostpaidActivity.this, LoginActivity.class).putExtra("finish","finish"));
            }else {
                Intent i = new Intent(MobileRechargePostpaidActivity.this, CompletePaymentActivity.class);
                i.putExtra("operator", operator);
                i.putExtra("number", mobilenumber);
                i.putExtra("amount", amount);
                i.putExtra("operatortype", "1");
                i.putExtra("type", "mobile");
                startActivity(i);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CONTACT) {
            if (resultCode == RESULT_OK) {
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
            }
        }
    }

    private void showOperatorDialog(ArrayList<Operator> mList){
        OperatorFragment op = OperatorFragment.newInstance(mList,"postpaid");
        op.show(getSupportFragmentManager(),"op");
    }

    public void updateOperator(Operator op) {
        mobileRchrg.setText(op.getOptype_name());
        operator = op.getOp_id();
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
