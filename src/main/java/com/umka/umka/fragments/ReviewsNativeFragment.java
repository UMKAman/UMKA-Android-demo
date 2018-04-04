package com.umka.umka.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.umka.umka.R;
import com.umka.umka.classes.BaseJsonHandler;
import com.umka.umka.classes.HttpClient;
import com.umka.umka.classes.ParseReviewsTask;
import com.umka.umka.classes.ReviewsViewHolder;
import com.umka.umka.classes.Utils;
import com.umka.umka.model.Review;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by trablone on 5/13/17.
 */

public class ReviewsNativeFragment extends BaseFragment {

    public static ReviewsNativeFragment newInstance(int id){
        ReviewsNativeFragment fragment = new ReviewsNativeFragment();
        Bundle args = new Bundle();
        args.putInt("id", id);
        fragment.setArguments(args);
        return fragment;
    }

    private int id;
    private LinearLayout layout;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reviews_native, container, false);
        layout = (LinearLayout)view.findViewById(R.id.inflate_layout);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        id = getArguments().getInt("id");
        getReview();
    }

    private void getReview(){
        HttpClient.get("/review?where={\"master\"="+ id +"}", getBaseActivity(), null, new BaseJsonHandler(getBaseActivity()){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                new ParseReviewsTask(false, 0){
                    @Override
                    protected void onPostExecute(List<Review> masters) {
                        super.onPostExecute(masters);
                        inflate(masters);
                    }
                }.execute(response);
            }
        });

    }

    private void inflate(List<Review> list){
        layout.removeAllViews();

        for (Review item : list){
            final View view = LayoutInflater.from(getBaseActivity()).inflate(R.layout.item_feedback, layout, false);
            final ReviewsViewHolder holder = new ReviewsViewHolder(view);
            holder.ratingBar.setRating(Float.parseFloat(item.rating));
            holder.textName.setText(item.user.getName());
            holder.textDate.setText(item.getDate());
            holder.textMessage.setText(item.text);
            if (holder.imageView != null)
                imageLoader.displayImage(HttpClient.BASE_URL_IMAGE + item.user.avatar, holder.imageView, Utils.getImageOptions(R.drawable.image_profile_no_avatar));
            layout.addView(view);
        }
    }
}
