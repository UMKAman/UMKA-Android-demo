package com.umka.umka.model;

import android.util.Log;

import com.umka.umka.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by trablone on 5/6/17.
 */

public class PortfolioPic extends BaseModel {
    public String pic;
    public int type;
    public int ressPic;
    public int id;


    public PortfolioPic(){
        type = 1;
        ressPic = R.drawable.image_profile_add_avatar;
    }

    public PortfolioPic(JSONObject object){
        parsePortfolioPic(object);
    }

    private void parsePortfolioPic(JSONObject object){
        try {
            pic = object.getString("pic");
            id = object.getInt("id");
            type = 0;
        } catch (JSONException e) {
            Log.e("tr", "parse pic: " + e.getMessage());
        }
    }
}
