package anthony.com.smsmmsbomber.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.formation.utils.DateUtils;

import java.util.Date;

import anthony.com.smsmmsbomber.MainActivity;
import anthony.com.smsmmsbomber.R;

/**
 * Created by Anthony on 14/03/2018.
 */

public class NotificationUtils {

    private static final String CHANNEL_ID = "ChannelId";
    private static final CharSequence CHANNEL_NAME = "Campagne";
    public static final int NOTIFICATION_ID = 3;

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
        channel.setDescription("Campagne");
        channel.enableLights(true);
        notificationManager.createNotificationChannel(channel);
    }

    public static void createInstantNotification(Context c, String message, Bitmap bitmap, Integer imageId) {
        //Envoyer la notification
        NotificationManagerCompat.from(c).notify(NOTIFICATION_ID, getNotif(c, message, bitmap, imageId));
    }

    public static Notification getNotif(Context c, String message, Bitmap bitmap, Integer imageId) {
        initChannel(c);

        Log.w("TAG_NOTIFICATION", "Notif:" + message);

        //Ce qui se passera quand on cliquera sur la notif
        Intent intent = new Intent(c, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(c, 28, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //La notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(c, CHANNEL_ID);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(c.getResources().getString(R.string.app_name) + " " + DateUtils.dateToString(new Date(), DateUtils.getFormat(c, DateUtils
                        .DATE_FORMAT
                        .HHmm)))
                .setContentText(message)
                .setContentIntent(pendingIntent)//le clic dessus
                .setDefaults(Notification.DEFAULT_ALL)
                .setBadgeIconType(Notification.BADGE_ICON_LARGE)

                .setLargeIcon(bitmap);

        if (imageId != null && imageId == R.mipmap.ic_error) {
            notificationBuilder.setColor(Color.RED);
        }
        else {
            notificationBuilder.setColor(Color.GREEN);
        }

        if (imageId != null) {
            notificationBuilder.setSmallIcon(imageId);
        }
        else {
            notificationBuilder.setSmallIcon(R.mipmap.ic_ok);
        }

        return notificationBuilder.build();
    }
}
