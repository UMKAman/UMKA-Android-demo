package com.umka.umka.classes;

import android.os.AsyncTask;
import android.util.Log;

import com.umka.umka.model.Day;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by trablone on 12/13/16.
 */

public class ParseDayTask extends AsyncTask<JSONArray, Void, List<Day>> {
    List<Day> listAll = new ArrayList<>();

    private Calendar calendar = Calendar.getInstance();

    private String work_day;

    public ParseDayTask(String work_day){
        this.work_day = work_day;
    }

    public Date convertToDate(String dateString){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return convertedDate;
    }

    @Override
    protected List<Day> doInBackground(JSONArray... jsonArrays) {

        for (int i = 8; i < 22; i++){
            Day item = new Day();
            calendar.setTime(convertToDate(work_day));
            calendar.set(Calendar.HOUR, i);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            item.title = i + ":00-" + (i+1) + ":00";
            item.date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(calendar.getTime()) + ".000Z";

            item.select = 0;
            listAll.add(item);
        }

        JSONArray response = jsonArrays[0];
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject object = response.getJSONObject(i);
                //Log.e("tr", "object: " + object);
                Day item = new Day();
                item.id = object.getInt("id");
                item.date = object.getString("hour");
                setListAll(item);
            } catch (JSONException e) {
                Log.e("tr", e.getMessage());
            }
        }

        return listAll;
    }

    private void setListAll(Day day){
        for (int i = 0; i < listAll.size(); i++){
            Day item = listAll.get(i);
            //Log.e("tr", "item:date: " + item.date);
            //Log.e("tr", "day:date: " + day.date);
            if (item.date.contains(day.date)){
                item.id = day.id;
                item.select = 1;
                listAll.set(i, item);
            }
        }
    }
}