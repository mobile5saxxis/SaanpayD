package com.saxxis.saanpaydestributor.models;

import android.support.annotation.NonNull;

/**
 * Created by Praveen-pc on 1/17/2018.
 */

public class Chat implements Comparable<Chat>{
    private String id;
    private String ticket;
    private String userid;
    private String replymessage;
    private String status;

    public  Chat() {

    }
    public Chat(String id, String ticket, String userid, String replymessage, String status) {
        this.id = id;
        this.ticket = ticket;
        this.userid = userid;
        this.replymessage = replymessage;
        this.status = status;
    }

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

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getReplymessage() {
        return replymessage;
    }

    public void setReplymessage(String replymessage) {
        this.replymessage = replymessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int compareTo(@NonNull Chat o) {
        int id1 = Integer.parseInt(((Chat)o).getId());
        int id2 = Integer.parseInt(id);
        return id2-id1;
    }
}
