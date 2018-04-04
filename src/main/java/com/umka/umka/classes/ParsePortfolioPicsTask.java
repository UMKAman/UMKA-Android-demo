package com.umka.umka.classes;

import android.os.AsyncTask;
import android.util.Log;

import com.umka.umka.model.PortfolioPic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by trablone on 5/8/17.
 */

public class ParsePortfolioPicsTask extends AsyncTask<JSONArray, Void, List<PortfolioPic>> {

    @Override
    protected List<PortfolioPic> doInBackground(JSONArray... jsonArrays) {
        List<PortfolioPic> images = new ArrayList<>();

        for (int i = 0; i < jsonArrays[0].length(); i++){
            try {
                JSONObject object = jsonArrays[0].getJSONObject(i);
                Log.e("tr", "object pic: " + object);
                images.add(new PortfolioPic(object));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return images;
    }
}
