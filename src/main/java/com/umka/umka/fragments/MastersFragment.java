package com.umka.umka.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umka.umka.activity.MasterProfileActivity;
import com.umka.umka.adapters.MasterAdapter;
import com.umka.umka.classes.ParseFavoritesTask;
import com.umka.umka.classes.ParseMastersTask;
import com.umka.umka.interfaces.MasterActionListener;
import com.umka.umka.interfaces.PageLoadListener;
import com.umka.umka.model.BaseModel;
import com.umka.umka.model.Master;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;

import java.util.List;

/**
 * Created by trablone on 11/13/16.
 */

public class MastersFragment extends BaseRecyclerFragment implements MasterActionListener{

    public static MastersFragment newInstance(String url, boolean favorite, boolean delete){
        MastersFragment fragment = new MastersFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        args.putBoolean("favorite", favorite);
        args.putBoolean("delete", delete);
        fragment.setArguments(args);
        return fragment;
    }

    private String url;
    private MasterAdapter adapter;
    private boolean favorite;
    private boolean delete = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        url = getArguments().getString("url");
        favorite = getArguments().getBoolean("favorite");
        delete = getArguments().getBoolean("delete");

        adapter = new MasterAdapter(getBaseActivity(), this, delete);
        getRecyclerView().setAdapter(adapter);
        getMasters();
    }

    @Override
    public void onItemClickListener(int position, BaseModel base) {
        Master item = (Master)base;
        MasterProfileActivity.showActivity(getActivity(), item.id, item);
    }

    public void setUrl(String url){
        this.url = url;
        getMasters();
    }

    @Override
    public void getNextPage(int page) {
        getMasters();
    }

    @Override
    public void updateData() {
        getMasters();
    }

    @Override
    public boolean isEnableUpdate() {
        return true;
    }

    private void getMasters(){
        RequestParams params = new RequestParams();

        loadPage(url, params, new PageLoadListener() {
            @Override
            public void onLoadSuccess(JSONArray array) {
                new ParseMastersTask(getBaseActivity().getUser().id){
                    @Override
                    protected void onPostExecute(List<Master> masters) {
                        super.onPostExecute(masters);
                        adapter.updateList(masters);
                    }
                }.execute(array);

                if (favorite){
                    new ParseFavoritesTask(getBaseActivity()){}.equals(array);
                }
            }

            @Override
            public void onLoadFailure() {

            }
        });
    }



    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public int getNumColumns() {
        return 1;
    }

    @Override
    public void onCall(Master item) {

    }

    @Override
    public void onMessage(Master item) {

    }

    @Override
    public void onFavorite(Master item) {

    }


}
