package com.umka.umka.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.umka.umka.R;
import com.umka.umka.classes.HttpClient;
import com.umka.umka.classes.ReviewsViewHolder;
import com.umka.umka.classes.Utils;
import com.umka.umka.model.Review;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by trablone on 11/14/16.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Review> list;
    private Context context;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private ReviewListener listener;

    public ReviewsAdapter(Context context, ReviewListener listener) {
        this.listener = listener;
        this.context = context;
        list = new ArrayList<>();
    }

    public void updateList(List<Review> list){
        this.list = list;
        Log.e("tr", "count : " + list.size());
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).type;
    }

    public List<Review> getList() {
        return list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0){
            View view = LayoutInflater.from(context).inflate(R.layout.item_review_create, parent, false);
            return new ViewHolderCreate(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_feedback, parent, false);
            return new ReviewsViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder bholder, int position) {

        Log.e("tr", "position: " + position);
        final Review item = list.get(position);
        if (item.type == 0){
            final ViewHolderCreate holder = (ViewHolderCreate)bholder;
            if (item.rating != null)
                holder.ratingBar.setRating(Float.parseFloat(item.rating));
            holder.editText.setText(item.text);

            holder.textSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (item.id == 0)
                        listener.createReview(holder.editText.getText().toString(), holder.ratingBar.getRating());
                    else
                        listener.updateReview(holder.editText.getText().toString(), holder.ratingBar.getRating(), item);

                }
            });
        }else {
            final ReviewsViewHolder holder = (ReviewsViewHolder)bholder;
            holder.ratingBar.setRating(Float.parseFloat(item.rating));
            holder.textName.setText(item.user.getName());
            holder.textDate.setText(item.getDate());
            holder.textMessage.setText(item.text);
            if (holder.imageView != null)
            imageLoader.displayImage(HttpClient.BASE_URL_IMAGE + item.user.avatar, holder.imageView, Utils.getImageOptions(R.drawable.image_profile_no_avatar));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolderCreate extends RecyclerView.ViewHolder{

        private final EditText editText;
        private final RatingBar ratingBar;
        private final TextView textSend;

        public ViewHolderCreate(View itemView) {
            super(itemView);
            editText = (EditText)itemView.findViewById(R.id.item_message);
            ratingBar = (RatingBar)itemView.findViewById(R.id.item_rating);
            textSend = (TextView)itemView.findViewById(R.id.item_send);
        }
    }



    public interface ReviewListener{
        void createReview(String message, float rating);
        void updateReview(String message, float rating, Review review);
    }
}
