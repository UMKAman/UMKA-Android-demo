package com.umka.umka.classes;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.umka.umka.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by trablone on 5/27/17.
 */

public class LocationHelper {

    public Context context;

    private final int PERMISSION_KAY = 155;
    private List<String> permissionList;
    private LocationHelperListener listener;

    public LocationHelper(Context context, LocationHelperListener listener) {
        Log.e("tr", "context: " + context);
        this.context = context;
        this.listener = listener;
        enableLocation();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("tr", "result RESULT");
        if (requestCode == 500) {
            if (resultCode == Activity.RESULT_OK) {
                getLocation();
            } else
                if (listener != null){
                    listener.onFailure();
            }
        }
    }

    public void getLocation() {
        if (listener != null)
            listener.onSuccess();
    }

    private boolean enableLocation() {

        permissionList = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        int size = permissionList.size();
        String[] permissions = new String[size];
        for (int i = 0; i < permissionList.size(); i++) {
            permissions[i] = permissionList.get(i);
        }

        if (size > 0) {
            ActivityCompat.requestPermissions((BaseActivity) context, permissions, PERMISSION_KAY);
            return false;
        }
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(context);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.e("tr", "result onSuccess");
                getLocation();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:

                        try {
                            LocationService.getInstance().setInit(false);
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult((BaseActivity) context, 500);

                        } catch (IntentSender.SendIntentException sendEx) {

                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                        break;
                }
            }
        });

        return true;
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_KAY) {
            boolean ACCESS_FINE_LOCATION = false;
            boolean ACCESS_COARSE_LOCATION = false;
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                ACCESS_FINE_LOCATION = true;
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                ACCESS_COARSE_LOCATION = true;

            if (ACCESS_COARSE_LOCATION && ACCESS_FINE_LOCATION) {
                enableLocation();
            }
        }
    }
}
