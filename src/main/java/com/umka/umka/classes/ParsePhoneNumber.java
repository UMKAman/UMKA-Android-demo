package com.umka.umka.classes;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by trablone on 11/24/16.
 */

public class ParsePhoneNumber {
    private EditText editText;
    private Context context;
    private PhoneListener listener;

    public static final int PHONE_LENTH_MIN = 10;

    private boolean ignoreTextChange = false;
    private int oldSelection = -1;
    private String phoneNumber;

    public ParsePhoneNumber(Context context, EditText editText, PhoneListener listener){
        this.listener = listener;
        this.context = context;
        this.editText = editText;
        init();
    }

    private void init(){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {

                if (!ignoreTextChange) {
                    ignoreTextChange = true;

                    int selection = editText.getSelectionStart();

                    if (oldSelection > 0 && oldSelection < selection) {
                        String number = s.toString();
                        number = number.substring(0, selection);
                        phoneNumber = parsePhone(number);
                        number = getParsePhone(phoneNumber);
                        editText.setText(number);
                        selection = editText.getText().length();
                        editText.setSelection(selection);

                    }
                    oldSelection = selection;
                    String phone = parsePhone(s.toString());

                    if (phone.length() >= PHONE_LENTH_MIN){
                        listener.onPhoneComplete(getActualPhone(phone));
                    }else {
                        listener.onPhoneComplete(null);
                    }

                    ignoreTextChange = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private String getActualPhone(String phone){
        return "+7" + getLenghtPhone(parsePhone(phone));
    }

    private String getLenghtPhone(String phone){
        if (phone.length() > PHONE_LENTH_MIN) {
            phone = phone.substring(0, PHONE_LENTH_MIN);
        }
        return phone;
    }

    private String parsePhone(String phone){
        return phone.replaceAll("[^0-9]+", "");
    }

    public String getParsePhone(String phone) {

        phone = getLenghtPhone(phone);
        String[] phoneArray = phone.split("");
        int length = phoneArray.length;

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(phoneArray[i]);
            if (i == 0){
                builder.append(" (");
            }
            if (i == 3) {
                builder.append(") ");
            }
            if (i == 6) {
                builder.append(" ");
            }
            if (i == 8) {
                builder.append(" ");
            }
        }
        return builder.toString();
    }

    public interface PhoneListener{
        void onPhoneComplete(String phone);
    }
}
