package com.umka.umka.billing;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.umka.umka.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by trablone on 2/19/17.
 */

public class BillingAdapter extends RecyclerView.Adapter<BillingAdapter.ViewHolder> {

    private List<InAppProduct> list;
    private OnBillingItemClick listener;
    private Context context;


    public BillingAdapter(Context context, OnBillingItemClick listener) {
        this.listener = listener;
        this.context = context;
        list = new ArrayList<>();
    }

    public void updateData(List<InAppProduct> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_billing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final InAppProduct item = list.get(position);
        holder.itemPrice.setText(item.price);
        holder.itemTitle.setText(item.storeName);
        holder.itemDesc.setText(item.storeDescription);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView itemTitle;
        private final TextView itemDesc;
        private final TextView itemPrice;
        public ViewHolder(View v) {
            super(v);
            itemDesc = (TextView)v.findViewById(R.id.item_description);
            itemTitle = (TextView)v.findViewById(R.id.item_title);
            itemPrice = (TextView)v.findViewById(R.id.item_price);
        }
    }


    public interface OnBillingItemClick{
        void onItemClick(InAppProduct item);
    }
}
