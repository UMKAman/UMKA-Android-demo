package com.umka.umka.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umka.umka.R;
import com.umka.umka.classes.ChatCreate;
import com.umka.umka.classes.CreateOrder;
import com.umka.umka.classes.Favorite;
import com.umka.umka.classes.HttpClient;
import com.umka.umka.classes.PriceCreator;
import com.umka.umka.classes.Utils;
import com.umka.umka.holders.BaseHolder;
import com.umka.umka.holders.MasterHolder;
import com.umka.umka.interfaces.MasterActionListener;
import com.umka.umka.model.Category;
import com.umka.umka.model.Master;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by trablone on 11/13/16.
 */

public class MasterAdapter extends RecyclerView.Adapter<BaseHolder> {

    private MasterActionListener listener;
    private List<Master> list;
    private Context context;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private boolean delete = true;

    public MasterAdapter(Context context, MasterActionListener listener){
        this.listener = listener;
        this.context = context;
        list = new ArrayList<>();
    }

    public MasterAdapter(Context context, MasterActionListener listener, boolean delete){
        this.listener = listener;
        this.context = context;
        this.delete = delete;
        list = new ArrayList<>();
    }

    public void updateList(List<Master> list){
        this.list = list;
        notifyDataSetChanged();
    }

    public List<Master> getList() {
        return list;
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(com.umka.umka.R.layout.item_master, parent, false);
        return new MasterHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseHolder bholder, final int position) {
        final MasterHolder holder = (MasterHolder) bholder;
        final Master item = list.get(position);
        holder.itemName.setText(item.user.getName());
        imageLoader.displayImage(HttpClient.BASE_URL_IMAGE + item.user.avatar, holder.itemAvatar, Utils.getImageOptions(com.umka.umka.R.drawable.image_profile_no_avatar));
        if (!TextUtils.isEmpty(item.averageRating)) {
            holder.itemRating.setRating(Float.parseFloat(item.averageRating));
            holder.itemDataRating.setText(item.averageRating + " (" + item.voices + context.getResources().getString(R.string.reviews) + ")");
        }
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < item.getServices().size(); i++){
            final Category rubric = item.getServices().get(i);
            builder.append(rubric.section_name);
            if (i < item.getServices().size() - 1){
                builder.append(", ");
            }
        }

        holder.itemServices.setText(builder.toString());

        final PriceCreator priceCreator = new PriceCreator(context, holder.layoutPrice);
        priceCreator.inflatePrice(item, false);

        final CreateOrder createOrder = new CreateOrder(holder.itemCall, context);
        createOrder.init(item.id, item.user.phone);
        final Favorite favorite = new Favorite(context, this);
        favorite.init(holder.itemFavorite, item.id, position);

        holder.itemDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClickListener(position, item);
            }
        });
        final ChatCreate chatCreate = new ChatCreate(context, item);
        chatCreate.init(holder.itemMessage);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void removeMaster(int position){
        if (delete) {
            list.remove(position);
            notifyDataSetChanged();
        }
    }
}
