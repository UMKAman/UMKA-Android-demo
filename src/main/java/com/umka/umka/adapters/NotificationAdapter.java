package com.umka.umka.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umka.umka.holders.BaseHolder;
import com.umka.umka.holders.NotificationHolder;
import com.umka.umka.interfaces.ItemClickListener;
import com.umka.umka.model.Notification;

/**
 * Created by trablone on 11/13/16.
 */

public class NotificationAdapter extends BaseAdapter {

    public NotificationAdapter(Context context, ItemClickListener listener){
        super(context, listener);
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(com.umka.umka.R.layout.item_notification, parent, false);
        return new BaseHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseHolder bholder, int position) {
        final NotificationHolder holder = (NotificationHolder) bholder;
        final Notification item = (Notification) list.get(position);
    }
}
