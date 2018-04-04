package com.umka.umka.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umka.umka.R;
import com.umka.umka.adapters.PagerAdapter;

import java.util.List;

/**
 * Created by trablone on 11/14/16.
 */

public abstract class BasePagerFragment extends BaseFragment {

    private ViewPager viewPager;
    private PagerAdapter adapter;
    private TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pager, container, false);
        viewPager = (ViewPager)view.findViewById(R.id.view_pager);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tabLayout = (TabLayout)getBaseActivity().findViewById(R.id.tab_layout);
        tabLayout.setVisibility(View.VISIBLE);
        adapter = new PagerAdapter(getBaseActivity(), getBaseActivity().getSupportFragmentManager(), getFragments(), getTitles());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onDestroy() {
        tabLayout.setVisibility(View.GONE);
        super.onDestroy();
    }

    public abstract int[] getTitles();
    public abstract List<? extends BaseFragment> getFragments();
}
