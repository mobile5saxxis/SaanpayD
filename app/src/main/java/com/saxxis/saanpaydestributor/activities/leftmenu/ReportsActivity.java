package com.saxxis.saanpaydestributor.activities.leftmenu;

import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReportsActivity extends AppCompatActivity {

    TextView submit_reports;
    TextInputEditText tvFromDate;
    TextInputEditText tvToDate;
    private DatePickerDialog mDatePickerDialogFron;
    private DatePickerDialog mDatePickerDialogTo;
    private UserPref userPref;
    String fromDate=null;
    String toDate=null;
    private String fileName;
    @BindView(R.id.toolbar)Toolbar toolbar;
    DownloadManager downloadManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setTitle("Reports");
        toolbar.setNavigationIcon(R.drawable.arrow);

        tvFromDate = (TextInputEditText) findViewById(R.id.txtpaid_fromdate);
        tvToDate = (TextInputEditText)findViewById(R.id.txtpaid_todate);
        submit_reports = (TextView) findViewById(R.id.submit);
        submit_reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Uri image_uri = Uri.parse("https://www.androidtutorialpoint.com/wp-content/uploads/2016/09/Beauty.jpg");
                DownloadData(image_uri,"Beauty.jpg");*/
                if(valid()){
                    getReports();
                }
            }
        });
        userPref = new UserPref(ReportsActivity.this);

        Calendar c = Calendar.getInstance();

        mDatePickerDialogFron = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                fromDate = year+"-"+(month+1)+"-"+dayOfMonth;
                tvFromDate.setText(year+"-"+(month+1)+"-"+dayOfMonth);
            }
        },c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        tvFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatePickerDialogFron.show();
            }
        });


        mDatePickerDialogTo = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                toDate = year+"-"+(month+1)+"-"+dayOfMonth;
                tvToDate.setText(year+"-"+(month+1)+"-"+dayOfMonth);
            }
        },c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        tvToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatePickerDialogTo.show();
            }
        });

    }
    private void getReports() {
        AppHelper.showDialog(ReportsActivity.this,"Loading Please Wait...");
        String url = AppConstants.REPORTS+userPref.getUserId()+"&fromdate="+fromDate+"&todate="+toDate;
        //String url = AppConstants.REPORTS+"1196"+"&fromdate="+fromDate+"&todate="+toDate;
        Log.e("reponse",url);

        StringRequest stringRequest = new StringRequest(url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppHelper.logout(ReportsActivity.this,response);
                Log.e("reponse",response);
                try {
                    AppHelper.hideDialog();
                    JSONObject jsonObject=new JSONObject(response);
                    if (jsonObject.getString("status").equals("ok")){
                        String url = jsonObject.getString("url");
                        Uri uri = Uri.parse(url);
                        String filename = jsonObject.getString("filename");
                        DownloadData(uri,filename);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                AppHelper.hideDialog();
            }
        });

        MixCartApplication.getInstance().addToRequestQueue(stringRequest);
    }
    private boolean valid() {
        if (fromDate==null){
            Toast.makeText(this,"Please Select Start date",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (toDate==null){
            Toast.makeText(this,"Please Select To date",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if (id==android.R.id.home){
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private long DownloadData (Uri uri, String fileName) {

        long downloadReference;

        // Create request for android download manager
        downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        //Setting title of request
        request.setTitle(fileName);

        //Setting description of request
        request.setDescription("Saanpay Report");

        //Set the local destination for the downloaded file to a path within the application's external files directory
        request.setDestinationInExternalFilesDir(ReportsActivity.this, Environment.DIRECTORY_DOWNLOADS,fileName);

        //Enqueue download and save into referenceId
        downloadReference = downloadManager.enqueue(request);

        return downloadReference;
    }





}
