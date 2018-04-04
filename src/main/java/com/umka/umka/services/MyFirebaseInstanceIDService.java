package com.umka.umka.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


/**
 * Created by trablone on 2/4/17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("tr", "Refreshed token: " + refreshedToken);
        //c2lOJJ-OXCA:APA91bEzLr_CyjQGSBfcqa7qpuV1NbBZsY5o4vnzS_AOOtk6WrJZ-YRgg5Mqc6g3b5Z5YOP3IFNa0bxEtBQV4yg0trcjPjusb0wuwLGV8GUcJ0K7wbBzNDKMn4aZ-aS1tN0ZYWnk_tzg

        //UpdateFirebaseToken.update(this, refreshedToken);
    }

}
