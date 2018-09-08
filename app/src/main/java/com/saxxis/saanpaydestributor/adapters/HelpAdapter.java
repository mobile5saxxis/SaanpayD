package com.saxxis.saanpaydestributor.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.saxxis.saanpaydestributor.R;
import com.saxxis.saanpaydestributor.activities.leftmenu.AddReplyActivity;
import com.saxxis.saanpaydestributor.app.AppConstants;
import com.saxxis.saanpaydestributor.models.Chat;
import com.saxxis.saanpaydestributor.models.Help;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by saxxis25 on 4/5/2017.
 */

public class HelpAdapter extends RecyclerView.Adapter<HelpAdapter.HelpHolder> {

    private Context mContext;
    private ArrayList<Help> mData;

    public HelpAdapter(Context cont, ArrayList<Help> data){
        this.mContext = cont;
        this.mData = data;
    }

    @Override
    public HelpHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.help_item,parent,false);
        return new HelpHolder(view);
    }

    @Override
    public void onBindViewHolder(HelpHolder holder, int position) {
        Help help = mData.get(position);
        ArrayList<Chat> chats = new ArrayList<>();
        chats = help.getChats();
        int length = chats.size();
        Collections.sort(chats);

        holder.title_help.setText(help.getTitle());
        holder.message_help.setText(help.getMessage());
        //holder.replymessage_help.setText(help.getReplymessage());
        //holder.userreplymessage_help.setText(help.getUserreplymessage());
        String text = "";
        for(int i=0;i<length;i++){
            Chat chat = chats.get(i);
            if(chat.getUserid().equalsIgnoreCase(AppConstants.ADMIN_ID)){
                text = text+"Admin: "+chat.getReplymessage()+"<br>";
            }else{
                text = text+"You: "+chat.getReplymessage()+"<br>";
            }
        }
        //holder.replymessage_help.setText(text);
        holder.replymessage_help.setText(Html.fromHtml(text));
        holder.ticket_number_value_help.setText(help.getTicket());

        /*if(help.getUserreplymessage()!=null && help.getUserreplymessage().equalsIgnoreCase("")){
            holder.title_help.setVisibility(View.VISIBLE);
        }else {
            holder.title_help.setVisibility(View.GONE);
        }*/

    }


    @Override
    public int getItemCount() {
        return mData.size();
    }


    class HelpHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.ticket_number_value_help)
        TextView ticket_number_value_help;
        @BindView(R.id.title_help)
        TextView title_help;
        @BindView(R.id.message_help)
        TextView message_help;
        @BindView(R.id.replymessage_help)
        TextView replymessage_help;
/*        @BindView(R.id.userreplymessage_help)
        TextView userreplymessage_help;*/

        TextView reply_help;

        public HelpHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            reply_help = (TextView)itemView.findViewById(R.id.reply_help);
            reply_help.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId()==reply_help.getId()){
                //Toast.makeText(v.getContext(), "ITEM PRESSED = " + txtOrderNumber.getText().toString(), Toast.LENGTH_SHORT).show();
                Intent mIntent = new Intent(mContext, AddReplyActivity.class);
                mIntent.putExtra("ticket",ticket_number_value_help.getText().toString());
                mContext.startActivity(mIntent);
            }
        }

    }
}
