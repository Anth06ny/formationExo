package anthony.com.smsmmsbomber.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.klinker.android.send_message.Message;
import com.klinker.android.send_message.Transaction;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import anthony.com.smsmmsbomber.BuildConfig;
import anthony.com.smsmmsbomber.broadcast.MultipleSendSMSBR;
import anthony.com.smsmmsbomber.model.AnswerBean;
import anthony.com.smsmmsbomber.model.wsbeans.getscheduleds.PhoneBean;

public class SmsMmsManager {

    public static final String NUMBER_EXTRA = "NUMBER_EXTRA";

    public static void sendSMS(final Context context, PhoneBean phoneBean, boolean notifEnvoie, boolean accuserReception) {
        Intent intent;
        ArrayList<String> parts = SmsManager.getDefault().divideMessage(phoneBean.getContent());

        //Notif d'envoie
        ArrayList<PendingIntent> sendList = null;

        if (notifEnvoie) {
            //Notif d'envoie
            intent = new Intent(MultipleSendSMSBR.SENT_SMS_ACTION_NAME);
            intent.putExtra(NUMBER_EXTRA, phoneBean.getNumber());
            sendList = new ArrayList<>();
            sendList.add(PendingIntent.getBroadcast(context, 0, intent, 0));
        }

        //Notif de reception
        ArrayList<PendingIntent> receiveList = null;
        if (accuserReception) {
            intent = new Intent(MultipleSendSMSBR.DELIVERED_SMS_ACTION_NAME);
            intent.putExtra(NUMBER_EXTRA, phoneBean.getNumber());
            receiveList = new ArrayList<>();
            receiveList.add(PendingIntent.getBroadcast(context, 0, intent, 0));
        }

        SmsManager.getDefault().sendMultipartTextMessage(phoneBean.getNumber(), null, parts, sendList, receiveList);
    }

    public static void sendMMS(Transaction transaction, PhoneBean phoneBean, Bitmap bitmap) {

        Message message = new Message(phoneBean.getContent(), phoneBean.getNumber());
        //        if (campagneBean.isVideo()) {
        //            message.addVideo(campagneBean.getVideoFile());
        //        }
        //        else {
        message.addImage(bitmap);
        //        }
        transaction.sendNewMessage(message, Transaction.NO_THREAD_ID);
    }

    public static void receiveSMS(Intent intent, AnswerBean answerBean) {
        //on tente de lire le message
        if (intent.getExtras() != null) {
            // get sms objects
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            if (pdus == null || pdus.length == 0) {
                LogUtils.w("TAG_SMS", "SMSSentListener : pdus vide");
                return;
            }
            // large message might be broken into many
            SmsMessage[] messages = new SmsMessage[pdus.length];
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < pdus.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                sb.append(messages[i].getMessageBody());
            }
            String expediteur = messages[0].getOriginatingAddress();
            String message = sb.toString();
            if (BuildConfig.DEBUG) {
                LogUtils.w("TAG_SMS", "MultipleSendSMSBR : SMS sauvegardé" + "\nExpediteur=" + expediteur + "\nmessage=" + message);
            }
            if (StringUtils.isNotBlank(expediteur)) {
                //ON cherche si on a déjà le numéro
                answerBean.setNumber(expediteur);
                answerBean.setText(message);
            }
        }
    }
}
