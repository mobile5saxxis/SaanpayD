package com.saxxis.saanpaydestributor.activities.leftmenu;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.activities.main.MainActivity;
import com.saxxis.saanpaydestributor.activities.payment.KYCUploadActivity;
import com.saxxis.saanpaydestributor.app.AppConstants;
import com.saxxis.saanpaydestributor.app.MixCartApplication;
import com.saxxis.saanpaydestributor.app.UserPref;
import com.saxxis.saanpaydestributor.helpers.AppHelper;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WalletPayActivity extends AppCompatActivity implements View.OnClickListener {

    static final int PICK_CONTACT_REQUEST = 1;


    @BindView(R.id.txt_walletbalance)
    TextView txtAvailBalance;

    @BindView(R.id.tipl_paidmobileno)
    TextInputLayout txtLayPayMobile;
    @BindView(R.id.tipl_paidamount)
    TextInputLayout txtLayPayAmount;
    @BindView(R.id.tipl_description)
    TextInputLayout txtLayPayDescrtipt;

    @BindView(R.id.txtpaid_mobileno)
    TextInputEditText txtPayMobile;
    @BindView(R.id.txtpaid_amount)
    TextInputEditText txtPayAmount;
    @BindView(R.id.txtpaid_descripetion)
    TextInputEditText txtPayDescript;
    @BindView(R.id.payamount)
    TextView txtViewAmount;
    @BindView(R.id.userName)
    TextView userName;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private UserPref userPref;
    private ImageView contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_pay);
        ButterKnife.bind(this);
        userPref = new UserPref(WalletPayActivity.this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setTitle("Pay");
        toolbar.setNavigationIcon(R.drawable.arrow);

        contact = (ImageView) findViewById(R.id.contact);
        contact.setOnClickListener(this);

        txtAvailBalance.setText(getResources().getString(R.string.walletbalanceout) + userPref.getWalletAmount());
        refreshWallet();

        txtPayMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() != 10) {
                    MixCartApplication.getInstance().cancelRequestQue("mobile");
                    userName.setVisibility(View.GONE);
                    return;
                }
                StringRequest request = new StringRequest(Request.Method.GET, AppConstants.GET_USERNAME_FROM_MOBILE + editable.toString(),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getString("status").equals("ok")) {
                                        userName.setVisibility(View.VISIBLE);
                                        userName.setText("User name: "+jsonObject.getString("name"));
                                    } else {
                                        userName.setVisibility(View.GONE);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                request.setTag("mobile");
                MixCartApplication.getInstance().addToRequestQueue(request);

            }
        });

    }


    @OnClick(R.id.payamount)
    void onPayAmount() {

        String mobile = txtPayMobile.getText().toString();
        String amount = txtPayAmount.getText().toString();
        String description = txtPayDescript.getText().toString();

        if (!validate(mobile, amount, description)) {
           // checkLimit(amount);
            submitPayment();
            /*String url=AppConstants.WALLETTOWALLET_TRSF +userPref.getUserId()+"&mobile="+
                    mobile.replaceAll(" ","%20")+"&amount="+amount.replaceAll(" ","%20")+
                    "&description="+description.replaceAll(" ","%20");
                //&userid=1040&mobile=9052001111&amount=10&description=Syed
            AppHelper.showDialog(WalletPayActivity.this, "Loading Please Wait...");
            StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                AppHelper.hideDialog();
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getString("status").equals("ok")) {
                                    String messageObject = jsonObject.getString("message");
                                    new AlertDialog.Builder(WalletPayActivity.this)
                                            .setMessage(messageObject)
                                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    Intent i = new Intent(WalletPayActivity.this, MainActivity.class);
                                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(i);
                                                }
                                            })
                                            .create().show();
                                }
                                if (jsonObject.getString("status").equals("ko")) {
                                    String messageObject = jsonObject.getString("message");
                                    new AlertDialog.Builder(WalletPayActivity.this)
                                            .setMessage(messageObject)
                                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .create().show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                AppHelper.hideDialog();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            });
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MixCartApplication.getInstance().addToRequestQueue(stringRequest);*/
        }
    }



    private boolean validate(String mobile, String amount, String description) {
//        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
        if (mobile.isEmpty()) {
            txtLayPayMobile.setError("Enter Mobile Number");
            return true;
        } else {
            txtLayPayMobile.setError("");
        }
        if (amount.isEmpty()) {
            txtLayPayAmount.setError("Enter Amount");
            return true;
        } else {
            txtLayPayAmount.setError("");
        }

        /*if (description.isEmpty()){
            txtLayPayDescrtipt.setError("Enter Description");
            return true;
        }else{
            txtLayPayDescrtipt.setError("");
        }*/


        if (!amount.isEmpty()) {
            if (Integer.parseInt(amount) > userPref.getWalletAmount()) {
                txtLayPayAmount.setError("Please Enter Sufficient Amount to Transfer");
                return true;
            }
            return false;
        }

        return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshWallet() {
//        mProgress.setVisibility(View.VISIBLE);
        Log.e("response", AppConstants.WALLET_URL + "&userid=" + userPref.getUserId());
        StringRequest request = new StringRequest(Request.Method.GET, AppConstants.WALLET_URL +
                "&userid=" + userPref.getUserId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
//                    mProgress.setVisibility(View.GONE);
                            if (status.equals("ok")) {
                                JSONObject dataobject = jsonObject.getJSONObject("data");
                                userPref.setWalletAmount(Float.parseFloat(dataobject.getString("walletamount")));
                                txtAvailBalance.setText(getResources().getString(R.string.walletbalanceout) + dataobject.getString("walletamount"));
                            }
                        } catch (Exception ignored) {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        MixCartApplication.getInstance().addToRequestQueue(request);
    }

    public void checkLimit(final String finalmoney) {
        String url = AppConstants.WALLET_URL + userPref.getUserId();

        AppHelper.showDialog(WalletPayActivity.this, "Checking transaction limit. Please Wait...");
        StringRequest str = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                AppHelper.logout(WalletPayActivity.this, s);
                try {
                    AppHelper.hideDialog();
                    JSONObject resp = new JSONObject(s);
                    String status = resp.getString("status");
                    if (status.equals("ok")) {
                        final String usertype = resp.getString("usertype");
                        String atomwallet = resp.getString("atomwallet");
                        String walletamt = resp.getString("walletamt");
                        String atomwalletremainingamount = resp.getString("atomwalletremainingamount");
                        String remainingwalletamount = resp.getString("remainingwalletamount");
                        double remainingwalletamountInt = 0;
                        if (remainingwalletamount != null) {
                            remainingwalletamountInt = Double.parseDouble(remainingwalletamount);
                        }
                        if (Double.parseDouble(finalmoney) <= remainingwalletamountInt) {
                            submitPayment();
                        } else {
                            new AlertDialog.Builder(WalletPayActivity.this)
                                    .setMessage("Available limit is " + remainingwalletamountInt + " Please re enter amount")
                                    .setPositiveButton("close", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                          //  if (usertype.equals("NonkycUser")) {
                                           //     showKYCAlert();
                                          //  }

                                        }
                                    }).show();
                        }

                    }
                } catch (Exception e) {
                    AppHelper.hideDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                AppHelper.hideDialog();
            }
        });

        str.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MixCartApplication.getInstance().addToRequestQueue(str);

    }

    public void submitPayment() {
        String mobile = txtPayMobile.getText().toString();
        String amount = txtPayAmount.getText().toString();
        String description = txtPayDescript.getText().toString();

        String url = AppConstants.WALLETTOWALLET_TRSF + userPref.getUserId() + "&mobile=" +
                mobile.replaceAll(" ", "%20") + "&amount=" + amount.replaceAll(" ", "%20") +
                "&description=" + description.replaceAll(" ", "%20");
        //&userid=1040&mobile=9052001111&amount=10&description=Syed
        AppHelper.showDialog(WalletPayActivity.this, "Loading Please Wait...");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.logout(WalletPayActivity.this, response);
                        Log.i("wallet pay",response);
                        try {
                            AppHelper.hideDialog();
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status").equals("ok")) {
                                String messageObject = jsonObject.getString("message");
                                new AlertDialog.Builder(WalletPayActivity.this)
                                        .setMessage(messageObject)
                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                Intent i = new Intent(WalletPayActivity.this, MainActivity.class);
                                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(i);
                                            }
                                        })
                                        .create().show();
                            }
                            /*if (jsonObject.getString("status").equals("ko")) {
                                String messageObject = jsonObject.getString("message");
                                new AlertDialog.Builder(WalletPayActivity.this)
                                        .setMessage(messageObject)
                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .create().show();
                            }*/
                        } catch (JSONException e) {
                            e.printStackTrace();
                            AppHelper.hideDialog();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MixCartApplication.getInstance().addToRequestQueue(stringRequest);
    }

    public void showKYCAlert() {
        new AlertDialog.Builder(WalletPayActivity.this)
                .setMessage("Verify KYC to increase limit")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivity(new Intent(WalletPayActivity.this, KYCUploadActivity.class));
                    }
                }).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request it is that we're responding to
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
                txtPayMobile.setText("");
                txtPayMobile.append(number);
            }
        }
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
