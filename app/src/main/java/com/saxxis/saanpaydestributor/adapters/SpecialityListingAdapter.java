package com.saxxis.saanpaydestributor.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.activities.specialities.SpecialityListingActivity;
import com.saxxis.saanpaydestributor.models.ProdAtrribute;
import com.saxxis.saanpaydestributor.models.Product;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by saxxis25 on 5/19/2017.
 */

public class SpecialityListingAdapter extends RecyclerView.Adapter<SpecialityListingAdapter.SpecialityListingHolder> {

    private ArrayList<Product> mData;

    private Context mActivity;

    private int totalnumber=0;

    public SpecialityListingAdapter(Context mActivity, ArrayList<Product> data){
        this.mActivity =mActivity;
        this.mData = data;
    }

    @Override
    public SpecialityListingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         View view = LayoutInflater.from(mActivity).inflate(R.layout.product_layout,parent,false);
        return new SpecialityListingHolder(view);
    }

    @Override
    public void onBindViewHolder(final SpecialityListingHolder holder, final int position) {
        final Product singleProduct = mData.get(position);
        final ArrayList<ProdAtrribute> mProdAttr = singleProduct.getAttributes();
        final List<String> list = new ArrayList<>();
        Picasso.with(mActivity)
                .load(singleProduct.getImagePath())
                .centerCrop()
                .resize(200,200)
                .into(holder.ivMain);
        holder.txtPrice.setText(mActivity.getResources().getString(R.string.Rs,singleProduct.getPrice()));
        holder.txtName.setText(singleProduct.getName());
        holder.txtnumber.setText(String.valueOf(totalnumber));



        holder.addQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalnumber++;
                holder.txtnumber.setText(String.valueOf(totalnumber));

            }
        });

        holder.remQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(totalnumber>0){
                    totalnumber--;
                    holder.txtnumber.setText(String.valueOf(totalnumber));
                }else {
                    totalnumber=1;
                }
            }
        });

        holder.btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SpecialityListingActivity)mActivity).addToCart(singleProduct);
            }
        });

        for(ProdAtrribute pa : mProdAttr){
            list.add(pa.getName());

        }
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, list);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        holder.spQuant.setAdapter(dataAdapter);
        holder.spQuant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                holder.txtPrice.setText(mActivity.getResources().getString(R.string.Rs,mProdAttr.get(position).getPrice()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }



    class SpecialityListingHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.product_item_iv) ImageView ivMain;

        @BindView(R.id.product_item_name) TextView txtName;

        @BindView(R.id.product_item_price) TextView txtPrice;

        @BindView(R.id.product_item_add_quan) TextView addQuantity;

        @BindView(R.id.product_item_num_quan) TextView txtnumber;

        @BindView(R.id.product_item_remove_quan) TextView remQuantity;

        @BindView(R.id.product_item_btn_cart) Button btnCart;
        @BindView(R.id.product_item_spinner)
        Spinner spQuant;

        public SpecialityListingHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}


