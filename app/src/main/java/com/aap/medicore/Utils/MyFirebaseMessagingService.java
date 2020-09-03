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
import com.aap.medicore.DatabaseHandler.DatabaseHandler;
import com.aap.medicore.Models.InboxMessage;
import com.aap.medicore.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessagingServ";
    private String messageBody, messageSound, messageTitle;
    private InboxMessageRepo inboxMessageRepo;
    private NotificationsHandler notificationsHandler;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        inboxMessageRepo = InboxMessageRepo.getInstance(getApplicationContext());
        notificationsHandler = NotificationsHandler.getInstance(getApplicationContext());
        //inbox message
        if (remoteMessage.getNotification() == null) {
            Map<String, String> data = remoteMessage.getData();
            if (!data.isEmpty() && data.containsKey(Constants.message_title) && data.containsKey(Constants.message_content)) {
                InboxMessage msg = inboxMessageRepo.createNewInboxMessage(remoteMessage);

                Intent intent = new Intent(Constants.INBOX_MESSAGE_EVENT);
                intent.setPackage(getPackageName());
                getApplicationContext().sendBroadcast(intent);
            }
        } else
        //notification
        {
            messageBody = remoteMessage.getNotification().getBody().toString();
            messageTitle = remoteMessage.getNotification().getTitle().toString();
            notificationsHandler.publishNotification(messageTitle, messageBody);
        }
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
