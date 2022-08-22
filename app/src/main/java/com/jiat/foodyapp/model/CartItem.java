package com.jiat.foodyapp.model;

public class CartItem {
    String id, pId, name, price, quantity, image;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public CartItem(String id, String pId, String name, String price, String quantity, String image) {
        this.id = id;
        this.pId = pId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
    }

    public CartItem() {
    }
}
