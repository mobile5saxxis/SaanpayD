package com.saxxis.saanpaydestributor.fragments.imps;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.saxxis.saanpaydestributor.activities.payment.TranBalToBankActivity;
import com.saxxis.saanpaydestributor.app.AppConstants;
import com.saxxis.saanpaydestributor.app.MixCartApplication;
import com.saxxis.saanpaydestributor.app.UserPref;
import com.saxxis.saanpaydestributor.helpers.AppHelper;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class TransferBankFragment extends Fragment {


    public TransferBankFragment() {
        // Required empty public constructor
    }


    @BindView(R.id.availbalance)
    TextView availBalance;

    @BindView(R.id.trasfer)
    TextView txtTransfer;

    String senderId="";

    private UserPref userPref;

    public static TransferBankFragment newInstance(){
        return new TransferBankFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_transfer_bank, container, false);
        ButterKnife.bind(this,view);
        userPref=new UserPref(getActivity());
        String wallewtbalance=getResources().getString(R.string.walletbalanceout)+AppHelper.getWalletAmount(getActivity());
        availBalance.setText(wallewtbalance);

        refreshWallet();
        getSenderId();

        return view;
    }

    private void getSenderId() {
        Log.e("response",AppConstants.GET_SENDER_ID + userPref.getUserId());
        StringRequest stringRequest  = new StringRequest(AppConstants.GET_SENDER_ID + userPref.getUserId(),
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppHelper.logout(getActivity(),response);
                Log.e("response",response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString("status").equals("ok")){
                        senderId = jsonObject.getString("message");
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

    private void refreshWallet() {
        Log.e("response", AppConstants.WALLET_URL+"&userid="+userPref.getUserId());
        StringRequest request = new StringRequest(Request.Method.GET, AppConstants.WALLET_URL+
                "&userid="+userPref.getUserId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.logout(getActivity(),response);
                        Log.e("response",response);
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            if (status.equals("ok")){
                                JSONObject dataobject=jsonObject.getJSONObject("data");
                                userPref.setWalletAmount(Float.parseFloat(dataobject.getString("walletamount")));
                                availBalance.setText(getResources().getString(R.string.walletbalanceout)+dataobject.getString("walletamount"));
                            }

                            /*if(status.equals("ko")) {
                                new AlertDialog.Builder(getActivity())
                                        .setMessage("User Session Timed Out...Login Again")
                                        .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                userPref.logoutUser();
                                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                Toast.makeText(getActivity(),"Logout Successfull",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }*/
                        }catch (Exception ignored){

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                mProgress.setVisibility(View.GONE);
            }
        });
//        {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<>();
//                headers.put("Cookie",userPref.getSessionId());
//                return headers;
//            }
//        };

        MixCartApplication.getInstance().addToRequestQueue(request);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            super.getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.trasfer)
    void gotoTransferActivity(){

        if (!senderId.equals("")) {
            Intent intent = new Intent(getActivity(), TranBalToBankActivity.class);
            intent.putExtra("senderId", senderId);
            getActivity().startActivity(intent);
        }

        if(senderId.equals("")){
            Toast.makeText(getActivity(), "Please Switch On Internet or Register First", Toast.LENGTH_SHORT).show();
        }

    }

}
