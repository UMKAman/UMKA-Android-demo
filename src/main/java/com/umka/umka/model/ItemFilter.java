package com.umka.umka.model;

import android.content.Context;

/**
 * Created by trablone on 01.10.17.
 */

public class ItemFilter extends BaseModel {

    public int ressId;
    private Context context;

    public ItemFilter(Context context, int ressId){
        this.context = context;
        this.ressId = ressId;
    }

    @Override
    public String toString() {
        return context.getResources().getString(ressId);
    }
}
