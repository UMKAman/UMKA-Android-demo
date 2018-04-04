package com.umka.umka.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umka.umka.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by trablone on 11/14/16.
 */

public class RatingReviewsFragment extends BasePagerFragment {


    public static RatingReviewsFragment newInstance(){
        RatingReviewsFragment fragment = new RatingReviewsFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public int[] getTitles() {
        return new int[]{R.string.review, R.string.rating};
    }

    @Override
    public List<? extends BaseFragment> getFragments() {
        List<BaseFragment> list = new ArrayList<>();
        list.add(ReviewsFragment.newInstance(getBaseActivity().getUser().master_id, false));
        list.add(RatingFragment.getInstance(getBaseActivity().getUser().master_id));
        return list;
    }
}
