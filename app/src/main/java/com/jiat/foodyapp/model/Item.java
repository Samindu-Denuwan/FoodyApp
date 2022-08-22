package com.jiat.foodyapp.model;

import com.google.firebase.firestore.Exclude;

import java.util.List;

public class Item {

    private String productId;
    private String itemName;
    private  String details;
    private String price;
    private  String discount;
    private  String discountNote;
    private  String image;
    private  String checkedCategory;
    private  String status;
    private String discountAvailable;



    public Item() {

    }

    public Item(String productId,String itemName, String details, String checkedCategory, String price, String discountAvailable,String discount, String discountNote, String image,  String status ) {

        this.productId = productId;
        this.itemName = itemName;
        this.details = details;
        this.price = price;
        this.discount = discount;
        this.discountNote = discountNote;
        this.image = image;
        this.checkedCategory = checkedCategory;
        this.status = status;
        this.discountAvailable = discountAvailable;

    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDetails() {
        return details;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setDetails(String details) {
        this.details = details;
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

    public String getDiscountNote() {
        return discountNote;
    }

    public void setDiscountNote(String discountNote) {
        this.discountNote = discountNote;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCheckedCategory() {
        return checkedCategory;
    }

    public void setCheckedCategory(String checkedCategory) {
        this.checkedCategory = checkedCategory;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDiscountAvailable() {
        return discountAvailable;
    }

    public void setDiscountAvailable(String discountAvailable) {
        this.discountAvailable = discountAvailable;
    }


}


