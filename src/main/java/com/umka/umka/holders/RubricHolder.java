package com.umka.umka.holders;

import android.view.View;
import android.widget.TextView;

/**
 * Created by trablone on 11/17/16.
 */

public class RubricHolder extends BaseHolder {

    public final TextView itemTitle;

    public RubricHolder(View itemView) {
        super(itemView);
        itemTitle = (TextView)itemView.findViewById(com.umka.umka.R.id.item_title);
    }
}
