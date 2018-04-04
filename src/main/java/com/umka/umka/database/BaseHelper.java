package com.umka.umka.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by trablone on 15.11.15.
 */
public class BaseHelper {
    public static final String SORT_ORDER = BaseColumns._ID + " ASC";
    public SQLiteDatabase db;

    public BaseHelper(SQLiteDatabase db) {
        this.db = db;

    }

    public Cursor query(String table) {
        return db.query(table, null, null, null, null, null, SORT_ORDER);
    }

    public long insert(String table, ContentValues contentValues) {
        long rowID = db.insert(table, null, contentValues);
        return rowID;
    }

    public int delete(String table) {
        int cnt = db.delete(table, null, null);
        return cnt;
    }


}
