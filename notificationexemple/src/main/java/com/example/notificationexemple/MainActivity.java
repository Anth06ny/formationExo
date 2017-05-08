package com.example.notificationexemple;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private Calendar calendar;

    private Button button, bt_timepicker, bt_date_picker, bt_notif_date, bt_alert_dialog;
    private Button bt_notif_with_button;
    private TextView tv_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        button = (Button) findViewById(R.id.button);
        bt_notif_date = (Button) findViewById(R.id.bt_notif_date);
        tv_date = (TextView) findViewById(R.id.tv_date);
        bt_timepicker = (Button) findViewById(R.id.bt_timepicker);
        bt_date_picker = (Button) findViewById(R.id.bt_date_picker);
        bt_alert_dialog = (Button) findViewById(R.id.bt_alert_dialog);
        bt_notif_with_button = (Button) findViewById(R.id.bt_notif_with_button);
        button.setOnClickListener(this);
        bt_timepicker.setOnClickListener(this);
        bt_date_picker.setOnClickListener(this);
        bt_notif_date.setOnClickListener(this);
        bt_alert_dialog.setOnClickListener(this);
        bt_notif_with_button.setOnClickListener(this);

        refreshScreen();
    }

    @Override
    public void onClick(View v) {
        if (v == bt_alert_dialog) {
            //Préparation de la fenêtre
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            //Message
            alertDialogBuilder.setMessage("Mon message");
            //titre
            alertDialogBuilder.setTitle("Mon titre");
            //bouton ok
            alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //Affiche un toast apres le click sur le bouton ok
                    Toast.makeText(MainActivity.this, "Click sur ok", Toast.LENGTH_SHORT).show();
                }
            });
            //Icone
            alertDialogBuilder.setIcon(R.mipmap.ic_launcher);
            //Afficher la fenêtre
            alertDialogBuilder.show();
        }
        else if (v == bt_timepicker) {
            //(Context, callback, heure, minute, 24h format)
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, this, 14, 33, true);
            timePickerDialog.show();
        }
        else if (v == button) {
            scheduleNotification("Ma notification dans 10 seconde", 10000);
        }
        else if (v == bt_date_picker) {

            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, this,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        }
        else if (v == bt_notif_date) {

            scheduleNotification("Ma notification programmée", calendar.getTimeInMillis() - System.currentTimeMillis());
        }

        else if (v == bt_notif_with_button) {
            scheduleNotificationWithButton("Mon message");
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Toast.makeText(MainActivity.this, hourOfDay + " : " + minute, Toast.LENGTH_SHORT).show();

        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

        refreshScreen();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Toast.makeText(MainActivity.this, year + "/" + monthOfYear + "/" + dayOfMonth, Toast.LENGTH_SHORT).show();

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        refreshScreen();
    }

    private void scheduleNotification(String message, long delay) {

        Log.w("TAG", "Delay : " + delay);

        //La notification
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Scheduled Notification");
        builder.setContentText(message);
        builder.setSmallIcon(R.mipmap.ic_launcher);

        //Redirection vers le broadcast
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, builder.build());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //La dans le futur
        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    public static void createInstantNotification(Context context, String message) {

        int notifReCode = 1;

        //Ce qui se passera quand on cliquera sur la notif
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_ONE_SHOT);

        //La notification
        Notification notification = new Notification.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)  //Permet d'enlever la notification quand on clique dessus
                .setContentTitle("Le titre").setContentText(message)
                .setContentIntent(pendingIntent).build();

        //Envoyer la notification
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        //ENVOIE
        notificationManager.notify(1, notification);
    }

    private void scheduleNotificationWithButton(String message) {

        int notifReCode = 1;

        //Ce qui se passera quand on cliquera sur le bouton
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);

        //Le bouton
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, "Go", pendingIntent).build();

        //La notification
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("Retourner sur l'application ?")
                .setContentTitle("Un truc fou vient d'arriver")
                .addAction(action) //ajout du bouton à la notification
                .build();

        //Envoyer la notification
        NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        //ENVOIE
        notificationManager.notify(1, notification);
    }

    private void refreshScreen() {

        String result = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(calendar.getTime());

        tv_date.setText(result);
        bt_notif_date.setText("Send me a notification at " + result);
    }
}
