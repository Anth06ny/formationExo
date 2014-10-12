package com.example.smsbroadcast;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NotificationHelper {

    private static final int NOTIFICATION_REQUEST_CODE = 13; //au hasard
    private static final int NOTIFICATION_ID = 14; //au hasard

    //UNiquemeent pour JellyBean
    public static void createNotification(final Context context) {

        final NotificationManager mNotification = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        final Intent launchNotifiactionIntent = new Intent(context, MainActivity.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, NOTIFICATION_REQUEST_CODE, launchNotifiactionIntent,
                PendingIntent.FLAG_ONE_SHOT);

        final Notification.Builder builder = new Notification.Builder(context).setWhen(System.currentTimeMillis()).setTicker("Ticker")
                .setSmallIcon(R.drawable.ic_launcher).setContentTitle("ContentTitle").setContentText("ContentText").setContentIntent(pendingIntent);

        mNotification.notify(NOTIFICATION_ID, builder.build());
    }
}
