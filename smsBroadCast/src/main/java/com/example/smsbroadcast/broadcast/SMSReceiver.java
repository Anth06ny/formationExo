package com.example.smsbroadcast.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.example.smsbroadcast.MainActivity;
import com.formation.utils.NotificationHelper;

public class SMSReceiver extends BroadcastReceiver {

    private final String ACTION_RECEIVE_SMS = "android.provider.Telephony.SMS_RECEIVED";

    /**
     * On implémente un Broadcast, c'est à dire, que c'est la methode que le systeme appelera lorsque l'événement
     * aura lieu.
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(final Context context, final Intent intent) {

        if (intent.getAction().equals(ACTION_RECEIVE_SMS)) {
            final Bundle bundle = intent.getExtras();
            if (bundle != null) {
                final Object[] pdus = (Object[]) bundle.get("pdus");

                final SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                if (messages.length > -1) {
                    final String messageBody = messages[0].getMessageBody();
                    final String phoneNumber = messages[0].getDisplayOriginatingAddress();

                    Toast.makeText(context, "Expediteur : " + phoneNumber, Toast.LENGTH_LONG).show();
                    Toast.makeText(context, "Message : " + messageBody, Toast.LENGTH_LONG).show();

                    NotificationHelper.createNotification(context, MainActivity.class);
                }
            }
        }
    }
}
