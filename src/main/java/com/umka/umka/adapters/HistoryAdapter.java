package com.umka.umka.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umka.umka.holders.BaseHolder;
import com.umka.umka.holders.HistoryHolder;
import com.umka.umka.interfaces.ItemClickListener;
import com.umka.umka.model.History;

/**
 * Created by trablone on 11/14/16.
 */

public class HistoryAdapter extends BaseAdapter {

    public HistoryAdapter(Context context, ItemClickListener listener) {
        super(context, listener);
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(com.umka.umka.R.layout.item_history, parent, false);
        return new BaseHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseHolder bholder, int position) {
        final HistoryHolder holder = (HistoryHolder)bholder;
        final History item = (History)list.get(position);
    }
}
