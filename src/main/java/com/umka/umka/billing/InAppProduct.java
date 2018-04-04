package com.umka.umka.billing;


import android.content.Context;

import com.umka.umka.R;
import com.umka.umka.model.BaseModel;

/**
 * Created by trablone on 2/19/17.
 */

public class InAppProduct extends BaseModel {

        public String productId;
        public String storeName;
        public String storeDescription;
        public String price;
        public boolean isSubscription;
        public int priceAmountMicros;
        public String currencyIsoCode;
        public boolean error;

        public String getSku() {
            return productId;
        }

        public String getType() {
            return isSubscription ? "subs" : "inapp";
        }

        public String getDescription(){
                if (getType().contains("subs")){
                        return price + "/" + getMonth();
                }else {
                        return price;
                }
        }

        private String getMonth(){
                if (productId.equals("premium_1")){
                        return "1 месяц";
                }
                if (productId.equals("premium_3")){
                        return "3 месяца";
                }
                if (productId.equals("premium_1_year")){
                        return "1 год";
                }
                return "какой то срок";
        }
}
