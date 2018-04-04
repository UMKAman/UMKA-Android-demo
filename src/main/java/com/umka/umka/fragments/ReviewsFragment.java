package com.umka.umka.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.umka.umka.R;
import com.umka.umka.adapters.ReviewsAdapter;
import com.umka.umka.classes.BaseJsonHandler;
import com.umka.umka.classes.HttpClient;
import com.umka.umka.classes.ParseReviewsTask;
import com.umka.umka.interfaces.PageLoadListener;
import com.umka.umka.model.BaseModel;
import com.umka.umka.model.Review;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by trablone on 11/14/16.
 */

public class ReviewsFragment extends BaseRecyclerFragment {

    private ReviewsAdapter adapter;

    public static ReviewsFragment newInstance(int id, boolean create){
        ReviewsFragment fragment = new ReviewsFragment();
        Bundle args = new Bundle();
        args.putInt("id", id);
        args.putBoolean("create", create);
        fragment.setArguments(args);
        return fragment;
    }

    private int id;
    private boolean create;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        id = getArguments().getInt("id");
        create = getArguments().getBoolean("create");
        adapter = new ReviewsAdapter(getBaseActivity(), new ReviewsAdapter.ReviewListener() {
            @Override
            public void createReview(String message, float rating) {
                createreview(message, rating);
            }

            @Override
            public void updateReview(String message, float rating, Review review) {
                updatereview(message, rating, review);
            }
        });

        getRecyclerView().setAdapter(adapter);
        getReview();
    }

    private void updatereview(String message, float rating, Review review){
        JSONObject object = new JSONObject();
        try {
            object.put("text", message);
            object.put("rating", String.valueOf(rating));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("tr", "obj: " + object);

        HttpClient.put(getBaseActivity(), "/review/" + review.id, new StringEntity(object.toString(), "UTF-8"), new BaseJsonHandler(getBaseActivity()){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Toast.makeText(getBaseActivity(), getResources().getString(R.string.review_edit_ok), Toast.LENGTH_LONG).show();
                getActivity().onBackPressed();

            }
        });
    }

    private void createreview(String message, float rating){
        JSONObject object = new JSONObject();
        try {
            object.put("writter", getBaseActivity().getUser().id);
            object.put("master", id);
            object.put("text", message);
            object.put("rating", String.valueOf(rating));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("tr", "obj: " + object);

        HttpClient.post(getBaseActivity(), "/review", new StringEntity(object.toString(), "UTF-8"), new BaseJsonHandler(getBaseActivity()){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Toast.makeText(getBaseActivity(), getResources().getString(R.string.review_create_ok), Toast.LENGTH_LONG).show();
                getBaseActivity().finish();
            }
        });
    }

    private void getReview(){
        loadPage("/review?where={\"master\":"+ id +"}", null, new PageLoadListener() {
            @Override
            public void onLoadSuccess(JSONArray array) {
                new ParseReviewsTask(create, getBaseActivity().getUser().id){
                    @Override
                    protected void onPostExecute(List<Review> masters) {
                        super.onPostExecute(masters);

                        adapter.updateList(masters);
                    }
                }.execute(array);

            }

            @Override
            public void onLoadFailure() {

            }
        });
    }


    @Override
    public void onItemClickListener(int position, BaseModel base) {

    }

    @Override
    public int getNumColumns() {
        return 1;
    }

    @Override
    public void getNextPage(int page) {

    }

    @Override
    public void updateData() {
        getReview();
    }

    @Override
    public boolean isEnableUpdate() {
        return true;
    }

}
