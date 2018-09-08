package com.saxxis.saanpaydestributor.helpers;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.activities.main.MainActivity;
import com.saxxis.saanpaydestributor.app.UserPref;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by saxxis25 on 3/25/2017.
 */

public class AppHelper {


    private static ProgressDialog mDialog;
    private static Dialog dialog;


    /**
     * method to show the progress dialog
     *
     * @param mContext this is parameter for showDialog method
     */
    public static void showDialog(Context mContext, String message) {
        mDialog = new ProgressDialog(mContext);
        mDialog.setMessage(message);
        mDialog.setIndeterminate(true);
        mDialog.setCancelable(true);
        mDialog.show();
    }

    /**
     * method to show the progress dialog
     *
     * @param mContext this is parameter for showDialog method
     */
    public static void showDialog(Context mContext, String message, boolean cancelable) {
        mDialog = new ProgressDialog(mContext);
        mDialog.setMessage(message);
        mDialog.setIndeterminate(true);
        mDialog.setCancelable(cancelable);
        mDialog.show();
    }

    /**
     * method to hide the progress dialog
     */
    public static void hideDialog() {
        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    public static void LaunchActivity(Activity mContext, Class mActivity) {
        Intent mIntent = new Intent(mContext, mActivity);
        mContext.startActivity(mIntent);
        mContext.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    /**
     * method to show snack bar
     *
     * @param mContext    this is the first parameter for Snackbar  method
     * @param view        this is the second parameter for Snackbar  method
     * @param Message     this is the thirded parameter for Snackbar  method
     * @param colorId     this is the fourth parameter for Snackbar  method
     * @param TextColorId this is the fifth parameter for Snackbar  method
     */
    public static void Snackbar(Context mContext, View view, String Message, int colorId, int TextColorId) {
        Snackbar snackbar = Snackbar.make(view, Message, Snackbar.LENGTH_LONG);
        View snackView = snackbar.getView();
        snackView.setBackgroundColor(ContextCompat.getColor(mContext, colorId));
        TextView snackbarTextView = (TextView) snackView.findViewById(android.support.design.R.id.snackbar_text);
        snackbarTextView.setTextColor(ContextCompat.getColor(mContext, TextColorId));
        snackbar.show();
    }

    public static boolean isAndroid6() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }


    public static String spanDateFormater(String datefromdata){
        String dateString = datefromdata;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // use SimpleDateFormat to define how to PARSE the INPUT
        try{
            Date date = sdf.parse(dateString);

            // at this point you have a Date-Object with the value of
            // 1437059241000 milliseconds
            // It doesn't have a format in the way you think

            // use SimpleDateFormat to define how to FORMAT the OUTPUT
            sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
            System.out.println(sdf.format(date));
            return sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getWalletAmount(Context context){
        UserPref userPref=new UserPref(context);
        if (userPref.getWalletAmount()==-1){
         return "--";
        }
        return userPref.getWalletAmount()+"";
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void logout(final Context context, String response){
        try {
            JSONObject jsonObject=new JSONObject(response);
            if(jsonObject.has("status")){
                if (jsonObject.getString("status").equals("ko")){
                    String message="", loginagain = "";
                    if(jsonObject.has("message")){
                        message = jsonObject.getString("message");
                    }
                    if(jsonObject.has("loginagain")){
                        loginagain = jsonObject.getString("loginagain");
                    }
                    if(!loginagain.equalsIgnoreCase("") && loginagain.equalsIgnoreCase("1")){
                        new AlertDialog.Builder(context)
                                .setMessage(message)
                                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        UserPref mUser;
                                        mUser = new UserPref(context);
                                        mUser.logoutUser();
                                        //Intent intent = new Intent(context, LoginActivity.class);
                                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        Intent intent = new Intent(context, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        ((Activity)context).startActivity(intent);
                                        Toast.makeText(context,"Logout Successfull",Toast.LENGTH_SHORT).show();
                                    }
                                }).show();
                    }else {
                        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
