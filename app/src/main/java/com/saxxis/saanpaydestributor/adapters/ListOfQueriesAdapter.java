package com.saxxis.saanpaydestributor.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.models.ListQueries;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by saxxis25 on 8/28/2017.
 */

public class ListOfQueriesAdapter extends RecyclerView.Adapter<ListOfQueriesAdapter.MyHolder> {

    ArrayList<ListQueries> mData;
    Context context;
    public ListOfQueriesAdapter(Context context, ArrayList<ListQueries> mData ){
        this.context=context;
        this.mData=mData;
    }


    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View ci= LayoutInflater.from(context).inflate(R.layout.query_item,parent,false);
        return new MyHolder(ci);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.txtTitle.setText(mData.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.querytitle)
        TextView txtTitle;

        public MyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
