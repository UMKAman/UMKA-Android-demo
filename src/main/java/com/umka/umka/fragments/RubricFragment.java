package com.umka.umka.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umka.umka.R;
import com.umka.umka.adapters.RubricAdapter;
import com.umka.umka.database.CategoryHelper;
import com.umka.umka.database.DbHelper;
import com.umka.umka.model.BaseModel;
import com.umka.umka.model.Category;

/**
 * Created by trablone on 11/17/16.
 */

public class RubricFragment extends BaseRecyclerFragment {

    private RubricAdapter adapter;

    public static RubricFragment newInstance(int parent_id){
        RubricFragment fragment = new RubricFragment();
        Bundle args = new Bundle();
        args.putInt("parent_id", parent_id);
        fragment.setArguments(args);
        return fragment;
    }

    private int parent_id;

    @Override
    public int getNumColumns() {
        return 1;
    }

    @Override
    public void getNextPage(int page) {

    }

    @Override
    public void updateData() {

    }

    @Override
    public boolean isEnableUpdate() {
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        adapter = new RubricAdapter(getBaseActivity(), this);
        getMainActivity().findViewById(R.id.card_search).setVisibility(View.GONE);
        getRecyclerView().setAdapter(adapter);
        parent_id = getArguments().getInt("parent_id");
        Log.e("tr", "id: " + parent_id);
        getCategories();
    }

    private void getCategories(){
        CategoryHelper categoryHelper = new CategoryHelper(new DbHelper(getBaseActivity()).getDataBase());
        adapter.updateList(categoryHelper.getCategories(parent_id));
    }

    @Override
    public void onItemClickListener(int position, BaseModel base) {
        Category item = (Category) base;
        Log.e("tr", "parent_id : " + item.id);
        if (TextUtils.isEmpty(item.next_lauer))
            getMainActivity().addFragment(MastersFragment.newInstance("/specialization/" + item.id +"/inMaster", false, false), item.section_name, true);
        else
            getMainActivity().addFragment(RubricFragment.newInstance(item.id), item.section_name, true);


        //
    }
}
