package com.umka.umka.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.umka.umka.model.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by trablone on 11/17/16.
 */

public class CategoryHelper extends BaseHelper {

    public CategoryHelper(SQLiteDatabase db) {
        super(db);
    }

    public Category addCategory(Category item){
        ContentValues cv = new ContentValues();
        cv.put(Content.Category.color, item.color);
        cv.put(Content.Category.id, item.id);
        cv.put(Content.Category.name, item.section_name);
        cv.put(Content.Category.nameEn, item.nameEn);
        cv.put(Content.Category.picture, item.picture);
        cv.put(Content.Category.parent_id, item.parent_id);
        cv.put(Content.Category.next_layer, item.next_lauer);
        insert(DbHelper.TABLE_CATEGORY, cv);
        return item;
    }

    //953 923 30 80
    public List<Category> getCategories(int parent_id){
        List<Category> list = new ArrayList<>();
        Cursor cursor = db.query(DbHelper.TABLE_CATEGORY, null, Content.Category.parent_id + "=?", new String[]{String.valueOf(parent_id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()){
            do {
                Category item = new Category();
                item.section_name = cursor.getString(cursor.getColumnIndexOrThrow(Content.Category.name));
                item.nameEn = cursor.getString(cursor.getColumnIndexOrThrow(Content.Category.nameEn));
                item.id = cursor.getInt(cursor.getColumnIndexOrThrow(Content.Category.id));
                item.parent_id = cursor.getInt(cursor.getColumnIndexOrThrow(Content.Category.parent_id));
                item.color = cursor.getString(cursor.getColumnIndexOrThrow(Content.Category.color));
                item.picture = cursor.getString(cursor.getColumnIndexOrThrow(Content.Category.picture));
                list.add(item);
            }while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }
    public List<Category> getCategoriewSearch(){
        List<Category> list = new ArrayList<>();
        Cursor cursor = db.query(DbHelper.TABLE_CATEGORY, null, Content.Category.parent_id + ">?", new String[]{String.valueOf("0")}, null, null, null);
        Category def = new Category();
        def.section_name = "Выбрать";
        list.add(def);
        if (cursor != null && cursor.moveToFirst()){
            do {
                Category item = new Category();
                item.section_name = cursor.getString(cursor.getColumnIndexOrThrow(Content.Category.name));
                item.id = cursor.getInt(cursor.getColumnIndexOrThrow(Content.Category.id));

                list.add(item);
            }while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }
}
