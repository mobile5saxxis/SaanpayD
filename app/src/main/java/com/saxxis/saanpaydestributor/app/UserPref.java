package com.saxxis.saanpaydestributor.app;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by saxxis25 on 3/25/2017.
 */

public class UserPref {
    private String TAG = UserPref.class.getSimpleName();

    // Shared Preferences
    private SharedPreferences mPref;

    // Editor for Shared preferences
    private SharedPreferences.Editor mEditor;

    // Context
    private Context _context;

    // Shared pref mode
    private int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "mixcart_user";

    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_MN = "mobile_number";
    private static final String KEY_IMAGE_PATH = "image_path";
    private static final String KEY_SESSION_ID = "session_id";
    private static final String KEY_LOGGED_IN = "isLogged";
    private static final String KEY_WALLET_AMOUNT = "walletamount";
    private static final String KEY_UNIQUE_ID = "uniqueid";
    private static final String KEY_GENDER="male";
    private static final String KEY_DOB="dob";
    private static final String KEY_PAN="";
    private static final String KEY_ADDRESS="address";
    public static final String KEY_USER_TYPE = "distributer_or_customer";

    private static final String KEY_REFFERAL_ID = "refferalId";


    public UserPref(Context context) {
        this._context = context;
        mPref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        mEditor = mPref.edit();
    }

    public boolean isLoggedIn(){
        return mPref.getBoolean(KEY_LOGGED_IN,false);
    }

    public void setLoggedIn(){
        mEditor.putBoolean(KEY_LOGGED_IN,true);
        mEditor.commit();
    }

    public void setUserDetails(String UserId) {
        mEditor.putString(KEY_USER_ID,UserId);
        mEditor.commit();
    }

    public void setName(String name){
        mEditor.putString(KEY_NAME,name);
        mEditor.commit();
    }

    public String getName(){
        return mPref.getString(KEY_NAME,"name");
    }

    public void setEmail(String id){
        mEditor.putString(KEY_EMAIL,id);
        mEditor.commit();
    }

    public String getEmail(){
        return mPref.getString(KEY_EMAIL,"email");
    }

    public void setMobileNumber(String id){
        mEditor.putString(KEY_MN,id);
        mEditor.commit();
    }

    public String getMobileNumber(){
        return mPref.getString(KEY_MN,"empty");
    }

    public void setImagePath(String id){
        mEditor.putString(KEY_IMAGE_PATH,id);
        mEditor.commit();
    }

    public String getImagePath(){
        return mPref.getString(KEY_IMAGE_PATH,"empty");
    }

    public String getUserId(){
        return mPref.getString(KEY_USER_ID,"empty");
    }

    public void setSessionId(String id){
        mEditor.putString(KEY_SESSION_ID,id);
        mEditor.commit();
    }

    public String getSessionId(){
        return mPref.getString(KEY_SESSION_ID,"empty");
    }

    public void setWalletAmount(Float amount){
        mEditor.putFloat(KEY_WALLET_AMOUNT,amount).apply();
    }

    public Float getWalletAmount(){
        return mPref.getFloat(KEY_WALLET_AMOUNT,-1);
    }

    public void setReferralId(String id ){
        mEditor.putString(KEY_REFFERAL_ID,id);
        mEditor.commit();
    }

    public String getReferralId(){
        return mPref.getString(KEY_REFFERAL_ID,"empty");
    }

    public void setUniqueId(String id ){
        mEditor.putString(KEY_UNIQUE_ID,id);
        mEditor.commit();
    }

    public String getUniqueId(){
        return mPref.getString(KEY_UNIQUE_ID,"empty");
    }


    public void setKeyGender(String gender){
        mEditor.putString(KEY_GENDER,gender);
        mEditor.commit();
    }

    public String getGender(){
        return mPref.getString(KEY_GENDER,"male");
    }


    public void  setDateOfBirth(String dob){
        mEditor.putString(KEY_DOB,dob);
        mEditor.commit();
    }

    public String getDateOfBirth(){
        return mPref.getString(KEY_DOB,"select dob");
    }

    public void setKeyAddress(String address){
        mEditor.putString(KEY_ADDRESS,address);
        mEditor.commit();
    }

    public String getKeyAddress(){
        return mPref.getString(KEY_ADDRESS,"address");
    }


    public void setPan(String pan){
        mEditor.putString(KEY_PAN,pan);
        mEditor.commit();
    }

    public String getPan(){
        return mPref.getString(KEY_PAN,"FUN");
    }


    public void setKeyUserType(String userType){
        mEditor.putString(KEY_USER_TYPE,userType).commit();
    }

    public String getKeyUserType(){
        return mPref.getString(KEY_USER_TYPE,"U");
    }

    public void logoutUser(){
        mEditor.clear();
        mEditor.commit();

    }


}
