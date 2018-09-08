package com.saxxis.saanpaydestributor.activities.payment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.fragments.BankDetailsFragment;
import com.saxxis.saanpaydestributor.fragments.CashPickUpFragment;
import com.saxxis.saanpaydestributor.fragments.ReqForPaymentFragment;
import com.saxxis.saanpaydestributor.fragments.ReqForPaymentFragmentMain;
import com.saxxis.saanpaydestributor.fragments.imps.TransferBankFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DepositTwoActivity extends AppCompatActivity {


    @BindView(R.id.deposittwotoolbar)
    Toolbar depositToolbar;

    @BindView(R.id.tablayout_deposit)
    TabLayout tabLayout;

    @BindView(R.id.deposit_pager)
    ViewPager depositPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit_two);
        ButterKnife.bind(this);

        setSupportActionBar(depositToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Deposit");
        depositToolbar.setNavigationIcon(R.drawable.arrow);
        setupViewPager(depositPager);
        tabLayout.setupWithViewPager(depositPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(BankDetailsFragment.newInstance(),"Bank Details");
        //adapter.addFragment(CashPickUpFragment.newInstance(),"Cash PickUp");
        adapter.addFragment(ReqForPaymentFragmentMain.newInstance(),"Request for payment");
        adapter.addFragment(TransferBankFragment.newInstance(),"Transfer To Bank");
//        adapter.addFragment(CashDepositFragment.newInstance(),"Cash Deposit");
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
