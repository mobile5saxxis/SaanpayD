package com.saxxis.saanpaydestributor.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.saxxis.saanpaydestributor.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllOrdersFragment extends Fragment {


    public static AllOrdersFragment newInstance() {

        return new AllOrdersFragment();
    }

    public AllOrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_orders, container, false);
    }

}
