package com.saxxis.saanpaydestributor.app;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by saxxis25 on 5/19/2017.
 */

public class ShoppingPref {

    private static final String SHARED_PREF = "shopping_pref";
    private static final String PRODUCT_COUNT = "product_count";
    private static final String PRODUCT_ID = "product_id";


    private SharedPreferences prefs;

    private Context context;

    public ShoppingPref(Context context){
        this.context = context;
        prefs = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
    }

    public void addProductToTheCart(String product){
        SharedPreferences.Editor edits = prefs.edit();
        edits.putString(PRODUCT_ID, product);
        edits.apply();
    }

    public String retrieveProductFromCart(){
        return prefs.getString(PRODUCT_ID, "");
    }

    public void addProductCount(int productCount){
        SharedPreferences.Editor edits = prefs.edit();
        edits.putInt(PRODUCT_COUNT, productCount);
        edits.apply();
    }

    public int retrieveProductCount(){
        return prefs.getInt(PRODUCT_COUNT, 0);
    }
}
