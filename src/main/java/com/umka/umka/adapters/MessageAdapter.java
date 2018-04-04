package com.umka.umka.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umka.umka.classes.HttpClient;
import com.umka.umka.classes.Utils;
import com.umka.umka.holders.MessageHolder;
import com.umka.umka.interfaces.ItemClickListener;
import com.umka.umka.model.Message;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by trablone on 11/14/16.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageHolder> {

    private static final int INCOME = 1;
    private static final int OUTGOING = 2;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    public int getItemCount() {
        return list.size();
    }

    private int id = 0;
    private Context context;
    private List<Message> list;

    public MessageAdapter(Context context, ItemClickListener listener, int id) {
        this.context = context;
        this.id = id;
        this.list = new ArrayList<>();
    }
    public List<Message> getList() {
        return list;
    }

    public void addPreList(List<Message> list){
        int count = list.size();
        list.addAll(this.list);
        this.list = list;
        notifyItemRangeInserted(0, count);
    }

    public void addList(List<Message> list){
        int count = this.list.size();
        this.list.addAll(list);
        notifyItemRangeInserted(count, list.size());
    }

    public void addMessage(Message list){
        int count = this.list.size();
        this.list.add(list);
        notifyDataSetChanged();
    }

    public void removeMessage(Message list){
        this.list.remove(list);
        notifyDataSetChanged();
    }

    public void updateList(List<Message> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {

        if(this.list.get(position).user_id == id) {
            return INCOME;
        }
        return OUTGOING;
    }


    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == INCOME)
            view = LayoutInflater.from(context).inflate(com.umka.umka.R.layout.item_chat_message, parent, false);
        else
            view = LayoutInflater.from(context).inflate(com.umka.umka.R.layout.item_chat_message_user, parent, false);
        return new MessageHolder(view);
    }


    @Override
    public void onBindViewHolder(MessageHolder holder, int position) {
        final int padding = Utils.dpToPx(context, 16);
        final int paddingBottom = Utils.dpToPx(context, 8);

        if (position == 0){
            holder.itemView.setPadding(padding, padding , padding, 0);
        }else if (position == getItemCount() - 1){
            holder.itemView.setPadding(padding, 0 , padding, padding);
        }else {
            holder.itemView.setPadding(padding, paddingBottom , padding, paddingBottom);
        }

        final Message item = list.get(position);
        if (item.createdAt != null)
            holder.textDate.setText(item.getChatDate());
        if (TextUtils.isEmpty(item.message)){
            holder.textMessage.setVisibility(View.GONE);
            holder.itemImage.setVisibility(View.VISIBLE);
            if (!item.imageFile)
            imageLoader.displayImage(HttpClient.BASE_URL_IMAGE + item.image, holder.itemImage, Utils.getImageOptions(com.umka.umka.R.drawable.ic_ab_attach));
            else
            imageLoader.displayImage("file://" + item.image, holder.itemImage, Utils.getImageOptions(com.umka.umka.R.drawable.ic_ab_attach));
        }else {
            holder.itemImage.setVisibility(View.GONE);
            holder.textMessage.setVisibility(View.VISIBLE);
            holder.textMessage.setText(item.message);
            holder.textMessage.setTextIsSelectable(true);
        }

        if (holder.itemAvatar != null){
            imageLoader.displayImage(HttpClient.BASE_URL_IMAGE + item.user_avatar, holder.itemAvatar, Utils.getImageOptions(com.umka.umka.R.drawable.image_profile_no_avatar));
        }
    }
}
