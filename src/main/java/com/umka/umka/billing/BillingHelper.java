package com.umka.umka.billing;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.android.vending.billing.IInAppBillingService;
import com.loopj.android.http.RequestParams;
import com.umka.umka.classes.BaseJsonHandler;
import com.umka.umka.classes.HttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


/**
 * Created by trablone on 2/25/17.
 */

public class BillingHelper {
    public static final int REQUEST_CODE_BUY = 1234;

    public static final int BILLING_RESPONSE_RESULT_OK = 0;

    private IInAppBillingService inAppBillingService;
    private Context context;
    private BillingListener listener;

    public BillingHelper(Context context, BillingListener listener) {
        this.context = context;
        this.listener = listener;

        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            inAppBillingService = IInAppBillingService.Stub.asInterface(service);
            listener.onBillingConnected(inAppBillingService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            inAppBillingService = null;
        }
    };

    public IInAppBillingService getInAppBillingService() {
        return inAppBillingService;
    }


    public ArrayList<String> getProductIds() {
        ArrayList<String> list = new ArrayList<>();
        list.add("raised_1");
        list.add("raised_3");
        list.add("raised_5");
        return list;
    }

    public ArrayList<String> getProductIdsSubs() {
        ArrayList<String> list = new ArrayList<>();
        list.add("premium_1");
        list.add("premium_3");
        list.add("premium_1_year");
        return list;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_BUY) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", -1);

            if (responseCode == BILLING_RESPONSE_RESULT_OK) {
                String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
                String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");
                // можете проверить цифровую подпись
                readPurchase(purchaseData);
            } else {
                // обрабатываем ответ
            }
        }
    }

    public void readPurchase(String purchaseData) {
        try {
            JSONObject jsonObject = new JSONObject(purchaseData);

            //String orderId = jsonObject.optString("orderId");
            //String packageName = jsonObject.getString("packageName");
            //String productId = jsonObject.getString("productId");
            //long purchaseTime = jsonObject.getLong("purchaseTime");
            //int purchaseState = jsonObject.getInt("purchaseState");
            //String developerPayload = jsonObject.optString("developerPayload");
            //String purchaseToken = jsonObject.getString("purchaseToken");
            checkBilling(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkBilling(JSONObject object) {
        final JSONObject body = new JSONObject();


        try {
            body.put("platform", "android");
            body.put("receipt", object);
            final String productId = object.getString("productId");
            final String purchaseToken = object.getString("purchaseToken");
            HttpClient.post(context, "/payments", new StringEntity(body.toString(), "UTF-8"), new BaseJsonHandler(context) {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        boolean payment = response.getBoolean("payment");
                        if (payment){
                            if (getProductIds().contains(productId)){
                                inAppBillingService.consumePurchase(3, context.getPackageName(), purchaseToken);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void onDestroy() {
        if (serviceConnection != null) {
            context.unbindService(serviceConnection);
        }
    }
}
