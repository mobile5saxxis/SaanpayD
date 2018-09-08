package com.saxxis.saanpaydestributor.activities.leftmenu;

import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.fragments.BanksListFragment;
import com.saxxis.saanpaydestributor.fragments.ReqForPaymentFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoneyDepositActivity extends AppCompatActivity {
    @BindView(R.id.deposittwotoolbar)
    Toolbar depositToolbar;

    @BindView(R.id.tablayout_deposit)
    TabLayout tabLayout;

    @BindView(R.id.deposit_pager)
    ViewPager depositPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_deposit);
        ButterKnife.bind(this);

        setSupportActionBar(depositToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Money Deposit");
        depositToolbar.setNavigationIcon(R.drawable.arrow);
        setupViewPager(depositPager);
        tabLayout.setupWithViewPager(depositPager);
    }
    private void setupViewPager(ViewPager viewPager) {
        MoneyDepositActivity.ViewPagerAdapter adapter = new MoneyDepositActivity.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(BanksListFragment.newInstance(),"Banks");
        adapter.addFragment(ReqForPaymentFragment.newInstance(),"Request for payment");
        viewPager.setAdapter(adapter);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        private ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        private void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            super.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
