package com.umka.umka.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.umka.umka.model.Profile;


/**
 * Created by trablone on 11/17/16.
 */

public class UserHelper extends BaseHelper {

    public UserHelper(SQLiteDatabase db) {
        super(db);
    }

    private ContentValues getValues(Profile item){
        ContentValues cv = new ContentValues();

        if (item.avatar != null)
            cv.put(Content.User.avatar, item.avatar);
        if (item.about != null)
            cv.put(Content.User.about, item.about);
        if (item.gender != null)
            cv.put(Content.User.gender, item.gender);

        cv.put(Content.User.master_id, item.master_id);

        if (item.name != null)
            cv.put(Content.User.first_name, item.name);

        if (item.pass != null)
            cv.put(Content.User.pass, item.pass);

        if (item.city != null)
            cv.put(Content.User.city, item.city);

        if (item.phone != null)
            cv.put(Content.User.phone, item.phone);
        if (item.token != null)
            cv.put(Content.User.token, item.token);

        cv.put(Content.User.profile, item.id);
        cv.put(Content.User.type, item.is_master ? 1 : 0);
        if (item.device_token != null){
            cv.put(Content.User.fcm_token, item.device_token);
        }
        return cv;
    }

    public Profile getUser(){
        Profile item = null;
        Cursor c = query(DbHelper.TABLE_USER);
        if (c != null && c.moveToFirst()){
            item = new Profile();
            item.is_master = c.getInt(c.getColumnIndexOrThrow(Content.User.type)) == 1;
            item.phone = c.getString(c.getColumnIndexOrThrow(Content.User.phone));
            item.token = c.getString(c.getColumnIndexOrThrow(Content.User.token));
            item.name = c.getString(c.getColumnIndexOrThrow(Content.User.first_name));
            item.avatar = c.getString(c.getColumnIndexOrThrow(Content.User.avatar));
            item.id = c.getInt(c.getColumnIndexOrThrow(Content.User.profile));
            item.master_id = c.getInt(c.getColumnIndexOrThrow(Content.User.master_id));
            item.about = c.getString(c.getColumnIndexOrThrow(Content.User.about));
            item.gender = c.getString(c.getColumnIndexOrThrow(Content.User.gender));
            item.city = c.getString(c.getColumnIndexOrThrow(Content.User.city));
            item.device_token = c.getString(c.getColumnIndexOrThrow(Content.User.fcm_token));
        }
        return item;
    }

    public void updateUser(Profile item){
        Cursor c = query(DbHelper.TABLE_USER);
        if (c != null && c.moveToFirst()){
            db.update(DbHelper.TABLE_USER, getValues(item), BaseColumns._ID + "=?", new String[]{c.getString(c.getColumnIndexOrThrow(BaseColumns._ID))});
            c.close();
        }else {
            insert(DbHelper.TABLE_USER, getValues(item));
        }
    }

    public boolean isLogin(){
        return getUser() != null;
    }

}
