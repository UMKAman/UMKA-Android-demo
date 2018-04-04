package com.umka.umka.billing;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umka.umka.fragments.BaseFragment;
import com.android.vending.billing.IInAppBillingService;

import java.util.ArrayList;


/**
 * Created by trablone on 2/19/17.
 */

public class BillingFragment extends BaseFragment implements BillingAdapter.OnBillingItemClick, SwipeRefreshLayout.OnRefreshListener{
    private BillingHelper billingHelper;
    private BillingAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayout imageView;
    private TextView billingText;
    private TextView textEmpty;
    private SwipeRefreshLayout refreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(com.umka.umka.R.layout.fragment_billing, container, false);
        refreshLayout = (SwipeRefreshLayout)v.findViewById(com.umka.umka.R.id.refresh);
        textEmpty = (TextView) v.findViewById(com.umka.umka.R.id.hint_listTextEmpty);
        recyclerView = (RecyclerView) v.findViewById(com.umka.umka.R.id.recyclerView);
        imageView = (LinearLayout) v.findViewById(com.umka.umka.R.id.layout_lacky);
        billingText = (TextView)v.findViewById(com.umka.umka.R.id.billing_text);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refreshLayout.setOnRefreshListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new BillingAdapter(getActivity(), this);
        recyclerView.setAdapter(adapter);
        billingHelper = new BillingHelper(getActivity(), new BillingListener() {
            @Override
            public void onBillingConnected(IInAppBillingService inAppBillingService) {
                getProductList(inAppBillingService);
            }
        });
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        billingHelper.onActivityResult(requestCode, resultCode, data);
    }

    private void setProgress(final boolean view){
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setEnabled(view);
                refreshLayout.setRefreshing(view);
            }
        });
    }

    private ArrayList<String> getProductIds(){
        ArrayList<String> list = new ArrayList<>();
        list.add("raised_1");
        list.add("raised_3");
        list.add("raised_5");
        return list;
    }

    private ArrayList<String> getProductIdsSubs(){
        ArrayList<String> list = new ArrayList<>();
        list.add("raised_1");
        list.add("raised_3");
        list.add("raised_5");
        return list;
    }
    private void getProductList(IInAppBillingService inAppBillingService){


        new BillingTask(getActivity(), billingHelper, getProductIds(), "inapp"){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                setProgress(true);
            }

            @Override
            protected void onPostExecute(Billing billing) {
                super.onPostExecute(billing);
                if (billing.list.size() > 0 ){
                    adapter.updateData(billing.list);
                }else if (billing.list.size() == 0){
                    imageView.setVisibility(View.VISIBLE);
                    billingText.setText("Что то пошло не так(");
                }

                setProgress(false);
            }
        }.execute(inAppBillingService);

        new BillingTask(getActivity(), billingHelper, getProductIdsSubs(), "subs"){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                setProgress(true);
            }

            @Override
            protected void onPostExecute(Billing billing) {
                super.onPostExecute(billing);
                if (billing.list.size() > 0 ){
                    adapter.updateData(billing.list);
                }else if (billing.list.size() == 0){
                    imageView.setVisibility(View.VISIBLE);
                    billingText.setText("Что то пошло не так(");
                }

                setProgress(false);
            }
        }.execute(inAppBillingService);
    }



    @Override
    public void onDestroy() {
        billingHelper.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onItemClick(InAppProduct item) {

        try {
            purchaseProduct(item);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void purchaseProduct(InAppProduct product) throws Exception {
        String sku = product.getSku();
        String type = product.getType();
        // сюда вы можете добавить произвольные данные
        // потом вы сможете получить их вместе с покупкой
        String developerPayload = "12345";
        Bundle buyIntentBundle = billingHelper.getInAppBillingService().getBuyIntent(3, getActivity().getPackageName(), sku, type, developerPayload);
        PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
        startIntentSenderForResult(pendingIntent.getIntentSender(), BillingHelper.REQUEST_CODE_BUY, new Intent(), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), null);
    }
}
