package com.umka.umka.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.umka.umka.model.Profile;

/**
 * Created by trablone on 5/26/17.
 */

public class UserProfileActivity extends BaseActivity {

    public static void showActivity(Context context, Profile profile){
        Intent intent = new Intent(context, UserProfileActivity.class);
        intent.putExtra("profile", profile);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.umka.umka.R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(com.umka.umka.R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
