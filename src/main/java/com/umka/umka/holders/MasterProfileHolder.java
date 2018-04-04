package com.umka.umka.holders;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

/**
 * Created by trablone on 5/6/17.
 */

public class MasterProfileHolder extends UserProfileHolder {

    public final Button addService;
    public final Button addPrice;
    public final Button addPortfolio;
    public final LinearLayout layoutMaster;
    public final LinearLayout layoutServices;
    public final LinearLayout layoutPrice;
    public final LinearLayout layoutPortfolio;
    public final RadioGroup radioGroupYo;



    public MasterProfileHolder(View itemView) {
        super(itemView);



        addPortfolio = (Button)itemView.findViewById(com.umka.umka.R.id.add_portfolio);
        addPrice = (Button)itemView.findViewById(com.umka.umka.R.id.add_price);
        addService = (Button)itemView.findViewById(com.umka.umka.R.id.add_service);
        layoutMaster = (LinearLayout)itemView.findViewById(com.umka.umka.R.id.item_layout_master);
        layoutServices = (LinearLayout)itemView.findViewById(com.umka.umka.R.id.item_layout_services);
        layoutPrice = (LinearLayout)itemView.findViewById(com.umka.umka.R.id.item_layout_prices);
        layoutPortfolio = (LinearLayout)itemView.findViewById(com.umka.umka.R.id.item_layout_portfolio);
        radioGroupYo = (RadioGroup)itemView.findViewById(com.umka.umka.R.id.item_yo);

    }
}
