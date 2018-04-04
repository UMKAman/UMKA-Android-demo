package com.umka.umka.billing;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.android.vending.billing.IInAppBillingService;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by trablone on 2/19/17.
 */

public class BillingTask extends AsyncTask<IInAppBillingService, Void, Billing> {
    Context context;
    String type;
    private final int MAX_ITEMS = 20;
    private BillingHelper billingHelper;
    ArrayList<String> list;

    public BillingTask(Context context, BillingHelper billingHelper,ArrayList<String> list,  String type){
        this.context = context;
        this.type = type;
        this.billingHelper = billingHelper;
        this.list = list;
    }


    @Override
    protected Billing doInBackground(IInAppBillingService... iInAppBillingServices) {

        Bundle query = new Bundle();
        query.putStringArrayList("ITEM_ID_LIST", list);
        Bundle skuDetails = null;
        Billing item = new Billing();
        item.list = new ArrayList<>();
        try {
            skuDetails = iInAppBillingServices[0].getSkuDetails(3, context.getPackageName(), type, query);
            ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");

                if (responseList != null && responseList.size() > 0){

                    for (int i = 0; i < responseList.size(); i++) {
                        String responseItem = responseList.get(i);
                        JSONObject jsonObject = new JSONObject(responseItem);
                        Log.e("tr","jsonObject: " + jsonObject);
                        InAppProduct product = new InAppProduct();
                        // "com.example.myapp_testing_inapp1"
                        product.productId = jsonObject.getString("productId");
                        // Покупка
                        product.storeName = jsonObject.getString("title");
                        // Детали покупки
                        product.storeDescription = jsonObject.getString("description");
                        if (product.productId.contains(""))
                        // "0.99USD"
                        product.price = jsonObject.getString("price");
                        // "true/false"
                        product.isSubscription = jsonObject.getString("type").equals("subs");
                        // "990000" = цена x 1000000
                        product.priceAmountMicros = Integer.parseInt(jsonObject.getString("price_amount_micros"));
                        // USD
                        product.currencyIsoCode = jsonObject.getString("price_currency_code");


                        item.list.add(product);
                    }
                }

        } catch (RemoteException | JSONException e) {
            Log.e("tr", "e: " + e.getMessage());
        }catch (Throwable e){
            Log.e("tr", "e: " + e.getMessage());
        }


        return item;
    }

}
