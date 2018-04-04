package com.umka.umka.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.umka.umka.R;
import com.umka.umka.classes.BaseJsonHandler;
import com.umka.umka.classes.HttpClient;
import com.umka.umka.classes.ParseProfileTask;
import com.umka.umka.classes.UpdateProfileListener;
import com.umka.umka.database.DbHelper;
import com.umka.umka.database.UserHelper;
import com.umka.umka.model.Profile;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by trablone on 7/5/17.
 */

public class SettingsFragment extends BaseFragment {

    private Switch itemNotification;
    private UserHelper userHelper;
    private Profile user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        itemNotification = view.findViewById(R.id.item_notification);
        return  view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userHelper = new UserHelper(new DbHelper(getBaseActivity()).getDataBase());
        user = userHelper.getUser();
        getProfile();
    }

    public void getProfile() {
        HttpClient.get("/user/" + user.id, getBaseActivity(), null, new BaseJsonHandler(getBaseActivity()){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                new ParseProfileTask() {
                    @Override
                    protected void onPostExecute(Profile user) {
                        super.onPostExecute(user);
                        setProfile(user);
                    }
                }.execute(response);
            }

        });
    }

    public void updateProfile(String key, Object value){

            JSONObject object = new JSONObject();
            try {
                object.put(key, value);
            } catch (JSONException e) {
                Log.e("tr", "e: " + e.getMessage());
            }
            Log.e("tr", "params: " + object);
            HttpClient.put(getBaseActivity(), "/user/" + user.id , new StringEntity(object.toString(), "UTF-8"), new BaseJsonHandler(getBaseActivity()){

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);


                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);

                }

            });

    }

    private void setProfile(Profile profile){
        itemNotification.setChecked(profile.pushNotify);
        itemNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean check = itemNotification.isChecked();
                updateProfile("pushNotify", check);
            }
        });
    }
}
