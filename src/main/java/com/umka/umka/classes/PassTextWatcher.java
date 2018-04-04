package com.umka.umka.classes;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

/**
 * Created by trablone on 11/30/16.
 */

public class PassTextWatcher {

    private EditText editText_1, editText_2, editText_3, editText_4;
    private boolean stop = false;

    private EnterCodeListener listener;
    private String[] code = new String[4];

    public PassTextWatcher(EditText editText_1, EditText editText_2, EditText editText_3, EditText editText_4, EnterCodeListener listener){
        this.editText_1 = editText_1;
        this.editText_2 = editText_2;
        this.editText_3 = editText_3;
        this.editText_4 = editText_4;
        this.listener = listener;

        init();
    }

    private void parseCode(CharSequence charSequence, EditText editText, EditText editTextNext, int position){
        Log.e("tr", "charSequence: " + charSequence.length() + " : " + charSequence);
        String c = charSequence.length() > 1 ? charSequence.toString().substring(charSequence.length() - 1, charSequence.length()) : charSequence.toString();
        Log.e("tr", "c: " + c.length() + " : " + c);
        if (c.length() == 1){
            editText.setText(c);
            code[position] = c;
            listener.onSuccess(getCode());
            if (editTextNext != null)
                editTextNext.requestFocus();
        }
    }

    private String getCode(){
        StringBuilder builder = new StringBuilder();
        for (String c : code){
            if (TextUtils.isEmpty(c))
                return null;

            builder.append(c);
        }
        return builder.toString();
    }

    private void init(){
        editText_1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!stop){
                    stop = true;
                    if (charSequence.length() == 0) {
                        code[0] = null;
                    }
                    parseCode(charSequence,editText_1, editText_2, 0);
                    stop = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editText_2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!stop){
                    stop = true;
                    if (charSequence.length() == 0){
                        code[1] = null;
                        editText_1.requestFocus();
                        editText_1.setSelection(editText_1.getText().length());
                    }else
                        parseCode(charSequence,editText_2, editText_3, 1);
                    stop = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editText_3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!stop){
                    stop = true;
                    if (charSequence.length() == 0){
                        code[2] = null;
                        editText_2.requestFocus();
                        editText_2.setSelection(editText_2.getText().length());
                    }else
                        parseCode(charSequence,editText_3, editText_4, 2);
                    stop = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editText_4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!stop){
                    stop = true;
                    if (charSequence.length() == 0){
                        code[3] = null;
                        editText_3.requestFocus();
                        editText_3.setSelection(editText_3.getText().length());
                    }else
                        parseCode(charSequence, editText_4, null, 3);
                    stop = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public interface EnterCodeListener{
        void onSuccess(String code);
    }
}
