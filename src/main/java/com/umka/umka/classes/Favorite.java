package com.umka.umka.classes;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.umka.umka.R;
import com.umka.umka.activity.BaseActivity;
import com.umka.umka.adapters.MasterAdapter;
import com.umka.umka.database.DbHelper;
import com.umka.umka.database.FavoriteHelper;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by trablone on 5/12/17.
 */

public class Favorite {

    private Context context;
    private ImageView imageView;
    private FavoriteHelper helper;
    private int id;
    private boolean deleteItem = false;
    private MasterAdapter adapter;
    private ProgressDialog progressDialog;

    public Favorite(Context context, MasterAdapter adapter){
        this.context = context;
        this.adapter = adapter;
        helper = new FavoriteHelper(new DbHelper(context).getDataBase());
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        deleteItem = true;
    }

    public Favorite(Context context){
        this.context = context;
        helper = new FavoriteHelper(new DbHelper(context).getDataBase());
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
    }

    public void init(ImageView imageView, int id, final int position){
        this.id = id;
        this.imageView = imageView;
        if (helper.isFavorite(id)) {
            imageView.setImageResource(R.drawable.ic_action_remove_from_fav);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.checkLogin(context, new LoginListener() {
                        @Override
                        public void onSuccess() {
                            remove(position);
                        }
                    });

                }
            });
        }else {
            imageView.setImageResource(R.drawable.ic_action_add_to_fav);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.checkLogin(context, new LoginListener() {
                        @Override
                        public void onSuccess() {
                            add();
                        }
                    });

                }
            });
        }
    }

    public void init(ImageView imageView, int id){
        this.id = id;
        this.imageView = imageView;
        if (helper.isFavorite(id)) {
            imageView.setImageResource(R.drawable.ic_action_remove_from_fav);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.checkLogin(context, new LoginListener() {
                        @Override
                        public void onSuccess() {
                            remove(0);
                        }
                    });

                }
            });
        }else {
            imageView.setImageResource(R.drawable.ic_action_add_to_fav);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.checkLogin(context, new LoginListener() {
                        @Override
                        public void onSuccess() {
                            add();
                        }
                    });

                }
            });
        }
    }

    public void add(){
        progressDialog.show();
        HttpClient.post("/user/"+ ((BaseActivity)context).getUser().id + "/favorite/" + id, context, null,new BaseJsonHandler(context){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                helper.add(id);
                init(imageView, id);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                progressDialog.dismiss();
            }
        });
    }

    public void remove(final int position){
        progressDialog.show();
        HttpClient.del("/user/"+ ((BaseActivity)context).getUser().id + "/favorite/" + id, context, null, new BaseJsonHandler(context){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                helper.delete(id);
                init(imageView, id, position);
                if(deleteItem)
                    adapter.removeMaster(position);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                progressDialog.dismiss();
            }
        });
    }
}
