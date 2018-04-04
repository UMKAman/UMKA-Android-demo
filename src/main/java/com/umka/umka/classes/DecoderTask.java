package com.umka.umka.classes;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;

/**
 * Created by trablone on 1/20/17.
 */

public class DecoderTask extends AsyncTask<Void, Void, List<Address>> {

    private Context context;
    double lat, lng;

    public DecoderTask(Context context,double lat, double lng) {
        this.context = context;
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    protected List<Address> doInBackground(Void... params) {
        Geocoder geocoder = new Geocoder(context);

        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return addresses;
    }

}
