package com.saxxis.saanpaydestributor.adapters.dropdown;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.saxxis.saanpaydestributor.R;

import java.util.List;

/**
 * Created by saxxis25 on 11/2/2017.
 */

public class BankListAdapter extends ArrayAdapter<String> {

//    R.layout.dropdown_list_item,R.id.text_item,

    Context context;
    List<String> titles;
    public BankListAdapter(Context context, List<String> titles){
        super(context,0,titles);
        this.context  = context;
        this.titles = titles;
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public String getItem(int position) {
        return titles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.dropdown_list_item,parent,false);
        view.setId(position);
        TextView textView=(TextView) view.findViewById(R.id.text_item);
        textView.setText(titles.get(position));
        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return super.getFilter();
    }

}
