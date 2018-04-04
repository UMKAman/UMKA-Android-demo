package com.umka.umka.fragments.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.umka.umka.activity.LoginActivity;
import com.umka.umka.fragments.BaseFragment;

/**
 * Created by trablone on 11/24/16.
 */

public class StepOneFragment extends BaseFragment {

    private TextView textMaster, textUser;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(com.umka.umka.R.layout.step_one_fragment, container, false);
        textUser = (TextView)view.findViewById(com.umka.umka.R.id.item_type_user);
        textMaster = (TextView)view.findViewById(com.umka.umka.R.id.item_type_master);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        textUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLoginActivity().setNextStep(false);
            }
        });

        textMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLoginActivity().setNextStep(true);
            }
        });


    }

    private LoginActivity getLoginActivity(){
        return (LoginActivity)getActivity();
    }
}
