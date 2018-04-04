package com.umka.umka.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.umka.umka.classes.Utils;
import com.umka.umka.database.DbHelper;
import com.umka.umka.database.UserHelper;
import com.umka.umka.model.Profile;


/**
 * Created by trablone on 11/13/16.
 */

public class BaseActivity extends AppCompatActivity {


    public boolean isLogin(){
        return getUser() != null;
    }

    public boolean isLoginUser(){
        Profile profile = getUser();
        return  !TextUtils.isEmpty(profile.token);
    }

    public void startLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.finish();
    }

    public String getToken(){
        Profile user = getUser();
        if (user != null)
            return user.token;

        return null;
    }

    public Profile getUser(){
        UserHelper userHelper = new UserHelper(new DbHelper(this).getDataBase());
        return userHelper.getUser();
    }

    private String phone;
    public void call(String phone){
        this.phone = phone;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 101);

            return;
        }
        Uri number = Uri.parse("tel:" + phone);
        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
        startActivity(callIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101){
            if(grantResults.length > 0){
                call(phone);
            }
        }
    }

    @Override
    protected void onDestroy() {
        Utils.hideKeyboard(this);
        super.onDestroy();
    }
}
