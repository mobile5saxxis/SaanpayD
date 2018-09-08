package com.saxxis.saanpaydestributor.activities.payment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.app.AppConstants;
import com.saxxis.saanpaydestributor.app.MixCartApplication;
import com.saxxis.saanpaydestributor.app.UserPref;
import com.saxxis.saanpaydestributor.helpers.AppHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class KYCUploadActivity extends AppCompatActivity {
    @BindView(R.id.kyctoolbar)
    Toolbar toolbar;
    private UserPref mUser;
    @BindView(R.id.upload_btn)
    AppCompatButton upload_btn;
    @BindView(R.id.filesTv)
    TextView filesTv;
    @BindView(R.id.ettitle)
    TextView ettitle;
    @BindView(R.id.ettitleidnumber)
    TextView ettitleidnumber;
    @BindView(R.id.titledocumentationIv)
    ImageView titledocumentationIv;
    int PICK_IMAGE_REQUEST = 111;
    Bitmap imageBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kycupload);
        ButterKnife.bind(this);
        mUser = new UserPref(KYCUploadActivity.this);
        setSupportActionBar(toolbar);

        toolbar.setTitle("KYC Upload");
        toolbar.setNavigationIcon(R.drawable.arrow);

    }
    @OnClick(R.id.upload_btn)
    void onUploadBtnClick() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            //filesTv.setText(filePath.toString());

            try {
                //getting image from gallery
                imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                //Setting image to ImageView
                titledocumentationIv.setImageBitmap(imageBitmap);
                titledocumentationIv.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            super.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @OnClick(R.id.submit_btn)
    void submit(){
        AppHelper.showDialog(KYCUploadActivity.this,"Loading Please Wait...");
        StringRequest stringRequest = null;
        final String ttitle = ettitle.getText().toString();
        final String ttitleidnumber = ettitleidnumber.getText().toString();

        String url= AppConstants.KYC_UPLOAD;
        System.out.print(url);

        stringRequest = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.logout(KYCUploadActivity.this,response);
                        System.out.print(response);
                        AppHelper.hideDialog();
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            if (jsonObject.getString("status").equals("ok")){
                                new AlertDialog.Builder(KYCUploadActivity.this)
                                        .setMessage(jsonObject.getString("data"))
                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();

                                                finish();
                                            }
                                        }).create().show();
                            }
                            /*if (jsonObject.getString("status").equals("ko")){
                                new AlertDialog.Builder(KYCUploadActivity.this)
                                        .setMessage(jsonObject.getString("message"))
                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                //finish();
                                            }
                                        }).create().show();
                            }*/
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                //converting image to base64 string
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                param.put("userid", mUser.getUserId());
                param.put("title", ttitle);
                param.put("titleidnumber", ttitleidnumber);
                param.put("titledocument", imageString);
                /*param.put("aadharcardno", aadharcardno);
                param.put("panno",panno);
                param.put("voteridentitycard",voteridentitycard);
                param.put("drivinglicence",drivinglicence);
                param.put("passport",passport);
                param.put("aadharlink",imageString);
*/
                System.out.println(param.toString());
                return param;
            }
        };
        MixCartApplication.getInstance().addToRequestQueue(stringRequest);
    }
}
