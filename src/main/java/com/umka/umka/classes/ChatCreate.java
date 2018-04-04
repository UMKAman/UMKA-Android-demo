package com.umka.umka.classes;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.umka.umka.activity.BaseActivity;
import com.umka.umka.activity.MessagesActivity;
import com.umka.umka.database.DbHelper;
import com.umka.umka.database.OrderHelper;
import com.umka.umka.model.Master;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by trablone on 5/18/17.
 */

public class ChatCreate {

    private Context context;
    private Master master;
    private int chat_id;
    private OrderHelper helper;

    public ChatCreate(Context context, Master master){
        this.master = master;
        this.context = context;
        helper = new OrderHelper(new DbHelper(context).getDataBase());
    }

    public void init(final ImageView imageView){
        Utils.checkLogin(context, new LoginListener() {
            @Override
            public void onSuccess() {
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (helper.isCreate(master.id)) {
                            getChat();
                        } else {
                            add();
                        }

                    }
                });
            }
        });
    }

    public void add(){
        JSONObject object = new JSONObject();
        try {
            object.put("user", ((BaseActivity)context).getUser().id);
            object.put("master", master.id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpClient.post(context, "/order", new StringEntity(object.toString(), "UTF-8"), new BaseJsonHandler(context){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                helper.add(master.id);
                getChat();
            }
        });
    }

    private void createChat(){
        RequestParams params = new RequestParams();
        params.put("user", ((BaseActivity)context).getUser().id);
        params.put("master", master.id);
        HttpClient.post("/chat", context, params, new BaseJsonHandler(context){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    chat_id = response.getInt("id");
                    startActivity();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getChat(){
        RequestParams params = new RequestParams();
        params.put("master", master.id);
        HttpClient.get("/chat", context, params , new BaseJsonHandler(context){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                if (response.length() == 0){
                    createChat();
                }else {
                    try {
                        JSONObject object = response.getJSONObject(0);
                        chat_id = object.getInt("id");
                        startActivity();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void startActivity(){
        MessagesActivity.showActivity(context, chat_id, master.user.getName());

    }

}
