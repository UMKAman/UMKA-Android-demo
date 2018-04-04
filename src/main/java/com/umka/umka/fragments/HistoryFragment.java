package com.umka.umka.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umka.umka.adapters.HistoryAdapter;
import com.umka.umka.classes.ParseOrdersTask;
import com.umka.umka.interfaces.PageLoadListener;
import com.umka.umka.model.BaseModel;
import com.umka.umka.model.History;
import com.umka.umka.model.Order;

import org.json.JSONArray;

import java.util.List;

/**
 * Created by trablone on 11/14/16.
 */

public class HistoryFragment extends BaseRecyclerFragment {

    public static HistoryFragment newInstance(String url){
        HistoryFragment fragment = new HistoryFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", url);
        fragment.setArguments(bundle);
        return fragment;
    }
    private HistoryAdapter adapter;
    private String url;

    @Override
    public int getNumColumns() {
        return 1;
    }

    @Override
    public void getNextPage(int page) {

    }

    @Override
    public void updateData() {

    }

    @Override
    public boolean isEnableUpdate() {
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new HistoryAdapter(getBaseActivity(), this);
        getRecyclerView().setAdapter(adapter);
    }

    private void getOrders(){
        loadPage(url, null, new PageLoadListener() {
            @Override
            public void onLoadSuccess(JSONArray array) {
                new ParseOrdersTask(getBaseActivity()){
                    @Override
                    protected void onPostExecute(List<Order> masters) {
                        super.onPostExecute(masters);
                        adapter.updateList(masters);
                    }
                }.execute(array);
            }

            @Override
            public void onLoadFailure() {

            }
        });
    }

    @Override
    public void onItemClickListener(int position, BaseModel base) {
        History item = (History)base;
    }
}
