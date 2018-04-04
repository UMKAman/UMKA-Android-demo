package com.umka.umka.classes;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.umka.umka.database.DbHelper;
import com.umka.umka.database.OrderHelper;
import com.umka.umka.model.Order;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by trablone on 5/13/17.
 */

public class ParseOrdersTask extends AsyncTask<JSONArray, Void, List<Order>> {

    private Context context;

    public ParseOrdersTask(Context context){
        this.context = context;
    }

    @Override
    protected List<Order> doInBackground(JSONArray... jsonArrays) {
        OrderHelper helper = new OrderHelper(new DbHelper(context).getDataBase());
        helper.delete(DbHelper.TABLE_ORDER_USER);
        List<Order> list = new ArrayList<>();
        JSONArray response = jsonArrays[0];
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject object = response.getJSONObject(i);
                int id = object.getInt("id");
                helper.add(id);
                list.add(new Order(object));

            } catch (JSONException e) {
                Log.e("tr", e.getMessage());
            }
        }
        Collections.reverse(list);
        return list;
    }
}