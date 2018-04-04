package com.umka.umka.holders;

import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

/**
 * Created by trablone on 12/11/16.
 */

public class EditProfileHolder {

    public final EditText itemFirstName;
    public final EditText itemLastName;
    public final EditText itemEmail;
    public final EditText itemAbout;
    public final RadioGroup radioGroup;


    public EditProfileHolder(View view){
        itemEmail = (EditText)view.findViewById(com.umka.umka.R.id.item_email);
        itemAbout = (EditText)view.findViewById(com.umka.umka.R.id.item_about);
        itemLastName = (EditText)view.findViewById(com.umka.umka.R.id.item_last_name);
        itemFirstName = (EditText)view.findViewById(com.umka.umka.R.id.item_first_name);
        radioGroup = (RadioGroup)view.findViewById(com.umka.umka.R.id.item_gender);

    }



    public String getGender(){
        if (radioGroup.getCheckedRadioButtonId() == com.umka.umka.R.id.check_left){
            return "Муж";
        }
        return "Жен";
    }
}
