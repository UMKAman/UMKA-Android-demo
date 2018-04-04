package com.umka.umka.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.umka.umka.MainActivity;
import com.umka.umka.R;
import com.umka.umka.classes.BaseJsonHandler;
import com.umka.umka.classes.HttpClient;
import com.umka.umka.classes.InetCheackConection;
import com.umka.umka.classes.UpdateProfileListener;
import com.umka.umka.database.DbHelper;
import com.umka.umka.database.UserHelper;
import com.umka.umka.fragments.BaseFragment;
import com.umka.umka.fragments.profile.EditProfileFragment;
import com.umka.umka.fragments.profile.MasterProfileFragment;
import com.umka.umka.model.Master;
import com.umka.umka.model.Profile;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by trablone on 5/7/17.
 */

public class CreateMasterActivity extends BaseActivity {

    private ProgressDialog progressDialog;
    private InetCheackConection inetCheack;
    public static void showActivity(Context context){
        Intent intent = new Intent(context, CreateMasterActivity.class);
        context.startActivity(intent);
    }

    private TextView buttonPrev, buttonNext, buttonDone;
    private Toolbar toolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_master);
        inetCheack = new InetCheackConection(this);
        buttonDone = (TextView)findViewById(R.id.item_done);
        buttonPrev = (TextView)findViewById(R.id.item_prew);
        buttonNext = (TextView)findViewById(R.id.item_next);
        toolbar = (Toolbar)findViewById(com.umka.umka.R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Настройка профиля");
        final EditProfileFragment fragment = EditProfileFragment.getInstance(false);
        showFragment(fragment);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(inetCheack.isConnect()) {
                    buttonNext.setClickable(false);
                    progressDialog.show();
                    fragment.updateProfile(new UpdateProfileListener() {
                        @Override
                        public void onSuccess() {
                            buttonDone.setClickable(true);
                            buttonNext.setClickable(true);
                            showFragment(MasterProfileFragment.newInstance(true));
                            buttonNext.setVisibility(View.GONE);
                            buttonPrev.setVisibility(View.VISIBLE);
                            buttonDone.setVisibility(View.VISIBLE);
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onFailure() {
                            buttonDone.setClickable(true);
                            buttonNext.setClickable(true);
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });


        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inetCheack.isConnect()) {
                    buttonDone.setClickable(false);
                    MasterProfileFragment fragment = (MasterProfileFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
                    Master master = fragment.getMaster();
                    if (master != null)
                        if (master.isCheck(buttonDone)) {
                            updateUser();
                        }
                }
            }
        });

        buttonPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFragment(EditProfileFragment.getInstance(false));
                buttonDone.setVisibility(View.GONE);
                buttonPrev.setVisibility(View.GONE);
                buttonNext.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SplashActivity.class));
        super.onBackPressed();
    }

    private void updateUser() {
        progressDialog.show();
        final UserHelper userHelper = new UserHelper(new DbHelper(this).getDataBase());
        final Profile item = userHelper.getUser();
        item.is_master = true;
        JSONObject object = new JSONObject();
        try {
            object.put("isMaster", item.is_master);
        } catch (JSONException e) {
            Log.e("tr", "e: " + e.getMessage());
        }
        Log.e("tr", "params: " + object);
        HttpClient.put(this, "/user/" + item.id, new StringEntity(object.toString(), "UTF-8"),new BaseJsonHandler(this){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                userHelper.updateUser(item);
                progressDialog.dismiss();
                Intent intent = new Intent(CreateMasterActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                CreateMasterActivity.this.finish();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                buttonDone.setClickable(true);
                buttonNext.setClickable(true);
                progressDialog.dismiss();
                Log.e("!!!!!!!!!!!!!!!!!", "NotInet");
            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (fragment != null){
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (fragment != null){
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void showFragment(BaseFragment fragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    public void showFragmentBack(BaseFragment fragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void setClicableButton(){

        if (buttonDone != null)
            buttonDone.setClickable(true);

        if(buttonNext != null)
            buttonNext.setClickable(true);
    }

}
