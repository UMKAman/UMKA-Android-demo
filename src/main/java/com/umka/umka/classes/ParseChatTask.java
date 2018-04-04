package com.umka.umka.classes;

import android.os.AsyncTask;

import com.umka.umka.model.Chat;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by trablone on 5/19/17.
 */

public class ParseChatTask extends AsyncTask<JSONArray, Void, List<Chat>>{

    String type;

    public ParseChatTask(String type){
        this.type = type;
    }
    @Override
    protected List<Chat> doInBackground(JSONArray... jsonArrays) {
        List<Chat> list = new ArrayList<>();
        JSONArray array = jsonArrays[0];
        for (int i = 0; i < array.length(); i++){
            try {
                Chat item = new Chat(type, array.getJSONObject(i));
                if (item.message != null)
                    list.add(item);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
