package com.saxxis.saanpaydestributor.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.saxxis.saanpaydestributor.app.AppConstants;
import com.saxxis.saanpaydestributor.app.UserPref;
import com.saxxis.saanpaydestributor.interfaces.SmsListener;

/**
 * Created by saxxis25 on 4/21/2017.
 */

public class IncomingSms extends BroadcastReceiver {

    private static final String TAG = IncomingSms.class.getSimpleName();
    private UserPref mUser;
    private static SmsListener mListener;

    @SuppressWarnings("deprecation")
    @Override
    public void onReceive(Context context, Intent intent) {
        mUser = new UserPref(context);
        Bundle bundle  = intent.getExtras();

        try {
            if (bundle != null) {
                Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (Object aPdusObj : pdusObj) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
                    String senderAddress = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();

                    System.out.println("senderAddress"+senderAddress);
                    System.out.println("message"+message);
                    // if the SMS is not from our gateway, ignore the message
                    if (!senderAddress.toLowerCase().contains(AppConstants.SMS_ORIGIN.toLowerCase())) {
                        return;
                    }

                    // verification code from sms
                    String verificationCode = getVerificationCode(message);
                    System.out.println("verificationCode"+verificationCode);
                    Log.e(TAG, "OTP received: " + verificationCode);
                    mListener.messageReceived(verificationCode);

                    /*Intent hhtpIntent = new Intent(context, HttpService.class);
                    hhtpIntent.putExtra("otp", verificationCode);
                    hhtpIntent.putExtra("mobilenumber",mUser.getMobileNumber());
                    context.startService(hhtpIntent);*/
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }


    private String getVerificationCode(String message) {
        String code = null;
        int index = message.indexOf(AppConstants.OTP_DELIMITER);

        if (index != -1) {
            int start = index + 1;
            int length = 5;
            code = message.substring(start, start + length);
            return code;
        }

        //code = message.substring(5,10);
        return code;
    }
    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }


}
