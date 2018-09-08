package com.saxxis.saanpaydestributor.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.zxing.Result;
import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.activities.leftmenu.WalletPayActivity;
import com.saxxis.saanpaydestributor.helpers.AppHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScanAndPayFragment extends Fragment implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;

    public ScanAndPayFragment() {
        // Required empty public constructor
    }

    @BindView(R.id.entermobileno)
    TextView enterMobileNo;

    public static Fragment newInstance() {
        return new ScanAndPayFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View ciew=inflater.inflate(R.layout.fragment_scan_and_pay, container, false);
        // Inflate the layout for this fragment
        ButterKnife.bind(this,ciew);
        mScannerView = (ZXingScannerView)ciew.findViewById(R.id.scanner_view);
        return ciew;
    }

    @OnClick(R.id.entermobileno)
    void enterMobile(){
        AppHelper.LaunchActivity(getActivity(),WalletPayActivity.class);
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
}
