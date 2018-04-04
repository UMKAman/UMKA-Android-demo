package com.umka.umka.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umka.umka.adapters.NotificationAdapter;
import com.umka.umka.model.BaseModel;
import com.umka.umka.model.Notification;

/**
 * Created by trablone on 11/13/16.
 */

public class NotificationFragment extends BaseRecyclerFragment {


    private NotificationAdapter adapter;

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
        adapter = new NotificationAdapter(getBaseActivity(), this);
        getRecyclerView().setAdapter(adapter);
    }

    @Override
    public void onItemClickListener(int position, BaseModel base) {
        Notification item = (Notification)base;
    }
}
