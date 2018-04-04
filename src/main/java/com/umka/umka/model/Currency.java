package com.umka.umka.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by trablone on 5/10/17.
 */

public class Currency extends BaseModel {

    public String name;
    public int id;

    public Currency(JSONObject object){
        try {
            name = object.getString("name");
            id = object.getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
