package com.umka.umka.model;

import android.content.Context;
import android.graphics.Color;

import com.umka.umka.R;

/**
 * Created by trablone on 11/13/16.
 */

public class Category extends BaseModel {
    public String color;
    public String section_name;
    public String nameEn;
    public int id;
    public String one_id;
    public int parent_id;
    public String layer;
    public String picture;
    public String next_lauer;

    public int getColor(Context context){
        if (color != null) {
            try {
                return Color.parseColor(color);
            }catch (NumberFormatException e){

            }
        }
        int c = R.color.colorPrimary;

        return context.getResources().getColor(c);
    }

    @Override
    public String toString() {
        return section_name;
    }
}
