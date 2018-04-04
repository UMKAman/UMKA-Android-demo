package com.umka.umka.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umka.umka.R;
import com.umka.umka.billing.InAppProduct;
import com.umka.umka.holders.BaseHolder;
import com.umka.umka.holders.PremiumHolder;
import com.umka.umka.holders.ProductHolder;
import com.umka.umka.interfaces.ItemClickListener;


/**
 * Created by trablone on 06.10.17.
 */

public class ProductAdapter extends BaseAdapter{



    public ProductAdapter(Context context, ItemClickListener listener) {
        super(context, listener);
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseHolder baseHolder, final int position) {

        final ProductHolder holder = (ProductHolder)baseHolder;
        final InAppProduct item = (InAppProduct) list.get(position);
        holder.textTitle.setText(item.storeName.replace("(Умка.city)", ""));
        holder.textDesk.setText(item.getDescription());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClickListener(position, item);
            }
        });

    }


}