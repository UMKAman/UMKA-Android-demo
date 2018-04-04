package com.umka.umka.classes;

import android.content.Context;
import android.util.Log;

import com.umka.umka.database.DbHelper;
import com.umka.umka.database.UserHelper;
import com.umka.umka.model.Profile;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cz.msebera.android.httpclient.conn.ssl.SSLSocketFactory;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.protocol.HTTP;


/**
 * Created by trablone on 17.11.15.
 */
public class HttpClient {

    public static final String BASE_URL = "https://umka.city/api";
    public static final String BASE_URL_IMAGE = "https://umka.city";

    private static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);

    public static AsyncHttpClient getClient(){
        return client;
    }
    public static void get(String url, Context context, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        Log.e("tr", "params: " + params);
        AsyncHttpClient client = new AsyncHttpClient();
        addToken(context, client);
        client.setEnableRedirects(true);
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    private static void addToken(Context context, AsyncHttpClient client){
        String token = null;
        client.removeAllHeaders();
        UserHelper userHelper = new UserHelper(new DbHelper(context).getDataBase());
        Profile uset = userHelper.getUser();
        if (uset != null)
            token = uset.token;
        if (token != null) {
            Log.e("tr", "token: " + token);
            client.addHeader("Authorization", "Bearer " + token);
        }
    }

    public static void post(String url, Context context, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        Log.e("tr", "params: " + params);
        AsyncHttpClient client = new AsyncHttpClient();
        addToken(context, client);
        //client.addHeader(HTTP.CONTENT_TYPE, "multipart/form-data");
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void postPic(String url, Context context, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        Log.e("tr", "params: " + params);
        AsyncHttpClient client = new AsyncHttpClient();
        addToken(context, client);
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(Context context, String url, StringEntity entity, AsyncHttpResponseHandler responseHandler) {

        AsyncHttpClient client = new AsyncHttpClient();
        addToken(context, client);
        client.addHeader(HTTP.CONTENT_TYPE, "application/json");
        client.post(context, getAbsoluteUrl(url), entity, "application/json", responseHandler);
    }

    public static void put(Context context, String url, StringEntity entity, AsyncHttpResponseHandler responseHandler) {
        AsyncHttpClient client = new AsyncHttpClient();
        addToken(context, client);
        client.addHeader(HTTP.CONTENT_TYPE, "application/json");
        client.put(context, getAbsoluteUrl(url), entity, "application/json", responseHandler);
    }

    public static void get(Context context, String url, StringEntity entity, AsyncHttpResponseHandler responseHandler) {
        AsyncHttpClient client = new AsyncHttpClient();
        addToken(context, client);
        client.addHeader(HTTP.CONTENT_TYPE, "application/json");
        client.get(context, getAbsoluteUrl(url), entity, "application/json", responseHandler);
    }
//multipart/form-data
    public static void put( String url, Context context, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        AsyncHttpClient client = new AsyncHttpClient();
        addToken(context, client);
        client.put(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void putImage( String url, Context context, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        AsyncHttpClient client = new AsyncHttpClient();
        addToken(context, client);
        //client.addHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");
        client.put(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void del( String url, Context context, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        AsyncHttpClient client = new AsyncHttpClient();
        addToken(context, client);
        client.delete(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        String url = BASE_URL + relativeUrl;
        Log.e("tr", "url: " + url);
        return url;
    }

    public static SSLContext getSslContext() {

        TrustManager[] byPassTrustManagers = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }
        } };

        SSLContext sslContext=null;

        try {
            sslContext = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sslContext.init(null, byPassTrustManagers, new SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return sslContext;

    }
}
