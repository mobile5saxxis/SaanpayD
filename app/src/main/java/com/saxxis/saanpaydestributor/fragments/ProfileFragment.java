package com.saxxis.saanpaydestributor.fragments;


import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.activities.leftmenu.AddressActivity;
import com.saxxis.saanpaydestributor.activities.leftmenu.ChangePasswordActivity;
import com.saxxis.saanpaydestributor.activities.leftmenu.HelpContactActivity;
import com.saxxis.saanpaydestributor.activities.main.ForgotPassActivity;
import com.saxxis.saanpaydestributor.activities.main.LoginActivity;
import com.saxxis.saanpaydestributor.activities.main.MainActivity;
import com.saxxis.saanpaydestributor.activities.main.SignupActivity;
import com.saxxis.saanpaydestributor.activities.payment.KYCUploadActivity;
import com.saxxis.saanpaydestributor.app.AppConstants;
import com.saxxis.saanpaydestributor.app.MixCartApplication;
import com.saxxis.saanpaydestributor.app.UserPref;
import com.saxxis.saanpaydestributor.helpers.AppHelper;
import com.saxxis.saanpaydestributor.helpers.PermissionHandler;
import com.saxxis.saanpaydestributor.interfaces.NetworkListener;
import com.saxxis.saanpaydestributor.interfaces.SmsListener;
import com.saxxis.saanpaydestributor.receivers.IncomingSms;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.saxxis.saanpaydestributor.app.AppConstants.SELECT_PROFILE_CAMERA;
import static com.saxxis.saanpaydestributor.app.AppConstants.SELECT_PROFILE_PICTURE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements NetworkListener {

    @BindView(R.id.profile_iv)
    CircleImageView userImage;

    @BindView(R.id.profile_et_fname)
    EditText etFName;




    @BindView(R.id.fl_profile)
    FrameLayout flProfile;
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

    @BindView(R.id.profile_inc)
    View profile;

    @BindView(R.id.login_inc)
    View loginAcc;

    @BindView(R.id.id_helpsupport)
    TextView helpSupport;

    @BindView(R.id.id_address)
    TextView idAddress;

    private String imagePath;
    private String dob;

    private String address;

    /**
     *
     *
     * login views
     *
     * */
    @BindView(R.id.tipl_logemailrnum)
    TextInputLayout textEmailnum;

    @BindView(R.id.tipl_logpassword)
    TextInputLayout textPassword;


    @BindView(R.id.regsterd_mbno)
    TextInputEditText userRegNum;

    @BindView(R.id.password)
    TextInputEditText userpassword;

    @BindView(R.id.terms_conditions)
    CheckBox termsCondions;

    @BindView(R.id.forget_pswd)
    Button forgotpasswordButton;

    @BindView(R.id.login)
    TextView loginBtn;

    @BindView(R.id.inputOtp)
    EditText etOTP;
    @BindView(R.id.progrs_vrfy)
    ProgressBar progrs_vrfy;
    @BindView(R.id.otp_layout)
    LinearLayout otp_layout;


//    @BindView(R.id.l_terms_condtns)
//    TextView termsCondtions;
//    @BindView(R.id.l_privacy_plcy)
//    TextView privacyPolicy;

    private UserPref mUser;

    private DatePickerDialog dobPickerDialog;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this,view);

        mUser = new UserPref(getActivity());
        if (!mUser.isLoggedIn()){
            profile.setVisibility(View.GONE);
            loginAcc.setVisibility(View.VISIBLE);
        }

        if (mUser.isLoggedIn()){
            profile.setVisibility(View.VISIBLE);
            loginAcc.setVisibility(View.GONE);
        }

//        fetchUserDetais();
        Calendar newCalendar = Calendar.getInstance();
        dobPickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dob=String.valueOf(dayOfMonth)+"/"+String.valueOf(month+1)+"/"+String.valueOf(year);
                txtdob.setText(dob);
                mUser.setDateOfBirth(dob);
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        etFName.setText(mUser.getName());
        etEmail.setText(mUser.getEmail());
        etMobileNumber.setText(mUser.getMobileNumber());
        txtdob.setText(mUser.getDateOfBirth());

        if(mUser.getGender().equalsIgnoreCase("Male")){
            rgGender.check(R.id.profile_rb_male);
        }
        if (mUser.getGender().equalsIgnoreCase("Female")){
            rgGender.check(R.id.profile_rb_female);
        }

        Glide.with(getActivity())
                .load(Uri.parse(mUser.getImagePath()))
                .error(R.drawable.profile)
                .into(userImage);
        if (mUser.isLoggedIn()) {
            fetchUserDetais();
        }

        IncomingSms.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                etOTP.setText(messageText);
                progrs_vrfy.setVisibility(View.GONE);
                setupOTP();
            }
        });
        //simpleSeekBar.setEnabled(false);
