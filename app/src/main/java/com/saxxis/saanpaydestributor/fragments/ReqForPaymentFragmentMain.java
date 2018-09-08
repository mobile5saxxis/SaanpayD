package com.saxxis.saanpaydestributor.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.saxxis.saanpaydestributor.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReqForPaymentFragmentMain extends Fragment implements View.OnClickListener {
    RadioButton cashPU;
    RadioButton cashDP;
    RadioButton chqueRB;
    RadioButton onlineRB;
    FragmentTransaction ft;
    CashPickUpFragment cashPickUpFragment;
    ReqForPaymentFragment reqForPaymentFragment;

    public static ReqForPaymentFragmentMain newInstance() {
        return new ReqForPaymentFragmentMain();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_req_for_payment_main, container, false);
        cashPU = (RadioButton) v.findViewById(R.id.cashPU);
        cashDP = (RadioButton) v.findViewById(R.id.cashDP);
        chqueRB = (RadioButton) v.findViewById(R.id.chqueRB);
        onlineRB = (RadioButton) v.findViewById(R.id.onlineRB);

        cashPickUpFragment = CashPickUpFragment.newInstance();
        reqForPaymentFragment = ReqForPaymentFragment.newInstance();

        ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.replace, reqForPaymentFragment);
        ft.commit();

        cashPU.setOnClickListener(this);
        cashDP.setOnClickListener(this);
        chqueRB.setOnClickListener(this);
        onlineRB.setOnClickListener(this);


        return v;
    }

    @Override
    public void onClick(View view) {
        ft = getFragmentManager().beginTransaction();

        if (view.getTag().equals("cashPU")) {
            ft.replace(R.id.replace, cashPickUpFragment);
        } else if (view.getTag().equals("cashDP")) {
            ft.replace(R.id.replace, reqForPaymentFragment);
            reqForPaymentFragment.setMode(1);
        } else if (view.getTag().equals("chqueRB")) {
            ft.replace(R.id.replace, reqForPaymentFragment);
            reqForPaymentFragment.setMode(2);
        } else if (view.getTag().equals("onlineRB")) {
            ft.replace(R.id.replace, reqForPaymentFragment);
            reqForPaymentFragment.setMode(3);
        }
        ft.commit();

    }
}
