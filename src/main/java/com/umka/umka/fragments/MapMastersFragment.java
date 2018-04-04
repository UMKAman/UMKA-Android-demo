package com.umka.umka.fragments;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.Circle;
import com.umka.umka.R;
import com.umka.umka.activity.MasterProfileActivity;
import com.umka.umka.classes.BaseJsonHandler;
import com.umka.umka.classes.FilterTask;
import com.umka.umka.classes.HttpClient;
import com.umka.umka.classes.LocationService;
import com.umka.umka.classes.ParseMastersTask;
import com.umka.umka.model.Filter;
import com.umka.umka.model.Master;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by trablone on 5/26/17.
 */

public class MapMastersFragment extends MapBaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        markers = new ArrayList<>();
    }

    private List<Master> list;
    private List<Marker> markers;
    private CircleOptions circleOptions;
    private Circle circle;
    private Filter filter;

    public void setMyLocation(Location location) {
        if (location != null){


            new FilterTask(getBaseActivity(), location){
                @Override
                protected void onPostExecute(Filter filter) {
                    super.onPostExecute(filter);
                    MapMastersFragment.this.filter = filter;
                    loadPage(filter.url, filter.radius);
                }
            }.execute();

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                @Override
                public boolean onMarkerClick(Marker marker) {
                    if (marker.getTag() != null) {
                        new MarkerTask(){
                            @Override
                            protected void onPostExecute(Master master) {
                                super.onPostExecute(master);
                                MasterProfileActivity.showActivity(getBaseActivity(), master.id, master);
                            }
                        }.execute(marker.getTag());
                    }
                    return true;
                }
            });
        }
    }


    public void setUrl(String url, int radius){
        Log.e("tr", "setUrl: " + url + " " + radius);
        loadPage(url, radius);
    }

    public void loadPage(String url, final int radius){
        Log.e("tr", "googleMap: " + googleMap);

        if (googleMap != null && myPosition != null) {

            if (circle == null) {
                drawMarkerWithCircle(myPosition, radius * 1000);
            } else {
                circle.setCenter(myPosition);
                circle.setRadius(radius * 1000);
            }
            if (radius > 40){
                MAP_ZOOM = 8;
            }else if (radius > 30 && radius <= 40){
                MAP_ZOOM = 9;
            }else if (radius> 20 && radius <= 30){
                MAP_ZOOM = 9;
            }else if (radius > 10 && radius <= 20){
                MAP_ZOOM = 10;
            }else {
                MAP_ZOOM = 11;
            }

            HttpClient.get(url, getBaseActivity(), null, new BaseJsonHandler(getBaseActivity()){

                @Override
                public void onStart() {
                    super.onStart();
                    setProgress(true);
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    setProgress(false);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);
                    new ParseMastersTask(getBaseActivity().getUser().id) {
                        @Override
                        protected void onPostExecute(List<Master> masters) {
                            super.onPostExecute(masters);
                            for (Marker marker : markers){
                                marker.remove();
                            }
                            markers.clear();
                            list = masters;
                            for (Master master : masters) {
                                if (!TextUtils.isEmpty(master.lon)){
                                    Marker marker = googleMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(Double.parseDouble(master.lat), Double.parseDouble(master.lon)))
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_specialist_location))
                                            .title(master.user.getName()));
                                    marker.setTag(master.id);
                                    markers.add(marker);
                                }

                                }
                        }
                    }.execute(response);
                }
            });
        }else {
            initLocationHelper();
        }
    }

    public class MarkerTask extends AsyncTask<Object, Void, Master>{

        @Override
        protected Master doInBackground(Object... voids) {
            int id = (Integer) voids[0];
            for (Master master : list){
                if (master.id == id)
                    return master;
            }
            return null;
        }
    }

    private void drawMarkerWithCircle(LatLng position, int radius){

        circleOptions = new CircleOptions()
                .center(position)
                .radius(radius)
                .fillColor(getResources().getColor(com.umka.umka.R.color.colorPrimaryTransparent))
                .strokeColor(getResources().getColor(com.umka.umka.R.color.colorPrimary))
                .strokeWidth(4);
        circle = googleMap.addCircle(circleOptions);

    }
}
