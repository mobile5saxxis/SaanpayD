package com.saxxis.saanpaydestributor.fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.activities.payment.LocationPickActivity;
import com.saxxis.saanpaydestributor.app.AppConstants;
import com.saxxis.saanpaydestributor.app.MixCartApplication;
import com.saxxis.saanpaydestributor.app.UserPref;
import com.saxxis.saanpaydestributor.helpers.AppHelper;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.saxxis.saanpaydestributor.activities.payment.LocationPickActivity.MY_PERMISSIONS_REQUEST_LOCATION;


/**
 * A simple {@link Fragment} subclass.
 */
public class CashPickUpFragment extends Fragment {


//    @BindView(R.id.tinplay_amount)
//    TextInputLayout textLayAmount;
//
//
//    @BindView(R.id.add_money_et_money)
//    TextInputEditText etMoney;
//
//    @BindView(R.id.txt_walletbalance)
//    TextView txtWalletBalance;
//
//    @BindView(R.id.txt_1000)
//    TextView txt1000;
//
//    @BindView(R.id.txt_500)
//    TextView txt500;
//
//    @BindView(R.id.txt_100)
//    TextView txt100;




    @BindView(R.id.deposit_name)
    TextInputEditText txtDepositName;
    @BindView(R.id.deposit_mobileno)
    TextInputEditText txtDepositMobileNo;
    @BindView(R.id.deposit_emailadd)
    TextInputEditText txtDeposiEmails;
    @BindView(R.id.deposit_amount)
    TextInputEditText txtDepositAmount;
    @BindView(R.id.deposit_houseno)
    TextInputEditText txtDepositHouseNo;
    @BindView(R.id.deposit_landmark_address)
    TextInputEditText txtLandMarkAddress;

    @BindView(R.id.deposit_addlocation)
    TextView addLocationtextView;

    @BindView(R.id.picksubmit)
    TextView pickTextSubmit;



    String latde="";
    String longtde="";


//    @BindView(R.id.add_money_btn)
//    TextView btnAddMoney;
//
//    String finalmoney="0";

    private UserPref mUser;

    // Mandatory
    private static String HOST_NAME = "";

    ArrayList<HashMap<String, String>> custom_post_parameters;

    private static final int ACC_ID = 24140; // Provided by EBS

    private static final String SECRET_KEY = "933ce832f7216ed360f9ad0a3e1f7c67";// Provided by EBS

    public CashPickUpFragment() {
        // Required empty public constructor
    }

