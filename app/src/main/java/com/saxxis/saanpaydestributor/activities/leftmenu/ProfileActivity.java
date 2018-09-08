package com.saxxis.saanpaydestributor.activities.leftmenu;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.activities.main.LoginActivity;
import com.saxxis.saanpaydestributor.app.AppConstants;
import com.saxxis.saanpaydestributor.app.MixCartApplication;
import com.saxxis.saanpaydestributor.app.UserPref;
import com.saxxis.saanpaydestributor.helpers.AppHelper;
import com.saxxis.saanpaydestributor.helpers.PermissionHandler;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.saxxis.saanpaydestributor.app.AppConstants.SELECT_PROFILE_CAMERA;
import static com.saxxis.saanpaydestributor.app.AppConstants.SELECT_PROFILE_PICTURE;

public class ProfileActivity extends AppCompatActivity {


    @BindView(R.id.cl_profile)
    CoordinatorLayout clProfile;

    @BindView(R.id.profile_iv)
    CircleImageView userImage;

    @BindView(R.id.profile_et_fname)
    EditText etFName;

//    @BindView(R.id.profile_et_lname)
//    EditText etLName;

    @BindView(R.id.profile_et_email)
    EditText etEmail;

    @BindView(R.id.profile_et_mn)
    EditText etMobileNumber;

    @BindView(R.id.profile_et_pc)
    EditText etPanCard;

//    @BindView(R.id.profile_et_address)
//    LinedEditText etAddress;

    @BindView(R.id.profile_txt_dob)
    TextView txtdob;

    @BindView(R.id.profile_iv_email_ver)
    LinearLayout ivEmailVer;

    @BindView(R.id.profile_iv_mn_ver)
    LinearLayout imMnVer;

    @BindView(R.id.profile_rg_gender)
    RadioGroup rgGender;

    @BindView(R.id.profilechangepassword)
    TextView profilrChangePassword;

    @BindView(R.id.profile_logout)
    TextView logiout;

    @BindView(R.id.profile_btn_save)
    Button profileSave;


    private String imagePath;
    private String dob;

    private UserPref mUser;

