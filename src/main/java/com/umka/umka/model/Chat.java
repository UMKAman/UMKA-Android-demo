package com.umka.umka.model;

import com.umka.umka.classes.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by trablone on 11/14/16.
 */

public class Chat extends BaseModel {
    public int id;
    public String name;
    public String avatar;
    public Message message;

    public Chat(){

    }

    public Chat(String type, JSONObject object){
        try {
            id = object.getInt("id");
            if (type.contains(Constants.TYPE_USER)){
                JSONObject master = object.getJSONObject("master").getJSONObject("user").getJSONObject("user");
                name = master.getString("name");
                avatar = master.getString("avatar");
            }else {
                JSONObject master = object.getJSONObject("user");
                name = master.getString("name");
                avatar = master.getString("avatar");
            }

            JSONArray messages = object.getJSONArray("messages");
            if (messages.length() > 0){
                JSONObject message = messages.getJSONObject(0);
                this.message = new Message(message);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
