package com.umka.umka.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.umka.umka.MainActivity;
import com.umka.umka.classes.SplashAnimation;
import com.umka.umka.interfaces.SplashStateListener;

public class SplashActivity extends BaseActivity implements SplashStateListener{

    private ImageView imageView, imageViewTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.umka.umka.R.layout.activity_splash);
        imageView = (ImageView)findViewById(com.umka.umka.R.id.item_image);
        imageViewTitle = (ImageView)findViewById(com.umka.umka.R.id.item_title_image);
        new SplashAnimation(this, imageView, this);
    }

    @Override
    public void onSplashEnd() {
        imageViewTitle.setImageResource(com.umka.umka.R.drawable.splash_logo);
        imageView.setImageResource(com.umka.umka.R.drawable.splash_logo_icon);

        Intent intent = new Intent(this, isLogin() ? MainActivity.class : LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.finish();
    }
}
