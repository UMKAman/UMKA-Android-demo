package com.umka.umka.fragments.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.umka.umka.fragments.BaseFragment;

/**
 * Created by trablone on 12/9/16.
 */

public class MasterInfoFragment extends BaseFragment {

    private TextView textView;

    public static MasterInfoFragment getInstance(String text){
        MasterInfoFragment fragment = new MasterInfoFragment();
        Bundle args = new Bundle();
        args.putString("text", text);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(com.umka.umka.R.layout.fragment_master_info, container, false);
        textView = (TextView)view.findViewById(com.umka.umka.R.id.item_text);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String text = getArguments().getString("text");
        textView.setText(text);
    }
}
