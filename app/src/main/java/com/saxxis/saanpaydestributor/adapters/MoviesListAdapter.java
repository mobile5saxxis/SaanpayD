package com.saxxis.saanpaydestributor.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.models.MovieList;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by saxxis25 on 8/9/2017.
 */

public class MoviesListAdapter extends RecyclerView.Adapter<MoviesListAdapter.MovieViewHolder> {

    private Context context;
    private ArrayList<MovieList> mList;
//    String[] moviename={"Fidaa","Nenu Raju Nene Mantri","Darshakudu","Bahubali-2","ninnu Kori","Vaishakam","Nakshtram","Gowtham Nanda"};
    int[] moviearr={R.drawable.fidaa,R.drawable.nenerajumnenemantri,R.drawable.fidaa,R.drawable.bahubalitwo,R.drawable.nennukori,R.drawable.vaisakham,
        R.drawable.nakshtram,R.drawable.gautamnanda};


    public MoviesListAdapter(Context context,ArrayList<MovieList> mList){
        this.context=context;
        this.mList=mList;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recv_movieadapter,parent,false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
//        holder.imageMovie.setImageResource(mList.get(position).getImageSource());

        Picasso.with(context).load(moviearr[mList.get(position).getImageSource()])
                .into(holder.imageMovie);
//        holder.imageMovie.setImageResource(R.drawable.fidaa);
        holder.textTitle.setText(mList.get(position).getMovieTitle());
        holder.textLanguage.setText(mList.get(position).getLanguage());
//        holder.textTitle.setText("Fidaa");
//        holder.textLanguage.setText("Telugu");
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.image_movie)
        ImageView imageMovie;

        @BindView(R.id.text_title)
        TextView textTitle;

        @BindView(R.id.text_lang)
        TextView textLanguage;

        public MovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
