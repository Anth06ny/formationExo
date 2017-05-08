package com.example.notificationexemple;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;

/**
 * Created by Anthony on 06/07/2016.
 */
public class NotificationPublisher extends BroadcastReceiver {

    public static final String NOTIFICATION_EXTRA = "NOTIFICATION_EXTRA";

    public void onReceive(Context context, Intent intent) {

        Notification notification = intent.getParcelableExtra(NOTIFICATION_EXTRA);

        //On demande au système d'afficher la notification
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(1, notification);
    }
}