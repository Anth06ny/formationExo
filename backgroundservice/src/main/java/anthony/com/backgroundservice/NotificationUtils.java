package anthony.com.backgroundservice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

/**
 * Created by Anthony on 14/03/2018.
 */

public class NotificationUtils {

    private static final String CHANNEL_ID = "ChannelId";
    private static final CharSequence CHANNEL_NAME = "Commandes";

    /**
     * Création du channel
     */
    private static void initChannel(Context c) {

        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        NotificationManager notificationManager =
                (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Commandes");
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        notificationManager.createNotificationChannel(channel);
    }

    public static Notification createInstantNotification(Context c, String message) {

        initChannel(c);

        //Ce qui se passera quand on cliquera sur la notif
        Intent intent = new Intent(c, ServicePiloteActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(c, 28, intent, PendingIntent.FLAG_ONE_SHOT);

        //La notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(c, CHANNEL_ID);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Le titre")
                .setContentText(message)
                .setContentIntent(pendingIntent)//le clic dessus
                .build();

        //Envoyer la notification
        NotificationManagerCompat ncm = NotificationManagerCompat.from(c);

        Notification notif = notificationBuilder.build();

        //ENVOIE
        ncm.notify(29, notif);

        return notif;
    }

    public static Notification getNotif(Context c, String message) {
        initChannel(c);

        //Ce qui se passera quand on cliquera sur la notif
        Intent intent = new Intent(c, ServicePiloteActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(c, 28, intent, PendingIntent.FLAG_ONE_SHOT);

        //La notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(c, CHANNEL_ID);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Le titre")
                .setContentText(message)
                .setContentIntent(pendingIntent)//le clic dessus
                .build();

        //Envoyer la notification
        NotificationManagerCompat ncm = NotificationManagerCompat.from(c);

        return notificationBuilder.build();
    }
}
