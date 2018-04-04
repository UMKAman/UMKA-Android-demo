package com.umka.umka.classes;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.umka.umka.R;
import com.pkmmte.view.CircularImageView;

/**
 * Created by trablone on 5/13/17.
 */

public class ReviewsViewHolder extends RecyclerView.ViewHolder{

    public final TextView textName;
    public final TextView textDate;
    public final TextView textMessage;
    public final RatingBar ratingBar;
    public final ImageView imageView;

    public ReviewsViewHolder(View itemView) {
        super(itemView);
        textDate = (TextView)itemView.findViewById(R.id.item_date);
        textName = (TextView)itemView.findViewById(R.id.item_name);
        textMessage = (TextView)itemView.findViewById(R.id.item_text);
        ratingBar = (RatingBar)itemView.findViewById(R.id.item_rating);
        imageView = (ImageView)itemView.findViewById(R.id.item_avatar);
    }
}