    public static CashPickUpFragment newInstance(){
        return new CashPickUpFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cash_pick_up, container, false);
        ButterKnife.bind(this,view);
        mUser = new UserPref(getActivity());
//        String walletvalue=getResources().getString(R.string.walletbalanceout)+ AppHelper.getWalletAmount(getActivity());
//        txtWalletBalance.setText(walletvalue);
//        refreshWallet();
        return view;
    }


    @OnClick(R.id.deposit_addlocation)
    void onStartSearchLocation(){

        if(!isLocationServiceEnabled()){
            new AlertDialog.Builder(getActivity()).setMessage("Please Switch on your Location Services").setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            }).setNegativeButton("Deny",null).show();
        }

        if (isLocationServiceEnabled()){
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkLocationPermission();
            }

            if (checkLocationPermission()){
                startActivityForResult(new Intent(getActivity(), LocationPickActivity.class),111);
            }

        }

    }


    @OnClick(R.id.picksubmit)
    void submitPickDetails(){

        String name=txtDepositName.getText().toString();
        String mobile=txtDepositMobileNo.getText().toString();
        String email=txtDeposiEmails.getText().toString();
        String amount=txtDepositAmount.getText().toString();
        String address=addLocationtextView.getText().toString();
        String landmark=txtLandMarkAddress.getText().toString();
        String houseno=txtDepositHouseNo.getText().toString();

        if (isValidAllFields(name,mobile,email,amount,address,landmark,houseno)){

            String url= null;
            try {
                url = AppConstants.DEPOSIT_PICKUP
                        +"&userid="+mUser.getUserId()
                        +"&name="+URLEncoder.encode(name,"utf-8")
                        +"&mobile="+URLEncoder.encode(mobile,"utf-8")
                        +"&email="+URLEncoder.encode(email,"utf-8")
                        +"&amount="+URLEncoder.encode(amount,"utf-8")
                        +"&address="+URLEncoder.encode(address,"utf-8")
                        +"&landmark="+URLEncoder.encode(landmark,"utf-8")
                        +"&housenumber="+URLEncoder.encode(houseno,"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            AppHelper.showDialog(getActivity(),"Loading Please Wait...");
            StringRequest request = new StringRequest(Request.Method.GET,url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            AppHelper.logout(getActivity(),response);
                            AppHelper.hideDialog();
                            Log.e("response",response);
                            try{
                                JSONObject jsonObject = new JSONObject(response);
                                String status = jsonObject.getString("status");
                                if(status.equals("ok")){
                                   new AlertDialog.Builder(getActivity())
                                           .setTitle("ThankYou For Your Request!")
                                            .setMessage("our field officer will contact you with in 24 hours")
                                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    getActivity().finish();
                                                }
                                            })
                                            .create().show();
                                }
                                if(status.equals("ko")){
                                    new AlertDialog.Builder(getActivity())
                                            .setMessage(jsonObject.getString("message"))
                                            .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                      dialog.dismiss();
                                                }
                                            })
                                            .create().show();
                                }
                            }catch (Exception ignored){
                                AppHelper.hideDialog();
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

    }

    private boolean isValidAllFields(String name, String mobile, String email, String amount, String address, String landmark, String houseno) {
            if (name.isEmpty()){
                txtDepositName.setError("Please Enter Name");
                return false;
            }

            if (!name.isEmpty()){
                txtDepositName.setError("");
            }

            if (mobile.length()<10){
                txtDepositMobileNo.setError("Please enter 10 digit mobile number");
                return false;
            }
            if (mobile.length()==10){
                txtDepositMobileNo.setError(null);
            }

            if (TextUtils.isEmpty(email)) {
                txtDeposiEmails.setError("Please Enter Email");
                return false;
            } else {
                boolean emailbug=android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
                String emailValid=emailbug?"":"Please Enter Valid Email";
                txtDeposiEmails.setError(emailValid);
                if (!emailbug){
                    return false;
                }
            }

            if (amount.isEmpty()){
                txtDepositAmount.setError("Please Enter Amount");
                return false;
            }
            if (!amount.isEmpty()){
                txtDepositAmount.setError("");
            }

       if (address.isEmpty()){
            Toast.makeText(getActivity(),"PLease Enter Address",Toast.LENGTH_LONG).show();
            return false;
        }

        if (landmark.isEmpty()){
            txtLandMarkAddress.setError("Please Enter Land Mark");
            return false;
        }
        if (!landmark.isEmpty()){
            txtLandMarkAddress.setError("");
        }

        if (houseno.isEmpty()){
            txtDepositHouseNo.setError("Please Enter House Number");
            return false;
        }

        if (!houseno.isEmpty()){
            txtDepositHouseNo.setError("");
        }


        return true;
    }


    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    public boolean isLocationServiceEnabled(){
        LocationManager locationManager  = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);;
        boolean gps_enabled= false;
        boolean network_enabled = false;

        try{
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch(Exception ex){
            //do nothing...
        }

        try{
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch(Exception ex){
            //do nothing...
        }

        return gps_enabled || network_enabled;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==111){
            if (resultCode==555){
                Bundle extras = data.getExtras();
                addLocationtextView.setText(extras.getString("address"));
//                latde=extras.getString("locationlatitude");
//                longtde=extras.getString("locationlongitude");
//                addLocationtextView.setText(
//                        "Latitude :"+extras.getString("locationlatitude")+
//                        "\n Longitude :"+extras.getString("locationlongitude"));
            }

        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            getActivity().onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //    @OnClick(R.id.txt_1000)
//    void add1000(){
//        etMoney.setText("1000");
//        finalmoney="1000";
//    }
//
//    @OnClick(R.id.txt_500)
//    void add500(){
//        etMoney.setText("500");
//        finalmoney="500";
//    }
//
//    @OnClick(R.id.txt_100)
//    void add100(){
//        etMoney.setText("100");
//        finalmoney="100";
//    }

//    @OnClick(R.id.add_money_btn)
//    void addmoney(){
//
//        String amount = etMoney.getText().toString().trim();
//
//        if(!finalmoney.equals("0")){
//            rediectToPaymentGateway(getActivity(),finalmoney);
//        }else{
//            if(amount.isEmpty()) {
//                new AlertDialog.Builder(getActivity())
//                        .setMessage("Enter Amount")
//                        .setPositiveButton("close", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        }).show();
//            }else{
//                //  rediectToPaymentGateway(AddMoneyActivity.this,amount);
//            }
//        }
//    }

//    private void refreshWallet() {
////        mProgress.setVisibility(View.VISIBLE);
//        Log.e("response", AppConstants.WALLET_URL+"&userid="+mUser.getUserId());
//        StringRequest request=new StringRequest(Request.Method.GET, AppConstants.WALLET_URL+
//                "&userid="+mUser.getUserId(),
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.e("response",response);
//
//                        try{
//                            JSONObject jsonObject = new JSONObject(response);
//                            String status = jsonObject.getString("status");
////                    mProgress.setVisibility(View.GONE);
//                            if (status.equals("ok")){
//                                JSONObject dataobject=jsonObject.getJSONObject("data");
//                                mUser.setWalletAmount(Integer.parseInt(dataobject.getString("walletamount")));
//                                txtWalletBalance.setText(getResources().getString(R.string.walletbalanceout)+dataobject.getString("walletamount"));
//                            }
//
//                            if(status.equals("ko")) {
//                                new AlertDialog.Builder(getActivity())
//                                        .setMessage("User Session Timed Out...Login Again")
//                                        .setPositiveButton("Login", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                mUser.logoutUser();
//                                                Intent intent = new Intent(getActivity(), LoginActivity.class);
//                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
//                                                startActivity(intent);
//                                                Toast.makeText(getActivity(),"Logout Successfull",Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//                            }
//                        }catch (Exception ignored){
//
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        });
//
//        MixCartApplication.getInstance().addToRequestQueue(request);
//    }


//    private void rediectToPaymentGateway(Context addMoneyActivity, final String finalmoney) {
//
//        String url = AppConstants.MONEY_ORDERID_REQ +"&userid="+mUser.getUserId()+
//                "&amount="+finalmoney+"&desc=RequestToAddMoney";
//
//        StringRequest str = new StringRequest(url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String s) {
//                try{
//                    JSONObject resp = new JSONObject(s);
//                    String status = resp.getString("status");
//                    if(status.equals("ok")){
//                        String orderId = resp.getString("message");
////                        callEbsKit((Activity)getActivity(),orderId,finalmoney);
//                    }
//                }catch (Exception e){
//
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//
//            }
//        });
//        MixCartApplication.getInstance().addToRequestQueue(str);
//    }



}
