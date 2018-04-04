package com.umka.umka.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umka.umka.holders.BaseHolder;
import com.umka.umka.holders.RubricHolder;
import com.umka.umka.interfaces.ItemClickListener;
import com.umka.umka.model.Category;

/**
 * Created by trablone on 11/17/16.
 */

public class RubricAdapter extends BaseAdapter {
    public RubricAdapter(Context context, ItemClickListener listener) {
        super(context, listener);
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(com.umka.umka.R.layout.item_rubric, parent, false);
        return new RubricHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseHolder bholder,final int position) {
        final RubricHolder holder = (RubricHolder)bholder;
        final Category item = (Category) list.get(position);
        holder.itemTitle.setText(item.section_name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClickListener(position, item);
            }
        });
    }
}
