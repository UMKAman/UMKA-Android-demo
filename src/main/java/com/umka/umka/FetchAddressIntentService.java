package com.umka.umka;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by trablone on 5/9/17.
 */

public class FetchAddressIntentService extends IntentService {


    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME =
            "com.google.android.gms.location.sample.locationaddress";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String RESULT_ERROR_KEY = PACKAGE_NAME + ".RESULT_ERROR_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";

    public FetchAddressIntentService(){
        super("");
    }
    public FetchAddressIntentService(String name) {
        super(name);
    }

    protected ResultReceiver mReceiver;

    private String request;

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        mReceiver = intent.getParcelableExtra(RECEIVER);
        return IntentService.START_STICKY;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String errorMessage = "неопределено";

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        // Get the location passed to this service through an extra.
        LatLng location = intent.getParcelableExtra(LOCATION_DATA_EXTRA);
        request = intent.getStringExtra("request");
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    // In this sample, get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            //errorMessage = getString(R.string.service_not_available);
            Log.e("tr", errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            //errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e("tr", errorMessage + ". " +
                    "Latitude = " + location.latitude +
                    ", Longitude = " +
                    location.longitude, illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                //errorMessage = getString(R.string.no_address_found);
                Log.e("tr", errorMessage);
            }
            deliverResultToReceiver(FAILURE_RESULT, "Адресс не определен");
        } else {
            Address address = addresses.get(0);
            //ArrayList<String> addressFragments = new ArrayList<String>();
            Log.e("tr", "getCountryName: " + address.getCountryName());
            Log.e("tr", "getAdminArea: " + address.getAdminArea());
            Log.e("tr", "getFeatureName: " + address.getFeatureName());
            Log.e("tr", "getLocality: " + address.getLocality());
            Log.e("tr", "getPremises: " + address.getPremises());
            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            /*for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                Log.e("tr", "line " + i + " " + address.getAddressLine(i));
                addressFragments.add(address.getAddressLine(i));
            }*/
            //Log.i("tr", getString(R.string.address_found));
            deliverResultToReceiver(SUCCESS_RESULT, address);//TextUtils.join(System.getProperty("line.separator"), addressFragments));
        }
    }

    private void deliverResultToReceiver(int resultCode, Address message) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(RESULT_DATA_KEY, message);
        if (request != null)
            bundle.putString("request", request);
        mReceiver.send(resultCode, bundle);
        stopSelf();
    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(RESULT_ERROR_KEY, message);
        mReceiver.send(resultCode, bundle);
        stopSelf();
    }
}
