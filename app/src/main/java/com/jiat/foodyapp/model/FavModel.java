package com.jiat.foodyapp.model;

public class FavModel {
    private String favId;
    private String productId;
    private String itemName;
    private String price;
    private String discountAvailable;
    private  String discount;
    private  String discountNote;
    private  String image;
    private  String status;

    public FavModel() {
    }

    public FavModel(String favId, String productId, String itemName, String price, String discountAvailable, String discount, String discountNote, String image, String status) {
        this.favId = favId;
        this.productId = productId;
        this.itemName = itemName;
        this.price = price;
        this.discountAvailable = discountAvailable;
        this.discount = discount;
        this.discountNote = discountNote;
        this.image = image;
        this.status = status;
    }

    public String getFavId() {
        return favId;
    }

    public void setFavId(String favId) {
        this.favId = favId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscountAvailable() {
        return discountAvailable;
    }

    public void setDiscountAvailable(String discountAvailable) {
        this.discountAvailable = discountAvailable;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
