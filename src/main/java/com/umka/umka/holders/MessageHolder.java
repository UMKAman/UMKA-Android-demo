package com.umka.umka.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;

import br.com.instachat.emojilibrary.model.layout.EmojiTextView;

/**
 * Created by trablone on 11/14/16.
 */

public class MessageHolder extends BaseHolder {
    public final EmojiTextView textMessage;
    public final TextView textDate;
    public final ImageView itemImage;
    public final ImageView itemAvatar;

    public MessageHolder(View itemView) {
        super(itemView);
        textMessage = (EmojiTextView)itemView.findViewById(com.umka.umka.R.id.item_message);
        textDate = (TextView)itemView.findViewById(com.umka.umka.R.id.item_date);
        itemImage = (ImageView)itemView.findViewById(com.umka.umka.R.id.item_image);
        itemAvatar = (ImageView)itemView.findViewById(com.umka.umka.R.id.item_avatar);
    }
}
