package com.umka.umka.classes;

import android.os.AsyncTask;

import com.umka.umka.model.Portfolio;

import org.json.JSONObject;

/**
 * Created by trablone on 5/6/17.
 */

public class PersePortfolioTask extends AsyncTask<JSONObject, Void, Portfolio>{
    @Override
    protected Portfolio doInBackground(JSONObject... jsonObjects) {
        return new Portfolio(jsonObjects[0]);
    }
}
