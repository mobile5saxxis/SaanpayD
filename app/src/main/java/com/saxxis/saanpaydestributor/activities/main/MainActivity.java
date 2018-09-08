package com.saxxis.saanpaydestributor.activities.main;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.activities.OrdersActivity;
import com.saxxis.saanpaydestributor.activities.leftmenu.MoneyDepositActivity;
import com.saxxis.saanpaydestributor.activities.leftmenu.QRCodeActivity;
import com.saxxis.saanpaydestributor.activities.leftmenu.ReportsActivity;
import com.saxxis.saanpaydestributor.activities.leftmenu.WalletPayActivity;
import com.saxxis.saanpaydestributor.activities.payment.PassBookActivity;
import com.saxxis.saanpaydestributor.activities.recharge.DataCardActivity;
import com.saxxis.saanpaydestributor.activities.recharge.DthActivity;
import com.saxxis.saanpaydestributor.activities.recharge.MobileRechargePrepaidActivity;
import com.saxxis.saanpaydestributor.activities.reservation.BusesActivity;
import com.saxxis.saanpaydestributor.activities.reservation.HotelsActivity;
import com.saxxis.saanpaydestributor.activities.utlity.GasActivity;
import com.saxxis.saanpaydestributor.activities.utlity.InsuranceActivity;
import com.saxxis.saanpaydestributor.adapters.TabAdapter;
import com.saxxis.saanpaydestributor.app.AppConstants;
import com.saxxis.saanpaydestributor.app.MixCartApplication;
import com.saxxis.saanpaydestributor.app.UserPref;
import com.saxxis.saanpaydestributor.helpers.AppHelper;
import com.saxxis.saanpaydestributor.interfaces.NetworkListener;
import com.saxxis.saanpaydestributor.interfaces.SmsListener;
import com.saxxis.saanpaydestributor.receivers.IncomingSms;
import com.saxxis.saanpaydestributor.utils.CustomViewPager;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.saxxis.saanpaydestributor.R.id.nav_data_card;
import static com.saxxis.saanpaydestributor.R.id.nav_dth;
import static com.saxxis.saanpaydestributor.R.id.nav_reports;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NetworkListener {

    private static final int PERMISSION_REQUEST_ID = 100;
    private static final int REQUEST_CONTACTS = 1;
    private static final String TAG = "";

    private final String BROADCAST_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    com.saxxis.saanpaydestributor.receivers.IncomingSms IncomingSms;
    SmsListener smsListener;
    Boolean isPermissionGranted = false;
    @BindView(R.id.cl_main_layout)
    CoordinatorLayout mView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    CustomViewPager viewPager;

//    @BindView(R.id.tab_layout_top)
//    TabLayout mTabLayoutTop;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    private IntentFilter intentFilter;
    private UserPref mUser;

    private CircleImageView userImage;
    private TextView title;
    private static String[] PERMISSIONS_CONTACT = {Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IncomingSms = new IncomingSms();
        intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_ACTION);
        requestRuntimePermissions(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS);

        ButterKnife.bind(this);

        mUser = new UserPref(this);
        setSupportActionBar(mToolbar);

        initializeTabs();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
        //mToolbar.setLogo(R.drawable.logo);
        setTitle("");
        //mToolbar.setNavigationIcon(R.drawable.menu);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout = navigationView.getHeaderView(0);
        updateHeader(headerLayout);

        if (!mUser.isLoggedIn()) {
            // user logged out
            navigationView.inflateMenu(R.menu.left_anyuser_navigation);
        } else {

            // user logged innn
            if (mUser.getKeyUserType().equalsIgnoreCase("D")) {
                navigationView.inflateMenu(R.menu.activity_main_drawer_reports);
            } else {
                navigationView.inflateMenu(R.menu.activity_main_drawer);
            }

            navigationView.inflateHeaderView(R.layout.nav_header_main);

            fetchUserDetails();
            refreshWallet();

            View headerView = navigationView.getHeaderView(0).getRootView();
            TextView txtname = (TextView) headerView.findViewById(R.id.header_title);
            ImageView image = (ImageView) headerView.findViewById(R.id.imageView);
            txtname.setText(mUser.getName());
        }
//        if(!PatternLockUtils.hasPattern(this)){
//           PatternLockUtils.setPatternByUser(this);
//        }
    }


    public void showContacts(View v) {
        Log.i(TAG, "Show contacts button pressed. Checking permissions.");

        // Verify that all required contact permissions have been granted.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            // Contacts permissions have not been granted.
            Log.i(TAG, "Contact permissions has NOT been granted. Requesting permissions.");
            requestContactsPermissions();

        } else {

            // Contact permissions have been granted. Show the contacts fragment.
            Log.i(TAG,
                    "Contact permissions have already been granted. Displaying contact details.");

        }
    }



    /**
     * Requests the Contacts permissions.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestContactsPermissions() {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_CONTACTS)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_CONTACTS)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            Log.i(TAG,
                    "Displaying contacts permission rationale to provide additional context.");

            // Display a SnackBar with an explanation and a button to trigger the request.

        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(this, PERMISSIONS_CONTACT, REQUEST_CONTACTS);
        }
        // END_INCLUDE(contacts_permission_request)
    }



    private void refreshWallet() {
//        mProgress.setVisibility(View.VISIBLE);
        Log.e("response", AppConstants.WALLET_URL + "&userid=" + mUser.getUserId());
        StringRequest request = new StringRequest(Request.Method.GET, AppConstants.WALLET_URL +
                "&userid=" + mUser.getUserId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
//                              mProgress.setVisibility(View.GONE);
                            if (status.equals("ok")) {
                                JSONObject dataobject = jsonObject.getJSONObject("data");
                                mUser.setWalletAmount(Float.parseFloat(dataobject.getString("walletamount")));

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

    private void fetchUserDetails() {

        StringRequest request = new StringRequest(Request.Method.GET, AppConstants.PROFILE_URL + mUser.getUserId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.logout(MainActivity.this, response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            if (status.equals("ok")) {
//                        JSONArray profile1 = jsonObject.getJSONArray("data");
//                        JSONArray profileImage = jsonObject.getJSONArray("Image");
                                JSONObject profile = jsonObject.getJSONObject("data");

//                        if(profileImage.length()!=0) {
//                            JSONObject imagePath = profileImage.getJSONObject(0);
//                            ipath = AppConstants.SERVER_URL+ imagePath.getString("measure_image");
////                            ipath = AppConstants.SERVER_URL+"images/userimages/"+ imagePath.getString("measure_image");
//                        }
                                String name = profile.getString("name");
                                String email = profile.getString("email");
                                String mn = profile.getString("mobile");
                                String dob = profile.getString("dob");
                                String pancard = profile.getString("pancard");
                                String gender = profile.getString("gender");
                                String address = profile.getString("address");
                                String emailverify = profile.getString("emailverification");
                                String mnverify = profile.getString("mobileverification");
                                String referCode = profile.getString("refercode");
                                String uniquecode = profile.getString("uniquecode");
                                String ipath = profile.getString("profileimagepath") + profile.getString("profileimage");

                                mUser.setName(name);
                                mUser.setEmail(email);
                                mUser.setMobileNumber(mn);
                                mUser.setKeyGender(gender);
                                mUser.setImagePath(ipath);
                                mUser.setKeyAddress(address);
                                mUser.setPan(pancard);
                                mUser.setDateOfBirth(dob);

                                title.setText(getResources().getString(R.string.welcome, name));
//                        if(!mUser.getImagePath().equals("empty")){
                                Glide.with(MainActivity.this)
                                        .load(Uri.parse(ipath))
                                        .error(R.mipmap.ic_launcher)
                                        .into(userImage);
//                        }

                            }
                    /*else if(status.equals("ko")) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setMessage("User Session Timed Out...Login Again")
                                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mUser.logoutUser();
                                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        Toast.makeText(MainActivity.this,"Logout Successfull",Toast.LENGTH_SHORT).show();
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

    private void updateHeader(View headerLayout) {
//        title = (TextView)headerLayout.findViewById(R.id.header_title);
//        userImage = (CircleImageView) headerLayout.findViewById(R.id.imageView);
    }

    private void initializeTabs() {
        TabAdapter pagerAdapter = new TabAdapter(getSupportFragmentManager(), this);
        viewPager.setPagingEnabled(false);
        viewPager.setAdapter(pagerAdapter);
        mTabLayout.setupWithViewPager(viewPager);
//        mTabLayoutTop.setVisibility(View.VISIBLE);

        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            tab.setCustomView(pagerAdapter.getTabView(i));
        }

        mTabLayout.getTabAt(0).getCustomView().setSelected(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MixCartApplication.getInstance().setConnectivityListener(this);
//        initializeTabs();

    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_orders) {
//            AppHelper.LaunchActivity(MainActivity.this,OrdersActivity.class);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav_home) {
//
//        } else
//         if (id == R.id.nav_my_account) {
//             Intent intent=new Intent(MainActivity.this,ProfileActivity.class);
//             startActivity(intent);
//        }else
        if (id == R.id.nav_pay) {

            if (mUser.isLoggedIn()) {
                AppHelper.LaunchActivity(MainActivity.this, WalletPayActivity.class);
            } else {
                AppHelper.LaunchActivity(MainActivity.this, LoginActivity.class);
            }
        } else if (id == R.id.nav_mobile_recharge) {

            if (mUser.isLoggedIn()) {
                startActivity(new Intent(MainActivity.this, MobileRechargePrepaidActivity.class).putExtra("checkoptiontype", "1"));
            } else {
                AppHelper.LaunchActivity(MainActivity.this, LoginActivity.class);
            }

        } else if (id == nav_data_card) {
            AppHelper.LaunchActivity(MainActivity.this, DataCardActivity.class);
        } else if (id == nav_dth) {
            AppHelper.LaunchActivity(MainActivity.this, DthActivity.class);
        } else if (id == R.id.nav_gas) {
            AppHelper.LaunchActivity(MainActivity.this, GasActivity.class);
        } else if (id == R.id.nav_insurance) {
            AppHelper.LaunchActivity(MainActivity.this, InsuranceActivity.class);
        } else if (id == R.id.nav_bus) {
            AppHelper.LaunchActivity(MainActivity.this, BusesActivity.class);
        } else if (id == R.id.nav_hotel) {
            AppHelper.LaunchActivity(MainActivity.this, HotelsActivity.class);
        } else if (id == R.id.nav_scanandpay) {
            AppHelper.LaunchActivity(MainActivity.this, QRCodeActivity.class);
        } else if (id == R.id.nav_wallet) {
            AppHelper.LaunchActivity(MainActivity.this, PassBookActivity.class);
        }
//        else if (id == R.id.nav_my_profile) {
//            AppHelper.LaunchActivity(MainActivity.this,ProfileActivity.class);
//
//        } else  if (id == R.id.nav_wallet) {
//
//            AppHelper.LaunchActivity(MainActivity.this,WalletActivity.class);
//        }
        else if (id == R.id.nav_my_orders) {
            AppHelper.LaunchActivity(MainActivity.this, OrdersActivity.class);
        }
//          else if (id == R.id.nav_pay_for_orders) {

//        } else if (id == R.id.nav_saved_cards) {
//
//
//        }else if (id == R.id.nav_my_qr_code) {
//
////            AppHelper.LaunchActivity(MainActivity.this,SetPatternActivity.class);
//
//        } else if (id == R.id.nav_refer_earn) {
//
//            AppHelper.LaunchActivity(MainActivity.this,ReferralActivity.class);

//        } else if (id == R.id.nav_aboutus) {
//
//            AppHelper.LaunchActivity(MainActivity.this,AboutUsActivity.class);
//        }
//        else
        if (id == R.id.nav_shareapp) {

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/developer?id=Saanvi+E+Payments+P+Ltd");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);

        } else if (id == R.id.nav_rateapp) {
            launchMarket();
//        }
//        else if (id == R.id.nav_hlpcntctus) {
//            AppHelper.LaunchActivity(MainActivity.this,HelpContactActivity.class);
        }
//        else if (id == R.id.nav_logout) {
//            mUser.logoutUser();
//            MainActivity.this.finish();
//            logoutUser();
//            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//            Toast.makeText(MainActivity.this,"Logout Successfull",Toast.LENGTH_SHORT).show();
//            PatternLockUtils.clearPattern(this);
//        }
//        else if (id==R.id.nav_login){
//                AppHelper.LaunchActivity(MainActivity.this,LoginActivity.class);
//        }

        if (id == nav_reports) {
            AppHelper.LaunchActivity(MainActivity.this, ReportsActivity.class);
        }
//        if (id == R.id.nav_moneydeposit) {
//            AppHelper.LaunchActivity(MainActivity.this, MoneyDepositActivity.class);
//        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, " Sorry, Not able to open!", Toast.LENGTH_SHORT).show();
        }
    }

    private void logoutUser() {
        StringRequest request = new StringRequest(Request.Method.GET, AppConstants.LOGOUT_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
            AppHelper.Snackbar(this, mView, getString(R.string.connection_is_not_available), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
        } else if (isConnecting && isConnected) {
            AppHelper.Snackbar(this, mView, getString(R.string.connection_is_available), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
            if (mUser.isLoggedIn()) {
                fetchUserDetails();
            }
        } else {
            AppHelper.Snackbar(this, mView, getString(R.string.waiting_for_network), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);
        }
    }

    private void requestRuntimePermissions(String... permissions) {
        for (String perm : permissions) {

            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{perm}, PERMISSION_REQUEST_ID);

            } else {
                isPermissionGranted = true;
            }
        }
    }

}
