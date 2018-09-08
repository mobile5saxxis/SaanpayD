package com.saxxis.saanpaydestributor.models;

import java.util.ArrayList;

/**
 * Created by saxxis25 on 5/19/2017.
 */

public class Product {

    private String id;
    private String name;
    private String price;
    private String discount;
    private String discountprice;
    private String imagePath;
    private ArrayList<ProdAtrribute> attributes;

    public Product(){}

    public Product( String id,String name,String price,String discount,String discountprice,String imagePath,ArrayList<ProdAtrribute> attributes){
        this.id= id;
        this.name = name;
        this.price = price;
        this.discount = discount;
        this.discountprice = discountprice;
        this.imagePath = imagePath;
        this.attributes = attributes;
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

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getDiscountprice() {
        return discountprice;
    }

    public void setDiscountprice(String discountprice) {
        this.discountprice = discountprice;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public ArrayList<ProdAtrribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(ArrayList<ProdAtrribute> attributes) {
        this.attributes = attributes;
    }
}
