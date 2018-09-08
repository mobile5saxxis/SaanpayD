package com.saxxis.saanpaydestributor.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.saxxis.saanpaydestributor.CommingSoonActivity;
import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.activities.leftmenu.QRCodeActivity;
import com.saxxis.saanpaydestributor.activities.main.LoginActivity;
import com.saxxis.saanpaydestributor.activities.payment.AddMoneyActivity;
import com.saxxis.saanpaydestributor.activities.payment.DepositTwoActivity;
import com.saxxis.saanpaydestributor.activities.payment.PassBookActivity;
import com.saxxis.saanpaydestributor.activities.recharge.DataCardActivity;
import com.saxxis.saanpaydestributor.activities.recharge.DthActivity;
import com.saxxis.saanpaydestributor.activities.recharge.MobileRechargePostpaidActivity;
import com.saxxis.saanpaydestributor.activities.recharge.MobileRechargePrepaidActivity;
import com.saxxis.saanpaydestributor.activities.reservation.BusesActivity;
import com.saxxis.saanpaydestributor.activities.reservation.FlightActivity;
import com.saxxis.saanpaydestributor.activities.reservation.HolidayActivity;
import com.saxxis.saanpaydestributor.activities.reservation.HotelsActivity;
import com.saxxis.saanpaydestributor.activities.reservation.MoviesActivity;
import com.saxxis.saanpaydestributor.activities.specialities.SpecialityListingActivity;
import com.saxxis.saanpaydestributor.activities.utlity.ElectricityActivity;
import com.saxxis.saanpaydestributor.activities.utlity.GasActivity;
import com.saxxis.saanpaydestributor.activities.utlity.InsuranceActivity;
import com.saxxis.saanpaydestributor.activities.utlity.WaterActivity;
import com.saxxis.saanpaydestributor.adapters.CategoryAdapter;
import com.saxxis.saanpaydestributor.adapters.CategoryAdapterFadedText;
import com.saxxis.saanpaydestributor.app.UserPref;
import com.saxxis.saanpaydestributor.helpers.AppHelper;
import com.saxxis.saanpaydestributor.helpers.ItemClickSupport;
import com.saxxis.saanpaydestributor.models.Category;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ss.com.bannerslider.banners.Banner;
import ss.com.bannerslider.banners.DrawableBanner;
import ss.com.bannerslider.views.BannerSlider;


public class HomeFragment extends Fragment {


    @BindView(R.id.rcy_money_trnsfr)
    RecyclerView rvMoneyTransfer;
    @BindView(R.id.rcy_rechage_paybills)
    RecyclerView rvPayBills;
    @BindView(R.id.rcy_book_online)
    RecyclerView rvBookOnline;
    @BindView(R.id.rcy_speciality_store)
    RecyclerView rvSpecialityStore;
    @BindView(R.id.rcy_reservation)
    RecyclerView rvReservation;
    @BindView(R.id.banner_slider)
    BannerSlider bannerSlider;

    @BindView(R.id.wv_bo)
    WebView wvbo;

    @BindView(R.id.wv_ss)
    WebView wvss;
    private UserPref userPref;

    public HomeFragment() {

    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        setupRecyclerViews();
        setuplisteners();
        setUpSlider();
        userPref = new UserPref(getActivity());
        setUpComingSoon();//Remove before implementing book online and ss
        return view;
    }

    private void setUpComingSoon() {
        assert wvbo != null;
        wvbo.clearCache(false);
        wvbo.getSettings().setLoadWithOverviewMode(true);
        wvbo.getSettings().setUseWideViewPort(true);
        wvbo.loadUrl("file:///android_asset/comingsoon.gif");

        assert wvss != null;
        wvss.clearCache(false);
        wvss.getSettings().setLoadWithOverviewMode(true);
        wvss.getSettings().setUseWideViewPort(true);
        wvss.loadUrl("file:///android_asset/comingsoon.gif");

    }

    private void setUpSlider() {
        List<Banner> banners = new ArrayList<>();
        banners.add(new DrawableBanner(R.drawable.banner_three));
        banners.add(new DrawableBanner(R.drawable.banner_three));
        banners.add(new DrawableBanner(R.drawable.banner_three));
        banners.add(new DrawableBanner(R.drawable.banner_three));
        banners.add(new DrawableBanner(R.drawable.banner_three));
        bannerSlider.setBanners(banners);
    }

