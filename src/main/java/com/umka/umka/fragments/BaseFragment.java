package com.umka.umka.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;

import com.umka.umka.MainActivity;
import com.umka.umka.activity.BaseActivity;
import com.umka.umka.classes.Utils;

/**
 * Created by trablone on 11/13/16.
 */

public class BaseFragment extends Fragment {

    private BaseActivity baseActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseActivity = (BaseActivity)getActivity();
    }

    public BaseActivity getBaseActivity(){
        return baseActivity;
    }

    public MainActivity getMainActivity(){
        return (MainActivity)getActivity();
    }


    public void setPrograss(final SwipeRefreshLayout refreshLayout, final boolean refresh){
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(refresh);
            }
        });
    }

    @Override
    public void onDestroy() {
        Utils.hideKeyboard(getBaseActivity());
        super.onDestroy();
    }
}
