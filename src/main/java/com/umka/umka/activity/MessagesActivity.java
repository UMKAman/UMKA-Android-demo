package com.umka.umka.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.umka.umka.classes.Utils;
import com.umka.umka.fragments.MessagesFragment;
import com.umka.umka.model.Message;

/**
 * Created by trablone on 12/17/16.
 */

public class MessagesActivity extends BaseActivity {

    public static void showActivity(Context context, int chat_id, String title){
        Intent intent = new Intent(context, MessagesActivity.class);
        intent.putExtra("chat_id", chat_id);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    private MessagesFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.umka.umka.R.layout.activity_messages);
        Toolbar toolbar = (Toolbar) findViewById(com.umka.umka.R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(getIntent().getStringExtra("title"));
        setResult(RESULT_CANCELED);
        fragment = (MessagesFragment)getSupportFragmentManager().findFragmentById(com.umka.umka.R.id.fragment_messages);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Message item = fragment.getLastMessage();
        Log.e("tr", "item: " + item);
        if (item != null){
            Intent intent = new Intent();
            intent.putExtra("message", item);
            setResult(RESULT_OK, intent);
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                Utils.hideKeyboard(this);
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
