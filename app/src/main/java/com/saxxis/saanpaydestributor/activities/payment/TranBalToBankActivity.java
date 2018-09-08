package com.saxxis.saanpaydestributor.activities.payment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.adapters.dropdown.BankListAdapter;
import com.saxxis.saanpaydestributor.app.AppConstants;
import com.saxxis.saanpaydestributor.app.MixCartApplication;
import com.saxxis.saanpaydestributor.app.UserPref;
import com.saxxis.saanpaydestributor.helpers.AppHelper;
import com.saxxis.saanpaydestributor.models.BanksList;
import com.saxxis.saanpaydestributor.models.BeneficiaryDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TranBalToBankActivity extends AppCompatActivity {

    @BindView(R.id.toolbartrnxbalbank)
    Toolbar mToolbar;

    @BindView(R.id.availbalance)
    TextView availBalance;

    @BindView(R.id.txtobank_accname)
    AppCompatAutoCompleteTextView txtAccountHolderName;

    @BindView(R.id.txtobank_bankname)
    AppCompatAutoCompleteTextView txtSelectBank;

    @BindView(R.id.txtobank_acno)
    TextInputEditText accNo;

    @BindView(R.id.txtobank_ifsccode)
    TextInputEditText ifscCode;

    @BindView(R.id.txtobank_amount)
    TextInputEditText amount;

    @BindView(R.id.fee_txt)
    TextView txtFee;
    @BindView(R.id.netpayable_txt)
    TextView txtNetPayble;


    @BindView(R.id.txtobank_describe)
    TextInputEditText describe;

    @BindView(R.id.proceed_topayto_bank)
    TextView textPayToBank;

    String senderId = "";
    String receiverId = "0";
    String selectedBankCode = "";
    UserPref userPref;
    ArrayList<BanksList> banksLists;


    String otpVerified = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tran_bal_to_bank);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userPref = new UserPref(TranBalToBankActivity.this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            senderId = extras.getString("senderId");
            getBeneficaryDetails(senderId);
        }

        mToolbar.setTitle("Transfer Balance To Bank");
        mToolbar.setNavigationIcon(R.drawable.arrow);
        getBankNAme();
        refreshWallet();

       /* amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable text) {
                Log.e("response", text.toString());
                int fee = 0;
                if (!text.toString().isEmpty()) {
                    Integer amt_t = Integer.parseInt(text.toString());
                    if (amt_t <= 300) {
                        fee = 10;
                    }
                    if (amt_t > 300) {
                        fee = (amt_t * 3) / 100;
                    }
                    int nettotal = amt_t + fee;
                    txtFee.setText("Fee \n+" + fee);
                    txtNetPayble.setText("Total Amt \n=" + nettotal);
                    txtFee.setVisibility(View.VISIBLE);
                    txtNetPayble.setVisibility(View.VISIBLE);
                }
                if (text.toString().isEmpty()) {
                    txtFee.setVisibility(View.GONE);
                    txtNetPayble.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
//                txtFee.setVisibility(View.GONE);
//                txtNetPayble.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {

            }
        });*/
        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    MixCartApplication.getInstance().cancelRequestQue("amount");
                    txtFee.setVisibility(View.GONE);
                    txtNetPayble.setVisibility(View.GONE);
                    return;
                }
                StringRequest request = new StringRequest(Request.Method.GET, AppConstants.AMOUNT_COMMISION + editable.toString(),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getString("status").equals("ok")) {
                                        txtFee.setText(String.format("Credit Amt \n+%s", (jsonObject.getString("creditamount").equals("null")) ? "0" : jsonObject.getString("creditamount")));
                                        txtNetPayble.setText(String.format("Total Amt \n=%s", jsonObject.getString("deductionamount")));
                                        txtFee.setVisibility(View.VISIBLE);
                                        txtNetPayble.setVisibility(View.VISIBLE);
                                    } else {
                                        txtFee.setVisibility(View.GONE);
                                        txtNetPayble.setVisibility(View.GONE);
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
                request.setTag("amount");
                MixCartApplication.getInstance().addToRequestQueue(request);

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }


        });
    }

    private void getBeneficaryDetails(final String senderIdstat) {
        StringRequest request = null;
        final ArrayList<BeneficiaryDetails> benefiviaryDetails = new ArrayList<>();
        Log.e("response", AppConstants.GET_BENEFICIARY_LIST + userPref.getUserId() + "&senderid=" + senderIdstat);
        request = new StringRequest(Request.Method.GET, AppConstants.GET_BENEFICIARY_LIST + userPref.getUserId() + "&senderid=" + senderIdstat,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.logout(TranBalToBankActivity.this, response);
                        Log.e("response", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            final List<String> alllist = new ArrayList<String>();
                            if (jsonObject.getString("status").equals("ok")) {
                                JSONArray resultArray = jsonObject.getJSONArray("result");
                                for (int i = 0; i < resultArray.length(); i++) {
                                    JSONObject result = resultArray.getJSONObject(i);
                                    String name = result.getString("name");
                                    String account = result.getString("account");
                                    String bankcode = result.getString("bankcode");
                                    String ifsc = result.getString("ifsc");
                                    String senderidcom = result.getString("senderid");
                                    String receiverid = result.getString("receiverid");
                                    String otpverified = result.getString("otpverified");
                                    alllist.add("Name: " + name + "\nAccount No: " + account + "\nIFSC Code: " + ifsc);

                                    benefiviaryDetails.add(new BeneficiaryDetails(name, account, bankcode, ifsc, senderidcom, receiverid, otpverified));
                                }

                                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                        TranBalToBankActivity.this,
                                        R.layout.dropdown_list_item, R.id.text_item, alllist);
//                                    arrayAdapter.setDropDownViewResource(R.layout.dropdown_list_item);

                                txtAccountHolderName.setThreshold(0);
                                txtAccountHolderName.setAdapter(arrayAdapter);
                                txtAccountHolderName.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        txtAccountHolderName.showDropDown();
                                    }
                                });

                                txtAccountHolderName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        txtAccountHolderName.setText("");
                                        txtAccountHolderName.setText(benefiviaryDetails.get(position).getName());
                                        accNo.setText(benefiviaryDetails.get(position).getAccount());
                                        ifscCode.setText(benefiviaryDetails.get(position).getIfsc());
                                        senderId = benefiviaryDetails.get(position).getSenderid();
                                        receiverId = benefiviaryDetails.get(position).getReceiverid();
                                        otpVerified = benefiviaryDetails.get(position).getOtpverified();
                                        String bankCode = benefiviaryDetails.get(position).getBankcode();
                                        for (int i = 0; i < banksLists.size(); i++) {
                                            if (banksLists.get(i).getBank_code().equals(bankCode)) {
                                                txtSelectBank.setText(banksLists.get(i).getBank_name());
                                                selectedBankCode = banksLists.get(i).getBank_code();
                                            }
                                        }
                                    }
                                });

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

    private void refreshWallet() {
//        mProgress.setVisibility(View.VISIBLE);
        Log.e("response", AppConstants.WALLET_URL + "&userid=" + userPref.getUserId());
        StringRequest request = new StringRequest(Request.Method.GET, AppConstants.WALLET_URL +
                "&userid=" + userPref.getUserId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.logout(TranBalToBankActivity.this, response);
                        Log.e("response", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");

                            if (status.equals("ok")) {
                                JSONObject dataobject = jsonObject.getJSONObject("data");
                                userPref.setWalletAmount(Float.parseFloat(dataobject.getString("walletamount")));
                                availBalance.setText(getResources().getString(R.string.walletbalanceout) + dataobject.getString("walletamount"));
                            }

                            /*if(status.equals("ko")) {
                                new AlertDialog.Builder(TranBalToBankActivity.this)
                                        .setMessage("User Session Timed Out...Login Again")
                                        .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                userPref.logoutUser();
                                                Intent intent = new Intent(TranBalToBankActivity.this, LoginActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                Toast.makeText(TranBalToBankActivity.this,"Logout Successfull",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }*/
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.txtobank_bankname)
    void getBankSelect() {
        final List<String> titles = new ArrayList<>();

        for (int i = 0; i < banksLists.size(); i++) {
            titles.add(banksLists.get(i).getBank_name());
        }
//        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
//                TranBalToBankActivity.this,
//                R.layout.dropdown_list_item,R.id.text_item,titles);

        BankListAdapter arrayAdapter = new BankListAdapter(TranBalToBankActivity.this, titles);
        arrayAdapter.getFilter().filter(txtSelectBank.getText().toString());

        txtSelectBank.showDropDown();
//        txtSelectBank.setThreshold(0);
        txtSelectBank.setAdapter(arrayAdapter);

//        txtSelectBank.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                txtSelectBank.showDropDown();
//            }
//        });
//        new AlertDialog.Builder(TranBalToBankActivity.this)
//                .setTitle("Select Your Bank")
//                .setItems(titles, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {

        txtSelectBank.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                txtSelectBank.setText(titles.get(view.getId()));
                selectedBankCode = banksLists.get(view.getId()).getBank_code();
                Toast.makeText(TranBalToBankActivity.this, "" + selectedBankCode, Toast.LENGTH_SHORT).show();
            }
        });

