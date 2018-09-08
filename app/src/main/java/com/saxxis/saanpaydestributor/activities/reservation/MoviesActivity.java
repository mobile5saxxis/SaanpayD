package com.saxxis.saanpaydestributor.activities.reservation;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.adapters.MoviesListAdapter;
import com.saxxis.saanpaydestributor.helpers.ui.RecvDecors;
import com.saxxis.saanpaydestributor.models.MovieList;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesActivity extends AppCompatActivity {


    @BindView(R.id.tablayout_languages)
    TabLayout tabLayout;

    @BindView(R.id.recv_movies)
    RecyclerView recvMovies;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    MovieList movieList;
    String[] moviename={"Fidaa","Nenu Raju Nene Mantri","Darshakudu","Bahubali-2","ninnu Kori","Vaishakam","Nakshtram","Gowtham Nanda"};

    ArrayList<MovieList> movieArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setTitle("Movies");
        mToolbar.setNavigationIcon(R.drawable.arrow);

        recvMovies.setHasFixedSize(true);
        recvMovies.setLayoutManager(new GridLayoutManager(this,2));
        recvMovies.addItemDecoration(new RecvDecors(this,R.dimen.job_listing_main_offset));

        movieArrayList=new ArrayList<MovieList>();
        for (int i = 0; i < 7; i++) {
            movieArrayList.add(new MovieList(moviename[i],"Telugu",i));
        }

        recvMovies.setAdapter(new MoviesListAdapter(MoviesActivity.this,movieArrayList));
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
