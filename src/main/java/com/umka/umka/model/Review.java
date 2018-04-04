package com.umka.umka.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by trablone on 11/14/16.
 */

public class Review extends BaseModel {
    public Profile user;
    public String text;
    public String rating;
    public int type;
    public int id;

    public Review(){
        type = 0;
    }
    public Review(JSONObject object){
        try {
            type = 1;
            user = new Profile(object.getJSONObject("writter"));
            text = object.getString("text");
            id = object.getInt("id");
            rating = object.getString("rating");
            createdAt = object.getString("createdAt");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("tr", "e: " + e.getMessage());
        }
    }
}
