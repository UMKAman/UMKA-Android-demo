package com.umka.umka.classes;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.umka.umka.database.DbHelper;
import com.umka.umka.database.FavoriteHelper;
import com.umka.umka.model.Master;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by trablone on 5/13/17.
 */

public class ParseFavoritesTask  extends AsyncTask<JSONArray, Void, List<Master>> {

    private Context context;

    public ParseFavoritesTask(Context context){
        this.context = context;
    }

    @Override
    protected List<Master> doInBackground(JSONArray... jsonArrays) {
        FavoriteHelper helper = new FavoriteHelper(new DbHelper(context).getDataBase());
        helper.delete(DbHelper.TABLE_FAVORITE_USER);
        List<Master> list = new ArrayList<>();
        JSONArray response = jsonArrays[0];
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject object = response.getJSONObject(i);
                int id = object.getInt("id");
                helper.add(id);
            } catch (JSONException e) {
                Log.e("tr", e.getMessage());
            }
        }

        return list;
    }
}
