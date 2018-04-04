package com.umka.umka.classes;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.umka.umka.activity.BaseActivity;
import com.umka.umka.database.DbHelper;
import com.umka.umka.database.OrderHelper;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by trablone on 5/13/17.
 */

public class CreateOrder {

    private ImageView imageView;
    private Context context;
    private OrderHelper helper;
    private int id;
    private String phone;


    public CreateOrder(ImageView imageView, Context context){
        this.imageView = imageView;
        this.context = context;
        helper = new OrderHelper(new DbHelper(context).getDataBase());
    }

    public void init(final int id, final String phone){
        this.id = id;
        this.phone = phone;

        if (imageView != null)
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.checkLogin(context, new LoginListener() {
                        @Override
                        public void onSuccess() {
                            if (helper.isCreate(id)) {
                                ((BaseActivity) context).call(phone);
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
            object.put("master", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpClient.post(context, "/order", new StringEntity(object.toString(), "UTF-8"), new BaseJsonHandler(context){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                helper.add(id);
                ((BaseActivity)context).call(phone);
            }

        });
    }


}
