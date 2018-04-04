package com.umka.umka.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by trablone on 11/29/16.
 */

public class Price extends BaseModel {

    public String name;
    public int id;
    public String cost;
    public String count;
    public String measure;
    public String currency;

    public Price(JSONObject object){
        try {
            name = object.getString("name");
            cost = object.getString("cost");
            id = object.getInt("id");
            count = object.getString("count");
            measure = object.getString("measure");
            currency = object.getString("currency");
        } catch (JSONException e) {
            Log.e("tr", "e service: " + e.getMessage());
        }
    }
}
