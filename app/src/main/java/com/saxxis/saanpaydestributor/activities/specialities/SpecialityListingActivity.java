package com.saxxis.saanpaydestributor.activities.specialities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.adapters.SpecialityListingAdapter;
import com.saxxis.saanpaydestributor.app.AppConstants;
import com.saxxis.saanpaydestributor.app.MixCartApplication;
import com.saxxis.saanpaydestributor.app.ShoppingPref;
import com.saxxis.saanpaydestributor.helpers.AppHelper;
import com.saxxis.saanpaydestributor.helpers.ItemClickSupport;
import com.saxxis.saanpaydestributor.interfaces.NetworkListener;
import com.saxxis.saanpaydestributor.models.ProdAtrribute;
import com.saxxis.saanpaydestributor.models.Product;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SpecialityListingActivity extends AppCompatActivity implements NetworkListener {

    @BindView(R.id.speciality_rv)
    RecyclerView rvSpeciality;

    @BindView(R.id.cl_speciality)
    CoordinatorLayout clLayout;

    private String catId,catTitle;
    private int cartProductNumber = 0;

    private ShoppingPref sharedPreference;
    private boolean added= false;
    private Gson gson;

    private ArrayList<Product> mList;
    private ArrayList<ProdAtrribute> mAttr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speciality_listing);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedPreference = new ShoppingPref(SpecialityListingActivity.this);

        GsonBuilder builder = new GsonBuilder();
        gson = builder.create();
        Bundle extras = getIntent().getExtras();
        if(extras!=null) {
            catId = String.valueOf(extras.getInt("id"));
            catTitle = String.valueOf(extras.getString("title"));
            setTitle(catTitle);
            fetchProducts(catId);
        }



    }

    private void fetchProducts(String catId) {

        String url = "http://sastabankproperty.in.md-38.webhostbox.net/recharge/index.php?option=com_jbackend&view=request&action=get&module=user&resource=catagory&catid="+catId;
        StringRequest str = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try{
                    JSONObject obj = new JSONObject(s);
                    String status = obj.getString("status");
                    if(status.equals("ok")){
                        JSONArray jsonArray = obj.getJSONArray("message");
                        int length = jsonArray.length();
                        mList = new ArrayList<>();
                        if(length!=0) {
                            for (int i = 0; i < length; i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Product product = new Product();
                                product.setId(jsonObject.getString("pid"));
                                product.setName(jsonObject.getString("name"));
                                product.setDiscount(jsonObject.getString("discountrate"));
                                product.setDiscountprice(jsonObject.getString("discountvalue"));
                                product.setPrice(jsonObject.getString("price"));
                                product.setImagePath("http://sastabankproperty.in.md-38.webhostbox.net/recharge/joobi/user/media/images/products/thumbnails/"+jsonObject.getString("image"));

                                mAttr = new ArrayList<>();
                                JSONArray attrarray = obj.getJSONArray("Attributes");
                                int len = attrarray.length();
                                if(len!=0) {
                                    for (int j = 0; j < len; j++) {
                                        JSONObject attrobj = attrarray.getJSONObject(j);

                                        ProdAtrribute prod = new ProdAtrribute();
                                        prod.setId(attrobj.getString("opvid"));
                                        prod.setName(attrobj.getString("name"));
                                        prod.setPrice(attrobj.getString("price"));
                                        mAttr.add(prod);
                                        System.out.println(attrobj.getString("opvid"));
                                    }
                                    product.setAttributes(mAttr);
                                }
                                mList.add(product);
                            }
                            updateGrid(mList);

                        }else{
                            //No Products
                        }

                    }


                }catch (Exception e){}


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        MixCartApplication.getInstance().addToRequestQueue(str);


    }

    public void addToCart(Product singleProduct){
        if(!added){
            String productsFromCart = sharedPreference.retrieveProductFromCart();
            if(productsFromCart.equals("")){
                List<Product> cartProductList = new ArrayList<Product>();
                cartProductList.add(singleProduct);
                String cartValue = gson.toJson(cartProductList);
                sharedPreference.addProductToTheCart(cartValue);
                cartProductNumber = cartProductList.size();
            }else{
                String productsInCart = sharedPreference.retrieveProductFromCart();
                Product[] storedProducts = gson.fromJson(productsInCart, Product[].class);

                List<Product> allNewProduct = convertObjectArrayToListObject(storedProducts);
                allNewProduct.add(singleProduct);
                String addAndStoreNewProduct = gson.toJson(allNewProduct);
                sharedPreference.addProductToTheCart(addAndStoreNewProduct);
                cartProductNumber = allNewProduct.size();
            }
            sharedPreference.addProductCount(cartProductNumber);
            invalidateCart();
            added= true;
        }else{
            new AlertDialog.Builder(SpecialityListingActivity.this)
                    .setMessage("Already addded to cart")
                    .setPositiveButton("close",null)
                    .show();
        }
    }

    private void updateGrid(final ArrayList<Product> mList) {
        LinearLayoutManager mGrid = new LinearLayoutManager(SpecialityListingActivity.this);
        rvSpeciality.setLayoutManager(mGrid);
        rvSpeciality.setHasFixedSize(true);


        SpecialityListingAdapter adapter = new SpecialityListingAdapter(this,mList);
        rvSpeciality.setAdapter(adapter);
        ItemClickSupport.addTo(rvSpeciality).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent productIntent = new Intent(SpecialityListingActivity.this, ProductDetailActivity.class);

                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();

                String stringObjectRepresentation = gson.toJson(mList.get(position));

                productIntent.putExtra("PRODUCT", stringObjectRepresentation);
                startActivity(productIntent);
            }
        });

    }


    private Drawable buildCounterDrawable(int count, int backgroundImageId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.shopping_layout, null);
        view.setBackgroundResource(backgroundImageId);
        if (count == 0) {
            View counterTextPanel = view.findViewById(R.id.counterValuePanel);
            counterTextPanel.setVisibility(View.GONE);
        } else {
            TextView textView = (TextView) view.findViewById(R.id.count);
            textView.setText("" + count);
        }
        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return new BitmapDrawable(getResources(), bitmap);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnecting, boolean isConnected) {
        if (!isConnecting && !isConnected) {
            AppHelper.Snackbar(this, clLayout, getString(R.string.connection_is_not_available), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
        } else if (isConnecting && isConnected) {
            AppHelper.Snackbar(this, clLayout, getString(R.string.connection_is_available), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
        } else {
            AppHelper.Snackbar(this, clLayout, getString(R.string.waiting_for_network), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_shop);
        int mCount = sharedPreference.retrieveProductCount();
        menuItem.setIcon(buildCounterDrawable(mCount, R.drawable.cart));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_shop) {
            Intent checkoutIntent = new Intent(SpecialityListingActivity.this, CheckoutActivity.class);
            startActivity(checkoutIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void invalidateCart() {
        invalidateOptionsMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateCart();
    }

    private List<Product> convertObjectArrayToListObject(Product[] allProducts){
        List<Product> mProduct = new ArrayList<Product>();
        Collections.addAll(mProduct, allProducts);
        return mProduct;
    }
}
