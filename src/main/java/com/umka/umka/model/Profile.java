package com.umka.umka.model;

import android.util.Log;

import com.umka.umka.R;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by trablone on 12/9/16.
 */

public class Profile extends BaseModel {

    public String token;
    public String pass;

    public boolean pushNotify;
    public int id;
    public int master_id;
    public String avatar;
    public String name;
    public String phone;
    public String gender;
    public String about;
    public String city;
    public boolean is_master;
    public String createdAt;
    public String device_token;

    public Profile(){

    }

    public Profile(JSONObject object){
        parseProfile(object);
    }


    public String getDate(){
        return createdAt;
    }
    public int getTextTypeUser(){
        if (!is_master){
            return com.umka.umka.R.string.type_user;
        }
        return com.umka.umka.R.string.type_master;
    }

    public boolean isMaster(){
        return is_master;
    }

    public String getName(){
        if (name != null && name.contains("null"))
            name = "";
        return name;
    }

    public String getAbout(){
        if (about != null && about.contains("null"))
            about = "";
        return about;
    }



    public void setGender(String gender){
        this.gender = gender;
    }

    public int getGenderId(){
        if (gender == null)
            gender = "Муж";
        if (gender.contains("Муж")){
            return com.umka.umka.R.id.check_left;
        }

        return com.umka.umka.R.id.check_right;
    }
    public int getGender(){
        if (gender != null)
        if (gender.contains("Муж"))
            return R.string.male;
        return R.string.female;
    }

    private void parseProfile(JSONObject object){
        try {

            Log.e("tr", "object: " + object);

            this.about = object.getString("about");
            this.avatar = object.getString("avatar");
            this.pushNotify = object.getBoolean("pushNotify");
            this.id = object.getInt("id");
            this.name = object.getString("name");
            this.gender = object.getString("gender");
            this.is_master = object.getBoolean("isMaster");
            this.phone = object.getString("phone");
            this.city = object.getString("city");
            this.createdAt = object.getString("createdAt");

        } catch (JSONException e) {
            Log.e("tr", e.getMessage());
        }
    }
}
