package com.saxxis.saanpaydestributor.app;

import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.models.Category;

import java.util.ArrayList;

/**
 * Created by saxxis25 on 3/25/2017.
 */

public class AppConstants {

    public static final String SERVER_URL = "http://saanpay.com/beta/";

    public static String LOGIN_URL = SERVER_URL + "index.php?option=com_jbackend&view=request&module=user&action=post&resource=login";

    public static String LOGOUT_URL = SERVER_URL + "index.php?option=com_jbackend&view=request&module=user&action=get&resource=logout";

    public static String REGISTER_URL = SERVER_URL + "index.php?option=com_jbackend&view=request&module=user&action=post&resource=register";

    public static String MOBILE_OPERATOR_URL = SERVER_URL + "index.php?option=com_jbackend&view=request&module=user&action=get&resource=getoperators&operatortype=";

    public static String DTH_OPERATOR_URL = SERVER_URL + "index.php?option=com_jbackend&view=request&module=user&action=get&resource=getoperators&operatortype=2";

    public static String DC_OPERATOR_URL = SERVER_URL + "index.php?option=com_jbackend&view=request&module=user&action=get&resource=getoperators&operatortype=3";

    public static String RECHARGE_URL = SERVER_URL + "index.php?option=com_jbackend&view=request&module=user&action=get&resource=generateorder";

    public static String DTH_RECHARGE_URL = SERVER_URL + "index.php?option=com_jbackend&view=request&module=user&action=get&resource=generateorder";

    public static String DC_RECHARGE_URL = SERVER_URL + "index.php?option=com_jbackend&view=request&module=user&action=get&resource=generateorder";

    public static String GENERATE_ORDER = SERVER_URL + "index.php?option=com_jbackend&view=request&module=user&action=get&resource=generateorder";

    public static String ORDERS_URL = SERVER_URL +  "index.php?option=com_jbackend&view=request&action=get&module=user&resource=getorderlist&userid=";

    public static String WALLET_URL = SERVER_URL + "index.php?option=com_jbackend&view=request&action=get&module=user&resource=getwalletamount";

    public static String FETCH_OPERATOR_URL = SERVER_URL + "index.php?option=com_jbackend&view=request&module=user&action=get&resource=operatorname&mobile=";

    //public static String SUBMIT_OTP_URL = SERVER_URL + "index.php?option=com_jbackend&view=request&action=get&module=user&resource=otpactive";
    public static String SUBMIT_OTP_URL = SERVER_URL + "index.php?option=com_jbackend&view=request&module=user&action=get&resource=otpactivation";
    public static String SUBMIT_OTP_LOGIN_URL = SERVER_URL + "index.php?option=com_jbackend&view=request&module=user&action=get&resource=loginwithotp";

    public static String ADS_SLIDER = SERVER_URL +"index.php?option=com_jbackend&view=request&action=post&module=user&resource=register&index.php?option=com_jbackend&view=request&action=get&module=user&resource=sliders";

    //public static String FETCH_OTP_URL = SERVER_URL +"index.php?option=com_jbackend&view=request&action=post&module=user&resource=otpactives&mobile=";
    public static String FETCH_OTP_URL = SERVER_URL +"index.php?option=com_jbackend&view=request&module=user&action=post&resource=register&";

    public static String PROFILE_URL = SERVER_URL +"index.php?option=com_jbackend&view=request&module=user&action=get&resource=getprofile&userid=";

    public static String ADD_IMAGE_URL = SERVER_URL + "index.php?option=com_jbackend&view=request&module=user&action=post&resource=addprofileimage";

    public static String FETCH_PLANS= SERVER_URL +"index.php?option=com_jbackend&view=request&action=get&module=user&resource=serviceplans";

    public static String UPDATE_PROFILE= SERVER_URL +"index.php?option=com_jbackend&view=request&module=user&action=post&resource=updateprofile";

    public static String MONEY_ORDERID_REQ= SERVER_URL +"index.php?option=com_jbackend&view=request&module=user&action=get&resource=tranferamounttowallet";

    public static String UPDATE_MONEY_URL = SERVER_URL+ "index.php?option=com_jbackend&view=request&action=post&module=user&resource=addmoneytowallet";

    public static String FORGOTPASSWORD=SERVER_URL+"index.php?option=com_jbackend&view=request&module=user&action=get&resource=forgotpassword&email=";

    public static final String RESETPASSWORD_OTP=SERVER_URL+"index.php?option=com_jbackend&view=request&module=user&action=get&resource=resetpasswordfromotp&otp=";

    public static final String CHANGE_PASSWORD=SERVER_URL+"index.php?option=com_jbackend&view=request&module=user&action=get&resource=changepassword&userid=";

    public static final String WALLETTOWALLET_TRSF=SERVER_URL+"index.php?option=com_jbackend&view=request&module=user&action=post&resource=transferamounttoanotherwallet&userid=";

    public static final String RECHARGEAPI=SERVER_URL+"index.php?option=com_jbackend&view=request&module=user&action=get&resource=connectrechargeapi&userid=";

