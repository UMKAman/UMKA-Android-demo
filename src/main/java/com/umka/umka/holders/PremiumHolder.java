package com.umka.umka.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.umka.umka.R;

/**
 * Created by trablone on 8/24/17.
 */

public class PremiumHolder extends BaseHolder {

    public final TextView textTitle;
    public final TextView textDesk;
    public final RecyclerView recyclerView;
    public final ImageView imageView;

    public PremiumHolder(View itemView) {
        super(itemView);
        textDesk = itemView.findViewById(R.id.item_text);
        recyclerView = itemView.findViewById(R.id.item_product_list);
        textTitle = itemView.findViewById(R.id.item_title);
        imageView = itemView.findViewById(R.id.item_image);
    }
}
