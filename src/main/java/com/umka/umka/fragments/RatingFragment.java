package com.umka.umka.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.umka.umka.R;
import com.umka.umka.classes.BaseJsonHandler;
import com.umka.umka.classes.HttpClient;
import com.umka.umka.model.Rating;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by trablone on 11/14/16.
 */

public class RatingFragment extends BaseFragment {

    private LinearLayout layout;
    private TextView textRating, textCount;
    private RatingBar ratingBar;
    //private SwipeRefreshLayout refreshLayout;

    public static RatingFragment getInstance(int id) {
        RatingFragment fragment = new RatingFragment();
        Bundle params = new Bundle();
        params.putInt("id", id);
        fragment.setArguments(params);
        return fragment;
    }

    private int id;
    public String voices;
    public String averageRating;
    private List<Rating> list;
    private int allCount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rating, container, false);
        layout = (LinearLayout) view.findViewById(R.id.inflate_layout);
        textCount = (TextView) view.findViewById(R.id.item_rating_data);
        textRating = (TextView) view.findViewById(R.id.item_rating_text);
        ratingBar = (RatingBar) view.findViewById(R.id.item_rating);
        //refreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.refresh_layout);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        id = getArguments().getInt("id");
        getProfile();
    }

    public void getProfile() {
        HttpClient.get("/master/" + id, getBaseActivity(), null, new BaseJsonHandler(getBaseActivity()){

            @Override
            public void onStart() {
                super.onStart();
                //refreshLayout.setEnabled(true);
                //setPrograss(refreshLayout, true);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                //refreshLayout.setEnabled(false);
                //setPrograss(refreshLayout, false);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                if (getBaseActivity() == null)
                    return;
                list = new ArrayList<>();
                try {
                    if (response.isNull("voices"))
                        voices = "0";
                    else{
                        voices = response.getString("voices");

                    }

                    if (response.isNull("averageRating"))
                        averageRating = "0";
                    else {
                        averageRating = response.getString("averageRating");
                        if (averageRating.length() > 3){
                            averageRating = averageRating.substring(0, 3);
                        }
                    }

                    textCount.setText(voices + " отзывов");
                    textRating.setText(averageRating);
                    ratingBar.setRating(Float.parseFloat(averageRating));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray orders = new JSONArray();
                JSONArray array = null;
                try {

                    array = response.getJSONArray("ratingsAndCounts");
                    getRating(array, orders, 0);
                } catch (JSONException e) {
                    Log.e("tr", "e: " + e.getMessage());
                }

                Log.e("tr", "array: " + array);

                Log.e("tr", "orders: " + orders);

                /*for (int i = 0; i < orders.length(); i++){
                    int star = i + 1;
                    String s = String.valueOf(star) + ".0";

                    Rating rating = new Rating();
                    try {
                        JSONObject object = orders.getJSONObject(i);
                        rating.count = object.getInt(s);
                        rating.stars = s;
                        rating.color = getColor(star);
                        rating.ress = getImage(star);
                        list.add(rating);

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }



                }*/

                Collections.reverse(list);
                inflateRatings();
            }

        });
    }

    private void getRating(JSONArray array, JSONArray orders, int i) {
        int j = i + 1;

        while (i < array.length()) {
            try {
                Log.e("tr", "i: " + i);
                JSONObject object = array.getJSONObject(i);

                Log.e("tr", "j: " + j);
                String s = String.valueOf(j) + ".0";

                if (object.isNull(s)) {
                    while (object.isNull(s)) {
                        Log.e("tr", "s: " + s);
                        createRating(j, 0);
                        ++j;
                        s = j + ".0";

                    }

                } else {

                    createRating(j, object.getInt(s));

                    ++i;
                    ++j;
                }


            } catch (JSONException e) {
                e.printStackTrace();
                createRating(j, 0);

                ++j;
                ++i;
            }
        }

    }

    private void createRating(int star, int count) {
        Rating rating = new Rating();
        rating.count = count;
        rating.stars = star + ".0";
        rating.color = getColor(star);
        rating.ress = getImage(star);
        list.add(rating);
        allCount += rating.count;
    }

    private int getColor(int i) {
        switch (i) {
            case 1:
                return R.drawable.progress_1;
            case 2:
                return R.drawable.progress_2;
            case 3:
                return R.drawable.progress_3;
            case 4:
                return R.drawable.progress_4;
            case 5:
                return R.drawable.progress_5;
        }
        return i;
    }

    private int getImage(int i) {
        switch (i) {
            case 1:
                return R.drawable.ic_rating_1_star;
            case 2:
                return R.drawable.ic_rating_2_stars;
            case 3:
                return R.drawable.ic_rating_3_stars;
            case 4:
                return R.drawable.ic_rating_4_stars;
            case 5:
                return R.drawable.ic_rating_5_stars;
        }
        return i;
    }

    private void inflateRatings() {
        Log.e("tr", "size: " + list.size());
        Log.e("tr", "allcount: " + allCount);
        if (list.size() == 0) {
            list.add(new Rating("5", 0, R.drawable.progress_5, R.drawable.ic_rating_5_stars));
            list.add(new Rating("5", 0, R.drawable.progress_4, R.drawable.ic_rating_4_stars));
            list.add(new Rating("5", 0, R.drawable.progress_3, R.drawable.ic_rating_3_stars));
            list.add(new Rating("5", 0, R.drawable.progress_2, R.drawable.ic_rating_2_stars));
            list.add(new Rating("5", 0, R.drawable.progress_1, R.drawable.ic_rating_1_star));
        }

        for (final Rating rating : list) {
            View view = LayoutInflater.from(getBaseActivity()).inflate(R.layout.item_rating, layout, false);
            ImageView imageView = (ImageView) view.findViewById(R.id.item_image);
            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.item_rating);
            TextView textView = (TextView) view.findViewById(R.id.item_count);
            imageView.setImageResource(rating.ress);
            textView.setText(String.valueOf(rating.count));
            progressBar.setMax(allCount);
            progressBar.setProgress(rating.count);
            if (getActivity()!=null)
                progressBar.setProgressDrawable(getResources().getDrawable(rating.color));
            layout.addView(view);

        }
    }
}
