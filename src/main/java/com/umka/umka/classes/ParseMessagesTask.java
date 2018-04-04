package com.umka.umka.classes;

import android.os.AsyncTask;
import android.util.Log;

import com.umka.umka.model.Message;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by trablone on 11/9/16.
 */

public class ParseMessagesTask extends AsyncTask<JSONArray, Void, List<Message>> {

    @Override
    protected List<Message> doInBackground(JSONArray... jsonArrays) {

        List<Message> list = new ArrayList<>();
        JSONArray response = jsonArrays[0];
        for (int i = 0; i < response.length(); i++) {
            JSONObject object = response.optJSONObject(i);
            Log.e("tr", "object: " + object);

            list.add(new Message(object));

        }
        Collections.reverse(list);
        return list;
    }
}