package com.saxxis.saanpaydestributor.activities.leftmenu;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.adapters.HelpAdapter;
import com.saxxis.saanpaydestributor.app.AppConstants;
import com.saxxis.saanpaydestributor.app.MixCartApplication;
import com.saxxis.saanpaydestributor.app.UserPref;
import com.saxxis.saanpaydestributor.helpers.AppHelper;
import com.saxxis.saanpaydestributor.models.Chat;
import com.saxxis.saanpaydestributor.models.Help;
import com.saxxis.saanpaydestributor.utils.ui.EndlessRecyclerOnScrollListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HelpContactActivity extends AppCompatActivity {


    @BindView(R.id.addquery)
    TextView addQuery;
    @BindView(R.id.item_progress_bar)
    ProgressBar recvReloadProgressBar;

    @BindView(R.id.recvprogressview)
    ProgressBar recvProgress;

    @BindView(R.id.recv_querieslist)
    RecyclerView recvQueriesList;

    private UserPref mPref;
    String ordernumber="";
    private HelpAdapter helpAdapter;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_contact);
        ButterKnife.bind(this);
        mContext = this;

        mPref=new UserPref(HelpContactActivity.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.arrow);


        Intent intent = getIntent();
        ordernumber = intent.getStringExtra("ordernumber");

        fetchQueries();
    }

    /*private void fetchQueries() {
        recvQueriesList.setHasFixedSize(true);
        recvQueriesList.setLayoutManager(new LinearLayoutManager(HelpContactActivity.this,LinearLayoutManager.VERTICAL,false));
        recvProgress.setVisibility(View.VISIBLE);
        Log.e("response", AppConstants.LISTOF_SUPPORT+mPref.getUserId());
        StringRequest request=new StringRequest(Request.Method.GET, AppConstants.LISTOF_SUPPORT+mPref.getUserId(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                recvProgress.setVisibility(View.GONE);
                final ArrayList<ListQueries> mData = new ArrayList<>();
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if(status.equals("ok")){
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        int len = jsonArray.length();
                        if (len==0){
                            Toast.makeText(HelpContactActivity.this, "No Recent Queries Found", Toast.LENGTH_SHORT).show();
                        }
                        for (int i=0;i<len;i++){
                            JSONObject obj = jsonArray.getJSONObject(i);
                            mData.add(new ListQueries(obj.getString("ticket"),obj.getString("title"),obj.getString("message")));
                        }
                        recvQueriesList.setAdapter(new ListOfQueriesAdapter(HelpContactActivity.this,mData));

                        ItemClickSupport.addTo(recvQueriesList).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                            @Override
                            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                               startActivity(new Intent(HelpContactActivity.this,AddReplyActivity.class ));
                            }
                        });
                    }
                }catch (Exception ignored){

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        MixCartApplication.getInstance().addToRequestQueue(request);

    }*/

    @OnClick(R.id.addquery)
    void addQuery(){
        //AppHelper.LaunchActivity(HelpContactActivity.this,AddQueryActivity.class);
        Intent mIntent = new Intent(HelpContactActivity.this, AddQueryActivity.class);
        mIntent.putExtra("ordernumber",ordernumber);
        startActivity(mIntent);
        finish();
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


    private void fetchQueries() {
        recvQueriesList.setHasFixedSize(true);
        recvQueriesList.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recvProgress.setVisibility(View.VISIBLE);
        Log.e("response", AppConstants.LISTOF_SUPPORT+mPref.getUserId()+"&ordernumber="+ordernumber);
        StringRequest request=new StringRequest(Request.Method.GET, AppConstants.LISTOF_SUPPORT+mPref.getUserId()+"&ordernumber="+ordernumber, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                AppHelper.logout(HelpContactActivity.this,response);
                recvProgress.setVisibility(View.GONE);
                final ArrayList<Help> mData = new ArrayList<>();
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if(status.equals("ok")){
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        int len = jsonArray.length();
                        for (int i=0;i<len;i++){
                            JSONObject obj = jsonArray.getJSONObject(i);
                            Help help = new Help();
                            help.setId(obj.getString("id"));
                            help.setTicket(obj.getString("ticket"));
                            help.setOrdernumber(obj.getString("ordernumber"));
                            help.setUserid(obj.getString("userid"));
                            help.setTitle(obj.getString("title"));
                            help.setMessage(obj.getString("message"));
                            help.setStatus(obj.getString("status"));
                            JSONArray jsonArrayChat = obj.getJSONArray("chatlist");
                            int lenChat = jsonArrayChat.length();
                            final ArrayList<Chat> mDataChat = new ArrayList<>();
                            for(int j=0;j<lenChat;j++){
                                JSONObject objChat = jsonArrayChat.getJSONObject(j);
                                Chat chat = new Chat();
                                chat.setId(objChat.getString("id"));
                                chat.setTicket(objChat.getString("ticket"));
                                chat.setUserid(objChat.getString("userid"));
                                chat.setReplymessage(objChat.getString("replymessage"));
                                chat.setStatus(objChat.getString("status"));
                                mDataChat.add(chat);
                            }
                            help.setChats(mDataChat);
                            mData.add(help);
                        }

                        helpAdapter=new HelpAdapter(mContext,mData);
                        recvQueriesList.setAdapter(helpAdapter);
                        recvQueriesList.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
                            @Override
                            public void onLoadMore() {
                                addDataToList();
                            }
                        });
                    }
                }catch (Exception ignored){

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        MixCartApplication.getInstance().addToRequestQueue(request);
    }

    private void addDataToList() {
        recvReloadProgressBar.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                for (int i = 0; i <= 30; i++) {
////                    mStringList.add("SampleText : " + mLoadedItems);
////                    mLoadedItems++;
//                }

                helpAdapter.notifyDataSetChanged();
                recvReloadProgressBar.setVisibility(View.GONE);
            }
        }, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchQueries();
        if(helpAdapter!=null){
            helpAdapter.notifyDataSetChanged();
        }

    }
}
