package com.saxxis.saanpaydestributor.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.activities.payment.DetailedPaymentActivity;
import com.saxxis.saanpaydestributor.adapters.WalletTranxAdapter;
import com.saxxis.saanpaydestributor.app.AppConstants;
import com.saxxis.saanpaydestributor.app.MixCartApplication;
import com.saxxis.saanpaydestributor.app.UserPref;
import com.saxxis.saanpaydestributor.helpers.AppHelper;
import com.saxxis.saanpaydestributor.helpers.ItemClickSupport;
import com.saxxis.saanpaydestributor.models.WalletTranxs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TranxReportsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TranxReportsFragment extends Fragment {

    @BindView(R.id.reportsrecv_wallettranx)
    RecyclerView recyclerView;

    @BindView(R.id.reportstrnxprogressbar)
    ProgressBar progressBar;

    private UserPref userPref;
    private ArrayList<WalletTranxs> mData;


    public TranxReportsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TranxReportsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TranxReportsFragment newInstance() {
        TranxReportsFragment fragment = new TranxReportsFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_tranx_reports, container, false);
        ButterKnife.bind(this,view);

        mData=new ArrayList<>();
        userPref = new UserPref(getActivity());

        fetchOrders();
        return view;
    }


    private  void fetchOrders() {

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));

        String url = AppConstants.WALLET_TRNX+userPref.getUserId()+"&walletcatagory=5";
        Log.e("response",url);
        progressBar.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.logout(getActivity(),response);
                        Log.e("response",response);
                        mData.clear();
                        progressBar.setVisibility(View.GONE);
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            if(status.equals("ok")){

                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                int len = jsonArray.length();
                                for (int i=0;i<len;i++){
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    mData.add(new WalletTranxs(obj.getString("amount"),obj.getString("order_number"),
                                            obj.getString("wallet_description"),obj.getString("date"),
                                            "","",obj.getString("wallet_type"),obj.getString("status")));
                                }

                                recyclerView.setAdapter(new WalletTranxAdapter(getActivity(),mData));
                                ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                                    @Override
                                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                                        startActivity(new Intent(getActivity(),DetailedPaymentActivity.class)
                                                .putExtra("ordernumber",mData.get(position)));
                                    }
                                });
                            }
                        }catch (Exception ignored){

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
//        {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<>();
//                headers.put("Cookie",mPref.getSessionId());
//                return headers;
//            }
//        };

        MixCartApplication.getInstance().addToRequestQueue(request);
    }


}
