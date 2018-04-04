package com.umka.umka.classes;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by Evgeniy on 24.11.2017.
 */

public class InetCheackConection {

    private Context context;

    public InetCheackConection(Context context) {
        this.context = context;
    }

    public boolean isConnect(){
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Service.CONNECTIVITY_SERVICE);
        if (manager != null){
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info != null){
                if (info.getState() == NetworkInfo.State.CONNECTED){
                    return true;
                }else{
                    Toast.makeText(context, "Проверьте соединение с интернетом", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else {
                Toast.makeText(context, "Проверьте соединение с интернетом", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else {
            Toast.makeText(context, "Проверьте соединение с интернетом", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