//                        dialog.dismiss();
//                    }
//                })
//                .create().show();
    }

    void getBankNAme() {
        AppHelper.showDialog(TranBalToBankActivity.this, "Loading Please Wait...");
        Log.e("response", AppConstants.BANKS_LIST);
        StringRequest stringRequest = new StringRequest(AppConstants.BANKS_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.logout(TranBalToBankActivity.this, response);
                        Log.e("response", response);
                        AppHelper.hideDialog();
                        try {
                            banksLists = new ArrayList<BanksList>();
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray banksArray = jsonObject.getJSONArray("banks");
                            for (int i = 0; i < banksArray.length(); i++) {
                                JSONObject bankObject = banksArray.getJSONObject(i);
                                banksLists.add(new BanksList(bankObject.getString("id"),
                                        bankObject.getString("bank_name"),
                                        bankObject.getString("bank_code"), ""));
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

        MixCartApplication.getInstance().addToRequestQueue(stringRequest);
    }


    @OnClick(R.id.proceed_topayto_bank)
    void proceedToPay() {
        if (valid(txtAccountHolderName.getText().toString(), accNo.getText().toString(), ifscCode.getText().toString(), selectedBankCode, amount.getText().toString())) {
/*        StringRequest request = null;
        try {
            final String url=AppConstants.RECEIVEING_REGISTER+userPref.getUserId()+
                    "&senderid="+senderId+"&name="+URLEncoder.encode(txtAccountHolderName.getText().toString(),"UTF-8")+
                    "&account="+accNo.getText().toString()+"&ifsc="+ifscCode.getText().toString()+
                    "&bankcode="+selectedBankCode+"&beneficiaryid="+receiverId+"&amount="+amount.getText().toString();
            Log.e("response", url+"&otpverified="+otpVerified);
            textPayToBank.setEnabled(false);

            AppHelper.showDialog(TranBalToBankActivity.this,"Loading Please Wait...",false);
            request = new StringRequest(url+"&otpverified="+otpVerified,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            AppHelper.hideDialog();
                            Log.e("response",response);
                            try{
                                JSONObject jsonObject = new JSONObject(response);

                                    String otp = jsonObject.optString("OTP");
                                    if (otp.equals("1")){
                                        JSONObject resultObject = jsonObject.optJSONObject("result");
                                        receiverId = resultObject.optString("beneficiaryid");
                                        Intent intent = new Intent(TranBalToBankActivity.this,PaymentResponseActivity.class);
                                        intent.putExtra("benefId",receiverId);
                                        intent.putExtra("senderId",senderId);
                                        intent.putExtra("moneyurl",AppConstants.RECEIVEING_REGISTER+userPref.getUserId()+
                                                "&senderid="+senderId+"&name="+URLEncoder.encode(txtAccountHolderName.getText().toString(),"UTF-8")+
                                                "&account="+accNo.getText().toString()+"&ifsc="+ifscCode.getText().toString()+
                                                "&bankcode="+selectedBankCode+"&beneficiaryid="+receiverId+"&amount="+amount.getText().toString());
                                        Log.e("response", url+"&otpverified="+otp);
                                        intent.putExtra("otpverified",otp);
                                        startActivity(intent);
                                        finish();
                                    }
                                    if (!otp.equals("1")){
                                        new AlertDialog.Builder(TranBalToBankActivity.this)
                                            .setCancelable(false)
                                            .setMessage(jsonObject.getString("message"))
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    finish();
                                                    AppHelper.LaunchActivity(TranBalToBankActivity.this,PassBookActivity.class);
                                                }
                                            })
//                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    dialog.dismiss();
//                                                }
//                                            }
//                                            )
                                        .create().show();
                                    }

                                }catch (Exception ignored){

                                }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MixCartApplication.getInstance().addToRequestQueue(request);*/
            checkLimit(amount.getText().toString());
        }
    }

    private boolean valid(String accountHolderName, String accountNumber, String ifscCode, String selectedBankCode, String amount) {
        if (accountHolderName.isEmpty()) {
            Toast.makeText(this, "Please Enter A/c Holder's Name ", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (accountNumber.isEmpty()) {
            Toast.makeText(this, "Please Enter A/c Number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (ifscCode.isEmpty()) {
            Toast.makeText(this, "Please Enter IFSC Code", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedBankCode.equals("")) {
            Toast.makeText(this, "Please Select Bank Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (amount == "" || amount.isEmpty() || amount == null) {
            Toast.makeText(this, "Please Enter Amount", Toast.LENGTH_SHORT).show();
            return false;
        }

            /*if (Integer.parseInt(amount)>20000){
                Toast.makeText(this, "Please Enter Amount Below ("+getResources().getString(R.string.currency)+")20000", Toast.LENGTH_SHORT).show();
                return false;
            }*/


        return true;
    }

    public void checkLimit(final String finalmoney) {
        String url = AppConstants.LIMIT_CHECK + userPref.getUserId();

        AppHelper.showDialog(TranBalToBankActivity.this, "Checking transaction limit. Please Wait...");
        StringRequest str = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                AppHelper.logout(TranBalToBankActivity.this, s);
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
                        Double atomwalletremainingamountInt = 0d;
                        if (atomwalletremainingamount != null) {
                            atomwalletremainingamountInt =  Double.parseDouble(remainingwalletamount);
                        }
                        if (Double.parseDouble(finalmoney) <= atomwalletremainingamountInt) {
                            submitPayment();
                        } else {
                            new AlertDialog.Builder(TranBalToBankActivity.this)
                                    .setMessage("Available limit is " + atomwalletremainingamountInt + " Please re enter amount")
                                    .setPositiveButton("close", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            if (usertype.equals("NonkycUser")) {
                                                showKYCAlert();
                                            }
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
                volleyError.fillInStackTrace();
            }
        });

        str.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MixCartApplication.getInstance().addToRequestQueue(str);

    }

    public void submitPayment() {
        StringRequest request = null;
        try {
            final String url = AppConstants.RECEIVEING_REGISTER + userPref.getUserId() +
                    "&senderid=" + senderId + "&name=" + URLEncoder.encode(txtAccountHolderName.getText().toString(), "UTF-8") +
                    "&account=" + accNo.getText().toString() + "&ifsc=" + ifscCode.getText().toString() +
                    "&bankcode=" + selectedBankCode + "&beneficiaryid=" + receiverId + "&amount=" + amount.getText().toString();
            Log.e("response", url + "&otpverified=" + otpVerified);
            textPayToBank.setEnabled(false);

            AppHelper.showDialog(TranBalToBankActivity.this, "Loading Please Wait...", false);
            request = new StringRequest(url + "&otpverified=" + otpVerified,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            AppHelper.logout(TranBalToBankActivity.this, response);
                            AppHelper.hideDialog();
                            Log.e("response", response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                String otp = jsonObject.optString("OTP");
                                if (otp.equals("1")) {
                                    JSONObject resultObject = jsonObject.optJSONObject("result");
                                    receiverId = resultObject.optString("beneficiaryid");
                                    Intent intent = new Intent(TranBalToBankActivity.this, PaymentResponseActivity.class);
                                    intent.putExtra("benefId", receiverId);
                                    intent.putExtra("senderId", senderId);
                                    intent.putExtra("moneyurl", AppConstants.RECEIVEING_REGISTER + userPref.getUserId() +
                                            "&senderid=" + senderId + "&name=" + URLEncoder.encode(txtAccountHolderName.getText().toString(), "UTF-8") +
                                            "&account=" + accNo.getText().toString() + "&ifsc=" + ifscCode.getText().toString() +
                                            "&bankcode=" + selectedBankCode + "&beneficiaryid=" + receiverId + "&amount=" + amount.getText().toString());
                                    Log.e("response", url + "&otpverified=" + otp);
                                    intent.putExtra("otpverified", otp);
                                    startActivity(intent);
                                    finish();
                                }
                                if (!otp.equals("1")) {
                                    new AlertDialog.Builder(TranBalToBankActivity.this)
                                            .setCancelable(false)
                                            .setMessage(jsonObject.getString("message"))
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    finish();
                                                    AppHelper.LaunchActivity(TranBalToBankActivity.this, PassBookActivity.class);
                                                }
                                            })
//                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    dialog.dismiss();
//                                                }
//                                            }
//                                            )
                                            .create().show();
                                }

                            } catch (Exception ignored) {

                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MixCartApplication.getInstance().addToRequestQueue(request);
    }

    public void showKYCAlert() {
        new AlertDialog.Builder(TranBalToBankActivity.this)
                .setMessage("Verify KYC to increase limit")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivity(new Intent(TranBalToBankActivity.this, KYCUploadActivity.class));
                    }
                }).show();
    }

}
