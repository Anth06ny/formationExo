package com.example.sendsms.broadcast;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.telephony.SmsManager;

import com.example.sendsms.MmsMessagingDemo;
import com.example.sendsms.MyApplication;
import com.example.sendsms.TelephoneBean;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by amonteiro on 05/12/2014.
 */
public class MultipleSendSMSBR extends BroadcastReceiver {

    private static final String ACTION_MMS_SENT = "com.example.android.apis.os.MMS_SENT_ACTION";
    private static final String ACTION_MMS_RECEIVED =
            "com.example.android.apis.os.MMS_RECEIVED_ACTION";
    public static final String SENT_SMS_ACTION_NAME = "SMS_SENT";

    private ArrayList<TelephoneBean> telephoneBeans;

    public MultipleSendSMSBR(ArrayList<TelephoneBean> telephoneBeans) {
        this.telephoneBeans = telephoneBeans;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //Detect l'envoie de sms
        if (intent.getAction().equals(SENT_SMS_ACTION_NAME)) {

            switch (getResultCode()) {
                case Activity.RESULT_OK: // Sms sent
                    MyApplication.getBus().post(true);
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE: // generic failure
                case SmsManager.RESULT_ERROR_NO_SERVICE: // No service
                case SmsManager.RESULT_ERROR_NULL_PDU: // null pdu
                case SmsManager.RESULT_ERROR_RADIO_OFF: //Radio off
                    MyApplication.getBus().post(false);
                    break;
            }
        }
    }

    /**
     * Methode permetant de tester si le device peut envoyer des sms.
     *
     * @param context
     * @return
     */
    public static boolean canSendSMS(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
    }

    public static void sendSMS(final Context context, ArrayList<TelephoneBean> phonesNumber, String message) {

        SmsManager sms = SmsManager.getDefault();
        //On divise notre messege en plusieurs SMS en fonction du format
        ArrayList<String> parts = sms.divideMessage(message);

        for (TelephoneBean telephoneBean : phonesNumber) {
            Intent intent = new Intent(SENT_SMS_ACTION_NAME);
            intent.putExtra("phone", telephoneBean);
            PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, intent, 0);
            ArrayList<PendingIntent> sendList = new ArrayList<>();
            sendList.add(sentPI);

            sms.sendMultipartTextMessage(telephoneBean.getNumero(), null, parts, sendList, null);
        }
    }

    public static void sendMMS(final Context context, String phoneNumber, String messageText, Uri uri) throws Exception {

        final String fileName = "send." + String.valueOf(Math.abs(new Random().nextLong())) + ".dat";
        File mSendFile = new File(context.getCacheDir(), fileName);
        final byte[] pdu = MmsMessagingDemo.buildPdu(context, phoneNumber, "hello", messageText);
        FileOutputStream writer = null;
        try {
            writer = new FileOutputStream(mSendFile);
            writer.write(pdu);
        }
        finally {
            if (writer != null) {
                writer.close();
            }
        }
        Uri writerUri = (new Uri.Builder())
                .authority("com.example.android.apis.os.MmsFileProvider")
                .path(fileName)
                .scheme(ContentResolver.SCHEME_CONTENT)
                .build();

        SmsManager sms = SmsManager.getDefault();
        sms.sendMultimediaMessage(context, writerUri, null, null, null);
    }



}
