package com.saxxis.saanpaydestributor.activities.payment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.saxxis.saanpaydestributor.app.AppConstants;
import com.saxxis.saanpaydestributor.app.MixCartApplication;
import com.saxxis.saanpaydestributor.app.UserPref;
import com.saxxis.saanpaydestributor.fragments.BankTransfers;
import com.saxxis.saanpaydestributor.fragments.TranxAddedFragment;
import com.saxxis.saanpaydestributor.fragments.TranxListFragment;
import com.saxxis.saanpaydestributor.fragments.TranxListReceiveFragment;
import com.saxxis.saanpaydestributor.fragments.TranxPaidFragment;
import com.saxxis.saanpaydestributor.fragments.TranxRechargeFragment;
import com.saxxis.saanpaydestributor.fragments.TranxReportsFragment;
import com.saxxis.saanpaydestributor.helpers.AppHelper;
import com.saxxis.saanpaydestributor.interfaces.NetworkListener;
import com.saxxis.saanpaydestributor.models.WalletTranxs;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PassBookActivity extends AppCompatActivity implements NetworkListener {


    @BindView(R.id.passbooktoolbar)
    Toolbar toolbar;

    @BindView(R.id.clLayout)
    CoordinatorLayout clLayout;

    @BindView(R.id.balance)
    TextView balanceText;

    @BindView(R.id.walletprogress)
    ProgressBar progressBar;

    @BindView(R.id.tranx_tabs)
    TabLayout mTabLayout;

    @BindView(R.id.tranx_pager)
    ViewPager tranxPager;

    UserPref userPref;


    ArrayList<WalletTranxs> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_book);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        userPref = new UserPref(PassBookActivity.this);
        mData = new ArrayList<>();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.arrow);
        toolbar.setTitle("Passbook");
        balanceText.setText(getResources().getString(R.string.currency)+" "+userPref.getWalletAmount());

        setupViewPager(tranxPager);
        mTabLayout.setupWithViewPager(tranxPager);

        refreshWallet();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(TranxListFragment.newInstance(), "All");//0
        adapter.addFragment(TranxPaidFragment.newInstance(), "Paid");//1
        adapter.addFragment(TranxListReceiveFragment.newInstance(), "Received");//2
        adapter.addFragment(TranxAddedFragment.newInstance(), "Added");//3
        adapter.addFragment(TranxRechargeFragment.newInstance(), "Recharges");//4
        if(userPref.getKeyUserType().equals("D")){
            adapter.addFragment(TranxReportsFragment.newInstance(), "Reports");//5
        }

        adapter.addFragment(BankTransfers.newInstance(), "Bank Transfers");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==android.R.id.home){
            super.onBackPressed();
            return true;
        }

        if (id == R.id.refresh) {
            refreshWallet();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh_menu,menu);
        return true;
    }

    private void refreshWallet() {
//        mProgress.setVisibility(View.VISIBLE);
        Log.e("response",AppConstants.WALLET_URL+"&userid="+userPref.getUserId());
        progressBar.setVisibility(View.VISIBLE);
        StringRequest request=new StringRequest(Request.Method.GET, AppConstants.WALLET_URL+"&userid="+userPref.getUserId(),
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppHelper.logout(PassBookActivity.this,response);
                Log.e("response",response);

                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
//                    mProgress.setVisibility(View.GONE);
                    if (status.equals("ok")){
                        progressBar.setVisibility(View.GONE);
                        JSONObject dataobject=jsonObject.getJSONObject("data");
                        userPref.setWalletAmount(Float.parseFloat(dataobject.getString("walletamount")));
                        balanceText.setText(getResources().getString(R.string.currency)+" "+dataobject.getString("walletamount"));
                    }

                    /*if(status.equals("ko")) {
                        new AlertDialog.Builder(PassBookActivity.this)
                                .setMessage("User Session Timed Out...Login Again")
                                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        userPref.logoutUser();
                                        Intent intent = new Intent(PassBookActivity.this, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        Toast.makeText(PassBookActivity.this,"Logout Successfull",Toast.LENGTH_SHORT).show();
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
