package com.saxxis.saanpaydestributor.models;

/**
 * Created by saxxis25 on 5/29/2017.
 */

public class ProdAtrribute {

    private String id;
    private String name;
    private String price;

    public ProdAtrribute(){}

    public ProdAtrribute( String id,String name,String price){
        this.id= id;
        this.name = name;
        this.price = price;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
