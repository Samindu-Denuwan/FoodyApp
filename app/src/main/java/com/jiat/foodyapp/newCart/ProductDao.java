package com.jiat.foodyapp.newCart;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProductDao {
    @Insert
    void insertrecord(CartProduct cartProduct);

    @Query("SELECT EXISTS(SELECT * FROM CartProduct WHERE productID = :productid)")
    Boolean is_exist(String productid);

    @Query("SELECT * FROM CartProduct")
    List<CartProduct> getAllProduct();

    @Query("DELETE FROM CartProduct Where primaryId = :id")
    void deleteById(int id);

    @Update
    public void updateQty(CartProduct cartProduct);

    /*@Delete
    void deleteAll(CartProduct cartProduct);*/

    /*@Query("UPDATE CartProduct where productID = :productid)")
    Boolean is_exist(String productid);*/

    /*@Delete
    void deleteAll(CartProduct cartProduct);*/

    @Query("DELETE FROM CartProduct")
    void deleteAll();


}
