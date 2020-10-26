package com.example.m6frontend;

import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.media.RingtoneManager;
import android.os.Looper;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import com.google.firebase.messaging.RemoteMessage;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;



public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private final String TAG = "MessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        handleMessage(remoteMessage);
        Log.d(TAG, "Notification Received!");
    }

    private void handleMessage(RemoteMessage remoteMessage) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                // TODO: handle notifications
                Toast.makeText(getBaseContext(), getString(R.string.handle_notification_now),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}


