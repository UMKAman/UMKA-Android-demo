package com.umka.umka.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umka.umka.R;
import com.umka.umka.classes.Constants;

/**
 * Created by trablone on 11/13/16.
 */

public class MainMasterFragment extends BaseFragment {

    private LinearLayout layoutBottomTab;
    BaseFragment fragment;
    private int[] bottomTabTitles = new int[]{R.string.schedule, R.string.messages, R.string.orders};
    private int[] bottomTabImages = new int[]{com.umka.umka.R.drawable.ic_bottom_nav_graphic, com.umka.umka.R.drawable.ic_bottom_nav_chat, com.umka.umka.R.drawable.ic_bottom_nav_orders};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(com.umka.umka.R.layout.fragment_main, container, false);
        layoutBottomTab = (LinearLayout)view.findViewById(com.umka.umka.R.id.bottom_tab);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initBottomTab(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //fragment.onActivityResult(requestCode, resultCode, data);
    }

    private void initBottomTab(int position){
        layoutBottomTab.removeAllViews();
        int colorWhite = getResources().getColor(com.umka.umka.R.color.colorWhite);
        int colorGray = getResources().getColor(R.color.colorWhiteTransparent);
        for (int i = 0; i < bottomTabTitles.length; i++){
            final int select = i;
            final View view = LayoutInflater.from(getBaseActivity()).inflate(com.umka.umka.R.layout.item_bottom_tab, layoutBottomTab, false);
            final ImageView imageView = (ImageView)view.findViewById(com.umka.umka.R.id.item_image);
            final TextView textView = (TextView)view.findViewById(com.umka.umka.R.id.item_title);
            textView.setText(getResources().getString(bottomTabTitles[i]));
            imageView.setImageResource(bottomTabImages[i]);
            if (position == select){
                replaceFragment(position);
                imageView.setColorFilter(colorWhite);
                textView.setTextColor(colorWhite);
            }else {
                imageView.setColorFilter(colorGray);
                textView.setTextColor(colorGray);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        initBottomTab(select);
                    }
                });
            }
            layoutBottomTab.addView(view);
        }
    }

    private void replaceFragment(int position){

        switch (position){
            case 0:
                fragment = new CalendarFragment();
                break;
            case 1:
                fragment = ChatFragment.newInstance(Constants.TYPE_MASTER);
                break;
            case 2:
                fragment = OrdersFragment.newInstance("/order?where={\"master\":" + getBaseActivity().getUser().master_id + "}");
                break;
        }
        getBaseActivity().getSupportFragmentManager().beginTransaction()
                .replace(com.umka.umka.R.id.content_main, fragment)
                .commit();
    }
}
