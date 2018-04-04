package com.umka.umka.classes;

import android.os.AsyncTask;
import android.util.Log;

import com.umka.umka.model.Review;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by trablone on 5/13/17.
 */

public class ParseReviewsTask extends AsyncTask<JSONArray, Void, List<Review>> {

    private boolean create;
    private int user_id;

    public ParseReviewsTask(boolean create, int user_id){
        this.create = create;
        this.user_id = user_id;
    }

    @Override
    protected List<Review> doInBackground(JSONArray... jsonArrays) {

        List<Review> list = new ArrayList<>();
        JSONArray response = jsonArrays[0];
        Review myReview =null;

        for (int i = 0; i < response.length(); i++) {
            try {
                Review review = new Review(response.getJSONObject(i));
                if (create){
                    if (user_id == review.user.id){
                        myReview = review;
                        myReview.type = 0;
                    }
                }else {
                    list.add(review);
                }
            } catch (JSONException e) {
                Log.e("tr", e.getMessage());
            }
        }

        Collections.reverse(list);
        List<Review> reviews = new ArrayList<>();
        if (create){
            if (myReview == null){
                myReview = new Review();
            }
            reviews.add(myReview);
        }
        reviews.addAll(list);
        return reviews;
    }
}