package com.saxxis.saanpaydestributor.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.app.MixCartApplication;
import com.saxxis.saanpaydestributor.app.UserPref;
import com.saxxis.saanpaydestributor.helpers.AppHelper;
import com.saxxis.saanpaydestributor.utils.VolleyMultipartRequest;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReqForPaymentFragment extends Fragment {

    @BindView(R.id.nameofbankET)
    EditText nameofbankET;
    @BindView(R.id.amountEt)
    EditText amountEt;
    //@BindView(R.id.dateET)
    //EditText dateET;
    @BindView(R.id.depositedbankET)
    Spinner depositedbankET;
    @BindView(R.id.browse)
    Button browse;
    @BindView(R.id.imageName)
    TextView imageName;
    @BindView(R.id.profile_btn_save)
    Button profile_btn_save;
    @BindView(R.id.accountHolderNameET)
    EditText accountHolderNameET;
    @BindView(R.id.accountNumberET)
    EditText accountNumberET;
    @BindView(R.id.referenceNumberET)
    EditText referenceNumberET;
    @BindView(R.id.checkNumberET)
    EditText checkNumberET;


    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.time)
    TextView time;

    @BindView(R.id.accountHolderNameTV)
    TextView accountHolderNameTV;
    @BindView(R.id.accountNumberTV)
    TextView accountNumberTV;
    @BindView(R.id.checkNumberTV)
    TextView checkNumberTV;
    @BindView(R.id.nameofbankTV)
    TextView nameofbankTV;

    @BindView(R.id.accountHolderNameLine)
    View accountHolderNameLine;
    @BindView(R.id.accountNumberLine)
    View accountNumberLine;
    @BindView(R.id.checkNumberLine)
    View checkNumberLine;
    @BindView(R.id.nameofbankLine)
    View nameofbankLine;


    Bitmap photoBitmap = null;
    String[] bankList = {"Axis bank"};
    int mode = 0;
    Calendar calender = Calendar.getInstance();

    public static ReqForPaymentFragment newInstance() {
        return new ReqForPaymentFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_req_for_payment, container, false);
        ButterKnife.bind(this, v);

        setMode(1);

        depositedbankET.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, bankList));

        return v;
    }

    public void setMode(int mode) {
        this.mode = mode;

        switch (mode) {
            case 1:
                nameofbankET.setVisibility(View.GONE);
                nameofbankTV.setVisibility(View.GONE);
                nameofbankLine.setVisibility(View.GONE);

                accountHolderNameET.setVisibility(View.GONE);
                accountHolderNameTV.setVisibility(View.GONE);
                accountHolderNameLine.setVisibility(View.GONE);

                accountNumberET.setVisibility(View.GONE);
                accountNumberTV.setVisibility(View.GONE);
                accountNumberLine.setVisibility(View.GONE);

                checkNumberET.setVisibility(View.GONE);
                checkNumberTV.setVisibility(View.GONE);
                checkNumberLine.setVisibility(View.GONE);
                break;
            case 2:
                nameofbankET.setVisibility(View.VISIBLE);
                nameofbankTV.setVisibility(View.VISIBLE);
                nameofbankLine.setVisibility(View.VISIBLE);

                accountHolderNameET.setVisibility(View.VISIBLE);
                accountHolderNameTV.setVisibility(View.VISIBLE);
                accountHolderNameLine.setVisibility(View.VISIBLE);

                accountNumberET.setVisibility(View.VISIBLE);
                accountNumberTV.setVisibility(View.VISIBLE);
                accountNumberLine.setVisibility(View.VISIBLE);

                checkNumberET.setVisibility(View.VISIBLE);
                checkNumberTV.setVisibility(View.VISIBLE);
                checkNumberLine.setVisibility(View.VISIBLE);
                break;
            case 3:
                nameofbankET.setVisibility(View.VISIBLE);
                nameofbankET.setVisibility(View.VISIBLE);
                nameofbankLine.setVisibility(View.VISIBLE);

                accountHolderNameET.setVisibility(View.VISIBLE);
                accountHolderNameTV.setVisibility(View.VISIBLE);
                accountHolderNameLine.setVisibility(View.VISIBLE);

                accountNumberET.setVisibility(View.GONE);
                accountNumberTV.setVisibility(View.GONE);
                accountNumberLine.setVisibility(View.GONE);

                checkNumberET.setVisibility(View.GONE);
                checkNumberTV.setVisibility(View.GONE);
                checkNumberLine.setVisibility(View.GONE);
                break;

        }


    }

    @OnClick(R.id.browse)
    void browse() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 1);
    }

    @OnClick(R.id.profile_btn_save)
    void save() {
        String bankName = nameofbankET.getText().toString().trim();
        String amount = amountEt.getText().toString().trim();
        String name = accountHolderNameET.getText().toString().trim();
        String accountNo = accountNumberET.getText().toString().trim();
        String referenceNo = referenceNumberET.getText().toString().trim();
        String checkNo = checkNumberET.getText().toString().trim();
        String timeString = time.getText().toString().trim();
        String dateString = date.getText().toString().trim();
        String bankDeposit = "Axis bank";

        if (bankName.isEmpty() && nameofbankET.getVisibility() == View.VISIBLE) {
            nameofbankET.setError("Cannot be empty");
            return;
        } else if (amount.isEmpty() && amountEt.getVisibility() == View.VISIBLE) {
            amountEt.setError("Cannot be empty");
            return;
        } else if (name.isEmpty() && accountHolderNameET.getVisibility() == View.VISIBLE) {
            accountHolderNameET.setError("Cannot be empty");
            return;
        } else if (accountNo.isEmpty() && accountNumberET.getVisibility() == View.VISIBLE) {
            accountNumberET.setError("Cannot be empty");
            return;
        } else if (referenceNo.isEmpty() && referenceNumberET.getVisibility() == View.VISIBLE) {
            referenceNumberET.setError("Cannot be empty");
            return;
        } else if (checkNo.isEmpty() && checkNumberET.getVisibility() == View.VISIBLE) {
            checkNumberET.setError("Cannot be empty");
            return;
        } else if (dateString.equals("Date")) {
            date.setError("Cannot be empty");
            return;
        } else if (timeString.equals("Time")) {
            time.setError("Cannot be empty");
            return;
        } else if (photoBitmap == null) {
            Toast.makeText(getActivity(), "Please select receipt", Toast.LENGTH_SHORT).show();
            return;
        }

        AppHelper.showDialog(getActivity(), "Loading Please Wait...");
        String url = null;
        try {
            switch (mode) {
                case 1:
                    url = "http://saanpay.com/beta/index.php?option=com_jbackend&view=request&module=user&action=post&resource=distributormoneydeposit" +
                            "&userid=" + new UserPref(getActivity()).getUserId() +
                            "&depositebank=" + URLEncoder.encode(bankDeposit, "utf-8") +
                            "&amount=" + URLEncoder.encode(amount, "utf-8") +
                            "&paymentmode=" + mode +
                            "&referencenumber=" + URLEncoder.encode(referenceNo, "utf-8") +
                            "&time=" + URLEncoder.encode(timeString, "utf-8") +
                            "&date=" + URLEncoder.encode(dateString, "utf-8");
                    break;
                case 2:
                    url = "http://saanpay.com/beta/index.php?option=com_jbackend&view=request&module=user&action=post&resource=distributormoneydeposit" +
                            "&userid=" + new UserPref(getActivity()).getUserId() +
                            "&bankname=" + URLEncoder.encode(bankName, "utf-8") +
                            "&name=" + URLEncoder.encode(name, "utf-8") +
                            "&depositebank=" + URLEncoder.encode(bankDeposit, "utf-8") +
                            "&amount=" + URLEncoder.encode(amount, "utf-8") +
                            "&paymentmode=" + mode +
                            "&accountno=" + URLEncoder.encode(accountNo, "utf-8") +
                            "&referencenumber=" + URLEncoder.encode(referenceNo, "utf-8") +
                            "&chequenumber=" + URLEncoder.encode(checkNo, "utf-8") +
                            "&time=" + URLEncoder.encode(timeString, "utf-8") +
                            "&date=" + URLEncoder.encode(dateString, "utf-8");
                    break;
                case 3:
                    url = "http://saanpay.com/beta/index.php?option=com_jbackend&view=request&module=user&action=post&resource=distributormoneydeposit" +
                            "&userid=" + new UserPref(getActivity()).getUserId() +
                            "&bankname=" + URLEncoder.encode(bankName, "utf-8") +
                            "&name=" + URLEncoder.encode(name, "utf-8") +
                            "&depositebank=" + URLEncoder.encode(bankDeposit, "utf-8") +
                            "&amount=" + URLEncoder.encode(amount, "utf-8") +
                            "&paymentmode=" + mode +
                            "&referencenumber=" + URLEncoder.encode(referenceNo, "utf-8") +
                            "&time=" + URLEncoder.encode(timeString, "utf-8") +
                            "&date=" + URLEncoder.encode(dateString, "utf-8");
                    break;
            }


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.i("url", "" + url);

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {

            @Override
            public void onResponse(NetworkResponse networkResponse) {
                String response = new String(networkResponse.data);
                AppHelper.logout(getActivity(), response);
                AppHelper.hideDialog();
                Log.i("response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("ok")) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("ThankYou For Your Request!")
                                .setMessage("our field officer will contact you with in 24 hours")
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        getActivity().finish();
                                    }
                                })
                                .create().show();
                    }
                    if (status.equals("ko")) {
                        new AlertDialog.Builder(getActivity())
                                .setMessage(jsonObject.getString("message"))
                                .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .create().show();
                    }
                } catch (Exception ignored) {
                    AppHelper.hideDialog();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                photoBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                params.put("receipt", new DataPart("fimage.png", byteArray, "image/jpeg"));

                return params;
            }
        };
        MixCartApplication.getInstance().addToRequestQueue(volleyMultipartRequest);

    }

    @OnClick(R.id.date)
    void pickDate() {
        DatePickerDialog dobPicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calender.set(year, monthOfYear, dayOfMonth);

                date.setText(DateFormat.format("yyyy-MM-dd", new Date(calender.getTimeInMillis())).toString());
            }

        }, calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DAY_OF_MONTH));
        dobPicker.show();
    }

    @OnClick(R.id.time)
    void pickTime() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                calender.set(Calendar.HOUR_OF_DAY, selectedHour);
                calender.set(Calendar.MINUTE, selectedMinute);
                time.setText(DateFormat.format("HH:mm", new Date(calender.getTimeInMillis())).toString());
            }
        }, calender.get(Calendar.HOUR_OF_DAY), calender.get(Calendar.MINUTE), true);
        mTimePicker.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            photoBitmap = BitmapFactory.decodeFile(picturePath);
            imageName.setText("receipt.jpg");
        }
    }
}
