package com.umka.umka.model;

import android.content.Context;

import com.umka.umka.R;

import java.io.File;
import java.util.List;

/**
 * Created by trablone on 5/24/17.
 */

public class Filter {

    private Context context;
    public ItemFilter[] sortList;
    public ItemFilter[] genderList;

    public Filter(Context context){
        this.context = context;
        sortList = new ItemFilter[3];
        sortList[0] = new ItemFilter(context, R.string.select);
        sortList[1] = new ItemFilter(context, R.string.by_prices);
        sortList[2] = new ItemFilter(context, R.string.by_rating);

        genderList = new ItemFilter[3];
        genderList[0] = new ItemFilter(context, R.string.select);
        genderList[1] = new ItemFilter(context, R.string.male);
        genderList[2] = new ItemFilter(context, R.string.female);

    }

    public List<Category> categories;
    public boolean visit;
    public boolean visit_home;
    public boolean review;
    public int sort;
    public int gender;
    public int category;
    public int progress;
    public int radius;
    public String request;

    public String url;



}
