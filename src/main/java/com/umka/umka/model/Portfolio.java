package com.umka.umka.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by trablone on 12/14/16.
 */

public class Portfolio extends BaseModel {

    public int id;
    public List<PortfolioPic> pics;
    public String description;

    public Portfolio(JSONObject object){
        parsePortfolio(object);
    }

    private void parsePortfolio(JSONObject object){
        try {
            id = object.getInt("id");
            description = object.getString("description");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
