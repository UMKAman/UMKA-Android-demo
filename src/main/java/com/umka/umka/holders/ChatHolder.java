package com.umka.umka.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.umka.umka.R;
import com.pkmmte.view.CircularImageView;

import br.com.instachat.emojilibrary.model.layout.EmojiTextView;

/**
 * Created by trablone on 11/14/16.
 */

public class ChatHolder extends BaseHolder {
    public final TextView textName;
    public final EmojiTextView textMessage;
    public final TextView textDate;
    public final ImageView imageView;

    public ChatHolder(View itemView) {
        super(itemView);
        textDate = (TextView)itemView.findViewById(R.id.item_date);
        textName = (TextView)itemView.findViewById(R.id.item_name);
        textMessage = (EmojiTextView)itemView.findViewById(R.id.item_message);
        imageView = (ImageView) itemView.findViewById(R.id.item_avatar);
    }
}
