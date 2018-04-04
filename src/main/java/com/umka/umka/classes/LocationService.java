package com.umka.umka.classes;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

/**
 * Created by trablone on 7/2/17.
 */

public class LocationService {

    private static LocationService INSTANCE = null;

    public LocationService() {
        INSTANCE = this;

    }
    public static LocationService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LocationService();
        }

        return INSTANCE;
    }

    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location location;

    public boolean isInit() {
        return isInit;
    }

    public void setInit(boolean init) {
        isInit = init;
    }

    private boolean isInit = false;

    public void init(final Context context){
        if (!isInit){
            isInit = true;
            try{
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
                mLocationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult != null){
                            location = locationResult.getLastLocation();
                            if (location != null){
                                Intent intent = new Intent("dimax.com.dimax.services.location");
                                intent.putExtra("location", location);
                                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                            }
                        }
                    }
                };

                mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(10000);
                mLocationRequest.setFastestInterval(5000);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                if (mFusedLocationClient != null)
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);

            }catch (Throwable e){
                Log.e("tr", "Service: " + e.getMessage());
            }
        }

    }


    public Location getLocation(){
        return location;
    }


    public void onDestroy() {
        if (mFusedLocationClient != null) {
            isInit = false;
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
        Log.e("tr", "Location onDestroy");

    }


}
