package com.umka.umka.classes;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.umka.umka.activity.BaseActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * Created by trablone on 12/11/16.
 */

public class Utils {

    public static void checkLogin(final Context context, LoginListener listener){
        final BaseActivity activity = (BaseActivity)context;
        if (activity.isLoginUser()){
            listener.onSuccess();
        }else {
            new AlertDialog.Builder(context)
                    .setTitle(com.umka.umka.R.string.app_name)
                    .setMessage("Вы не авторизированы! Пожалуйста авторизируйтесь.")
                    .setPositiveButton("Авторизироваться", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            activity.startLogin();
                        }
                    })
                    .setNegativeButton("Отменить", null)
                    .show();
        }

    }

    public static boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if(status != ConnectionResult.SUCCESS) {
            if(googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }
        return true;
    }
    public static int dpToPx(Context context, int px) {
        return px * (int) context.getResources().getDisplayMetrics().density;
    }

    public static int pxToDp(Context context, int dp) {
        return dp / (int)context.getResources().getDisplayMetrics().density;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public static DisplayImageOptions getImageOptions(int ressId) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageOnLoading(ressId)
                .showImageForEmptyUri(ressId)
                .showImageOnFail(ressId)
                .resetViewBeforeLoading()
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        return options;
    }

    public static Bitmap makeRoundedBitmap(Bitmap bitmap) {
        Bitmap bmp;
        bmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_4444);
        int color = 0xff424242;
        int circleRadius;
        int height = bmp.getHeight();
        int width = bmp.getWidth();

        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, width, height);

        if (width <= height) {
            circleRadius = width / 2;
        } else
            circleRadius = height / 2;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(width / 2, height / 2, circleRadius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return bmp;
    }

    public static String getParsePhone(String phone) {

        if (phone == null)
            return null;
        if (phone.length() < 12){
            phone = "+" + phone;
        }
        if (phone.length() > 12) {
            phone = phone.substring(0, 12);
        }

        String[] phoneArray = phone.split("");
        int length = phoneArray.length;

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(phoneArray[i]);
            if (i == 2) {
                builder.append(" (");
            }
            if (i == 5) {
                builder.append(") ");
            }
            if (i == 8) {
                builder.append(" ");
            }
            if (i == 10){
                builder.append(" ");
            }
        }
        return builder.toString();
    }

}
