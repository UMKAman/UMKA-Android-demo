package com.umka.umka.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.umka.umka.R;
import com.pkmmte.view.CircularImageView;

/**
 * Created by trablone on 11/13/16.
 */

public class MasterHolder extends BaseHolder {

    public final ImageView itemAvatar;
    public final TextView itemName;
    public final RatingBar itemRating;
    public final TextView itemDataRating;
    public final TextView itemServices;
    public final LinearLayout layoutPrice;
    public final LinearLayout layoutPortfolio;
    public final TextView itemDetail;
    public final ImageView itemFavorite;
    public final ImageView itemMessage;
    public final ImageView itemCall;

    public MasterHolder(View itemView) {
        super(itemView);
        layoutPortfolio = (LinearLayout)itemView.findViewById(R.id.item_layout_portfolio);
        itemAvatar = (ImageView) itemView.findViewById(R.id.item_avatar);
        itemName = (TextView)itemView.findViewById(R.id.item_name);
        itemDataRating = (TextView)itemView.findViewById(R.id.item_rating_data);
        itemServices = (TextView)itemView.findViewById(R.id.item_services);
        itemRating = (RatingBar)itemView.findViewById(R.id.item_rating);
        layoutPrice = (LinearLayout)itemView.findViewById(R.id.item_layout_prices);
        itemDetail = (TextView)itemView.findViewById(R.id.item_detail);
        itemFavorite = (ImageView)itemView.findViewById(R.id.item_favorite);
        itemMessage = (ImageView)itemView.findViewById(R.id.item_message);
        itemCall = (ImageView)itemView.findViewById(R.id.item_call);
    }
}
