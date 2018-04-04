package com.umka.umka.fragments.profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.umka.umka.R;
import com.umka.umka.classes.BaseJsonHandler;
import com.umka.umka.classes.HttpClient;
import com.umka.umka.classes.ParseProfileTask;
import com.umka.umka.classes.Utils;
import com.umka.umka.database.DbHelper;
import com.umka.umka.database.UserHelper;

import com.umka.umka.fragments.NavigationFragment;
import com.umka.umka.holders.UserProfileHolder;
import com.umka.umka.model.Profile;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


/**
 * Created by trablone on 11/14/16.
 */

public class ProfileFragment extends BaseProfileFragment {

    public static ProfileFragment newInstance(boolean type) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    private UserProfileHolder holder;

    private boolean type;
    private UserHelper userHelper;
    private Profile user;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        holder = new UserProfileHolder(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

            user = (Profile) getActivity().getIntent().getSerializableExtra("profile");
        if (user != null){
            setLocalUserData();
        }else {
            setHasOptionsMenu(true);
            userHelper = new UserHelper(new DbHelper(getBaseActivity()).getDataBase());
            user = userHelper.getUser();
            profile = user.id;

            setLocalUserData();
            getProfile();

        }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void getProfile() {
        HttpClient.get("/user/" + profile, getBaseActivity(), null, new BaseJsonHandler(getBaseActivity()){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                new ParseProfileTask() {
                    @Override
                    protected void onPostExecute(Profile profile) {
                        super.onPostExecute(profile);
                        setProfile(profile);
                    }
                }.execute(response);
            }

        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(0, 1, 0, R.string.title_activity_edit_profile).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS).setIcon(R.drawable.ic_toolbar_edit);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                getMainActivity().addFragment(EditProfileFragment.getInstance(true), getString(R.string.title_activity_edit_profile), false);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setProfile(Profile item) {

        user = item;
        if (userHelper != null){
            userHelper.updateUser(user);
        }

        setLocalUserData();
    }

    private void setLocalUserData() {
        holder.itemName.setText(user.getName());
        holder.itemPhone.setText(Utils.getParsePhone(user.phone));
        holder.itemAbout.setText(user.about);
        holder.itemCity.setText(user.city);
        holder.itemGender.setText(getResources().getString(user.getGender()));
//        Glide.with(this).load(HttpClient.BASE_URL + user.avatar).into(holder.imageView);
        ImageLoader.getInstance().displayImage(HttpClient.BASE_URL_IMAGE + user.avatar, holder.imageView, Utils.getImageOptions(R.drawable.image_profile_no_avatar));
        if (userHelper != null){
            NavigationFragment fragment = (NavigationFragment)getMainActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_navigation);
            fragment.updateUserData();
        }

    }
}
