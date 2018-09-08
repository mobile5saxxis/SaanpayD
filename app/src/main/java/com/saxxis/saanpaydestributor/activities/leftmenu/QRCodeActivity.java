package com.saxxis.saanpaydestributor.activities.leftmenu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.zxing.Result;
import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.app.MixCartApplication;
import com.saxxis.saanpaydestributor.helpers.AppHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    @BindView(R.id.entermobileno)
    TextView textMobileno;
    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setTitle("Pay");
        //MixCartApplication.getInstance().setupActionbar(this, "Pay");


        toolbar.setNavigationIcon(R.drawable.arrow);

        mScannerView = (ZXingScannerView) findViewById(R.id.scanner_view);
    }

    @OnClick(R.id.entermobileno)
    void enterMobileNumber() {
        AppHelper.LaunchActivity(QRCodeActivity.this, WalletPayActivity.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }


    @Override
    public void handleResult(Result result) {

        // Do something with the result here
        System.out.println(result.getText()); // Prints scan results
        System.out.println(result.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        // If you would like to resume scanning, call this method below:
        //  mScannerView.resumeCameraPreview(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
