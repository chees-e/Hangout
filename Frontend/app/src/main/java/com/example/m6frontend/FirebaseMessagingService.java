package com.example.m6frontend;


import android.os.Looper;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private final String TAG = "MessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String message = remoteMessage.getNotification().getBody();
        handleMessage(message);
        Log.d(TAG, "Notification Received!");
    }

    private void handleMessage(String message) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {

                Toast.makeText(getBaseContext(), message,
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}


