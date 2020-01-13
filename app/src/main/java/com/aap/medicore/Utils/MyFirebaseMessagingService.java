package com.aap.medicore.Utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.aap.medicore.Activities.Splash;
import com.aap.medicore.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessagingServ";
    private String messageBody, messageSound, messageTitle;
    private static final String CHANNEL_ID = "com.aap.medicore.Utils";


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().size() > 0) {

            Map<String, String> message = remoteMessage.getData();
            for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key.equals("body"))
                    messageBody = entry.getValue();
                else if (key.equals("sound"))
                    messageSound = entry.getValue();
                else if (key.equals("title"))
                    messageTitle = entry.getValue();
            }
        } else if (remoteMessage.getNotification() != null) {
            messageBody = remoteMessage.getNotification().getBody().toString();
            messageTitle = remoteMessage.getNotification().getTitle().toString();
        }


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        CharSequence name = getString(R.string.channel_name);
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    CHANNEL_ID, name, importance);

            assert notificationManager != null;
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);
        //NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, Splash.class), PendingIntent.FLAG_UPDATE_CURRENT);


        mBuilder.setContentIntent(contentIntent);
        notificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        new TinyDB(getApplicationContext()).putString(Constants.tokenFCM, token);
    }


}
