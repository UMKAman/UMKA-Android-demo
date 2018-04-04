package com.umka.umka.model;

import android.util.Log;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by trablone on 11/14/16.
 */

public class Message extends BaseModel {
    public int id;
    public int user_id;
    public String message;
    public boolean isread;
    public String image;
    public boolean imageFile;
    public String user_avatar;
    public String user_name;
    public int chat_id;


    public Message(){

    }

    public Message(JSONObject object){
        try {
            id = object.getInt("id");
            Log.e("tr", "id: " + id);
            if (!object.isNull("text")) {
                try {
                    message = StringEscapeUtils.unescapeJava(object.getString("text"));
                }catch (Throwable e){
                    message = object.getString("text");
                }
            }

            if (!object.isNull("pic"))
                image = object.getString("pic");
            isread = object.getBoolean("status");
            createdAt = object.getString("createdAt");
            if (!object.isNull("chat")){
                try {
                    JSONObject chat = object.getJSONObject("chat");
                    chat_id = chat.getInt("id");
                }catch (JSONException e){
                    chat_id = object.getInt("chat");
                }
            }
            if (!object.isNull("ownerUser")){
                try{
                    JSONObject ownerUser = object.getJSONObject("ownerUser");
                    user_id = ownerUser.getInt("id");
                    user_avatar = ownerUser.getString("avatar");
                    user_name = ownerUser.getString("name");
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }

            if (!object.isNull("ownerMaster")){
                try{
                    JSONObject ownerUser = object.getJSONObject("ownerMaster");
                    JSONObject user = ownerUser.getJSONObject("user");
                    user_id = user.getInt("id");
                    user_avatar = user.getString("avatar");
                    user_name = user.getString("name");
                }catch (JSONException e){
                    e.printStackTrace();
                    JSONObject ownerUser = object.getJSONObject("ownerMaster");
                    user_id = ownerUser.getInt("user");
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
