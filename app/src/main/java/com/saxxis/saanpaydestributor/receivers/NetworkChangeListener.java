package com.saxxis.saanpaydestributor.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

import com.saxxis.saanpaydestributor.interfaces.NetworkListener;

/**
 * Created by saxxis25 on 3/25/2017.
 */

public class NetworkChangeListener extends BroadcastReceiver {

    private boolean is_Connected = false;
    public static NetworkListener networkListener;


    public NetworkChangeListener() {
        super();
    }

    @Override
    public void onReceive(final Context mContext, Intent intent) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isNetworkAvailable(mContext);
            }
        },5000);
    }

    private void isNetworkAvailable(Context mContext) {

        ConnectivityManager cm = (ConnectivityManager) mContext.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean is_Connecting = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (networkListener != null) {
            networkListener.onNetworkConnectionChanged(is_Connecting, is_Connected);
        }

    }
}
