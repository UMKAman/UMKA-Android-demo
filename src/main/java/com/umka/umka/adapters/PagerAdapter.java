package com.umka.umka.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.umka.umka.fragments.BaseFragment;

import java.util.List;

/**
 * Created by trablone on 11/14/16.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {

    private List<? extends BaseFragment> list;
    private int[] titles;
    private Context context;



    public PagerAdapter(Context context, FragmentManager fm, List<? extends BaseFragment> list, int[] titles) {
        super(fm);
        this.context = context;
        this.list = list;
        this.titles = titles;
    }


    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (titles != null)
            return context.getResources().getString(titles[position]);
        return null;
    }
}
