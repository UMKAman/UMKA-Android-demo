package com.umka.umka.classes;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.umka.umka.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by trablone on 5/11/17.
 */

public class BaseJsonHandler extends JsonHttpResponseHandler {

    public JSONObject object;
    public ProgressDialog dialog;
    private Context context;


    public BaseJsonHandler(Context context){
        this.context = context;
        dialog = new ProgressDialog(context);
        dialog.setMessage(context.getResources().getString(R.string.send_request));
        dialog.setCancelable(false);
    }
    private void replaceNull(JSONObject object){
        try {
            this.object = new JSONObject(object.toString().replaceAll("null", "\"\""));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("tr", "e: " + e.getMessage());
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        super.onFailure(statusCode, headers, responseString, throwable);
        Log.e("tr", "responseString: " + statusCode + " "+ responseString);
        dialog.dismiss();
        showErrorToast(statusCode);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        super.onFailure(statusCode, headers, throwable, errorResponse);
        Log.e("tr", "errorResponse o: " + statusCode + " "+ errorResponse);
        dialog.dismiss();
        showErrorToast(statusCode);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        super.onFailure(statusCode, headers, throwable, errorResponse);
        Log.e("tr", "errorResponse a: " + statusCode + " "+ errorResponse);
        dialog.dismiss();
        showErrorToast(statusCode);
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
        super.onSuccess(statusCode, headers, response);
        Log.e("tr", "response: " + statusCode + " "+ response);
        dialog.dismiss();
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        super.onSuccess(statusCode, headers, response);
        replaceNull(response);
        Log.e("tr", "response: " + statusCode + " "+ response);
        dialog.dismiss();
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String responseString) {
        super.onSuccess(statusCode, headers, responseString);
        Log.e("tr", "responseString: " + statusCode + " "+ responseString);
        dialog.dismiss();
    }

    @Override
    public void onFinish() {
        super.onFinish();
        dialog.dismiss();
    }

    private void showErrorToast(int statusCode){
        if (statusCode != 201 && statusCode != 400 && statusCode != 0);
//            Toast.makeText(context, context.getResources().getString(R.string.error) + statusCode, Toast.LENGTH_SHORT).show();
    }
}
