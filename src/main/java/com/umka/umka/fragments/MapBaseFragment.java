package com.umka.umka.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.umka.umka.classes.LocationHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.umka.umka.classes.LocationHelperListener;
import com.umka.umka.classes.LocationService;
import com.umka.umka.classes.Utils;

/**
 * Created by trablone on 4/20/17.
 */

public abstract class MapBaseFragment extends BaseFragment implements OnMapReadyCallback {

    public int MAP_ZOOM = 8;

    private SupportMapFragment mapFragment;
    public GoogleMap googleMap;
    public LatLng myPosition;
    public Marker myMarker;

    private SwipeRefreshLayout refreshLayout;
    private LocationHelper locationHelper;

    private IntentFilter intentFilter;
    private BroadcastReceiver receiver;

    public abstract void setMyLocation(Location location);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(com.umka.umka.R.layout.fragment_map, container, false);
        refreshLayout = (SwipeRefreshLayout)view.findViewById(com.umka.umka.R.id.refresh_layout);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(com.umka.umka.R.id.map);

        intentFilter = new IntentFilter();
        intentFilter.addAction("dimax.com.dimax.services.location");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("tr", "receiver location: " + this);
                Location location = intent.getParcelableExtra("location");
                initMap(location);
            }
        };
        mapFragment.getMapAsync(this);
        setProgress(false);
    }

    public void setProgress(final boolean show){
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setEnabled(show);
                refreshLayout.setRefreshing(show);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onDestroy() {
        if (receiver != null) {
            LocalBroadcastManager.getInstance(getBaseActivity()).unregisterReceiver(receiver);
        }
        LocationService.getInstance().onDestroy();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (locationHelper != null)
        locationHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (locationHelper != null)
        locationHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean toPosition = true;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        initLocationHelper();
    }

    public void initLocationHelper(){
        if (Utils.isGooglePlayServicesAvailable(getBaseActivity())) {
            locationHelper = new LocationHelper(getBaseActivity(), new LocationHelperListener() {
                @Override
                public void onSuccess() {
                    Log.e("tr", "onSuccess");
                    LocationService.getInstance().init(getBaseActivity());
                    Location location = LocationService.getInstance().getLocation();
                    LocalBroadcastManager.getInstance(getBaseActivity()).registerReceiver(receiver, intentFilter);

                    if (location != null) {
                        initMap(location);
                    }

                }

                @Override
                public void onFailure() {

                }

            });
        }
    }

    private void initMap(Location location){
        myPosition = new LatLng(location.getLatitude(), location.getLongitude());
        if (myMarker != null)
            myMarker.remove();
        myMarker = MapBaseFragment.this.googleMap.addMarker(new MarkerOptions()
                .position(myPosition)
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(com.umka.umka.R.drawable.ic_user_location)));

        setMyLocation(location);

        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                if (toPosition && myPosition != null) {
                    goToPosition();
                    toPosition = false;
                }
            }
        });
    }

    public void goToPosition() {
        CameraPosition.Builder builder = new CameraPosition.Builder();
        builder.zoom(MAP_ZOOM);
        builder.target(myPosition);
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
    }

}
