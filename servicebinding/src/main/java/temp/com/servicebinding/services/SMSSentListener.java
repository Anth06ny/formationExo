package temp.com.servicebinding.services;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by amonteiro on 05/12/2014.
 */
public class SMSSentListener extends BroadcastReceiver {

    public static final String SENT_SMS_ACTION_NAME = "SMS_SENT";
    public static final String DELIVERED_SMS_ACTION_NAME = "SMS_DELIVERED";

    @Override
    public void onReceive(Context context, Intent intent) {
        //Detect l'envoie de sms
        if (intent.getAction().equals(SENT_SMS_ACTION_NAME)) {
            switch (getResultCode()) {
                case Activity.RESULT_OK: // Sms sent
                    Toast.makeText(context, "Message sent ", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE: // generic failure
                    Toast.makeText(context, "Failed to send message", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE: // No service
                    Toast.makeText(context, "Failed to send message because no service", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU: // null pdu
                    Toast.makeText(context, "Failed to send message, no pdu", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF: //Radio off
                    Toast.makeText(context, "Failed to send message because radio is turned off", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        //detect la reception d'un sms
        else if (intent.getAction().equals(DELIVERED_SMS_ACTION_NAME)) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(context, "SMS delivered", Toast.LENGTH_SHORT).show();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(context, "SMS not delivered", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    }

    /**
     * Methode permetant de tester si le device peut envoyer des sms.
     * @param context
     * @return
     */
    public static boolean canSendSMS(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
    }

    public static void sendSMS(final Context context, String phoneNumber, String message) {

        if (!canSendSMS(context)) {
            Toast.makeText(context, "Le device ne permet pas l'envoie de SMS", Toast.LENGTH_LONG).show();
            return;
        }

        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT_SMS_ACTION_NAME), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, new Intent(DELIVERED_SMS_ACTION_NAME), 0);

        final SMSSentListener smsUtils = new SMSSentListener();
        //on s'enregistre à l'envoie du sms
        context.registerReceiver(smsUtils, new IntentFilter(SENT_SMS_ACTION_NAME));
        // Receiver for Delivered SMS.
        context.registerReceiver(smsUtils, new IntentFilter(DELIVERED_SMS_ACTION_NAME));

        SmsManager sms = SmsManager.getDefault();
        //On divise notre messege en plusieurs SMS en fonction du format
        ArrayList<String> parts = sms.divideMessage(message);

        ArrayList<PendingIntent> sendList = new ArrayList<>();
        sendList.add(sentPI);

        ArrayList<PendingIntent> deliverList = new ArrayList<>();
        deliverList.add(deliveredPI);

        sms.sendMultipartTextMessage(phoneNumber, null, parts, sendList, deliverList);

        //Penser à se desenregistrer
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                context.unregisterReceiver(smsUtils);
            }
        }, 10000);//on se desinscrit au bout de 10s

    }
}
