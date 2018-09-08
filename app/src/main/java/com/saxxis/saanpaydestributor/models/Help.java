package com.saxxis.saanpaydestributor.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by saxxis25 on 4/5/2017.
 */

public class Help implements Parcelable {

    private String id;
    private String ticket;
    private String ordernumber;
    private String userid;
    private String title;
    private String message;
    private String status;
    private ArrayList<Chat> chats;

    public Help(){

    }

    public Help(String id, String ticket, String ordernumber, String userid, String title, String message, String status, ArrayList<Chat> chats) {
        this.id = id;
        this.ticket = ticket;
        this.ordernumber = ordernumber;
        this.userid = userid;
        this.title = title;
        this.message = message;
        this.status = status;
        this.chats = chats;
    }
    public Help(Parcel in) {
        this.id = in.readString();
        this.ticket = in.readString();
        this.ordernumber = in.readString();
        this.userid = in.readString();
        this.title = in.readString();
        this.message = in.readString();
        this.status = in.readString();
        this.chats = in.readArrayList(Chat.class.getClassLoader());
    }

    public static final Creator<Help> CREATOR = new Creator<Help>() {
        @Override
        public Help createFromParcel(Parcel in) {
            return new Help(in);
        }

        @Override
        public Help[] newArray(int size) {
            return new Help[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getOrdernumber() {
        return ordernumber;
    }

    public void setOrdernumber(String ordernumber) {
        this.ordernumber = ordernumber;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static Creator<Help> getCREATOR() {
        return CREATOR;
    }

    public ArrayList<Chat> getChats() {
        return chats;
    }

    public void setChats(ArrayList<Chat> chats) {
        this.chats = chats;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(ticket);
        dest.writeString(ordernumber);
        dest.writeString(userid);
        dest.writeString(title);
        dest.writeString(message);
        dest.writeString(status);
        dest.writeList(chats);
    }
}
