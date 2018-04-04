package com.umka.umka.fragments;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.umka.umka.FetchAddressIntentService;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.umka.umka.R;

import java.util.ArrayList;

/**
 * Created by trablone on 5/26/17.
 */

public class MapAddressFragment extends MapBaseFragment {

    private LinearLayout layout;
    private TextView textAddress;
    private ProgressBar progressBar;

    private AddressResultReceiver mResultReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        layout = (LinearLayout) view.findViewById(com.umka.umka.R.id.layout_address);
        textAddress = (TextView) view.findViewById(com.umka.umka.R.id.item_address);
        progressBar = (ProgressBar) view.findViewById(com.umka.umka.R.id.item_progress);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        layout.setVisibility(View.VISIBLE);
        textAddress.setText(getResources().getString(R.string.get_location));
        mResultReceiver = new AddressResultReceiver(new Handler());
    }

    private boolean isVisibleMenu = false;

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.getItem(0).setVisible(isVisibleMenu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(0, 1, 0, getResources().getString(R.string.ready)).setIcon(com.umka.umka.R.drawable.ic_fab_ok).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                Intent intent = new Intent();
                intent.putExtra("position", selectPosition);
                intent.putExtra("address", textAddress.getText().toString());
                getBaseActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    protected void startIntentService(String request) {
        isVisibleMenu = false;
        getBaseActivity().invalidateOptionsMenu();
        progressBar.setVisibility(View.VISIBLE);
        textAddress.setText(getResources().getString(R.string.title_activity_master_address));
        Intent intent = new Intent(getBaseActivity(), FetchAddressIntentService.class);
        intent.putExtra(FetchAddressIntentService.RECEIVER, mResultReceiver);
        intent.putExtra(FetchAddressIntentService.LOCATION_DATA_EXTRA, selectPosition);
        intent.putExtra("request", request);
        getBaseActivity().startService(intent);
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            progressBar.setVisibility(View.GONE);
            isVisibleMenu = true;
            getBaseActivity().invalidateOptionsMenu();
            Address address = resultData.getParcelable(FetchAddressIntentService.RESULT_DATA_KEY);
            if (address != null) {
                ArrayList<String> addressFragments = new ArrayList<String>();
                Log.e("tr", "getCountryName: " + address.getCountryName());
                Log.e("tr", "getAdminArea: " + address.getAdminArea());
                Log.e("tr", "getFeatureName: " + address.getFeatureName());
                Log.e("tr", "getLocality: " + address.getLocality());
                Log.e("tr", "getPremises: " + address.getPremises());

                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    Log.e("tr", "line " + i + " " + address.getAddressLine(i));
                    addressFragments.add(address.getAddressLine(i));
                }

                final String text = TextUtils.join(System.getProperty("line.separator"), addressFragments);
                textAddress.setText(text);
                String request = resultData.getString("request");
                if (request.contains("my_location")) {
                    if (myMarker != null)
                        myMarker.remove();
                    myMarker = googleMap.addMarker(new MarkerOptions()
                            .position(myPosition)
                            .title(text)
                            .anchor(0.5f, 0.5f)
                            .icon(BitmapDescriptorFactory.fromResource(com.umka.umka.R.drawable.ic_user_location)));

                } else {
                    if (driverPosition != null)
                    googleMap.addMarker(new MarkerOptions()
                            .position(driverPosition)
                            .title(text)
                            .icon(BitmapDescriptorFactory.fromResource(com.umka.umka.R.drawable.ic_specialist_location)));
                }
            } else {
                String error = resultData.getString(FetchAddressIntentService.RESULT_ERROR_KEY);
                textAddress.setText(error + ". Использовать координаты?");
            }
        }
    }

    private LatLng driverPosition, selectPosition;

    private boolean toUpdate = true;

    public void setMyLocation(Location mLastLocation) {

        if (googleMap != null && toUpdate){
            toUpdate = false;
            textAddress.setText(getResources().getString(R.string.title_activity_master_address));
            progressBar.setVisibility(View.VISIBLE);

            selectPosition = myPosition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

            startIntentService("my_location");
            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    goToPosition();
                }
            });


            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    selectPosition = driverPosition = latLng;
                    startIntentService("driver_location");
                }
            });
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    selectPosition = marker.getPosition();
                    textAddress.setText(marker.getTitle());
                    return true;
                }
            });
        }


    }

}
