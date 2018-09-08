package com.saxxis.saanpaydestributor.activities.leftmenu;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.activities.payment.AddMoneyActivity;
import com.saxxis.saanpaydestributor.app.AppConstants;
import com.saxxis.saanpaydestributor.app.FetchWalletAmount;
import com.saxxis.saanpaydestributor.app.MixCartApplication;
import com.saxxis.saanpaydestributor.app.UserPref;
import com.saxxis.saanpaydestributor.helpers.AppHelper;
import com.saxxis.saanpaydestributor.interfaces.NetworkListener;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WalletActivity extends AppCompatActivity implements NetworkListener {

    @BindView(R.id.loading_wallet)
    ProgressBar mProgress;

    @BindView(R.id.wallet_balance)
    TextView mBalance;

    @BindView(R.id.cl_wallet)
    CoordinatorLayout clLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private UserPref mUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        FetchWalletAmount.getAmount(this);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitle("My Wallet");
        mToolbar.setNavigationIcon(R.drawable.arrow);
        mUser = new UserPref(this);
        mBalance.setText(getResources().getString(R.string.Rs,String.valueOf(mUser.getWalletAmount())));
    }

    @OnClick(R.id.wallet_add_money)
    void redirectPayment(){
        AppHelper.LaunchActivity(WalletActivity.this, AddMoneyActivity.class);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.refresh) {
            refreshWallet();
            return true;
        }if (item.getItemId()==android.R.id.home){
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshWallet() {
        mProgress.setVisibility(View.VISIBLE);
        StringRequest request=new StringRequest(Request.Method.GET, AppConstants.WALLET_URL+"&userid="+mUser.getUserId(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppHelper.logout(WalletActivity.this,response);
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    mProgress.setVisibility(View.GONE);
                    if (status.equals("ok")){
                     mBalance.setText(jsonObject.getString("walletamount"));
                    }
                    /*else if(status.equals("ko")) {
                        new AlertDialog.Builder(WalletActivity.this)
                                .setMessage("User Session Timed Out...Login Again")
                                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mUser.logoutUser();
                                        Intent intent = new Intent(WalletActivity.this, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        Toast.makeText(WalletActivity.this,"Logout Successfull",Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }*/
                }catch (Exception ignored){

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgress.setVisibility(View.GONE);
            }
        });
//        {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<>();
//                headers.put("Cookie",mUser.getSessionId());
//                return headers;
//            }
//        };
        MixCartApplication.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnecting, boolean isConnected) {
        if (!isConnecting && !isConnected) {
            AppHelper.Snackbar(this, clLayout, getString(R.string.connection_is_not_available), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
        } else if (isConnecting && isConnected) {
            AppHelper.Snackbar(this, clLayout, getString(R.string.connection_is_available), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
        } else {
            AppHelper.Snackbar(this, clLayout, getString(R.string.waiting_for_network), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);

        }
    }
}
