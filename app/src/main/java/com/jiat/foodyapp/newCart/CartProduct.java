package com.jiat.foodyapp.newCart;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class CartProduct {
    @PrimaryKey (autoGenerate = true)
    public int primaryId;

    @ColumnInfo(name = "productID")
    public String productID;

    @ColumnInfo(name = "pname")
    public String pname;

    @ColumnInfo(name = "price")
    public int price;

    @ColumnInfo(name = "qnt")
    public int qnt;

    @ColumnInfo(name = "pimage")
    public String pimage;


    public CartProduct(int primaryId, String productID, String pname, int price, int qnt, String pimage) {
        this.primaryId = primaryId;
        this.productID = productID;
        this.pname = pname;
        this.price = price;
        this.qnt = qnt;
        this.pimage = pimage;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public int getPrimaryId() {
        return primaryId;
    }

    public void setPrimaryId(int primaryId) {
        this.primaryId = primaryId;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQnt() {
        return qnt;
    }

    public void setQnt(int qnt) {
        this.qnt = qnt;
    }

    public String getPimage() {
        return pimage;
    }

    public void setPimage(String pimage) {
        this.pimage = pimage;
    }
}
