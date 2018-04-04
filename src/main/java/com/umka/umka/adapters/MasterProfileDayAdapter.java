package com.umka.umka.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umka.umka.holders.BaseHolder;
import com.umka.umka.holders.DayHolder;
import com.umka.umka.interfaces.ItemClickListener;
import com.umka.umka.model.Day;

/**
 * Created by trablone on 12/9/16.
 */

public class MasterProfileDayAdapter extends BaseAdapter {


    private int type;

    public MasterProfileDayAdapter(Context context, ItemClickListener listener) {
        super(context, listener);
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(com.umka.umka.R.layout.item_day, parent, false);
        return new DayHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseHolder bholder, final int position) {
        final Day item = (Day)getList().get(position);
        final DayHolder holder = (DayHolder)bholder;
        holder.itemTitle.setText(item.title);
        if (listener != null){
            if (item.isSelect()) {
                holder.itemImage.setColorFilter(context.getResources().getColor(com.umka.umka.R.color.colorAccent));
                holder.itemImage.setVisibility(View.VISIBLE);
            }else {
                holder.itemImage.setVisibility(View.GONE);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClickListener(position, item);
                }
            });

        }else {
            holder.itemImage.setVisibility(View.GONE);
        }

        if (item.isSelect()){
            holder.itemTitle.setTextColor(context.getResources().getColor(com.umka.umka.R.color.colorLightGray));
        }else {
            holder.itemTitle.setTextColor(context.getResources().getColor(com.umka.umka.R.color.colorBlack));
        }
    }
}
