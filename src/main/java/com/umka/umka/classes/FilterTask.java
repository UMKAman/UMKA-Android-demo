package com.umka.umka.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.umka.umka.R;
import com.umka.umka.database.CategoryHelper;
import com.umka.umka.database.DbHelper;
import com.umka.umka.model.Category;
import com.umka.umka.model.Filter;

/**
 * Created by trablone on 5/24/17.
 */

public class FilterTask extends AsyncTask<Void, Void, Filter> {

    private Context context;
    private SharedPreferences preferences;
    private Location location;


    public FilterTask(Context context, Location location){
        this.context = context;
        this.location = location;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    protected Filter doInBackground(Void... voids) {
        Filter filter = new Filter(context);
        StringBuilder builder = new StringBuilder();
        builder.append("/search?");
        final CategoryHelper categoryHelper = new CategoryHelper(new DbHelper(context).getDataBase());
        filter.categories = categoryHelper.getCategoriewSearch();
        for (int i = 0; i < filter.genderList.length; i++){
            if (filter.genderList[i].toString().contains(preferences.getString("gender", context.getResources().getString(R.string.select)))){
                filter.gender = i;
                if (i > 0){
                    builder.append("gender=" + filter.genderList[i].toString().substring(0, 3));
                    builder.append("&");
                }
            }
        }

        for (int i = 0; i < filter.sortList.length; i++){
            if (filter.sortList[i].toString().contains(preferences.getString("sort", context.getResources().getString(R.string.select)))){
                filter.sort = i;
                if (i > 0){
                    builder.append("orderBy=" + (filter.sortList[i].toString().contains(filter.sortList[1].toString()) ? "cost" : "rating"));
                    builder.append("&");
                }
            }
        }

        int categoryId = preferences.getInt("category", 0);
        for (int i = 0; i < filter.categories.size(); i++){
            Category category = filter.categories.get(i);
            if (category.id == categoryId){
                filter.category = i;
                if (i > 0){
                    builder.append("specialization=" + category.id);
                    builder.append("&");
                }
                break;
            }
        }

        filter.visit_home = preferences.getBoolean("visit_home", true);
        filter.review = preferences.getBoolean("review", false);
        filter.visit = preferences.getBoolean("visit", false);
        filter.progress = preferences.getInt("price_progress", 100);
        filter.radius = preferences.getInt("radius_progress", 2);

        if (filter.visit){
            builder.append("visit=" + filter.visit);
            builder.append("&");
        }
        if (filter.review){
            builder.append("reviewsonly=" + filter.review);
            builder.append("&");
        }
        if (filter.visit_home){
            builder.append("visitathome=" + filter.visit_home);
            builder.append("&");
        }

        String request = preferences.getString("request", "");
        if (!TextUtils.isEmpty(request)) {
            builder.append("&request=" + request);
            builder.append("&");
        }

        builder.append("maxCost=" + filter.progress);
        builder.append("&");
        builder.append("minCost=" + preferences.getInt("minCost", 100));
        builder.append("&");
        builder.append("dist=" + filter.radius);
        if (location != null){
            builder.append("&");
            builder.append("lat=" + location.getLatitude());
            builder.append("&");
            builder.append("lon=" + location.getLongitude());
        }

        filter.url = builder.toString();
        return filter;
    }
}
