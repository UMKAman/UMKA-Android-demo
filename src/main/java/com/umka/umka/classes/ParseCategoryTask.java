package com.umka.umka.classes;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.umka.umka.database.CategoryHelper;
import com.umka.umka.database.Content;
import com.umka.umka.database.DbHelper;
import com.umka.umka.model.Category;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by trablone on 11/17/16.
 */

public class ParseCategoryTask extends AsyncTask<JSONArray, Void, List<Category>> {

    private Context context;

    public ParseCategoryTask(Context context){
        this.context = context;
    }

    @Override
    protected List<Category> doInBackground(JSONArray... params) {

        JSONArray array = params[0];
        CategoryHelper categoryHelper = new CategoryHelper(new DbHelper(context).getDataBase());
        categoryHelper.delete(DbHelper.TABLE_CATEGORY);
        List<Category> categories = getCategories(categoryHelper, array);

        return categories;
    }

    private List<Category> getCategories(CategoryHelper helper, JSONArray array){
        List<Category> categories = new ArrayList<>();
        for (int i = 0; i < array.length(); i++){
            try {
                JSONObject category = array.getJSONObject(i);
                Log.e("tr", "category: " + category);
                Category itemCategory = new Category();
                itemCategory.color = category.getString(Content.Category.color);
                itemCategory.id = category.getInt(Content.Category.id);

                if (category.isNull("parent"))
                    itemCategory.parent_id = 0;
                else
                    itemCategory.parent_id = category.getInt(Content.Category.parent_id);

                itemCategory.picture = category.getString("pic");
                itemCategory.section_name = category.getString("name");
                itemCategory.nameEn = category.getString("nameEn");

                if (!category.isNull("child")){
                    JSONArray childs = category.getJSONArray("child");

                    if (childs.length() > 0){
                        itemCategory.next_lauer = "child";
                        categories.addAll(getCategories(helper, childs));
                    }
                }

                categories.add(helper.addCategory(itemCategory));

            } catch (JSONException e) {
                Log.e("tr", "e: " + e.getMessage());
            }
        }

        return categories;
    }
}
