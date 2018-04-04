package com.umka.umka.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umka.umka.R;
import com.umka.umka.adapters.OrdersAdapter;
import com.umka.umka.classes.ParseOrdersTask;
import com.umka.umka.interfaces.PageLoadListener;
import com.umka.umka.model.BaseModel;
import com.umka.umka.model.Order;

import org.json.JSONArray;

import java.util.List;

/**
 * Created by trablone on 5/5/17.
 */

public class OrdersFragment extends BaseRecyclerFragment {

    public static OrdersFragment newInstance(String url){
        OrdersFragment fragment = new OrdersFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        fragment.setArguments(args);
        return fragment;
    }

    private String url;
    private OrdersAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        url = getArguments().getString("url");

        adapter = new OrdersAdapter(getBaseActivity(), getBaseActivity().getUser().is_master, new OrdersAdapter.OrdersListener() {
            @Override
            public void createReview(Order item) {
                getMainActivity().addFragment(ReviewsFragment.newInstance(item.master.id, true), getResources().getString(R.string.add_review), false);
            }
        });
        getRecyclerView().setAdapter(adapter);
        getOrders();
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

    }

    @Override
    public int getNumColumns() {
        return 1;
    }

    @Override
    public void getNextPage(int page) {

    }

    @Override
    public void updateData() {
        getOrders();
    }

    @Override
    public boolean isEnableUpdate() {
        return true;
    }
}
