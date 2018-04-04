package com.umka.umka.database;

/**
 * Created by trablone on 10.05.16.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


/**
 * Created by trablone on 15.11.15.
 */
public class DbHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 15;

    public static final String TABLE_USER = "table_user";
    public static final String TABLE_CATEGORY = "table_section";
    public static final String TABLE_FAVORITE_USER = "table_favorite_master";
    public static final String TABLE_ORDER_USER = "table_order_user";

    private SQLiteDatabase db;
    private Context context;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.db = getWritableDatabase();
    }

    public SQLiteDatabase getDataBase() {
        return db;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + TABLE_USER + "("
                + BaseColumns._ID + " integer primary key autoincrement,"
                + Content.User.id + " integer,"
                + Content.User.master_id + " integer,"
                + Content.User.profile + " integer,"
                + Content.User.first_name + " text,"
                + Content.User.avatar + " text,"
                + Content.User.about + " text,"
                + Content.User.gender + " text,"
                + Content.User.last_name + " text,"
                + Content.User.fcm_token + " text,"
                + Content.User.type + " text,"
                + Content.User.city + " text,"
                + Content.User.phone + " text,"
                + Content.User.pass + " text,"
                + Content.User.token + " text,"
                + Content.User.email + " text"
                + ");");

        db.execSQL("create table " + TABLE_CATEGORY + "("
                + BaseColumns._ID + " integer primary key autoincrement,"
                + Content.Category.id + " integer,"
                + Content.Category.name + " text,"
                + Content.Category.nameEn + " text,"
                + Content.Category.next_layer + " text,"
                + Content.Category.parent_id + " integer,"
                + Content.Category.picture + " text,"
                + Content.Category.color + " text"
                + ");");

        db.execSQL("create table " + TABLE_ORDER_USER + "("
                + BaseColumns._ID + " integer primary key autoincrement,"
                + Content.Favorite.id + " integer"
                + ");");

        db.execSQL("create table " + TABLE_FAVORITE_USER + "("
                + BaseColumns._ID + " integer primary key autoincrement,"
                + Content.Favorite.id + " integer"
                + ");");
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
        dropTables(db, newVersion);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTables(db, newVersion);

    }

    private void dropTables(SQLiteDatabase db, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITE_USER);
        onCreate(db);
    }


}
