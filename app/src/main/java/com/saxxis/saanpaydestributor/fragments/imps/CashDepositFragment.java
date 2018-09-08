package com.saxxis.saanpaydestributor.fragments.imps;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.app.UserPref;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class CashDepositFragment extends Fragment {


    public CashDepositFragment() {
        // Required empty public constructor
    }

    private UserPref userPref;

    public static CashDepositFragment newInstance(){
        return new CashDepositFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_cash_deposit, container, false);

        ButterKnife.bind(this,view);
        userPref=new UserPref(getActivity());

        return view;
    }
}
