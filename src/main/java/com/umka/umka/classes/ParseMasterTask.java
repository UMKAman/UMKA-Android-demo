package com.umka.umka.classes;

import android.os.AsyncTask;

import com.umka.umka.model.Master;

import org.json.JSONObject;

/**
 * Created by trablone on 5/6/17.
 */

public class ParseMasterTask extends AsyncTask<JSONObject, Void, Master> {

    @Override
    protected Master doInBackground(JSONObject... jsonObjects) {
        return new Master(jsonObjects[0]);
    }
}
