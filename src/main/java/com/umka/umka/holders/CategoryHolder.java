package com.umka.umka.holders;

import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by trablone on 11/13/16.
 */

public class CategoryHolder extends BaseHolder{

    public final ImageView itemImage;
    public final TextView itemTitle;
    public final CardView cardView;

    public CategoryHolder(View itemView) {
        super(itemView);
        itemImage = (ImageView)itemView.findViewById(com.umka.umka.R.id.item_image);
        itemTitle = (TextView)itemView.findViewById(com.umka.umka.R.id.item_title);
        cardView = (CardView)itemView.findViewById(com.umka.umka.R.id.item_card);
    }
}
