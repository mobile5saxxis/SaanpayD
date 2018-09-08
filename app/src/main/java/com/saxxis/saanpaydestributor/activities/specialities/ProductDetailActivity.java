package com.saxxis.saanpaydestributor.activities.specialities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.app.MixCartApplication;
import com.saxxis.saanpaydestributor.app.ShoppingPref;
import com.saxxis.saanpaydestributor.models.Product;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductDetailActivity extends AppCompatActivity {


    private static final String TAG = ProductDetailActivity.class.getSimpleName();

    @BindView(R.id.product_name)
    TextView productName;

    @BindView(R.id.product_disPrice)
    TextView productDiscPrice;

    @BindView(R.id.product_price)
    TextView productPrice;

    @BindView(R.id.product_description)
    TextView productDescription;

    @BindView(R.id.full_product_image)
    ImageView productImage;

    private Gson gson;

    private int cartProductNumber = 0;

    private ShoppingPref sharedPreference;
    private boolean added= false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sharedPreference = new ShoppingPref(ProductDetailActivity.this);

        GsonBuilder builder = new GsonBuilder();
        gson = builder.create();

        String productInStringFormat = getIntent().getExtras().getString("PRODUCT");
        final Product singleProduct = gson.fromJson(productInStringFormat, Product.class);
        if(singleProduct != null){
            setTitle(singleProduct.getName());

            productName.setText(singleProduct.getName());
            productDiscPrice.setText(getString(R.string.Rs,singleProduct.getDiscountprice()));
            productPrice.setText(getString(R.string.Rs,singleProduct.getPrice()));
            fetchDetailProducts(singleProduct.getId());
        }

        Button addToCartButton = (Button)findViewById(R.id.add_to_cart);
        assert addToCartButton != null;
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //increase product count
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
                  new AlertDialog.Builder(ProductDetailActivity.this)
                          .setMessage("Already addded to cart")
                          .setPositiveButton("close",null)
                          .show();
              }
            }
        });
    }


    private void fetchDetailProducts(String prodid) {
        final ArrayList<Product> mList = new ArrayList<>();
        String url = "http://sastabankproperty.in.md-38.webhostbox.net/recharge/index.php?option=com_jbackend&view=request&action=get&module=user&resource=proddetails&prodid="+prodid;
        StringRequest str = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try{
                    JSONObject obj = new JSONObject(s);
                    String status = obj.getString("status");
                    if(status.equals("ok")){
                        JSONArray jsonArray = obj.getJSONArray("message");

                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                        Picasso.with(ProductDetailActivity.this)
                                .load("http://sastabankproperty.in.md-38.webhostbox.net/recharge/joobi/user/media/images/products/thumbnails/"+jsonObject.getString("name"))
                                .centerCrop()
                                .resize(300,500)
                                .into(productImage);
                        productDescription.setText(Html.fromHtml("<strong>Product Description</strong><br/>" + jsonObject.getString("description")));

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

    private List<Product> convertObjectArrayToListObject(Product[] allProducts){
        List<Product> mProduct = new ArrayList<Product>();
        Collections.addAll(mProduct, allProducts);
        return mProduct;
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
            Intent checkoutIntent = new Intent(ProductDetailActivity.this, CheckoutActivity.class);
            startActivity(checkoutIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    private void invalidateCart() {
        invalidateOptionsMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateCart();
    }
}
