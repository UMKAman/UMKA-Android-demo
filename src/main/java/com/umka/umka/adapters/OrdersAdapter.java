package com.umka.umka.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.umka.umka.activity.MasterProfileActivity;
import com.umka.umka.activity.UserProfileActivity;
import com.umka.umka.classes.HttpClient;
import com.umka.umka.classes.Utils;
import com.umka.umka.model.Order;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pkmmte.view.CircularImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by trablone on 5/13/17.
 */

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder>{


    private List<Order> list;
    private Context context;
    private boolean type;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private OrdersListener listener;

    public OrdersAdapter(Context context, boolean type, OrdersListener listener){
        this.listener = listener;
        this.type = type;
        this.context = context;
        list = new ArrayList<>();
    }

    public void updateList(List<Order> list){
        this.list = list;

        notifyDataSetChanged();
    }

    public List<Order> getList() {
        return list;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(com.umka.umka.R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Order item = list.get(position);

        if (type){
            imageLoader.displayImage(HttpClient.BASE_URL_IMAGE + item.user.avatar, holder.imageView, Utils.getImageOptions(com.umka.umka.R.drawable.image_profile_no_avatar));

            holder.textDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserProfileActivity.showActivity(context, item.user);
                }
            });

            holder.textName.setText(item.user.getName());
            holder.textDate.setText(item.getDate());

        }else {
            imageLoader.displayImage(HttpClient.BASE_URL_IMAGE + item.master.user.avatar, holder.imageView, Utils.getImageOptions(com.umka.umka.R.drawable.image_profile_no_avatar));
            holder.textReview.setVisibility(View.VISIBLE);
            holder.textReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.createReview(item);
                }
            });
            holder.textDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MasterProfileActivity.showActivity(context, item.master.id, item.master);
                }
            });

            holder.textName.setText(item.master.user.getName());
            holder.textDate.setText(item.getDate());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView textName;
        private final TextView textDate;
        private final TextView textDetail;
        private final TextView textReview;
        private final ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            textDate = (TextView)itemView.findViewById(com.umka.umka.R.id.item_date);
            textName = (TextView)itemView.findViewById(com.umka.umka.R.id.item_name);
            textDetail = (TextView)itemView.findViewById(com.umka.umka.R.id.item_detail);
            textReview = (TextView)itemView.findViewById(com.umka.umka.R.id.item_review);
            imageView = (ImageView)itemView.findViewById(com.umka.umka.R.id.item_avatar);
        }
    }

    public interface OrdersListener{
        void createReview(Order item);
    }
}
