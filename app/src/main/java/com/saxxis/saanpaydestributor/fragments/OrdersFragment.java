package com.saxxis.saanpaydestributor.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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
import com.saxxis.saanpaydestributor.activities.OrderDertailsActivity;
import com.saxxis.saanpaydestributor.adapters.OrdersAdapter;
import com.saxxis.saanpaydestributor.app.AppConstants;
import com.saxxis.saanpaydestributor.app.MixCartApplication;
import com.saxxis.saanpaydestributor.app.UserPref;
import com.saxxis.saanpaydestributor.helpers.AppHelper;
import com.saxxis.saanpaydestributor.helpers.ItemClickSupport;
import com.saxxis.saanpaydestributor.models.Order;
import com.saxxis.saanpaydestributor.utils.ui.EndlessRecyclerOnScrollListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersFragment extends Fragment {

    RecyclerView rvOrders;

    @BindView(R.id.recvprogressview)
    ProgressBar recvProgress;

    @BindView(R.id.item_progress_bar)
    ProgressBar recvReloadProgressBar;

    private Unbinder unbinder;

    private UserPref mPref;
    private OrdersAdapter orderadapter;

    public static OrdersFragment newInstance() {
        return new OrdersFragment();
    }

    public OrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPref = new UserPref(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        ButterKnife.bind(this,view);
        rvOrders = (RecyclerView) view.findViewById(R.id.orders_rv);

        fetchOrders();
        return view;
    }

    private void fetchOrders() {
        rvOrders.setHasFixedSize(true);
        rvOrders.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        recvProgress.setVisibility(View.VISIBLE);
        Log.e("response", AppConstants.ORDERS_URL+mPref.getUserId());
        StringRequest request=new StringRequest(Request.Method.GET, AppConstants.ORDERS_URL+mPref.getUserId(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                AppHelper.logout(getActivity(),response);
                recvProgress.setVisibility(View.GONE);
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

                        orderadapter=new OrdersAdapter(getActivity(),mData);
                        rvOrders.setAdapter(orderadapter);

                        ItemClickSupport.addTo(rvOrders).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                            @Override
                            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                                    getActivity().startActivity(new Intent(getActivity(), OrderDertailsActivity.class).putExtra("ordereddata",mData.get(position)));
                                }
                        });

                        rvOrders.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
                            @Override
                            public void onLoadMore() {
                                addDataToList();
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

        MixCartApplication.getInstance().addToRequestQueue(request);
    }

    private void addDataToList() {
        recvReloadProgressBar.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                for (int i = 0; i <= 30; i++) {
////                    mStringList.add("SampleText : " + mLoadedItems);
////                    mLoadedItems++;
//                }

                orderadapter.notifyDataSetChanged();
                recvReloadProgressBar.setVisibility(View.GONE);
            }
        }, 2000);
    }

}
