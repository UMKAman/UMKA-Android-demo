package com.umka.umka.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umka.umka.R;
import com.umka.umka.classes.HttpClient;
import com.umka.umka.holders.BaseHolder;
import com.umka.umka.holders.CategoryHolder;
import com.umka.umka.interfaces.ItemClickListener;
import com.umka.umka.model.Category;

import java.util.Locale;

/**
 * Created by trablone on 11/13/16.
 */

public class CategoryAdapter extends BaseAdapter{


    public CategoryAdapter(Context context, ItemClickListener listener){
        super(context, listener);
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseHolder bholder, int position) {
        final CategoryHolder holder = (CategoryHolder)bholder;
        final Category item = (Category)list.get(position);
        holder.cardView.setCardBackgroundColor(item.getColor(context));
        holder.itemTitle.setText(Locale.getDefault().getLanguage().contains("ru") ? item.section_name : item.nameEn);
        imageLoader.displayImage(HttpClient.BASE_URL_IMAGE + item.picture, holder.itemImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClickListener(holder.getAdapterPosition(), item);
            }
        });
    }
}
