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
public class UpdatesFragment extends Fragment {


    public UpdatesFragment() {
        // Required empty public constructor
    }

    public static UpdatesFragment newInstance() {
        return new UpdatesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_updates, container, false);
        // Inflate the layout for this fragment
        return view;
    }
}
