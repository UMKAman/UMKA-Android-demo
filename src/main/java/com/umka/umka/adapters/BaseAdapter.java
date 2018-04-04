package com.umka.umka.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.umka.umka.holders.BaseHolder;
import com.umka.umka.interfaces.ItemClickListener;
import com.umka.umka.model.BaseModel;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by trablone on 11/13/16.
 */

public abstract class BaseAdapter extends RecyclerView.Adapter<BaseHolder> {

    public ImageLoader imageLoader = ImageLoader.getInstance();


    public List<? extends BaseModel> list;
    public Context context;
    public ItemClickListener listener;

    public BaseAdapter(Context context, ItemClickListener listener){
        this.context = context;
        this.listener = listener;
        this.list = new ArrayList<>();
    }

    public void updateList(List<? extends BaseModel> list){
        this.list = list;
        notifyDataSetChanged();
    }

    public List<? extends BaseModel> getList() {
        return list;
    }

    public void clearData(){
        list.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
