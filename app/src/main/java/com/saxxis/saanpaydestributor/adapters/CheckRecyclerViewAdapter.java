package com.saxxis.saanpaydestributor.adapters;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.activities.specialities.CheckoutActivity;
import com.saxxis.saanpaydestributor.app.ShoppingPref;
import com.saxxis.saanpaydestributor.models.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CheckRecyclerViewAdapter extends RecyclerView.Adapter<CheckRecyclerViewAdapter.CheckRecyclerViewHolder> {

    private Context context;

    private List<Product> mProductObject;

    private ShoppingPref sharedPreference;
    private Gson gson;

    private int cartProductNumber = 0,totalnumber=1;

    public CheckRecyclerViewAdapter(Context context, List<Product> mProductObject) {
        this.context = context;
        this.mProductObject = mProductObject;
        sharedPreference = new ShoppingPref(context);

        GsonBuilder builder = new GsonBuilder();
        gson = builder.create();
    }

    @Override
    public CheckRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_layout, parent, false);
        CheckRecyclerViewHolder productHolder = new CheckRecyclerViewHolder(layoutView);
        return productHolder;
    }

    @Override
    public void onBindViewHolder(final CheckRecyclerViewHolder holder, final int position) {
        //get product quantity

        holder.productName.setText(mProductObject.get(position).getName());
        holder.productPrice.setText(context.getString(R.string.Rs,mProductObject.get(position).getPrice()));

        holder.removeProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    String productsInCart = sharedPreference.retrieveProductFromCart();
                    Product[] storedProducts = gson.fromJson(productsInCart, Product[].class);

                    List<Product> allNewProduct = convertObjectArrayToListObject(storedProducts);
                    allNewProduct.remove(holder.getAdapterPosition());
                    String addAndStoreNewProduct = gson.toJson(allNewProduct);
                    sharedPreference.addProductToTheCart(addAndStoreNewProduct);
                    cartProductNumber = allNewProduct.size();
                  ((CheckoutActivity) context).RemoveAmount(allNewProduct,cartProductNumber);
                mProductObject.remove(holder.getAdapterPosition());
                notifyDataSetChanged();


                sharedPreference.addProductCount(cartProductNumber);
            }
        });

        holder.addQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalnumber++;
                holder.txtnumber.setText(String.valueOf(totalnumber));
                ((CheckoutActivity) context).AddAmount(mProductObject.get(position).getPrice(),totalnumber);
            }
        });

        holder.remQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalnumber--;
                if(totalnumber>0){
                    holder.txtnumber.setText(String.valueOf(totalnumber));
                    ((CheckoutActivity) context).RemoveIndividualAmount(mProductObject.get(position).getPrice(),totalnumber);
                }else{
                    new AlertDialog.Builder(context)
                            .setTitle("Remove Product?")
                            .setMessage("Do you want to remove the product from cart??")
                            .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String productsInCart = sharedPreference.retrieveProductFromCart();
                                    Product[] storedProducts = gson.fromJson(productsInCart, Product[].class);

                                    List<Product> allNewProduct = convertObjectArrayToListObject(storedProducts);
                                    allNewProduct.remove(holder.getAdapterPosition());
                                    String addAndStoreNewProduct = gson.toJson(allNewProduct);
                                    sharedPreference.addProductToTheCart(addAndStoreNewProduct);
                                    cartProductNumber = allNewProduct.size();
                                    ((CheckoutActivity) context).RemoveAmount(allNewProduct, cartProductNumber);
                                    mProductObject.remove(holder.getAdapterPosition());
                                    notifyDataSetChanged();


                                    sharedPreference.addProductCount(cartProductNumber);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mProductObject.size();
    }

    class CheckRecyclerViewHolder extends RecyclerView.ViewHolder{

        TextView productName, productPrice, removeProduct,addQuantity,txtnumber,remQuantity;
        ImageView ivMain;

        CheckRecyclerViewHolder(View itemView) {
            super(itemView);

            ivMain = (ImageView)itemView.findViewById(R.id.cart_iv_main);
            productName =(TextView)itemView.findViewById(R.id.product_name);
            productPrice = (TextView)itemView.findViewById(R.id.product_price);
            removeProduct = (TextView)itemView.findViewById(R.id.remove_from_cart);
            addQuantity =(TextView)itemView.findViewById(R.id.add_quantity);
            txtnumber = (TextView)itemView.findViewById(R.id.num_quantity);
            remQuantity = (TextView)itemView.findViewById(R.id.remove_quantity);
        }
    }

    private List<Product> convertObjectArrayToListObject(Product[] allProducts){
        List<Product> mProduct = new ArrayList<Product>();
        Collections.addAll(mProduct, allProducts);
        return mProduct;
    }

}
