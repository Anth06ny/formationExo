package anthony.com.smsmmsbomber.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import com.formation.utils.exceptions.TechnicalException;
import com.klinker.android.send_message.Message;
import com.klinker.android.send_message.Transaction;

import java.util.ArrayList;

import anthony.com.smsmmsbomber.broadcast.GestionAccuserReceptionBR;
import anthony.com.smsmmsbomber.model.wsbeans.getscheduleds.PhoneBean;

public class SmsMmsManager {

    public static final String NUMBER_EXTRA = "NUMBER_EXTRA";

    public static String outboxFormat(String code) {
        return "AN-OUT-" + code;
    }

    public static void sendSMS(final Context context, final PhoneBean phoneBean, boolean accuserReception) {

        if (phoneBean == null) {
            return;
        }
        Intent intent;
        ArrayList<String> parts = SmsManager.getDefault().divideMessage(phoneBean.getContent());
        //Notif d'envoie
        ArrayList<PendingIntent> sendList = new ArrayList<>();
        //Notif d'envoie
        sendList.add(PendingIntent.getBroadcast(context, 0, new Intent(GestionAccuserReceptionBR.SENT_SMS_ACTION_NAME), 0));

        //Notif de reception
        ArrayList<PendingIntent> receiveList = new ArrayList<>();
        if (accuserReception) {
            intent = new Intent(GestionAccuserReceptionBR.DELIVERED_SMS_ACTION_NAME);
            intent.putExtra(NUMBER_EXTRA, phoneBean.getNumber());
        }
        else {
            intent = new Intent();
        }
        receiveList.add(PendingIntent.getBroadcast(context, 0, intent, 0));

        //une instance par sms sinon cela mixte les numéros
        context.registerReceiver(new GestionAccuserReceptionBR(phoneBean), new IntentFilter(GestionAccuserReceptionBR.SENT_SMS_ACTION_NAME));

        SmsManager.getDefault().sendMultipartTextMessage(phoneBean.getNumber(), null, parts, sendList, receiveList);
    }

    public static void sendMMS(Context context, Transaction transaction, PhoneBean phoneBean, Bitmap bitmap) {

        Message message = new Message(phoneBean.getContent(), phoneBean.getNumber());
        //        if (campagneBean.isVideo()) {
        //            message.addVideo(campagneBean.getVideoFile());
        //        }
        //        else {
        message.addImage(bitmap);
        //        }
        Intent intent = new Intent(GestionAccuserReceptionBR.SENT_MMS_ACTION_NAME);
        transaction.setExplicitBroadcastForSentMms(intent);

        context.registerReceiver(new GestionAccuserReceptionBR(phoneBean), new IntentFilter(GestionAccuserReceptionBR.SENT_MMS_ACTION_NAME));

        transaction.sendNewMessage(message, Transaction.NO_THREAD_ID);
    }

    private static void testSimCard(Context c) throws TechnicalException {
        TelephonyManager telMgr = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT:
                throw new TechnicalException("Aucune carte Sim");
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                throw new TechnicalException("Carte Sim bloqué");
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                throw new TechnicalException("Carte Sim non dévérouillée");
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                throw new TechnicalException("Carte Sim Code Puk nécéssaire");
        }
    }
}
