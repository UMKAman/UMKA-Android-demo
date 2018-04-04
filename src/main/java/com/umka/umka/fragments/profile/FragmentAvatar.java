package com.umka.umka.fragments.profile;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.umka.umka.classes.BaseJsonHandler;
import com.umka.umka.classes.CropImage;
import com.umka.umka.classes.HttpClient;
import com.umka.umka.classes.Utils;
import com.umka.umka.database.DbHelper;
import com.umka.umka.database.UserHelper;
import com.umka.umka.fragments.BaseFragment;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pkmmte.view.CircularImageView;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;


/**
 * Created by trablone on 12.12.15.
 */
public class FragmentAvatar extends BaseFragment {
    public static final int PICK_IMAGE = 700;
    private final int PERMISSION_READ_STORAGE = 500;

    private ImageView imageProfile;
    private ProgressBar itemProgress;
    private String imageUrl;
    private int user_id;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(com.umka.umka.R.layout.fragment_avatar, container, false);
        imageProfile = (ImageView) view.findViewById(com.umka.umka.R.id.item_image);
        itemProgress = (ProgressBar) view.findViewById(com.umka.umka.R.id.item_progress);
        return view;
    }

    public void setImageUrl(String url){
        this.imageUrl = url;
        updateUserPhoto();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        UserHelper userHelper = new UserHelper(new DbHelper(getBaseActivity()).getDataBase());
        user_id = userHelper.getUser().id;

        imageProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectImage();

                    }
                });
    }

    private void selectImage(){
        if (Build.VERSION.SDK_INT >= 23){
            if (ContextCompat.checkSelfPermission(getBaseActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getBaseActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getBaseActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_READ_STORAGE);
                return;
            }
        }
        getBaseActivity().startActivityForResult(CropImage.getPickImageChooserIntent(getBaseActivity()), PICK_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0){
            selectImage();
        }
    }

    private void updateUserPhoto() {
        ImageLoader.getInstance().displayImage(HttpClient.BASE_URL_IMAGE + imageUrl, imageProfile, Utils.getImageOptions(com.umka.umka.R.drawable.image_profile_add_avatar));
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUserPhoto();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE) {
            Uri uri = CropImage.getPickImageResultUri(getBaseActivity(), data);
//            imageProfile.setImageURI(uri);
            File finalFile;
            if (uri.getPath().contains("pickImageResult.jpeg")){
                finalFile = new File(uri.getPath());
            }else {
                finalFile = new File(getRealPathFromGallery(data.getData()));
            }

            sendPhoto(finalFile);
        }
    }

    private String getRealPathFromGallery(Uri contentURI) {
        String result;
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }

        return result;
    }

    private void sendPhoto(File file){
        RequestParams params = new RequestParams();
        try {
            params.put("avatar", file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        HttpClient.putImage("/user/" + user_id, getActivity(), params, new BaseJsonHandler(getBaseActivity()){

            @Override
            public void onStart() {
                super.onStart();
                itemProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                if(statusCode == 200 && statusCode == 201) {
                    imageUrl = response.optString("avatar");
                    updateUserPhoto();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                itemProgress.setVisibility(View.GONE);
            }
        });
    }

}
