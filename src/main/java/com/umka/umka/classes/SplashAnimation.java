package com.umka.umka.classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.umka.umka.interfaces.SplashStateListener;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by trablone on 11/23/16.
 */

public class SplashAnimation {

    private Context context;
    private ImageView imagView;
    private Timer timer;
    private int index;
    private MyHandler handler;
    SplashStateListener listener;

    public SplashAnimation(Context context, ImageView imageView, SplashStateListener listener){
        this.context = context;
        this.imagView = imageView;
        this.listener = listener;
        handler = new MyHandler();
        index = 1;
        timer = new Timer();
        timer.schedule(new TickClass(), 200, 40);
    }

    private class TickClass extends TimerTask
    {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            handler.sendEmptyMessage(index);
            index++;
        }
    }

    private class MyHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if (index <= 33){
                try {
                    Bitmap bmp= BitmapFactory.decodeStream(context.getAssets().open("animation_splash/" + getDensityName() + "/" + getAssetsName(index)));
                    imagView.setImageBitmap(bmp);
                } catch (IOException e) {
                    Log.e("Exception in Handler ",e.getMessage());
                }
            }else {
                listener.onSplashEnd();
                timer.cancel();
            }
        }
    }

    private String getAssetsName(Integer index){
        String value = String.valueOf(index);
        if (value.length() == 1){
            value = "000" + value;
        }else {
            value = "00" + value;
        }
        return value + ".png";
    }

    private String getDensityName() {
        float density = context.getResources().getDisplayMetrics().density;

        if (density >= 3.0) {
            return "drawable-xxhdpi";
        }
        if (density >= 2.0) {
            return "drawable-xhdpi";
        }
        if (density >= 1.5) {
            return "drawable-hdpi";
        }

        return "drawable-mdpi";

    }

}