//        if (mUser.isLoggedIn()) {
//            checkLimit();
//        }

        return view;
    }

    @OnClick(R.id.id_helpsupport)
    public void gotoSupport(){
        AppHelper.LaunchActivity(getActivity(), HelpContactActivity.class);
    }

    private void fetchUserDetais() {
        Log.e("response",AppConstants.PROFILE_URL+mUser.getUserId());
        StringRequest request=new StringRequest(Request.Method.GET, AppConstants.PROFILE_URL+mUser.getUserId(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    AppHelper.logout(getActivity(),response);
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
                        address = profile.getString("address");
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

                        mUser.setName(name);
                        mUser.setKeyGender(gender);
                        mUser.setEmail(email);
                        mUser.setKeyAddress(address);
                        mUser.setMobileNumber(mn);
                        mUser.setPan(pancard);

                        mUser.setDateOfBirth(dob);
//                        if(!mUser.getImagePath().equals("empty")){
                        Glide.with(getActivity())
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
                        new AlertDialog.Builder(getActivity())
                                .setMessage("User Session Timed Out...Login Again")
                                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mUser.logoutUser();
                                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        Toast.makeText(getActivity(),"Logout Successfull",Toast.LENGTH_SHORT).show();
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
        getActivity().finish();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        Toast.makeText(getActivity(),"Logout Successfull",Toast.LENGTH_SHORT).show();
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
        AppHelper.LaunchActivity(getActivity(),ChangePasswordActivity.class);
    }

    @OnClick(R.id.profile_btn_save)
    void submitData(){
        try {
            String fName = etFName.getText().toString().trim();
//            String lName = etLName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String mn = etMobileNumber.getText().toString().trim();
            String pc = etPanCard.getText().toString().trim();
            String address =mUser.getKeyAddress();

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
                    AppHelper.logout(getActivity(),response);
                    try {
                        AppHelper.logout(getActivity(),response);
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        if (status.equals("ok")) {
                            String message = jsonObject.getString("message");
                            AppHelper.Snackbar(getActivity(), flProfile, message, AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
                        }
                    } catch (Exception ignored) {

                    }
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            getActivity().finish();
//                        }
//                    }, 3000);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    AppHelper.Snackbar(getActivity(),flProfile, error.getMessage(), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);
                }
            });

            MixCartApplication.getInstance().addToRequestQueue(str);
            //Submit To server
        }catch (Exception e){

        }
    }

    @OnClick(R.id.id_address)
    void addressChange(){
        Intent addressIntent=new Intent(getActivity(),AddressActivity.class);
        getActivity().startActivity(addressIntent);
    }

    @OnClick(R.id.profile_image_layout)
    void PickImage(){

        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
        if (PermissionHandler.checkPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent mIntent = new Intent();
            mIntent.setType("image/*");
            mIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(mIntent, getString(R.string.select_picture)), SELECT_PROFILE_PICTURE);
        } else {
            PermissionHandler.requestPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        }

    }

    private void cameraIntent() {
        if (PermissionHandler.checkPermission(getActivity(), Manifest.permission.CAMERA)) {

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            startActivityForResult(cameraIntent, SELECT_PROFILE_CAMERA);
        } else {

            PermissionHandler.requestPermission(getActivity(), Manifest.permission.CAMERA);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SELECT_PROFILE_PICTURE:
                    Uri filePath = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
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
            super.getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




    /**
     *
     *
     *
     * for login layout
     *
     *
     *
     *
     * */
//    @OnClick(R.id.l_terms_condtns)
//    void trmscon()
//    {
//        AppHelper.LaunchActivity(getActivity(),TermsConditionsActivity.class);
//    }
//
//    @OnClick(R.id.l_privacy_plcy)
//    void prcypolicy()
//    {
//        AppHelper.LaunchActivity(getActivity(),PrivacyPolicyActivity.class);
//    }

    @OnClick(R.id.create_act)
    void RedirectToSignUp() {
        AppHelper.LaunchActivity(getActivity(), SignupActivity.class);
    }

    @OnClick(R.id.forget_pswd)
    void forgotPassword(){
        AppHelper.LaunchActivity(getActivity(),ForgotPassActivity.class);
    }

    @OnClick(R.id.login)
    void userLogin () {

        if (termsCondions.isChecked()) {

            String email = userRegNum.getText().toString().trim();
            String password = userpassword.getText().toString().trim();

            if(!validate(password, email)){
                String url = null;
                try {
                    url = AppConstants.LOGIN_URL + "&username=" + URLEncoder.encode(email,"utf-8")
                            + "&password=" + URLEncoder.encode(password,"utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                Log.e("response",url);
                AppHelper.showDialog(getActivity(), "Please Relax..while we set up your account...");

                StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        handleresponse(response);
                        AppHelper.hideDialog();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AppHelper.hideDialog();
                        AppHelper.Snackbar(getActivity(), flProfile,
                                getString(R.string.connection_is_not_available),
                                AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);

                    }
                });
//            {
//                @Override
//                protected Response<String> parseNetworkResponse(NetworkResponse response) {
//                    try {
//                        JSONObject jsonObject = JsonUtils.mapToJson(response.headers);
//                        String response1 = jsonObject.getString("Set-Cookie");
//                        String[] parts = response1.split(";");
//                        mUser.setSessionId(parts[0]);
//                    }catch (Exception ignored){}
//                    return super.parseNetworkResponse(response);
//                }
//            };

                MixCartApplication.getInstance().addToRequestQueue(request);
            }
        } else {
            Toast.makeText(getActivity(), "Please Agree the Terms & Conditions", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validate(String password, String email) {
//        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
        if (email.isEmpty()){
            AppHelper.Snackbar(getActivity(),flProfile,"Please Enter Valid EmailId/UserName", AppConstants.MESSAGE_COLOR_ERROR,AppConstants.TEXT_COLOR);
            return true;
        }else if (password.isEmpty()){
            AppHelper.Snackbar(getActivity(),flProfile,"Please Enter Password", AppConstants.MESSAGE_COLOR_ERROR,AppConstants.TEXT_COLOR);
            return true;
        }
        return false;
    }

    private void handleresponse(String response) {

        try{
            AppHelper.logout(getActivity(),response);
            JSONObject jsonObject = new JSONObject(response);
            System.out.print(response);
            Log.e("response",response);
            String status = jsonObject.getString("status");
            if (status.equals("ok")){
                otp_layout.setVisibility(View.VISIBLE);
                profile.setVisibility(View.GONE);
                loginAcc.setVisibility(View.GONE);
                /*AppHelper.Snackbar(getActivity(),flProfile,"Login Success", AppConstants.MESSAGE_COLOR_SUCCESS,AppConstants.TEXT_COLOR);
                String userid = jsonObject.getString("userid");
                String name = jsonObject.getString("name");
                String ReferCode = jsonObject.getString("Refercode");
                String email = jsonObject.getString("email");
                String mobile = jsonObject.getString("mobile");
                String uniqueid = jsonObject.getString("uniquecode");
                String userType = jsonObject.getString("usertype");

                mUser.setMobileNumber(mobile);
                mUser.setReferralId(ReferCode);
                mUser.setUniqueId(uniqueid);
                mUser.setName(name);
                mUser.setUserDetails(userid);
                mUser.setKeyUserType(userType);*/
                //mUser.setLoggedIn();
                String session_id =jsonObject.getString("session_id");
                mUser.setSessionId(session_id);
                //Auto submit login
                //setupOTP();

                /*etFName.setText(mUser.getName());
                etEmail.setText(mUser.getEmail());
                etMobileNumber.setText(mUser.getMobileNumber());
                txtdob.setText(mUser.getDateOfBirth());*/


                /*if(mUser.getGender().equalsIgnoreCase("Male")){
                    rgGender.check(R.id.profile_rb_male);
                }

                if (mUser.getGender().equalsIgnoreCase("Female")){
                    rgGender.check(R.id.profile_rb_female);
                }

                Glide.with(getActivity())
                        .load(Uri.parse(mUser.getImagePath()))
                        .error(R.drawable.profile)
                        .into(userImage);*/
//                FetchWalletAmount.getAmount(getActivity());
                //AppHelper.LaunchActivity(getActivity(), MainActivity.class);

//                getActivity().finish();
            }else {
                //message
                new AlertDialog.Builder(getActivity())
                        .setMessage("Login Failed Please try Again")
                        .setCancelable(false)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
//                AppHelper.Snackbar(getActivity(),flProfile,"Login Failed Please try Again", AppConstants.MESSAGE_COLOR_ERROR,AppConstants.TEXT_COLOR);
            }
        }catch (Exception ignored){

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MixCartApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnecting, boolean isConnected) {
        if (!isConnecting && !isConnected) {
            AppHelper.Snackbar(getActivity(), flProfile, getString(R.string.connection_is_not_available), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
        } else if (isConnecting && isConnected) {
            AppHelper.Snackbar(getActivity(), flProfile, getString(R.string.connection_is_available), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
        } else {
            AppHelper.Snackbar(getActivity(), flProfile, getString(R.string.waiting_for_network), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);
        }
    }
    @OnClick(R.id.btn_verify_otp)
    void setupOTP(){
        String otp = etOTP.getText().toString().trim();
        if (otp.isEmpty()){
            //AppHelper.Snackbar(LoginActivity.this,clLogin,"Please Enter OTP", AppConstants.MESSAGE_COLOR_ERROR,AppConstants.TEXT_COLOR);
        }else{
            /*Intent hhtpIntent = new Intent(LoginActivity.this, HttpService.class);
            hhtpIntent.putExtra("otp", otp);
            hhtpIntent.putExtra("sessionid",mUser.getSessionId());
            startService(hhtpIntent);*/
            verifyOtpLogin(mUser.getSessionId(),otp);
        }

    }
    private void verifyOtpLogin(String sessionid, String otp) {

        String url = AppConstants.SUBMIT_OTP_LOGIN_URL + "&sessionid="+sessionid+"&otp="+otp;
        System.out.println(url);
        StringRequest str = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                try{
                    AppHelper.logout(getActivity(),response);
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if(status.equals("ok")){
                        //AppHelper.Snackbar(LoginActivity.this,clLogin,"Login Success", AppConstants.MESSAGE_COLOR_SUCCESS,AppConstants.TEXT_COLOR);
                        mUser.setLoggedIn();

                        //String username = jsonObject.getString("username");
                        String userid = jsonObject.getString("userid");
                        String name = jsonObject.getString("name");
                        String ReferCode = jsonObject.getString("Refercode");
                        String email = jsonObject.getString("email");
                        String mobile = jsonObject.getString("mobile");
                        String uniqueid = jsonObject.getString("uniquecode");
                        String userType = jsonObject.getString("usertype");

                        mUser.setMobileNumber(mobile);
                        mUser.setReferralId(ReferCode);
                        mUser.setUniqueId(uniqueid);
                        mUser.setName(name);
                        mUser.setUserDetails(userid);
                        mUser.setKeyUserType(userType);
                        mUser.setEmail(email);

                        etFName.setText(mUser.getName());
                        etEmail.setText(mUser.getEmail());
                        etMobileNumber.setText(mUser.getMobileNumber());
                        txtdob.setText(mUser.getDateOfBirth());


                        if(mUser.getGender().equalsIgnoreCase("Male")){
                            rgGender.check(R.id.profile_rb_male);
                        }

                        if (mUser.getGender().equalsIgnoreCase("Female")){
                            rgGender.check(R.id.profile_rb_female);
                        }

                        Glide.with(getActivity())
                        .load(Uri.parse(mUser.getImagePath()))
                        .error(R.drawable.profile)
                        .into(userImage);

                        /*Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);*/
                        AppHelper.LaunchActivity(getActivity(), MainActivity.class);
                        Toast.makeText(getActivity(),"Login Successfull",Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }

                }catch (Exception e){

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MixCartApplication.getInstance().addToRequestQueue(str);


    }
//    public void checkLimit(){
//        String url = AppConstants.LIMIT_CHECK+mUser.getUserId();
//
//        AppHelper.showDialog(getActivity(),"Checking transaction limit. Please Wait...");
//        StringRequest str = new StringRequest(url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String s) {
//                AppHelper.logout(getActivity(),s);
//                try{
//                    AppHelper.logout(getActivity(),s);
//                    AppHelper.hideDialog();
//                    JSONObject resp = new JSONObject(s);
//                    String status = resp.getString("status");
//                    if(status.equals("ok")){
//                        final String usertype = resp.getString("usertype");
//                        String atomwallet = resp.getString("atomwallet");
//                        String walletamt = resp.getString("walletamt");
//                        String atomwalletremainingamount = resp.getString("atomwalletremainingamount");
//                        String remainingwalletamount = resp.getString("remainingwalletamount");
//
//                        double remainingwalletamountInt = 0;
//                        if(remainingwalletamount!=null){
//                            remainingwalletamountInt = Double.parseDouble(remainingwalletamount);
//                        }
//                        double walletamtInt = 0;
//                        if(walletamt!=null){
//                            walletamtInt = Double.parseDouble(walletamt);
//                            //walletamtInt = walletamtInt+5000;
//                        }
//                        int max = (int) (walletamtInt+remainingwalletamountInt);
//                        simpleSeekBar.setMax(max);
//                        simpleSeekBar.setProgress((int) walletamtInt);
//                        //int per = ((walletamtInt *100)/(walletamtInt+remainingwalletamountInt));
//                        double amount = walletamtInt+remainingwalletamountInt;
//                        usedTV.setText(""+walletamtInt);
//                        maxTv.setText(""+amount);
//
//
//                    }
//                }catch (Exception e){
//                    AppHelper.hideDialog();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                AppHelper.hideDialog();
//            }
//        });
//
//        str.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        MixCartApplication.getInstance().addToRequestQueue(str);
//
//    }

}