    private DatePickerDialog dobPickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.arrow);
        toolbar.setTitle("My Account");


        mUser = new UserPref(this);
        fetchUserDetais();
        Calendar newCalendar = Calendar.getInstance();
        dobPickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    dob=String.valueOf(dayOfMonth)+"/"+String.valueOf(month+1)+"/"+String.valueOf(year);
                txtdob.setText(dob);
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void fetchUserDetais() {
        Log.e("response",AppConstants.PROFILE_URL+mUser.getUserId());
        StringRequest request=new StringRequest(Request.Method.GET, AppConstants.PROFILE_URL+mUser.getUserId(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppHelper.logout(ProfileActivity.this,response);
                try{
                    System.out.println(response);
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("ok")){
//                        JSONArray profile1 = jsonObject.getJSONArray("data");
//                        JSONArray profileImage = jsonObject.getJSONArray("Image");
                        JSONObject profile = jsonObject.getJSONObject("data");

//                        if(profileImage.length()!=0) {
//                            JSONObject imagePath = profileImage.getJSONObject(0);
//                            ipath = AppConstants.SERVER_URL+ imagePath.getString("measure_image");
////                            ipath = AppConstants.SERVER_URL+"images/userimages/"+ imagePath.getString("measure_image");
//                        }

                        String name = profile.getString("name");
                        String username = profile.getString("username");
                        String email = profile.getString("email");
                        String mn = profile.getString("mobile");
                        String dob = profile.getString("dob");
                        String pancard = profile.getString("pancard");
                        String gender = profile.getString("gender");
                        String address = profile.getString("address");
                        String emailverify = profile.getString("emailverification");
                        String mnverify = profile.getString("mobileverification");
                        String referCode=profile.getString("refercode");
                        String uniquecode=profile.getString("uniquecode");
                        String ipath =profile.getString("profileimagepath")+profile.getString("profileimage");

                        etFName.setText(name);
                        etEmail.setText(email);
                        etMobileNumber.setText(mn);
                        txtdob.setText(dob);
                        etPanCard.setText(pancard);
//                        etAddress.setText(address);

//                        if(!mUser.getImagePath().equals("empty")){
                            Glide.with(ProfileActivity.this)
                                    .load(Uri.parse(ipath))
                                    .error(R.drawable.profile)
                                    .into(userImage);
//                        }
                        if(emailverify.equals("Verify")){
                            ivEmailVer.setVisibility(View.VISIBLE);
                        }
                        if(mnverify.equals("Verify")){
                            imMnVer.setVisibility(View.VISIBLE);
                        }

                        if(gender.equalsIgnoreCase("Male")){
                            rgGender.check(R.id.profile_rb_male);
                        }
                        if (gender.equalsIgnoreCase("Female")){
                            rgGender.check(R.id.profile_rb_female);
                        }

                    }
                    /*else if(status.equals("ko")) {
                        new AlertDialog.Builder(ProfileActivity.this)
                                .setMessage("User Session Timed Out...Login Again")
                                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mUser.logoutUser();
                                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        Toast.makeText(ProfileActivity.this,"Logout Successfull",Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }*/
                }catch (Exception ignored){

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
//        {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<>();
//                headers.put("Cookie",mUser.getSessionId());
//                return headers;
//            }
//        };
        MixCartApplication.getInstance().addToRequestQueue(request);

    }

    @OnClick(R.id.profile_txt_dob)
    void dobPicker(){
        dobPickerDialog.show();

    }

    @OnClick(R.id.profile_logout)
    void profileLogout(){
        mUser.logoutUser();
        logoutUser();
        ProfileActivity.this.finish();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        Toast.makeText(ProfileActivity.this,"Logout Successfull",Toast.LENGTH_SHORT).show();
    }

    private void logoutUser() {
        StringRequest request=new StringRequest(Request.Method.GET, AppConstants.LOGOUT_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
//        {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<>();
//                headers.put("Cookie",mUser.getSessionId());
//                return headers;
//            }
//        };

        MixCartApplication.getInstance().addToRequestQueue(request);
    }

    @OnClick(R.id.profilechangepassword)
    void changfePassword(){
        AppHelper.LaunchActivity(ProfileActivity.this,ChangePasswordActivity.class);
    }

    @OnClick(R.id.profile_btn_save)
    void submitData(){
        try {
            String fName = etFName.getText().toString().trim();
//            String lName = etLName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String mn = etMobileNumber.getText().toString().trim();
            String pc = etPanCard.getText().toString().trim();
            String address = mUser.getKeyAddress();

            int id = rgGender.getCheckedRadioButtonId();
            String gender;
            if (id == R.id.profile_rb_male) {
                gender = "Male";
            } else {
                gender = "Female";
            }
            String url = AppConstants.UPDATE_PROFILE + "&email=" + email + "&mobile=" + mn + "&firstname=" + URLEncoder.encode(fName,"utf-8")
                    + "&lastname=" + URLEncoder.encode("","utf-8") + "&gender=" +
                    gender + "&dob=" + dob + "&pancard=" + pc+ "&address=" + URLEncoder.encode(address,"utf-8") + "&id=" + mUser.getUserId();
            System.out.println(url);
            final StringRequest str = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        if (status.equals("ok")) {
                            String message = jsonObject.getString("message");
                            AppHelper.Snackbar(ProfileActivity.this, clProfile, message, AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
                        }
                    } catch (Exception ignored) {

                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 3000);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    AppHelper.Snackbar(ProfileActivity.this, clProfile, error.getMessage(), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);

                }
            });

            MixCartApplication.getInstance().addToRequestQueue(str);
            //Submit To server
        }catch (Exception e){

        }
    }

    @OnClick(R.id.profile_image_layout)
    void PickImage(){

        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }

    private void galleryIntent() {
        if (PermissionHandler.checkPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent mIntent = new Intent();
            mIntent.setType("image/*");
            mIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(mIntent, getString(R.string.select_picture)), SELECT_PROFILE_PICTURE);
        } else {
            PermissionHandler.requestPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        }

    }


    private void cameraIntent() {
        if (PermissionHandler.checkPermission(ProfileActivity.this, Manifest.permission.CAMERA)) {

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            startActivityForResult(cameraIntent, SELECT_PROFILE_CAMERA);
        } else {

            PermissionHandler.requestPermission(ProfileActivity.this, Manifest.permission.CAMERA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SELECT_PROFILE_PICTURE:
                    Uri filePath = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                        onCaptureImageResult(bitmap);
                    }catch (Exception e){

                    }
                    break;
                case SELECT_PROFILE_CAMERA:
                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    onCaptureImageResult(thumbnail);
                    break;
            }
        }
    }

    private void onCaptureImageResult(Bitmap thumbnail) {

        imagePath = getStringImage(thumbnail);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        userImage.setImageBitmap(thumbnail);
        uploadImagetoServer();
    }

    private void uploadImagetoServer() {
        StringRequest request=new StringRequest(Request.Method.POST, AppConstants.ADD_IMAGE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                AppHelper.logout(ProfileActivity.this,response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        })
        {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<>();
//                headers.put("Cookie",mUser.getSessionId());
//                return headers;
//            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("userid",mUser.getUserId());
                headers.put("image",imagePath);
                return headers;
            }
        };
        MixCartApplication.getInstance().addToRequestQueue(request);
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private int dpToPx(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
