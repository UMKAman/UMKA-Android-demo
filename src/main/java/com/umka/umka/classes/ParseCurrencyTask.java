package com.umka.umka.classes;

import android.os.AsyncTask;

import com.umka.umka.model.Currency;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by trablone on 5/10/17.
 */

public class ParseCurrencyTask extends AsyncTask<JSONArray, Void, List<Currency>> {
    @Override
    protected List<Currency> doInBackground(JSONArray... jsonArrays) {
        List<Currency> list = new ArrayList<>();
        JSONArray array = jsonArrays[0];
        for (int i = 0; i < array.length(); i++){
            try {
                list.add(new Currency(array.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return list;
    }
}
