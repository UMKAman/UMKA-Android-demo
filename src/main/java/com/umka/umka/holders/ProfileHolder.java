package com.umka.umka.holders;

import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by trablone on 12/9/16.
 */

public class ProfileHolder extends MasterHolder {


    public final TabLayout tabLayout;
    public final RecyclerView recyclerView;
    public final TextView itemCalendar;

    public ProfileHolder(View itemView) {
        super(itemView);

        tabLayout = (TabLayout)itemView.findViewById(com.umka.umka.R.id.tab_layout);
        recyclerView = (RecyclerView)itemView.findViewById(com.umka.umka.R.id.recycler_view);
        itemCalendar = (TextView)itemView.findViewById(com.umka.umka.R.id.item_calendar);
    }
}
