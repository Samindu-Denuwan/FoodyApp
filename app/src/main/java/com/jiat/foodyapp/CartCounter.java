package com.jiat.foodyapp;

import android.content.Context;
import android.database.Cursor;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class CartCounter {

    Context context;

    public CartCounter(Context context) {
        this.context = context;
    }

    public int cartCount(){
        int count = 0;

        EasyDB easyDB = EasyDB.init(context, "ITEM_DB")
                .setTableName("ITEM_TABLE")
                .addColumn(new Column("item_id", new String[]{"text", "unique"}))
                .addColumn(new Column("item_pid", new String[]{"text", "not null"}))
                .addColumn(new Column("item_name", new String[]{"text", "not null"}))
                .addColumn(new Column("item_price", new String[]{"text", "not null"}))
                .addColumn(new Column("item_qty", new String[]{"text", "not null"}))
                .addColumn(new Column("item_image", new String[]{"text", "not null"}))
                .doneTableColumn();

        Cursor res = easyDB.getAllData();
        while (res.moveToNext()){
            count++;
        }
        return count;
    }
}
