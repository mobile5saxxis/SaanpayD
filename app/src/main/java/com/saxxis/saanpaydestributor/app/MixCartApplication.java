package com.saxxis.saanpaydestributor.app;

import android.app.Activity;
import android.app.Application;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.activities.main.LoginActivity;
import com.saxxis.saanpaydestributor.interfaces.NetworkListener;
import com.saxxis.saanpaydestributor.receivers.NetworkChangeListener;

/**
 * Created by saxxis25 on 3/25/2017.
 */

public class MixCartApplication extends Application {

    static MixCartApplication mInstance;
    private RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        setmInstance(this);

    }

    public static synchronized MixCartApplication getInstance() {
        return mInstance;
    }

    public void setmInstance(MixCartApplication mInstance) {
        MixCartApplication.mInstance = mInstance;
    }


    public void setConnectivityListener(NetworkListener listener) {
        NetworkChangeListener.networkListener = listener;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(this);
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
        req.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void cancelRequestQue(String tag){
        getRequestQueue().cancelAll(tag);
    }

    public void setupActionbar(final AppCompatActivity activity, String title) {
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        actionBar.setCustomView(R.layout.ab_title_layout);

        View view = actionBar.getCustomView();

        Toolbar parent =(Toolbar) view.getParent();
        parent.setPadding(0,0,0,0);
        parent.setContentInsetsAbsolute(0,0);
        parent.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        parent.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        TextView textviewTitle = (TextView) view.findViewById(R.id.mytext);
        ImageView backarr = (ImageView) view.findViewById(R.id.backarr);

        backarr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //LoginActivity.super.onBackPressed();
                activity.finish();
            }
        });
        textviewTitle.setText(title);

    }


}
