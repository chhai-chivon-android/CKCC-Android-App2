package com.leapkh.ckcc;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class CkccFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String message = remoteMessage.getNotification().getBody();
        Log.d("ckcc", "Message received: " + message);
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        Log.d("ckcc", "onNewToken: " + s);
    }
}
