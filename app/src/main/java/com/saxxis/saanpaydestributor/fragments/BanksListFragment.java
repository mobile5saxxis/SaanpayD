package com.saxxis.saanpaydestributor.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.adapters.BanksListAdapter;
import com.saxxis.saanpaydestributor.app.AppConstants;
import com.saxxis.saanpaydestributor.app.MixCartApplication;
import com.saxxis.saanpaydestributor.helpers.AppHelper;
import com.saxxis.saanpaydestributor.models.BanksList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.*/
public class BanksListFragment extends Fragment {
    ArrayList<BanksList> banksLists;
    @BindView(R.id.banksList)
    RecyclerView banksList;
    BanksListAdapter banksListAdapter;
    public BanksListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_banks_list, container, false);
        ButterKnife.bind(this,view);

        getBanksList();
        return view;
    }

    public static BanksListFragment newInstance() {
        return new BanksListFragment();
    }

    void getBanksList(){
        banksList.setHasFixedSize(true);
        banksList.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        AppHelper.showDialog(getActivity(),"Loading Please Wait...");
        Log.e("request", AppConstants.BANKS_LIST);
        StringRequest stringRequest  = new StringRequest(AppConstants.BANKS_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.hideDialog();
                        AppHelper.logout(getActivity(),response);
                        Log.e("response",response);
                        try {
                            banksLists = new ArrayList<BanksList>();
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray banksArray = jsonObject.getJSONArray("banks");
                            for (int i = 0; i < banksArray.length(); i++) {
                                JSONObject bankObject = banksArray.getJSONObject(i);
                                banksLists.add(new BanksList(bankObject.getString("id"),
                                        bankObject.getString("bank_name"),
                                        bankObject.getString("bank_code"),bankObject.getString("ifsc")));
                            }
                            banksListAdapter=new BanksListAdapter(getActivity(),banksLists);
                            banksList.setAdapter(banksListAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        MixCartApplication.getInstance().addToRequestQueue(stringRequest);
    }


}
