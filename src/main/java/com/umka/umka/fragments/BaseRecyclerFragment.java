package com.umka.umka.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umka.umka.classes.BaseJsonHandler;
import com.umka.umka.classes.HttpClient;
import com.umka.umka.interfaces.ItemClickListener;
import com.umka.umka.interfaces.PageLoadListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by trablone on 11/13/16.
 */

public abstract class BaseRecyclerFragment extends BaseFragment implements ItemClickListener{

    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private StaggeredGridLayoutManager layoutManager;
    public abstract int getNumColumns();
    public abstract void getNextPage(int page);
    public abstract void updateData();
    public abstract boolean isEnableUpdate();
    public boolean loading = true;
    private int page = 1;
    private int maxPage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(com.umka.umka.R.layout.fragment_recycler, container, false);
        recyclerView = (RecyclerView)view.findViewById(com.umka.umka.R.id.recycler_view);
        refreshLayout = (SwipeRefreshLayout)view.findViewById(com.umka.umka.R.id.refresh_layout);
        return view;
    }

    public RecyclerView getRecyclerView(){
        return recyclerView;
    }

    public SwipeRefreshLayout getRefreshLayout(){
        return refreshLayout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        layoutManager = new StaggeredGridLayoutManager(getNumColumns(), StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            boolean scroll_down;
            int pastVisiblesItems, visibleItemCount, totalItemCount;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 70) {
                    scroll_down = true;

                } else if (dy < -5) {
                    scroll_down = false;
                }

                visibleItemCount = layoutManager.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                int[] firstVisibleItems = null;
                firstVisibleItems = layoutManager.findFirstVisibleItemPositions(firstVisibleItems);
                if (firstVisibleItems != null && firstVisibleItems.length > 0) {
                    pastVisiblesItems = firstVisibleItems[0];
                }

                if (loading && isLoadNext()) {
                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        loading = false;
                        ++page;
                        getNextPage(page);
                    }
                }
            }
        });

        refreshLayout.setEnabled(isEnableUpdate());
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateData();
            }
        });
    }

    public void setProgress(final boolean show){
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
            refreshLayout.setRefreshing(show);
            }
        });
    }

    private boolean isLoadNext(){
        return page < maxPage;
    }

    public void loadPage(String url, RequestParams params, final PageLoadListener listener){

        HttpClient.get(url,getBaseActivity(), params, new BaseJsonHandler(getBaseActivity()){

            @Override
            public void onStart() {
                super.onStart();
                setProgress(true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                --page;
                listener.onLoadFailure();

            }


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                if (response != null)
                    listener.onLoadSuccess(response);
                else {
                    listener.onLoadFailure();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                loading = true;
                setProgress(false);
            }
        });
    }

}
