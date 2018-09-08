package com.saxxis.saanpaydestributor.activities.recharge;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.app.UserPref;
import com.saxxis.saanpaydestributor.fragments.PlansFragment;
import com.saxxis.saanpaydestributor.models.Category;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BrowsePlansActivity extends AppCompatActivity {

    @BindView(R.id.plans_tab)
    TabLayout mTabs;

    @BindView(R.id.plans_pager)
    ViewPager mPager;

    private ArrayList<Category> mData;
    private String serviceid,servicename;
    private UserPref mUser;
   // private ArrayList<Plan> mPlans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_plans);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mUser = new UserPref(this);

        toolbar.setTitle("Browse Plans");
        toolbar.setNavigationIcon(R.drawable.arrow);

        Bundle extras = getIntent().getExtras();
        serviceid = extras.getString("serviceid");
        servicename = extras.getString("servicename");
        setTitle(servicename);
        mData = extras.getParcelableArrayList("plans");

        assert mData != null;
        for (Category p:  mData ) {
            mTabs.addTab(mTabs.newTab().setText(p.getmTitle()));
        }

        mPager.setAdapter(new PlansPagerAdapter(getSupportFragmentManager(),mData,serviceid));
        if(mData.size()>1)
        mPager.setOffscreenPageLimit(mData.size()-1);

        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

      mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabs));
    }

    public void sendToActivity(String amount) {
        Intent i = new Intent();
        i.putExtra("amount",amount);
        setResult(RESULT_OK,i);
        finish();

    }

    public class PlansPagerAdapter extends FragmentStatePagerAdapter{

        private ArrayList<Category> mTabs;
        private String serviceid;

        public PlansPagerAdapter(FragmentManager fm, ArrayList<Category> mTabs, String serviceid) {
            super(fm);
            this.mTabs = mTabs;
            this.serviceid = serviceid;
        }

        @Override
        public Fragment getItem(int position) {
            return PlansFragment.newInstance(position,mTabs.get(position).getId(),serviceid);
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            finish();
        return true;
    }
}
