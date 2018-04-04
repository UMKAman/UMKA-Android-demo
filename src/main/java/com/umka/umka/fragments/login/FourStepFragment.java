package com.umka.umka.fragments.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umka.umka.fragments.BaseFragment;

/**
 * Created by trablone on 11/24/16.
 */

public class FourStepFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(com.umka.umka.R.layout.step_four_fragment, container, false);

        return view;
    }
}
