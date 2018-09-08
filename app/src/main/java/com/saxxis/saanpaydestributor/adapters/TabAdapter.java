package com.saxxis.saanpaydestributor.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.app.UserPref;
import com.saxxis.saanpaydestributor.fragments.ChatFragment;
import com.saxxis.saanpaydestributor.fragments.HomeFragment;
import com.saxxis.saanpaydestributor.fragments.ProfileFragment;
import com.saxxis.saanpaydestributor.fragments.ScanAndPayFragment;
import com.saxxis.saanpaydestributor.fragments.UpdatesFragment;

/**
 * Created by saxxis25 on 3/27/2017.
 */

public class TabAdapter extends FragmentStatePagerAdapter {

    public final int PAGE_COUNT = 4;

    private final String[] mTabsTitle = {"Home", "Chat", "Scan", "Profile", "Updates"};
    private final String[] mTabsLogoutTitle = {"Home", "Chat", "Scan", "Profile", "Updates"};
    private int[] mTabsIcons = {
            R.drawable.home_two,
            R.drawable.chat,
            R.drawable.scan,
            R.drawable.tab_profile,
            R.drawable.updates};
    private int[] mTabslogoutIcons = {
            R.drawable.home_two,
            R.drawable.chat,
            R.drawable.scan,
            R.drawable.tab_profile,
            R.drawable.updates};

    private Context mContext;
    private UserPref userPref;

    public TabAdapter(FragmentManager fm, Context cont) {
        super(fm);
        this.mContext = cont;
        userPref = new UserPref(cont);

    }

    public View getTabView(int position) {
        // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_tab_cart_img, null);
        TextView title = (TextView) view.findViewById(R.id.tab_txt);
        ImageView icon = (ImageView) view.findViewById(R.id.tab_img);
//        if (userPref.isLoggedIn()){
//            title.setText(mTabsLogoutTitle[position]);
//            icon.setImageResource(mTabslogoutIcons[position]);
//        }

//        if (!userPref.isLoggedIn()){
        title.setText(mTabsTitle[position]);
        icon.setImageResource(mTabsIcons[position]);
//        }

        return view;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return HomeFragment.newInstance();
            case 1:
                return new ChatFragment();
            case 2:
                return ScanAndPayFragment.newInstance();
            case 3:
                return ProfileFragment.newInstance();
            case 4:
                return UpdatesFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return mTabsIcons.length;
    }
}

