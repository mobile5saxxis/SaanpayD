package com.saxxis.saanpaydestributor.activities.main;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.activities.AbstractPermissionActivity;
import com.saxxis.saanpaydestributor.app.MixCartApplication;
import com.saxxis.saanpaydestributor.app.UserPref;
import com.saxxis.saanpaydestributor.helpers.AppHelper;
import com.saxxis.saanpaydestributor.interfaces.NetworkListener;

import butterknife.ButterKnife;

/**
 * Created by saxxis25 on 3/25/2017.
 */

public class SplashActivity extends AbstractPermissionActivity implements NetworkListener {

//    @BindView(R.id.splash_layout)
//    View mView;

//    @BindView(R.id.splash_wv)
//    WebView wv;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS
    };
    private UserPref mUser;

    @Override
    protected String[] getDesiredPermissions() {
        return PERMISSIONS;
    }

    @Override
    protected void onPermissionDenied() {
        finish();
        Toast.makeText(this, "App cannot open if any permission is denied", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onReady(Bundle savedInstanceState) {
        setContentView(R.layout.splash);
        ButterKnife.bind(this);
        mUser = new UserPref(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (mUser.isLoggedIn()) {
                    AppHelper.LaunchActivity(SplashActivity.this, MainActivity.class);
                    finish();
                } else {
                    AppHelper.LaunchActivity(SplashActivity.this, LoginActivity.class);
                    //AppHelper.LaunchActivity(SplashActivity.this, LoginMainActivity.class);
                    finish();
                }
            }
        }, 800);
    }


    @Override
    protected void onResume() {
        super.onResume();
        MixCartApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnecting, boolean isConnected) {

//        if (!isConnecting && !isConnected) {
//            AppHelper.Snackbar(this, mView, getString(R.string.connection_is_not_available), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
//        } else if (isConnecting && isConnected) {
//            AppHelper.Snackbar(this, mView, getString(R.string.connection_is_available), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
//        } else {
//            AppHelper.Snackbar(this, mView, getString(R.string.waiting_for_network), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }
}