    /*private void setupSLider() {
        StringRequest str = new StringRequest(AppConstants.ADS_SLIDER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppHelper.logout(getActivity(),response);
                ArrayList<String> mData = new ArrayList<>();
                try{
                    JSONObject obj = new JSONObject(response);
                    String status = obj.getString("status");
                    JSONArray jsonArray = obj.getJSONArray("data");
                    int length = jsonArray.length();
                    if(length!=0){

                        for(int i = 0;i<length;i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            mData.add(jsonObject.getString("image"));
                        }

                        for (String i: mData) {
                            DefaultSliderView dsv = new DefaultSliderView(getActivity());
                            dsv.image(AppConstants.SERVER_URL+i);
                            dsv.setScaleType(BaseSliderView.ScaleType.CenterCrop);
                            mPager.addSlider(dsv);
                        }
                        mPager.setDuration(5000);
                    }else{
                        //No Ads Display
                    }

                }catch (Exception ignored){

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        MixCartApplication.getInstance().addToRequestQueue(str);

    }*/

    private void setupRecyclerViews() {

        rvMoneyTransfer.setHasFixedSize(true);
        rvPayBills.setHasFixedSize(true);
        rvBookOnline.setHasFixedSize(true);
        rvSpecialityStore.setHasFixedSize(true);
        rvReservation.setHasFixedSize(true);

        rvMoneyTransfer.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvPayBills.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvBookOnline.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvSpecialityStore.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvReservation.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        rvMoneyTransfer.setAdapter(new CategoryAdapterFadedText(Category.getMoneyData(), getActivity()));
        rvPayBills.setAdapter(new CategoryAdapter(Category.getPayBills(), getActivity()));
        rvBookOnline.setAdapter(new CategoryAdapter(Category.getBookOnline(), getActivity()));
        rvSpecialityStore.setAdapter(new CategoryAdapter(Category.getSpecialityStore(), getActivity()));
        rvReservation.setAdapter(new CategoryAdapter(Category.getReservationData(), getActivity()));
    }


