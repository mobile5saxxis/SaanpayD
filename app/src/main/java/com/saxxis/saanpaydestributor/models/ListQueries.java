package com.saxxis.saanpaydestributor.models;

/**
 * Created by saxxis25 on 8/28/2017.
 */

public class ListQueries {

    private String ticket;
    private String title;
    private String message;

    public ListQueries(String ticket,String title,String message){
        this.ticket=ticket;
        this.title=title;
        this.message=message;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
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
}
