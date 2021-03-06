package com.umka.umka.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.umka.umka.R;

/**
 * Created by trablone on 06.10.17.
 */

public class ProductHolder extends BaseHolder {
    public final TextView textTitle;
    public final TextView textDesk;
    public final ImageView imageView;

    public ProductHolder(View itemView) {
        super(itemView);
        textDesk = itemView.findViewById(R.id.item_text);
        textTitle = itemView.findViewById(R.id.item_title);
        imageView = itemView.findViewById(R.id.item_image);
    }
}
