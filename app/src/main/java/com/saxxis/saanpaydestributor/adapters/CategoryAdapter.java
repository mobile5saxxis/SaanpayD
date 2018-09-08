package com.saxxis.saanpaydestributor.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.models.Category;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by saxxis25 on 3/30/2017.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {

    private ArrayList<Category> data;
    private Context mActivity;

    public CategoryAdapter(ArrayList<Category> images,Context xont) {
        this.data = images;
        this.mActivity = xont;
    }

    @Override
    public CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.category_item, parent, false);
        return new CategoryHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryHolder holder, int position) {
        holder.mbleImage.setImageResource(data.get(position).getmIcon());
        holder.moneryTxt.setText(data.get(position).getmTitle());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class CategoryHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.image_mbl)
        ImageView mbleImage;

        @BindView(R.id.text_mbl)
        TextView moneryTxt;

        @BindView(R.id.iconitem)
        LinearLayout itemIdea;

        CategoryHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }
}
