package com.umka.umka.billing;

import com.android.vending.billing.IInAppBillingService;

/**
 * Created by trablone on 2/25/17.
 */

public interface BillingListener {
    void onBillingConnected(IInAppBillingService inAppBillingService);
}
