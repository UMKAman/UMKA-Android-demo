package com.umka.umka.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umka.umka.R;
import com.umka.umka.classes.HttpClient;
import com.umka.umka.classes.Utils;
import com.umka.umka.holders.ChatHolder;
import com.umka.umka.interfaces.ItemClickListener;
import com.umka.umka.model.Chat;
import com.umka.umka.model.Message;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by trablone on 11/14/16.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatHolder> {
    private Context context;
    private ItemClickListener listener;

    public List<Chat> getList() {
        return list;
    }

    private List<Chat> list;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    public ChatAdapter(Context context, ItemClickListener listener){
        this.context = context;
        this.listener = listener;
        list = new ArrayList<>();
    }

    @Override
    public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(com.umka.umka.R.layout.item_chat, parent, false);
        return new ChatHolder(view);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateMessage(Message message, int position){

        for (int i = 0; i < list.size(); i++){
            Chat item = list.get(i);
            if (item.id == position){
                item.message = message;
                list.set(i, item);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void updateList(List<Chat> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ChatHolder holder, final int position) {

        final Chat item = list.get(position);

        holder.textDate.setText(item.message.getChatDate());
        holder.textMessage.setText(item.message.message == null ? context.getResources().getString(R.string.image) : item.message.message);

        holder.textName.setText(item.name);
        imageLoader.displayImage(HttpClient.BASE_URL_IMAGE + item.avatar, holder.imageView, Utils.getImageOptions(com.umka.umka.R.drawable.image_profile_no_avatar));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClickListener(position, item);
            }
        });
    }
}
