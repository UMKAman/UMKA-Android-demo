package com.umka.umka.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by trablone on 5/13/17.
 */

public class Order extends BaseModel {

    public int id;
    public Profile user;
    public Master master;
    public boolean masterApprove;


    public Order(JSONObject object){
        try {
            id = object.getInt("id");
            masterApprove = object.getBoolean("masterApprove");
            createdAt = object.getString("createdAt");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("tr", "e " + e.getMessage());
        }

        try {
            user = new Profile(object.getJSONObject("user"));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("tr", "e " + e.getMessage());
        }

        try {
            master = new Master(object.getJSONObject("master"));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("tr", "e " + e.getMessage());
        }
    }
}