    private void setuplisteners() {
        ItemClickSupport.addTo(rvMoneyTransfer).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                switch (position) {
                    case 0:
                        if (userPref.isLoggedIn()) {
                            AppHelper.LaunchActivity(getActivity(), QRCodeActivity.class);
                        } else {
                            AppHelper.LaunchActivity(getActivity(), LoginActivity.class);
                        }
                        //Add Money Acitivty
                        break;
//                    case 1:
//                        Intent ip = new Intent(getActivity(), SendMoneyActivity.class);
//                        ip.putExtra("type","pay");
//                        getActivity().startActivity(ip);
//                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                        //Pay Money Acitivty
//                        break;
                    case 1:
                        if (userPref.isLoggedIn()) {
                            AppHelper.LaunchActivity(getActivity(), AddMoneyActivity.class);
                        } else {
                            AppHelper.LaunchActivity(getActivity(), LoginActivity.class);
                        }
                        //Receive Money Acitivty
                        break;
//                    case 3:
//                        Intent is = new Intent(getActivity(), SendMoneyActivity.class);
//                        is.putExtra("type","send");
//                        getActivity().startActivity(is);
//                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                        //Send Money Acitivty
//                        break;
                    case 2:
                        if (userPref.isLoggedIn()) {
                            Intent it = new Intent(getActivity(), PassBookActivity.class);
                            getActivity().startActivity(it);
                            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        } else {
                            AppHelper.LaunchActivity(getActivity(), LoginActivity.class);
                        }
                        //Transfer Money Acitivty
                        break;
//                    case 3:
////                        AppHelper.LaunchActivity(getActivity(),QRCodeActivity.class);
//                        AppHelper.LaunchActivity(getActivity(), WalletActivity.class);
//                        //Scan Pay Acitivty
//                        break;
                    case 3:
                        if (userPref.isLoggedIn()) {
                            AppHelper.LaunchActivity(getActivity(), DepositTwoActivity.class);
                        } else {
                            AppHelper.LaunchActivity(getActivity(), LoginActivity.class);
                        }
                        //Pass Book Acitivty
                        break;
//                    case 5:
//
//                        AppHelper.LaunchActivity(getActivity(),WalletActivity.class);
//                        //Wallet Store Acitivty
//                        break;
//                    case 6:
//                        AppHelper.LaunchActivity(getActivity(),DepositActivity.class);
//                        //Deposit Store Acitivty
//                        break;
//                    case 8:
//                        //Google Play Acitivty
//                        break;
                }
            }
        });

        ItemClickSupport.addTo(rvPayBills).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(getActivity(), MobileRechargePrepaidActivity.class).putExtra("checkoptiontype", "1"));
                        //Prepaid Activity
                        break;
                    case 1:
                        startActivity(new Intent(getActivity(), MobileRechargePostpaidActivity.class).putExtra("checkoptiontype", "2"));
                        //Postpaid Activity
                        break;
                    case 2:
                        AppHelper.LaunchActivity(getActivity(), DthActivity.class);
                        //DTH Activity
                        break;
                    case 3:
                        AppHelper.LaunchActivity(getActivity(), DataCardActivity.class);
                        //Datacard Activity
                        break;


                }
            }
        });

        ItemClickSupport.addTo(rvBookOnline).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                switch (position) {
                    case 0:
                        AppHelper.LaunchActivity(getActivity(), ElectricityActivity.class);
                        break;
                    case 1:
                        AppHelper.LaunchActivity(getActivity(), GasActivity.class);
                        break;
                    case 2:
                        AppHelper.LaunchActivity(getActivity(), InsuranceActivity.class);
                        break;
                    case 3:
                        AppHelper.LaunchActivity(getActivity(), WaterActivity.class);
                        break;
                    case 4:
//                        AppHelper.LaunchActivity(getActivity(), TestActivity.class);
                        break;
                    case 5:

                        break;
                    case 6:

                        break;
                    case 7:

                        break;
                    case 8:

                        break;

                }
            }
        });

        ItemClickSupport.addTo(rvReservation).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                switch (position) {
                    case 0:
                        //AppHelper.LaunchActivity(getActivity(), BusesActivity.class);
                        AppHelper.LaunchActivity(getActivity(), CommingSoonActivity.class);
                        break;
                    case 1:
                        //AppHelper.LaunchActivity(getActivity(), FlightActivity.class);
                        AppHelper.LaunchActivity(getActivity(), CommingSoonActivity.class);
                        break;
                    case 2:
                        //AppHelper.LaunchActivity(getActivity(), HolidayActivity.class);
                        AppHelper.LaunchActivity(getActivity(), CommingSoonActivity.class);
                        break;
                    case 3:
                        //AppHelper.LaunchActivity(getActivity(), HotelsActivity.class);
                        AppHelper.LaunchActivity(getActivity(), CommingSoonActivity.class);
                        break;
                    case 4:
                        //AppHelper.LaunchActivity(getActivity(), MoviesActivity.class);
                        AppHelper.LaunchActivity(getActivity(), CommingSoonActivity.class);
                        break;
                }
            }
        });


        ItemClickSupport.addTo(rvSpecialityStore).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                switch (position) {
                    case 0:
                        Intent i = new Intent(getActivity(), SpecialityListingActivity.class);
                        i.putExtra("id", Category.getSpecialityStore().get(position).getId());
                        i.putExtra("title", Category.getSpecialityStore().get(position).getmTitle());
                        getActivity().startActivity(i);

                        break;
                    case 1:
                        Intent i1 = new Intent(getActivity(), SpecialityListingActivity.class);
                        i1.putExtra("id", Category.getSpecialityStore().get(position).getId());
                        i1.putExtra("title", Category.getSpecialityStore().get(position).getmTitle());
                        getActivity().startActivity(i1);

                        break;
                    case 2:
                        Intent i2 = new Intent(getActivity(), SpecialityListingActivity.class);
                        i2.putExtra("id", Category.getSpecialityStore().get(position).getId());
                        i2.putExtra("title", Category.getSpecialityStore().get(position).getmTitle());
                        getActivity().startActivity(i2);

                        break;
                    case 3:
                        Intent i3 = new Intent(getActivity(), SpecialityListingActivity.class);
                        i3.putExtra("id", Category.getSpecialityStore().get(position).getId());
                        i3.putExtra("title", Category.getSpecialityStore().get(position).getmTitle());
                        getActivity().startActivity(i3);

                        break;
                    case 4:
                        Intent i4 = new Intent(getActivity(), SpecialityListingActivity.class);
                        i4.putExtra("id", Category.getSpecialityStore().get(position).getId());
                        i4.putExtra("title", Category.getSpecialityStore().get(position).getmTitle());
                        getActivity().startActivity(i4);

                        break;
                    case 5:
                        Intent i5 = new Intent(getActivity(), SpecialityListingActivity.class);
                        i5.putExtra("id", Category.getSpecialityStore().get(position).getId());
                        i5.putExtra("title", Category.getSpecialityStore().get(position).getmTitle());
                        getActivity().startActivity(i5);

                        break;
                    case 6:
                        Intent i6 = new Intent(getActivity(), SpecialityListingActivity.class);
                        i6.putExtra("id", Category.getSpecialityStore().get(position).getId());
                        i6.putExtra("title", Category.getSpecialityStore().get(position).getmTitle());
                        getActivity().startActivity(i6);

                        break;
//                    case 7:
//                        Intent i7 = new Intent(getActivity(), SpecialityListingActivity.class);
//                        i7.putExtra("id",Category.getSpecialityStore().get(position).getId());
//                        i7.putExtra("title",Category.getSpecialityStore().get(position).getmTitle());
//                        getActivity().startActivity(i7);
//
//                        break;
//                    case 8:
//                        Intent i8 = new Intent(getActivity(), SpecialityListingActivity.class);
//                        i8.putExtra("id",Category.getSpecialityStore().get(position).getId());
//                        i8.putExtra("title",Category.getSpecialityStore().get(position).getmTitle());
//                        getActivity().startActivity(i8);
//
//                        break;

                }
            }
        });
    }


}
