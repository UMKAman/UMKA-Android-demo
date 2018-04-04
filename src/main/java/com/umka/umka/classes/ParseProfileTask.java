package com.umka.umka.classes;

import android.os.AsyncTask;

import com.umka.umka.model.Profile;

import org.json.JSONObject;

/**
 * Created by trablone on 12/9/16.
 */

public class ParseProfileTask extends AsyncTask<JSONObject, Void, Profile> {

    @Override
    protected Profile doInBackground(JSONObject... jsonObjects) {

        return new Profile(jsonObjects[0]);
    }
}
