package com.umka.umka.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.umka.umka.R;
import com.umka.umka.activity.BaseActivity;
import com.umka.umka.classes.Constants;
import com.umka.umka.interfaces.ItemClickListener;
import com.umka.umka.model.Navigation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by trablone on 11/13/16.
 */

public class NavigationAdapter extends BaseAdapter {
    private List<Navigation> list;
    private Context context;
    private int select_position = 0;
    private ItemClickListener listener;
    private BaseActivity baseActivity;
    private int colorSelect, colorDefault, colorDisable;
    private boolean logined;

    public NavigationAdapter(Context context, ItemClickListener listener){
        this.context = context;
        baseActivity = (BaseActivity)context;
        this.listener = listener;
        list = new ArrayList<>();
        colorSelect = context.getResources().getColor(com.umka.umka.R.color.colorAccent);
        colorDefault = context.getResources().getColor(com.umka.umka.R.color.colorBlack);
        colorDisable = context.getResources().getColor(com.umka.umka.R.color.colorGray);
        logined = baseActivity.isLoginUser();
    }

    public void initUserList(){
        list = new ArrayList<>();
        list.add(new Navigation(Constants.TYPE_USER, Constants.MENU_INDEX, com.umka.umka.R.string.menu_index_menu, com.umka.umka.R.drawable.ic_drawer_main));
        list.add(new Navigation(Constants.TYPE_USER, Constants.MENU_PROFILE, com.umka.umka.R.string.menu_profile, com.umka.umka.R.drawable.ic_drawer_profile));
        list.add(new Navigation(Constants.TYPE_USER, Constants.MENU_WALLET, R.string.menu_wallet, com.umka.umka.R.drawable.ic_account_balance_wallet_24dp));
        list.add(new Navigation(Constants.TYPE_USER, Constants.MENU_FAVORITE, com.umka.umka.R.string.menu_favorite, com.umka.umka.R.drawable.ic_drawer_favorites));
//        list.add(new Navigation(Constants.TYPE_USER, Constants.MENU_HISTORY, com.umka.umka.R.string.menu_history, com.umka.umka.R.drawable.ic_drawer_history));
//        list.add(new Navigation(Constants.TYPE_USER, Constants.MENU_RATING, R.string.menu_rating, R.drawable.ic_drawer_reviews));
        list.add(new Navigation(Constants.TYPE_USER, Constants.MENU_SETTING, com.umka.umka.R.string.menu_settings, com.umka.umka.R.drawable.ic_drawer_settings));
        list.add(new Navigation(Constants.TYPE_USER, Constants.MENU_FEEDBACK, com.umka.umka.R.string.menu_feedback, com.umka.umka.R.drawable.ic_drawer_feedback));
        notifyDataSetChanged();
    }

    public void initMasterList(){
        list = new ArrayList<>();
        list.add(new Navigation(Constants.TYPE_MASTER, Constants.MENU_INDEX_MASTER, com.umka.umka.R.string.menu_index_menu, com.umka.umka.R.drawable.ic_drawer_main));
        list.add(new Navigation(Constants.TYPE_MASTER, Constants.MENU_PREMIUM, com.umka.umka.R.string.menu_premium, com.umka.umka.R.drawable.ic_drawer_location));
        list.add(new Navigation(Constants.TYPE_MASTER, Constants.MENU_PROFILE_MASTER, com.umka.umka.R.string.menu_profile, com.umka.umka.R.drawable.ic_drawer_profile));
        list.add(new Navigation(Constants.TYPE_USER, Constants.MENU_WALLET, R.string.menu_wallet, com.umka.umka.R.drawable.ic_account_balance_wallet_24dp));
        //list.add(new Navigation(Constants.TYPE_MASTER, Constants.MENU_FAVORITE, R.string.menu_favorite, R.drawable.ic_drawer_favorites));
        //list.add(new Navigation(Constants.TYPE_MASTER, Constants.MENU_HISTORY, R.string.menu_history_master, R.drawable.ic_drawer_history));
        list.add(new Navigation(Constants.TYPE_MASTER, Constants.MENU_RATING, com.umka.umka.R.string.menu_rating, com.umka.umka.R.drawable.ic_drawer_reviews));
        list.add(new Navigation(Constants.TYPE_MASTER, Constants.MENU_SETTING, com.umka.umka.R.string.menu_settings, com.umka.umka.R.drawable.ic_drawer_settings));
        list.add(new Navigation(Constants.TYPE_MASTER, Constants.MENU_FEEDBACK, com.umka.umka.R.string.menu_feedback, com.umka.umka.R.drawable.ic_drawer_feedback));
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public void setSelect_position(int position){
        this.select_position = position;
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        Log.d("ItemCount!!!!!!!!!!!!", String.valueOf(i));
        view = LayoutInflater.from(context).inflate(com.umka.umka.R.layout.item_navigation, viewGroup, false);
        final ImageView imageView = (ImageView)view.findViewById(com.umka.umka.R.id.item_image);
        final TextView textView = (TextView)view.findViewById(com.umka.umka.R.id.item_title);
        final Navigation item = list.get(i);
        imageView.setImageResource(item.image);
        textView.setText(item.title);

        if (select_position == i && select_position != 5){
            textView.setTextColor(colorSelect);
            imageView.setColorFilter(colorSelect);
            view.setOnClickListener(null);
        }else if (logined){
            textView.setTextColor(colorDefault);
            imageView.setColorFilter(colorDefault);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClickListener(i, item);
                }
            });
        }else if (i == 1 || i == 2 || i == 3){
            textView.setTextColor(colorDisable);
            imageView.setColorFilter(colorDisable);
            view.setOnClickListener(null);
        }else {
            textView.setTextColor(colorDefault);
            imageView.setColorFilter(colorDefault);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClickListener(i, item);
                }
            });
        }

        return view;
    }


}
