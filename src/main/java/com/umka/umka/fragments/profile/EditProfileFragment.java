package com.umka.umka.fragments.profile;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.umka.umka.MainActivity;
import com.umka.umka.classes.BaseJsonHandler;
import com.umka.umka.classes.HttpClient;
import com.umka.umka.classes.InetCheackConection;
import com.umka.umka.classes.UpdateProfileListener;
import com.umka.umka.classes.Utils;
import com.umka.umka.fragments.BaseFragment;
import com.umka.umka.holders.EditProfileHolder;
import com.umka.umka.model.Profile;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by trablone on 12/11/16.
 */

public class EditProfileFragment extends BaseFragment {

    private EditProfileHolder holder;
    private Profile item;
    private FragmentAvatar fragmentAvatar;
    private boolean menu;
    private ProgressDialog progressDialog;
    private InetCheackConection inetCheack;

    public static EditProfileFragment getInstance(boolean menu){
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle params = new Bundle();
        params.putBoolean("menu", menu);
        fragment.setArguments(params);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(com.umka.umka.R.layout.fragment_edit_profile, container, false);
        holder = new EditProfileHolder(view);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        inetCheack = new InetCheackConection(getActivity());
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        menu = getArguments().getBoolean("menu");
        setHasOptionsMenu(menu);
        fragmentAvatar = (FragmentAvatar)getChildFragmentManager().findFragmentById(com.umka.umka.R.id.fragment_avatar_edit);
        item = getBaseActivity().getUser();

        fragmentAvatar.setImageUrl(item.avatar);
        holder.itemFirstName.setText(item.getName());
        //holder.itemLastName.setText(item.lastname);
        holder.radioGroup.check(item.getGenderId());
        holder.itemAbout.setText(item.getAbout());

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fragmentAvatar.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        fragmentAvatar.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(0, 1, 0, com.umka.umka.R.string.menu_save).setIcon(com.umka.umka.R.drawable.ic_toolbar_done).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 1:
                if(inetCheack.isConnect())
                    updateProfile(null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
   public boolean setProfile(){
        String name = holder.itemFirstName.getText().toString();
        String about = holder.itemAbout.getText().toString();

        if (TextUtils.isEmpty(name)){
            holder.itemFirstName.setError("Напишите имя");
            return false;
        }

        if (TextUtils.isEmpty(about)){
            holder.itemAbout.setError("Напишите несколько слов о себе");
            return false;
        }

        item.name = name;
        item.setGender(holder.getGender());
        item.about = about;

        return true;
    }

    public void updateProfile(final UpdateProfileListener listener){

        if (setProfile()){
            progressDialog.show();
            JSONObject object = new JSONObject();
            try {
                //object.put("firstname", item.firstname);
                object.put("name", item.name);
                object.put("gender", item.gender);
                object.put("about", item.about);
                String city = item.city;
                if (city != null){
                    if (!city.contains("неопределено"))
                        object.put("city", item.city);
                }

                //object.put("phone", item.phone);
            } catch (JSONException e) {
                Log.e("tr", "e: " + e.getMessage());
            }

            HttpClient.put(getBaseActivity(), "/user/" + item.id , new StringEntity(object.toString(), "UTF-8"), new BaseJsonHandler(getBaseActivity()){

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                        progressDialog.dismiss();
                        if (listener == null ) {
                            startActivity((new Intent(getActivity(), MainActivity.class)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        }else {
                            listener.onSuccess();
                        }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    progressDialog.dismiss();
                    if (listener != null){
                        listener.onFailure();
                    }
                }

            });
        }else {
            if (listener != null){
                listener.onFailure();
            }
        }

    }


    @Override
    public void onDestroy() {
        Utils.hideKeyboard(getBaseActivity());
        super.onDestroy();
    }
}
