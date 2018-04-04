package com.umka.umka.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by trablone on 5/13/17.
 */

public class OrderHelper extends BaseHelper {

    public OrderHelper(SQLiteDatabase db) {
        super(db);
    }

    private ContentValues getValues(int id){
        ContentValues cv = new ContentValues();
        cv.put(Content.Favorite.id, id);
        return cv;
    }

    public boolean isCreate(int id){
        Cursor c = db.query(DbHelper.TABLE_ORDER_USER, null, Content.Favorite.id + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (c != null && c.moveToFirst()){
            c.close();
            return true;
        }
        return false;
    }

    public void add(int id){
        insert(DbHelper.TABLE_ORDER_USER, getValues(id));
    }

    public void delete(int id){
        db.delete(DbHelper.TABLE_ORDER_USER, Content.Favorite.id + "=?", new String[]{String.valueOf(id)});
    }
}
