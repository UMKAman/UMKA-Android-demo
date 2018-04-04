package com.umka.umka.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.umka.umka.R;
import com.umka.umka.activity.MessagesActivity;
import com.umka.umka.classes.HttpClient;
import com.umka.umka.classes.Utils;
import com.umka.umka.database.DbHelper;
import com.umka.umka.database.UserHelper;
import com.umka.umka.model.Message;
import com.umka.umka.model.Profile;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


public class MyGcmListenerService extends FirebaseMessagingService {

    public void onMessageReceived(RemoteMessage message){
        Log.e("tr", "From: " + message.getFrom());
        Log.e("tr", "data: " + message.getData());

        UserHelper helper = new UserHelper(new DbHelper(this).getDataBase());
        Profile user = helper.getUser();
        if (message.getData().size() > 0) {
            Map<String, String> map = message.getData();
            try {
                final Message m = new Message(new JSONObject(map.get("message")));
                Log.e("tr", "user_id: " + user.id + " : m: " + m.user_id);
                if (m.user_id != user.id){
                    downloadAvatar(m);

                }

                //sendNotification(MyGcmListenerService.this, m, BitmapFactory.decodeResource(getResources(), R.drawable.image_profile_no_avatar));

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("tr", "e: notif: " + e.getMessage());
            }
        }
    }


    private void downloadAvatar(final Message message){
        ImageLoader.getInstance().loadImage(HttpClient.BASE_URL + message.user_avatar, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                sendNotification(MyGcmListenerService.this, message, BitmapFactory.decodeResource(getResources(), R.drawable.image_profile_no_avatar));

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                sendNotification(MyGcmListenerService.this, message, Utils.makeRoundedBitmap(loadedImage));
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }
    private void sendNotification(Context context, Message message, Bitmap bitmap) {
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, MessagesActivity.class);
        intent.putExtra("chat_id", message.chat_id);
        intent.putExtra("title", message.user_name);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(message.user_name);
        String m = message.message;
        if (TextUtils.isEmpty(message.message) && !TextUtils.isEmpty(message.image)){
            m = getResources().getString(R.string.image);
        }
        inboxStyle.addLine(m);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_umka)
                        .setLargeIcon(bitmap)
                        .setAutoCancel(true)
                        .setContentTitle(message.user_name)
                        .setStyle(inboxStyle)
                        .setContentText(m)
                        .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(message.chat_id, mBuilder.build());
        Intent b = new Intent();
        b.setAction("com.android.umka.message");
        b.putExtra("message", message);
        sendBroadcast(b);
    }
}
