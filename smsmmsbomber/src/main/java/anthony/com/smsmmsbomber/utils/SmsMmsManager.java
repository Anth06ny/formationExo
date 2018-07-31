package anthony.com.smsmmsbomber.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.klinker.android.send_message.Message;
import com.klinker.android.send_message.Transaction;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import anthony.com.smsmmsbomber.BuildConfig;
import anthony.com.smsmmsbomber.broadcast.MultipleSendSMSBR;
import anthony.com.smsmmsbomber.model.CampagneBean;
import anthony.com.smsmmsbomber.model.TelephoneBean;
import anthony.com.smsmmsbomber.model.dao.TelephoneDaoManager;

public class SmsMmsManager {

    public static final String NUMBER_EXTRA = "NUMBER_EXTRA";

    public static void sendSMS(final Context context, TelephoneBean telephoneBean, ArrayList<String> parts, boolean notifEnvoie, boolean accuserReception) {
        Intent intent;

        //Notif d'envoie
        ArrayList<PendingIntent> sendList = null;

        if (notifEnvoie) {
            //Notif d'envoie
            intent = new Intent(MultipleSendSMSBR.SENT_SMS_ACTION_NAME);
            intent.putExtra(NUMBER_EXTRA, telephoneBean.getNumero());
            sendList = new ArrayList<>();
            sendList.add(PendingIntent.getBroadcast(context, 0, intent, 0));
        }

        //Notif de reception
        ArrayList<PendingIntent> receiveList = null;
        if (accuserReception) {
            intent = new Intent(MultipleSendSMSBR.DELIVERED_SMS_ACTION_NAME);
            intent.putExtra(NUMBER_EXTRA, telephoneBean.getNumero());
            receiveList = new ArrayList<>();
            receiveList.add(PendingIntent.getBroadcast(context, 0, intent, 0));
        }

        SmsManager.getDefault().sendMultipartTextMessage(telephoneBean.getNumero(), null, parts, sendList, receiveList);
    }

    public static void sendMMS(Transaction transaction, CampagneBean campagneBean, TelephoneBean telephoneBean) {

        Message message = new Message(campagneBean.getMessage(), telephoneBean.getNumero());
        if (campagneBean.isVideo()) {
            message.addVideo(campagneBean.getVideoFile());
        }
        else {
            message.addImage(campagneBean.getBitmap());
        }
        transaction.sendNewMessage(message, Transaction.NO_THREAD_ID);
    }

    public static void receiveSMS(Intent intent) {
        //on tente de lire le message
        if (intent.getExtras() != null) {
            // get sms objects
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            if (pdus == null || pdus.length == 0) {
                Log.w("TAG_SMS", "SMSSentListener : pdus vide");
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
                Log.w("TAG_SMS", "MultipleSendSMSBR" + "\nExpediteur=" + expediteur + "\nmessage=" + message);
            }
            if (StringUtils.isNotBlank(expediteur)) {
                //ON cherche si on a déjà le numéro
                TelephoneBean telephoneBean = new TelephoneBean();
                telephoneBean.setNumero(expediteur);
                telephoneBean.setAnswer(message);
                telephoneBean.setSend(false); //ON met le champs à false pour indiquer qu'i n'a pas encore été envoyé au serveur
                TelephoneDaoManager.save(telephoneBean);
            }
        }
    }
}
