package com.umka.umka.billing;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.android.vending.billing.IInAppBillingService;


import org.json.JSONObject;

import java.util.List;

/**
 * Created by trablone on 2/25/17.
 */

public class PurchasesTask extends AsyncTask<Void, Void, Boolean> {

    Context context;
    private BillingHelper billingHelper;
    String purchaseToken = "delete";

    public PurchasesTask(Context context, BillingHelper billingHelper){
        this.context = context;
        this.billingHelper = billingHelper;

    }

    @Override
    protected Boolean doInBackground(Void ... args) {
        try {
            String continuationToken = null;
            do {
                Bundle result = billingHelper.getInAppBillingService().getPurchases(3, context.getPackageName(), "subs", continuationToken);
                if (result.getInt("RESPONSE_CODE", -1) != 0) {
                    throw new Exception("Invalid response code");
                }
                List<String> responseList = result.getStringArrayList("INAPP_PURCHASE_DATA_LIST");

                List<String> products = billingHelper.getProductIdsSubs();
                for (String text : responseList){
                    try {
                        JSONObject jsonObject = new JSONObject(text);
                        Log.e("tr","jsonObject: " + jsonObject);
                        String orderId = jsonObject.optString("orderId");
                        String packageName = jsonObject.getString("packageName");
                        String productId = jsonObject.getString("productId");
                        long purchaseTime = jsonObject.getLong("purchaseTime");
                        int purchaseState = jsonObject.getInt("purchaseState");
                        String developerPayload = jsonObject.optString("developerPayload");
                        purchaseToken = jsonObject.getString("purchaseToken");
                        if (products.contains(productId))
                            return true;

                    } catch (Exception e) {

                    }
                }

                continuationToken = result.getString("INAPP_CONTINUATION_TOKEN");
            } while (continuationToken != null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }




}
