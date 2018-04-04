package com.umka.umka.activity;

import android.animation.ArgbEvaluator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.umka.umka.MainActivity;
import com.umka.umka.R;
import com.umka.umka.adapters.PagerAdapter;
import com.umka.umka.classes.BaseJsonHandler;
import com.umka.umka.classes.HttpClient;

import com.umka.umka.classes.InetCheackConection;
import com.umka.umka.classes.ParseMasterTask;
import com.umka.umka.classes.Utils;
import com.umka.umka.database.DbHelper;
import com.umka.umka.database.UserHelper;
import com.umka.umka.fragments.BaseFragment;
import com.umka.umka.fragments.login.FourStepFragment;
import com.umka.umka.fragments.login.StepOneFragment;
import com.umka.umka.fragments.login.ThreeStepFragment;
import com.umka.umka.fragments.login.TwoStepFragment;
import com.umka.umka.interfaces.LoginPageListener;
import com.umka.umka.model.Master;
import com.umka.umka.model.Profile;

import com.umka.umka.views.CustomViewPager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class LoginActivity extends BaseActivity {

    private CirclePageIndicator pageIndicator;
    private TextView buttonPrev, buttonNext;
    private LinearLayout layoutBackdround;

    private Profile user;

    private CustomViewPager viewPager;
    private PagerAdapter adapter;
    private List<BaseFragment> fragments;

    private Integer[] colors = null;
    private ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private int currentItem = 0;
    public boolean userCreate = false;
    private String pass;
    public boolean remindPass = false;
    private ProgressDialog progressDialog;
    private InetCheackConection inetCheack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.umka.umka.R.layout.activity_login);
        user = new Profile();
        inetCheack = new InetCheackConection(this);
        pageIndicator = (CirclePageIndicator)findViewById(com.umka.umka.R.id.page_indicator);
        viewPager = (CustomViewPager)findViewById(com.umka.umka.R.id.view_pager);
        layoutBackdround = (LinearLayout)findViewById(com.umka.umka.R.id.layout_background);
        viewPager.setOffscreenPageLimit(0);
        fragments = new ArrayList<>();
        fragments.add(new StepOneFragment());
        fragments.add(new TwoStepFragment());
        fragments.add(new ThreeStepFragment());
        fragments.add(new FourStepFragment());

        adapter = new PagerAdapter(this, getSupportFragmentManager(), fragments, null);
        viewPager.setAdapter(adapter);
        pageIndicator.setViewPager(viewPager);
        setUpColors();

        buttonPrev = (TextView)findViewById(com.umka.umka.R.id.button_prev);
        buttonNext = (TextView)findViewById(com.umka.umka.R.id.button_next);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        buttonPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentItem == 3 && user.phone != null)
                    --currentItem;
                else if (currentItem == 3)
                    currentItem -= 2;
                else
                    --currentItem;
                viewPager.setCurrentItem(currentItem, true);
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("tr", "currentItem: " + currentItem);
                if (currentItem < fragments.size() - 1){

                    if (currentItem == 1 && user.phone != null) {
                        if(inetCheack.isConnect()) {
                            progressDialog.show();
                            sendPhone("/auth/checkPhone");
                        }
                    } else if (currentItem == 2 && user.pass != null){
                        if(inetCheack.isConnect()) {
                            progressDialog.show();
                            login();
                        }
                    }else if (currentItem == 1) {
                        if(inetCheack.isConnect()) {
                            currentItem += 2;
                            progressDialog.show();
                            viewPager.setCurrentItem(currentItem, true);
                        }
                    }else {
                        if(inetCheack.isConnect()) {
                            ++currentItem;
                            progressDialog.show();
                            viewPager.setCurrentItem(currentItem, true);
                        }
                    }

                }else if (currentItem == fragments.size() -1){

                    if (user.isMaster()){
                        if(inetCheack.isConnect())
                            getMaster();
                    }else {
                        if(inetCheack.isConnect()) {
                            updateUser();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            LoginActivity.this.finish();
                        }
                    }

                }
            }
        });

        viewPager.setEnable(false);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position < (adapter.getCount() - 1) && position < (colors.length - 1))
                {
                    layoutBackdround.setBackgroundColor((Integer) argbEvaluator.evaluate(positionOffset, colors[position], colors[position + 1]));
                } else {
                    layoutBackdround.setBackgroundColor(colors[colors.length - 1]);
                }
            }

            @Override
            public void onPageSelected(int position)
            {
                if (position == 2){
                    ThreeStepFragment fragment = (ThreeStepFragment)fragments.get(position);
                    fragment.setRemind(userCreate);
                }
                setNavigationText(position);
                pageIndicator.setCurrentItem(position);
            }


            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setNavigationText(currentItem);
    }

    private void getMaster(){
        if (user.id > 0){
            HttpClient.get("/master?where={\"user\":"+ user.id + "}", this, null, new BaseJsonHandler(this){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);
                    if (response.length() > 0){
                        try {
                            JSONObject object = response.getJSONObject(0);
                            new ParseMasterTask(){
                                @Override
                                protected void onPostExecute(Master master) {
                                    super.onPostExecute(master);
                                    user.master_id = master.id;
                                    updateUser();
                                    CreateMasterActivity.showActivity(LoginActivity.this);
                                    LoginActivity.this.finish();
                                }
                            }.execute(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        createMaster();
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    progressDialog.dismiss();
                }
            });
        }else {
            user.is_master = false;
            updateUser();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            LoginActivity.this.finish();
        }

    }

    private void createMaster(){
        HttpClient.post("/master", this, null, new BaseJsonHandler(this){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progressDialog.dismiss();
                user.master_id = response.optInt("id");
                updateUser();
                CreateMasterActivity.showActivity(LoginActivity.this);
                LoginActivity.this.finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                user.is_master = false;
                updateUser();
                progressDialog.dismiss();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                LoginActivity.this.finish();
            }

        });
    }

    private void updateUser(){
        UserHelper userHelper = new UserHelper(new DbHelper(this).getDataBase());
        userHelper.updateUser(user);
    }

    public void setCode(String code){
        user.pass = code;
        buttonNext.setEnabled(code != null);
        if (code != null){
            Utils.hideKeyboard(LoginActivity.this);
        }
    }

    public void setPass(String code){
        pass = code;
        buttonNext.setEnabled(code != null);
        if (code != null){
            Utils.hideKeyboard(LoginActivity.this);
        }
    }

    public void setPhone(String phone) {
        user.phone = phone;
        if (phone != null){
            Utils.hideKeyboard(LoginActivity.this);
        }
    }

    public void setCity(String city) {
        user.city = city;

    }

    private void sendPhone(String url){
        try {
            JSONObject object = new JSONObject();
            object.put("phone", user.phone);
            Log.e("tr", "object: " + object);
            HttpClient.post(this, url, new StringEntity(object.toString(), "UTF-8"), new BaseJsonHandler(this){

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        userCreate = response.getBoolean("result");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ++currentItem;
                    viewPager.setCurrentItem(currentItem, true);
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    progressDialog.dismiss();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void remindPass(){

        remindPass = true;
        RequestParams params = new RequestParams();
        params.put("phone", user.phone);
            HttpClient.post("/auth/remindPassword", this, params, new BaseJsonHandler(this){

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    String code = response.optString("code");
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, code, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    progressDialog.dismiss();
                }
            });

    }

    public void login(){


        try {
            if (userCreate){

                if (remindPass){
                    final String fireToken = FirebaseInstanceId.getInstance().getToken();
                    JSONObject object = new JSONObject();
                    object.put("phone", user.phone);
                    object.put("code", user.pass);
                    object.put("password", pass);
                    object.put("fireToken", fireToken);
                    object.put("device", "Android device");
                    Log.e("tr", "object: " + object);
                    HttpClient.post(this, "/auth/resetPassword", new StringEntity(object.toString(), "UTF-8"), new BaseJsonHandler(this){

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            if (!response.isNull("jwt")){
                                user.token = response.optString("jwt");
                                if (!response.isNull("user")){
                                    try{
                                        JSONObject profile = response.getJSONObject("user");
                                        user.id = profile.getInt("id");
                                        user.name = profile.getString("name");
                                        user.avatar = profile.getString("avatar");
                                        user.about = profile.getString("about");
                                        user.gender = profile.getString("gender");
                                    }catch (JSONException e){

                                    }
                                }

                                updateUser();
                                if (user.city != null){
                                    updateUserCity();
                                }
                                ++currentItem;
                                viewPager.setCurrentItem(currentItem, true);
                            }
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            progressDialog.dismiss();
                        }
                    });
                }else {


                    final String fireToken = FirebaseInstanceId.getInstance().getToken();
                    JSONObject object = new JSONObject();
                    object.put("phone", user.phone);
                    object.put("password", user.pass);
                    object.put("fireToken", fireToken);
                    object.put("device", "Android device");
                    Log.e("tr", "object: " + object);
                    HttpClient.post(this, "/auth", new StringEntity(object.toString(), "UTF-8"), new BaseJsonHandler(this) {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            if (!response.isNull("jwt")) {
                                user.token = response.optString("jwt");
                                if (!response.isNull("user")) {
                                    try {
                                        JSONObject profile = response.getJSONObject("user");
                                        user.id = profile.getInt("id");
                                        user.name = profile.getString("name");
                                        user.avatar = profile.getString("avatar");
                                        user.about = profile.getString("about");
                                        user.gender = profile.getString("gender");
                                    } catch (JSONException e) {

                                    }
                                }

                                updateUser();
                                if (user.city != null) {
                                    updateUserCity();
                                }
                                ++currentItem;
                                viewPager.setCurrentItem(currentItem, true);
                            }
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            progressDialog.dismiss();
                            if (statusCode == 500)
                                Toast.makeText(LoginActivity.this, "Неправильный пароль", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }else {
                final String fireToken = FirebaseInstanceId.getInstance().getToken();
                JSONObject object = new JSONObject();
                object.put("phone", user.phone);
                object.put("password", user.pass);
                object.put("fireToken", fireToken);
                object.put("device", "Android device");
                Log.e("tr", "object: " + object);
                HttpClient.post(this, "/auth/registration", new StringEntity(object.toString(), "UTF-8"), new BaseJsonHandler(this){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        if (!response.isNull("jwt")){
                            user.token = response.optString("jwt");
                            if (!response.isNull("user")){
                                try{
                                    JSONObject profile = response.getJSONObject("user");
                                    user.id = profile.getInt("id");
                                    user.name = profile.getString("name");
                                    user.avatar = profile.getString("avatar");
                                    user.about = profile.getString("about");
                                    user.gender = profile.getString("gender");
                                }catch (JSONException e){

                                }
                            }

                            updateUser();
                            if (user.city != null){
                                updateUserCity();
                            }
                            ++currentItem;
                            viewPager.setCurrentItem(currentItem, true);
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        progressDialog.dismiss();
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void setNextStep(boolean type){
        user.is_master = type;
        ++currentItem;
        viewPager.setCurrentItem(currentItem, true);
    }

    private void setUpColors() {
        Integer color1 = getResources().getColor(com.umka.umka.R.color.colorPrimary);
        Integer color2 = getResources().getColor(com.umka.umka.R.color.colorPrimary);
        Integer color3 = getResources().getColor(com.umka.umka.R.color.colorPrimary);
        Integer color4 = getResources().getColor(com.umka.umka.R.color.colorAccent);

        Integer[] colors_temp = {color1, color2, color3, color4};
        colors = colors_temp;
    }

    private void setNavigationText(int position){

        if (position == 0){
            buttonPrev.setText("");
            buttonPrev.setEnabled(false);
            buttonNext.setText("");
            buttonNext.setEnabled(false);
        }else {
            buttonNext.setEnabled(true);
            buttonPrev.setEnabled(true);
            buttonPrev.setText(getResources().getString(R.string.back));
        }

        if (position == fragments.size() - 1){
            buttonNext.setText(getResources().getString(R.string.begin));

        }else if (position > 0){
            buttonNext.setText(getResources().getString(R.string.further));
        }

        if (position == 1){
            LoginPageListener listener = (LoginPageListener)adapter.getItem(position);
            listener.onSetMode(getMode());
        }

    }

    private String getMode(){
        if (!user.is_master)
            return getResources().getString(R.string.mode_user);

        return getResources().getString(R.string.mode_macter);
    }

    private void updateUserCity() {


        JSONObject object = new JSONObject();
        try {
            object.put("city", user.city);
        } catch (JSONException e) {
            Log.e("tr", "e: " + e.getMessage());
        }
        Log.e("tr", "params: " + object);
        HttpClient.put(this, "/user/" + user.id, new StringEntity(object.toString(), "UTF-8"), new BaseJsonHandler(this){

        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Fragment fragment = adapter.getItem(viewPager.getCurrentItem());
        if (fragment != null)
        fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = adapter.getItem(viewPager.getCurrentItem());
        if (fragment != null)
            fragment.onActivityResult(requestCode, resultCode, data);
    }
}
