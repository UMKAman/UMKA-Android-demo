package com.umka.umka.services;

import android.content.Context;
import android.util.Log;

import com.umka.umka.classes.BaseJsonHandler;
import com.umka.umka.classes.HttpClient;
import com.umka.umka.database.DbHelper;
import com.umka.umka.database.UserHelper;
import com.umka.umka.model.Profile;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;


import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by trablone on 2/4/17.
 */

public class UpdateFirebaseToken {

    public static void update(final Context context, final String token){
        final UserHelper helper =  new UserHelper(new DbHelper(context).getDataBase());
        final Profile user = helper.getUser();
        if (user != null){

            RequestParams params = new RequestParams();
            params.put("user", user.id);
            params.put("device", "Android device");
            params.put("token", token);
            HttpClient.put("/user/firetoken",context, params, new BaseJsonHandler(context){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);

                    user.device_token = token;
                    helper.updateUser(user);
                }
            });
        }
    }
}
