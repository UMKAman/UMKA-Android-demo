package com.umka.umka.fragments;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.vending.billing.IInAppBillingService;
import com.umka.umka.R;
import com.umka.umka.adapters.BaseAdapter;
import com.umka.umka.adapters.PremiumAdapter;
import com.umka.umka.billing.Billing;
import com.umka.umka.billing.BillingHelper;
import com.umka.umka.billing.BillingListener;
import com.umka.umka.billing.BillingTask;
import com.umka.umka.billing.InAppProduct;
import com.umka.umka.interfaces.ItemClickListener;
import com.umka.umka.model.BaseModel;
import com.umka.umka.model.Premium;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by trablone on 11/17/16.
 */

public class PremiumFragment extends BaseFragment implements ItemClickListener{

    private PremiumAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private BillingHelper billingHelper;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        refreshLayout = view.findViewById(R.id.refresh_layout);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseActivity()));
        adapter = new PremiumAdapter(getBaseActivity(), this);
        recyclerView.setAdapter(adapter);

        billingHelper = new BillingHelper(getBaseActivity(), new BillingListener() {
            @Override
            public void onBillingConnected(IInAppBillingService inAppBillingService) {
                getBillingList(inAppBillingService);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        billingHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        billingHelper.onDestroy();
        super.onDestroy();
    }



    private void getBillingList(IInAppBillingService inAppBillingService){
        final List<Premium> list = new ArrayList<>();
        new BillingTask(getActivity(), billingHelper, billingHelper.getProductIds(), "inapp"){

            @Override
            protected void onPostExecute(Billing billing) {
                super.onPostExecute(billing);
                list.add(new Premium(billing.list, R.string.item_b_title_1, R.string.item_b_desk_1, R.drawable.ic_shop_lift));
                adapter.updateList(list);
            }
        }.execute(inAppBillingService);

        new BillingTask(getActivity(), billingHelper, billingHelper.getProductIdsSubs(), "subs"){

            @Override
            protected void onPostExecute(Billing billing) {
                super.onPostExecute(billing);
                list.add(new Premium(billing.list, R.string.item_b_title_2, R.string.item_b_desk_2, R.drawable.ic_shop_premium));
                adapter.updateList(list);
                setProgress(false);
            }
        }.execute(inAppBillingService);
    }

    public void setProgress(final boolean show){
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setEnabled(show);
                refreshLayout.setRefreshing(show);
            }
        });
    }

    @Override
    public void onItemClickListener(int position, BaseModel base) {
        try {
            purchaseProduct((InAppProduct) base);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void purchaseProduct(InAppProduct product) throws Exception {
        String sku = product.getSku();
        String type = product.getType();

        String developerPayload = "12345";
        Bundle buyIntentBundle = billingHelper.getInAppBillingService().getBuyIntent(3, getActivity().getPackageName(), sku, type, developerPayload);
        PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
        startIntentSenderForResult(pendingIntent.getIntentSender(), BillingHelper.REQUEST_CODE_BUY, new Intent(), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), null);
    }
}
