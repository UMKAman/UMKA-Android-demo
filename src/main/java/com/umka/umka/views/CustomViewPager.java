package com.umka.umka.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by trablone on 11/24/16.
 */

public class CustomViewPager extends ViewPager {

    private boolean enable = true;

    public boolean isEnable() {
        return enable;
    }
    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.isEnable() && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.isEnable() && super.onInterceptTouchEvent(event);
    }
}