    public static final String WALLET_TRNX=SERVER_URL+"index.php?option=com_jbackend&view=request&module=user&action=get&resource=getwallettransactions&userid=";

    public static final String ALL_OPERATORS=SERVER_URL+"index.php?option=com_jbackend&view=request&module=user&action=get&resource=getoperators&operatortype=";

    public static final String ORDER_DETAILS=SERVER_URL+"index.php?option=com_jbackend&view=request&module=user&action=get&resource=getorderdetails&userid=";//1040&ordernumber=";

    public static final String SUPPORT_AND_HELP_SUBMIT=SERVER_URL+"index.php?option=com_jbackend&view=request&module=user&action=post&resource=genarateticketsupport&userid=";

    public static final String LISTOF_SUPPORT=SERVER_URL+"index.php?option=com_jbackend&view=request&module=user&action=get&resource=getticketsupport&userid=";

    public static final String DEPOSIT_PICKUP=SERVER_URL+"index.php?option=com_jbackend&view=request&module=user&action=post&resource=cashpickup";


    /*IMPS URLS */
    public static final String GET_SENDER_ID = SERVER_URL+"index.php?option=com_jbackend&view=request&module=user&action=get&resource=senderid&userid=";

    public static final String BANKS_LIST = SERVER_URL+"index.php?option=com_jbackend&view=request&module=user&action=post&resource=banklist";

    public static final String RECEIVEING_REGISTER = SERVER_URL+"index.php?option=com_jbackend&view=request&module=user&action=post&resource=impsmoneytranfer&userid=";

    public static final String GET_BENEFICIARY_LIST = SERVER_URL + "index.php?option=com_jbackend&view=request&module=user&action=get&resource=getbeneficierylist&userid=";

    public static final String AMOUNT_COMMISION = SERVER_URL + "index.php?option=com_jbackend&view=request&module=user&action=get&resource=amountcomission&amount=";

    public static final String GET_USERNAME_FROM_MOBILE = SERVER_URL + "index.php?option=com_jbackend&view=request&module=user&action=get&resource=userdetailsusingmobile&mobile=";

    public static final String RESEND_OTP = SERVER_URL+"index.php?option=com_jbackend&view=request&module=user&action=post&resource=resendbeneficiaryotp&userid=";

    public static final String SUBMIT_BANK_PAYMENT_OTP = SERVER_URL+"index.php?option=com_jbackend&view=request&module=user&action=post&resource=beneficiaryotpverifiction&userid=";

    public static final String LIMIT_CHECK = SERVER_URL+"index.php?option=com_jbackend&view=request&module=user&action=get&resource=getuserdetails&userid=";

    public static final String KYC_UPLOAD = SERVER_URL+"index.php?option=com_jbackend&view=request&module=user&action=post&resource=kycdocuments";

    public static final String HELP_REPLY = SERVER_URL+"index.php?option=com_jbackend&view=request&module=user&action=get&resource=userreplyticket&userid=";

    public static final String REPORTS = SERVER_URL+"index.php?option=com_jbackend&view=request&module=user&action=get&resource=distributordetais&userid=";



    /**
     * for toast and snackbar
     */
    public static final int MESSAGE_COLOR_ERROR = R.color.colorOrangeLight;
    public static final int MESSAGE_COLOR_WARNING = R.color.colorOrange;
    public static final int MESSAGE_COLOR_SUCCESS = R.color.colorGreenDark;
    public static final int TEXT_COLOR = R.color.colorWhite;


    public static final int PERMISSION_REQUEST_CODE = 0x009;
    public static final int CONTACTS_PERMISSION_REQUEST_CODE = 0x009;

    public static final int SELECT_PROFILE_PICTURE = 0x005;
    public static final int SELECT_PROFILE_CAMERA = 0x006;

    public static final int PLAN_REQUEST = 0x001;


    public static final String SMS_ORIGIN = "SANPAY";

    public static final String OTP_DELIMITER = ":";
    public static final String ADMIN_ID = "852";

    /**
     * Activity Results Constants
     */
    public static final int PICK_CONTACT = 101;

    public static ArrayList<Category> getPlansCategories(){
        ArrayList<Category> mData = new ArrayList<>();
        mData.add(new Category(1,1,"2G Plans"));
        mData.add(new Category(2,2,"Combo Packs"));
        mData.add(new Category(3,3,"Full Talktime"));
        mData.add(new Category(4,4,"ISD Plans"));
        mData.add(new Category(5,5,"SMS Plans"));
        mData.add(new Category(6,6,"Special Packs"));
        mData.add(new Category(7,7,"Top Up Recharges"));
        mData.add(new Category(8,8,"3G Plans"));
        return mData;
    }

    public static ArrayList<Category> getDTHPlans(){
        ArrayList<Category> mData = new ArrayList<>();
        mData.add(new Category(10,10,"Monthly Packs"));
        mData.add(new Category(11,11,"2 Months Packs"));
        mData.add(new Category(12,12,"3 Months Packs"));
        mData.add(new Category(13,13,"Annual Pack"));

        return mData;
    }
}
