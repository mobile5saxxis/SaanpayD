package com.saxxis.saanpaydestributor.activities.utlity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.saxxis.saanpaydestributor.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GasActivity extends AppCompatActivity {

    @BindView(R.id.gastoolbar)
    Toolbar gasbilltoolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas);

        ButterKnife.bind(this);

        setSupportActionBar(gasbilltoolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gasbilltoolbar.setTitle("Gas");
        gasbilltoolbar.setNavigationIcon(R.drawable.arrow);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==android.R.id.home){
            super.onBackPressed();
            return  true;
        }

        return super.onOptionsItemSelected(item);
    }
}
