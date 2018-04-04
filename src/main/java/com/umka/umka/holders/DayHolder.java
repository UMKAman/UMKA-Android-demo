package com.umka.umka.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.umka.umka.R;

/**
 * Created by trablone on 12/9/16.
 */

public class DayHolder extends BaseHolder {
    public final TextView itemTitle;
    public final ImageView itemImage;

    public DayHolder(View itemView) {
        super(itemView);
        itemImage= (ImageView)itemView.findViewById(R.id.item_image);
        itemTitle = (TextView)itemView.findViewById(R.id.item_title);
    }
}
