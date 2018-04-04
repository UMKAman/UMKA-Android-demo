package com.umka.umka.fragments.profile;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.umka.umka.R;
import com.umka.umka.fragments.BaseFragment;


public class MyWalletFragment extends BaseFragment {

    public MyWalletFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_wallet, container, false);
        final Button buttonAdd = (Button)view.findViewById(R.id.add_button);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(buttonAdd, "Данная функция находится в разработке", Snackbar.LENGTH_SHORT).show();
            }
        });

        return view;
    }

}
