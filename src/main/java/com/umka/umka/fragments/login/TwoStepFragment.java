package com.umka.umka.fragments.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.umka.umka.R;
import com.umka.umka.activity.LoginActivity;
import com.umka.umka.classes.ParsePhoneNumber;
import com.umka.umka.fragments.BaseFragment;
import com.umka.umka.interfaces.LoginPageListener;

/**
 * Created by trablone on 11/24/16.
 */

public class TwoStepFragment extends BaseFragment implements LoginPageListener{

    private EditText editTextPhone;
    private TextView textMode;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.step_two_fragment, container, false);
        editTextPhone = (EditText)view.findViewById(R.id.item_edit_text_phone);
        textMode = (TextView)view.findViewById(R.id.item_mode);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new ParsePhoneNumber(getActivity(), editTextPhone, new ParsePhoneNumber.PhoneListener() {
            @Override
            public void onPhoneComplete(String phone) {
                getLoginActivity().setPhone(phone);
            }
        });
    }

    @Override
    public void onSetMode(String mode) {
        if (textMode != null){
            textMode.setText(mode);
        }
    }

    private LoginActivity getLoginActivity(){
        return (LoginActivity)getActivity();
    }


}
