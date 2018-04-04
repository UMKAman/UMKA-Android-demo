package com.umka.umka.classes;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.umka.umka.model.Master;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by trablone on 11/22/16.
 */

public class ParseMastersTask extends AsyncTask<JSONArray, Void, List<Master>> {


    private int user_id;
    private int radius;
    private LatLng latLngStart;

    public ParseMastersTask(int user_id){
        this.user_id = user_id;
    }

    public ParseMastersTask(){

    }

    @Override
    protected List<Master> doInBackground(JSONArray... jsonArrays) {

        List<Master> list = new ArrayList<>();
        JSONArray response = jsonArrays[0];
        for (int i = 0; i < response.length(); i++) {
            try {
                Master master = new Master(response.getJSONObject(i));
                //if (master.user.id != user_id) {
                    list.add(master);
                    /*if (latLngStart != null){
                        if (!TextUtils.isEmpty(master.lon)) {
                            LatLng end = new LatLng(Double.parseDouble(master.lat), Double.parseDouble(master.lon));
                            String distance = String.valueOf(calculationByDistance(latLngStart, end));
                            Log.e("tr", "distance: " + distance);
                            if (isSuccessRadius(distance)) {
                                list.add(master);
                            }
                        }
                    }else*/

                //}
            } catch (JSONException e) {
                Log.e("tr", e.getMessage());
            }
        }

        return list;
    }

    private boolean isSuccessRadius(String distance) {
        try {
            String parseDistance = distance.substring(0, distance.indexOf(".") + 4);
            String[] parse = parseDistance.split("\\.");
            if (parse.length == 2) {
                int m = (Integer.parseInt(parse[0]) * 1000) + Integer.parseInt(parse[1]);
                Log.e("tr", "дистанция: " + m);
                if (m <= radius) {
                    return true;
                }
            }
        } catch (Throwable e) {

        }

        return false;
    }


    public double calculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        return valueResult;
    }
}