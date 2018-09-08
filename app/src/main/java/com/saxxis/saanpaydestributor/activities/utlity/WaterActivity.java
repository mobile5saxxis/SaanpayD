package com.saxxis.saanpaydestributor.activities.utlity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.saxxis.saanpaydestributor.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WaterActivity extends AppCompatActivity {

    @BindView(R.id.watertoolbar)
    Toolbar waterbilltoolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water);

        ButterKnife.bind(this);

        setSupportActionBar(waterbilltoolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        waterbilltoolbar.setTitle("Water");
        waterbilltoolbar.setNavigationIcon(R.drawable.arrow);
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
