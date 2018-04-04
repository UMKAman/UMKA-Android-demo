package com.umka.umka.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umka.umka.R;
import com.umka.umka.adapters.CategoryAdapter;

import com.umka.umka.classes.ParseCategoryTask;
import com.umka.umka.database.CategoryHelper;
import com.umka.umka.database.DbHelper;
import com.umka.umka.interfaces.PageLoadListener;
import com.umka.umka.model.BaseModel;
import com.umka.umka.model.Category;

import org.json.JSONArray;

import java.util.List;

/**
 * Created by trablone on 11/13/16.
 */

public class CategoryFragment extends BaseRecyclerFragment {

    private CategoryAdapter adapter;

    @Override
    public int getNumColumns() {
        return 2;
    }

    @Override
    public void getNextPage(int page) {

    }

    @Override
    public void updateData() {
        CategoryHelper categoryHelper = new CategoryHelper(new DbHelper(getBaseActivity()).getDataBase());
        categoryHelper.delete(DbHelper.TABLE_CATEGORY);
        getCategories();
    }

    @Override
    public boolean isEnableUpdate() {
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundResource(R.color.colorPrimary);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new CategoryAdapter(getBaseActivity(), this);
        getRecyclerView().setAdapter(adapter);
        getCategories();
    }

    private void getCategories(){
        final CategoryHelper categoryHelper = new CategoryHelper(new DbHelper(getBaseActivity()).getDataBase());
        List<Category> list = categoryHelper.getCategories(0);
        if (list.size() > 0){
            adapter.updateList(list);
        }else {
            loadPage("/specialization", null, new PageLoadListener() {
                @Override
                public void onLoadSuccess(JSONArray array) {
                    new ParseCategoryTask(getBaseActivity()){
                        @Override
                        protected void onPostExecute(List<Category> categories) {
                            super.onPostExecute(categories);
                            adapter.updateList(categoryHelper.getCategories(0));
                        }
                    }.execute(array);
                }

                @Override
                public void onLoadFailure() {

                }
            });
        }
    }


    @Override
    public void onItemClickListener(int position, BaseModel base) {

        Category item = (Category)base;
        getMainActivity().addFragment(RubricFragment.newInstance(item.id), item.section_name, true);
    }
}
