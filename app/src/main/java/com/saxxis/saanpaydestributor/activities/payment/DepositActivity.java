package com.saxxis.saanpaydestributor.activities.payment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.saxxis.saanpaydestributor.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DepositActivity extends AppCompatActivity {
//        implements
//        OnMapReadyCallback,
//        GoogleApiClient.ConnectionCallbacks,
//        GoogleApiClient.OnConnectionFailedListener{

    @BindView(R.id.deposittoolbar)
    Toolbar depositToolbar;


//    @BindView(R.id.txto_bank)
//    TextView transfertobank;
//
//    @BindView(R.id.addmony)
//    TextView addmoney;

//    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);
        ButterKnife.bind(this);

        setSupportActionBar(depositToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Deposit");
        depositToolbar.setNavigationIcon(R.drawable.arrow);

//        SupportMapFragment mapFragment = (SupportMapFragment)
//                getSupportFragmentManager().findFragmentById(R.id.depositmap);
//        mapFragment.getMapAsync(DepositActivity.this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            super.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    @OnClick(R.id.addmony)
//    void padssBook(){
//        AppHelper.LaunchActivity(DepositActivity.this,AddMoneyActivity.class);
//    }
//
//    @OnClick(R.id.txto_bank)
//    void bankTo(){
//        AppHelper.LaunchActivity(DepositActivity.this,TranBalToBankActivity.class);
//    }

//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap=googleMap;
//    }
}

