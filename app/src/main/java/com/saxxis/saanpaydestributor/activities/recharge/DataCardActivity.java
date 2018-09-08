package com.saxxis.saanpaydestributor.activities.recharge;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

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
import com.saxxis.saanpaydestributor.helpers.parsers.JSONParser;
import com.saxxis.saanpaydestributor.models.Category;
import com.saxxis.saanpaydestributor.models.Operator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.saxxis.saanpaydestributor.app.AppConstants.PLAN_REQUEST;

public class DataCardActivity extends AppCompatActivity implements View.OnClickListener {

    static final int PICK_CONTACT_REQUEST = 1;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.cl_dc)
    CoordinatorLayout clLayout;



    @BindView(R.id.rgv_prepostpaid)
    RadioGroup radiopGrp;

    @BindView(R.id.rb_prepaid)
    RadioButton radioButtonPrepaid;

    @BindView(R.id.rb_postpaid)
    RadioButton radiobuttonPostPaid;


    @BindView(R.id.dc_number)
    TextInputEditText etDcNum;
    @BindView(R.id.dc_operator)
    TextView dcOperator;
    @BindView(R.id.amount_dc)
    TextInputEditText etAmount;
    @BindView(R.id.proceed_to_pay_dc)
    TextView proceedPay;


    @BindView(R.id.dc_browse_plans)
    TextView dcPlans;

    private ImageView contact;

    private String operator="empty",dcNumber,serviceid,amount=null,servicename;

    private UserPref mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_card);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mUser = new UserPref(this);

        mToolbar.setTitle("Data Card");
        mToolbar.setNavigationIcon(R.drawable.arrow);

        contact = (ImageView) findViewById(R.id.contact);
        contact.setOnClickListener(this);

        radioButtonPrepaid.setChecked(true);
    }



    @OnClick(R.id.dc_browse_plans)
    void browseplans(){
        if(dcPlans.isEnabled()) {
            ArrayList<Category> mPlans = AppConstants.getDTHPlans();
            Intent i = new Intent(DataCardActivity.this, BrowsePlansActivity.class);
            i.putExtra("serviceid", serviceid);
            i.putExtra("servicename", servicename);
            i.putParcelableArrayListExtra("plans", mPlans);
            startActivityForResult(i, PLAN_REQUEST);
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(DataCardActivity.this);
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

    @OnClick(R.id.dc_operator)
    void selectOperator(){
        AppHelper.showDialog(DataCardActivity.this,"Fetching Operators.......");
        StringRequest request=new StringRequest(Request.Method.GET, AppConstants.DC_OPERATOR_URL,
           new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                AppHelper.logout(DataCardActivity.this,response);
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("ok")){
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        final ArrayList<Operator> mList = JSONParser.getDTHOperators(jsonArray);
                        AppHelper.hideDialog();
                        showOperatorDialog(mList);
//                        List<CharSequence> charSequences = new ArrayList<>();
//                        for (int i = 0; i<mList.size();i++){
//                            charSequences.add(mList.get(i).getOptype_name());
//                        }
//                        final CharSequence[] charSequenceArray = charSequences.toArray(new
//                                CharSequence[charSequences.size()]);
//                        AppHelper.hideDialog();
//                        AlertDialog.Builder builder = new AlertDialog.Builder(DataCardActivity.this);
//                        builder.setTitle("Make your selection");
//                        builder.setItems(charSequenceArray, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int item) {
//                                // Do something with the selection
//                                dcOperator.setText(charSequenceArray[item]);
//                                operator = mList.get(item).getOp_id();
//                                serviceid = mList.get(item).getId();
//                                dcPlans.setEnabled(true);
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
        });
//        {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Cookie",mUser.getSessionId());
//                return headers;
//            }
//        };
        MixCartApplication.getInstance().addToRequestQueue(request);
    }

    @OnClick(R.id.proceed_to_pay_dc)
    void ProceedToPay(){

        dcNumber = etDcNum.getText().toString().trim();
        if (amount==null)
            amount = etAmount.getText().toString().trim();

        if(dcNumber.isEmpty()){
            AppHelper.Snackbar(this,clLayout,"Enter DataCard Number",AppConstants.MESSAGE_COLOR_ERROR,AppConstants.TEXT_COLOR);
        }else if(amount.isEmpty()){
            AppHelper.Snackbar(this,clLayout,"Enter the recharge amount",AppConstants.MESSAGE_COLOR_ERROR,AppConstants.TEXT_COLOR);
        }else if(operator.equals("empty")){
            AppHelper.Snackbar(this,clLayout,"Select Operator",AppConstants.MESSAGE_COLOR_ERROR,AppConstants.TEXT_COLOR);
        }else{

            if (!mUser.isLoggedIn()){
                Intent in=new Intent(DataCardActivity.this, LoginActivity.class);
                in.putExtra("finish","finish");
                startActivity(in);
            }
            else{
            Intent i = new Intent(DataCardActivity.this, CompletePaymentActivity.class);
            i.putExtra("operator", operator);
            i.putExtra("number", dcNumber);
            i.putExtra("amount", amount);
            i.putExtra("operatortype","3");
            i.putExtra("type","dc");
            startActivity(i);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       if (requestCode == PLAN_REQUEST){
           if(resultCode == RESULT_OK) {
               amount = data.getStringExtra("amount");
               etAmount.setText(amount);
           }
        }

        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                // Get the URI that points to the selected contact
                Uri contactUri = data.getData();

                //experimenting

                // We only need the NUMBER column, because there will be only one row in the result
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};

                // Perform the query on the contact to get the NUMBER column
                // We don't need a selection or sort order (there's only one result for the given URI)
                // CAUTION: The query() method should be called from a separate thread to avoid blocking
                // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
                // Consider using CursorLoader to perform the query.
                Cursor cursor = this.getContentResolver().query(contactUri, null, null, null, null);
                cursor.moveToFirst();

                // Retrieve the phone number from the NUMBER column
                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String number = cursor.getString(column).trim();

                number = number.replaceAll("[\\s()]+","");
                number = number.replaceAll("[+]+","");
                number = number.replace("[^a-zA-Z]", "");
                number= number.replaceFirst("91", "");



                //Log.d(tag, "Picked Number: " + number + " Of: " + name);
                // Do something with the phone number...
                //    EditText txtPayMobile = (EditText) findViewById(R.id.txtPayMobile);
                etDcNum.setText("");
                etDcNum.append(number);
            }
        }
    }

    private void showOperatorDialog(ArrayList<Operator> mList){
        OperatorFragment op = OperatorFragment.newInstance(mList,"dc");
        op.show(getSupportFragmentManager(),"op");
    }


    public void updateOperator(Operator op) {
        servicename = op.getOptype_name();
        dcOperator.setText(op.getOptype_name());
        operator = op.getOp_id();
        serviceid = op.getId();
        dcPlans.setEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==android.R.id.home){
            super.onBackPressed();
            return  true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.contact:

                ///  if(new MainActivity().checkReadContacts()) {
                Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
                pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
                startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);


                // }else{
                //  Ask.on(this)
                //       .forPermissions(Manifest.permission.READ_CONTACTS)
                //       .withRationales("In Order to make your life easy for contact pick up application needs Read Contact permission") //optional
                //    .go();
                // }

        }
    }
}
