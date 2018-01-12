package com.example.notificationexemple;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

public class NotificationUtils {

    public static void envoyerNotification(Context context, String message) {

        Notification notification = creerNotification(context, message);

        //On demande au système d'afficher la notification
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(1, notification);
    }

    private static Notification creerNotification(Context context, String message) {
        //Action quand on clique sur la notification
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Image de droite sur la notification
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                R.mipmap.ic_launcher);

        //Création de la notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(icon)
                .setContentTitle("Le titre")
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)  //Permet d'enlever la notification quand on clique dessus
                .setPriority(Notification.PRIORITY_HIGH) // Permet un affichage de la notification à la récéption
                .setDefaults(Notification.DEFAULT_ALL);  //Affichage + son + vibration

        //Mettre une couleur au titre
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            builder.setColor(Color.CYAN)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        return builder.build();
    }

    public static void programmerNotification(Context context, String message, long delayInMillis) {

        Log.w("TAG", "Delay : " + delayInMillis);

        Notification notification = creerNotification(context, message);

        //On prépare un Broadcast et on met en paramètre la notification
        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_EXTRA, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //La date dans le futur
        long futureInMillis = SystemClock.elapsedRealtime() + delayInMillis;

        //Grâce à l'alarme Manager, on demande au système de déclancher ce Broadcast à l'heure représentée par futurInMillis.
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }
}
