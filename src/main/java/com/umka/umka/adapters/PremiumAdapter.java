package com.umka.umka.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umka.umka.R;
import com.umka.umka.holders.BaseHolder;
import com.umka.umka.holders.PremiumHolder;
import com.umka.umka.interfaces.ItemClickListener;
import com.umka.umka.model.BaseModel;
import com.umka.umka.model.Premium;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by trablone on 8/24/17.
 */

public class PremiumAdapter  extends BaseAdapter{



    public PremiumAdapter(Context context, ItemClickListener listener) {
        super(context, listener);
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_premium, parent, false);
        return new PremiumHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseHolder baseHolder, final int position) {

        final PremiumHolder holder = (PremiumHolder)baseHolder;
        final Premium item = (Premium) list.get(position);
        holder.imageView.setImageResource(item.image);
        holder.textTitle.setText(item.title);
        holder.textDesk.setText(item.desk);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClickListener(position, item);
            }
        });

        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        final ProductAdapter adapter = new ProductAdapter(context, new ItemClickListener() {
            @Override
            public void onItemClickListener(int position, BaseModel base) {
                listener.onItemClickListener(position, base);
            }
        });

        holder.recyclerView.setAdapter(adapter);
        adapter.updateList(item.products);
    }


}
