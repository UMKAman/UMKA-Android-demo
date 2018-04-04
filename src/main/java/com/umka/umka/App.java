package com.umka.umka;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.facebook.stetho.Stetho;
import com.umka.umka.classes.AuthImageDownloader;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;


/**
 * Created by trablone on 03.11.15.
 */
public class App extends MultiDexApplication {

    private static App INSTANCE = null;
    /** {@value} */
    public static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 5 * 1000; // milliseconds
    /** {@value} */
    public static final int DEFAULT_HTTP_READ_TIMEOUT = 20 * 1000; // milliseconds
    public App() {
        INSTANCE = this;
    }

    public static App getInstance() {

        if (INSTANCE == null) {
            INSTANCE = new App();
        }

        return INSTANCE;
    }

    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        initImageLoader(this);

    }

    public static void initImageLoader(Context context) {

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .resetViewBeforeLoading()
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .imageDownloader(new AuthImageDownloader(App.getInstance(), BaseImageDownloader.DEFAULT_HTTP_CONNECT_TIMEOUT, BaseImageDownloader.DEFAULT_HTTP_READ_TIMEOUT))
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(options)
                .build();

        ImageLoader.getInstance().init(config);
    }
}
