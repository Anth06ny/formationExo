package com.example.smsbroadcast.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.example.smsbroadcast.MainActivity;
import com.formation.utils.NotificationHelper;
import com.formation.utils.ToastUtils;

public class SMSReceiver extends BroadcastReceiver {

    /**
     * On implémente un Broadcast, c'est à dire, que c'est la methode que le systeme appelera lorsque l'événement
     * aura lieu.
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(final Context context, final Intent intent) {

        //mode avion
        if (intent.getAction().equals(Intent.ACTION_AIRPLANE_MODE_CHANGED)) {
            if (intent.getBooleanExtra("state", false)) {
                Toast.makeText(context, "ModeAvion désactivé", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(context, "ModeAvion activé", Toast.LENGTH_SHORT).show();
            }
        }
        //Sms
        else if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
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

                    String message = "Expediteur : " + phoneNumber + "\nMessage : " + messageBody;
                    ToastUtils.showToastOnUIThread(context, message, Toast.LENGTH_LONG);

                    NotificationHelper.createNotification(context, MainActivity.class);
                }
            }
        }
        //Wifi on / off
        else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
            String state = getStringState(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN));
            Toast.makeText(context, state, Toast.LENGTH_SHORT).show();
        }
    }

    public static String getStringState(int state) {
        switch (state) {
            case WifiManager.WIFI_STATE_DISABLING:
                return "WIFI_STATE_DISABLING";
            case WifiManager.WIFI_STATE_ENABLED:
                return "WIFI_STATE_ENABLED";
            case WifiManager.WIFI_STATE_DISABLED:
                return "WIFI_STATE_DISABLED";
            case WifiManager.WIFI_STATE_ENABLING:
                return "WIFI_STATE_ENABLING";
            default:
                return "WIFI_STATE_UNKNOWN";
        }
    }
}
