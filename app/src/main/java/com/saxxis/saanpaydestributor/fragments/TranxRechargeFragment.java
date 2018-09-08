package com.saxxis.saanpaydestributor.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.activities.OrderDertailsActivity;
import com.saxxis.saanpaydestributor.adapters.OrdersAdapter;
import com.saxxis.saanpaydestributor.app.AppConstants;
import com.saxxis.saanpaydestributor.app.MixCartApplication;
import com.saxxis.saanpaydestributor.app.UserPref;
import com.saxxis.saanpaydestributor.helpers.AppHelper;
import com.saxxis.saanpaydestributor.helpers.ItemClickSupport;
import com.saxxis.saanpaydestributor.models.Order;
import com.saxxis.saanpaydestributor.models.WalletTranxs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class TranxRechargeFragment extends Fragment {

    @BindView(R.id.rechargerecv_wallettranx)
    RecyclerView recvOrders;

    @BindView(R.id.rechargetrnxprogressbar)
    ProgressBar trnxProgressBar;


    private UserPref userPref;
    private ArrayList<WalletTranxs> mData;
    public static TranxRechargeFragment newInstance() {
        return new TranxRechargeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public TranxRechargeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_tranx_recharge, container, false);
        ButterKnife.bind(this,view);

        userPref = new UserPref(getActivity());
        mData = new ArrayList<WalletTranxs>();
        fetchOrders();

        return view;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.refresh) {
            fetchOrders();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void fetchOrders() {
        recvOrders.setHasFixedSize(true);
        recvOrders.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        String url= AppConstants.WALLET_TRNX+userPref.getUserId()+"&walletcatagory=4";
        Log.e("response",url);
        trnxProgressBar.setVisibility(View.VISIBLE);
        StringRequest request=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppHelper.logout(getActivity(),response);
                System.out.println(response);
                trnxProgressBar.setVisibility(View.GONE);
                final ArrayList<Order> mData = new ArrayList<>();
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if(status.equals("ok")){
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        int len = jsonArray.length();
                        for (int i=0;i<len;i++){
                            JSONObject obj = jsonArray.getJSONObject(i);
                            Order order = new Order();
                            order.setMobileno(obj.getString("mobileno"));
                            order.setOrder_id(obj.getString("order_number"));
                            order.setAmount(obj.getString("amount"));
                            order.setPay_status(obj.getString("status"));
                            order.setPay_date(obj.getString("date"));
                            order.setOptype_name(obj.getString("operator"));
                            order.setOperatorname(obj.getString("operatorname"));
                            mData.add(order);
                        }

                        OrdersAdapter orderadapter=new OrdersAdapter(getActivity(),mData);
                        recvOrders.setAdapter(orderadapter);

                        ItemClickSupport.addTo(recvOrders).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                            @Override
                            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                                getActivity().startActivity(new Intent(getActivity(), OrderDertailsActivity.class).putExtra("ordereddata",mData.get(position)));
                            }
                        });

//                        rvOrders.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
//                            @Override
//                            public void onLoadMore() {
//                                addDataToList();
//                            }
//                        });
                    }
                }catch (Exception ignored){

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        MixCartApplication.getInstance().addToRequestQueue(request);
    }
}
