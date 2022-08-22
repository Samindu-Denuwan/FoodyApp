package com.jiat.foodyapp.newCart;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {CartProduct.class}, version = 4)

public abstract class AppDatabase extends RoomDatabase {
    public abstract  ProductDao ProductDao();
}
