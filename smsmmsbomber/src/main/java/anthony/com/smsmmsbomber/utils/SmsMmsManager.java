package anthony.com.smsmmsbomber.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import com.formation.utils.exceptions.TechnicalException;
import com.klinker.android.send_message.Message;
import com.klinker.android.send_message.Transaction;

import java.util.ArrayList;

import anthony.com.smsmmsbomber.broadcast.AccuserReceptionMMSBR;
import anthony.com.smsmmsbomber.broadcast.AccuserReceptionSMSBR;
import anthony.com.smsmmsbomber.model.wsbeans.getscheduleds.PhoneBean;

public class SmsMmsManager {

    public static final String NUMBER_EXTRA = "NUMBER_EXTRA";

    public static void sendSMS(final Context context, final PhoneBean phoneBean, boolean accuserReception) {

        if (phoneBean == null) {
            return;
        }
        Intent intent;
        ArrayList<String> parts = SmsManager.getDefault().divideMessage(phoneBean.getContent());
        //Notif d'envoie
        ArrayList<PendingIntent> sendList = new ArrayList<>();
        //Notif d'envoie
        sendList.add(PendingIntent.getBroadcast(context, 0, new Intent(AccuserReceptionSMSBR.SENT_SMS_ACTION_NAME), 0));

        //Notif de reception
        ArrayList<PendingIntent> receiveList = new ArrayList<>();
        if (accuserReception) {
            intent = new Intent(AccuserReceptionSMSBR.DELIVERED_SMS_ACTION_NAME);
            intent.putExtra(NUMBER_EXTRA, phoneBean.getNumber());
        }
        else {
            intent = new Intent();
        }
        receiveList.add(PendingIntent.getBroadcast(context, 0, intent, 0));

        //une instance par sms sinon cela mixte les numéros
        AccuserReceptionSMSBR.createGestionAccuserReceptionBR(context, phoneBean);

        SmsManager.getDefault().sendMultipartTextMessage(phoneBean.getNumber(), null, parts, sendList, receiveList);
    }

    public static void sendMMS(Context context, Transaction transaction, PhoneBean phoneBean, Bitmap bitmap) {

        Message message = new Message(phoneBean.getContent(), phoneBean.getNumber());
        message.addImage(bitmap);
        AccuserReceptionMMSBR.createGestionAccuserReceptionBR(context, phoneBean);
        Intent intennt = new Intent(AccuserReceptionMMSBR.SENT_MMS_ACTION_NAME);
        transaction.setExplicitBroadcastForSentMms(intennt);
        transaction.sendNewMessage(message, Transaction.NO_THREAD_ID);
    }

    public static void testSimCard(Context c) throws TechnicalException {
        TelephonyManager telMgr = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);

        switch (telMgr.getSimState()) {
            case TelephonyManager.SIM_STATE_READY:
                return;
            case TelephonyManager.SIM_STATE_UNKNOWN:
                throw new TechnicalException("SIM_STATE_UNKNOWN : Carte Sim en cours de changement d'etat : ");
            case TelephonyManager.SIM_STATE_ABSENT:
                throw new TechnicalException("SIM_STATE_ABSENT : Aucune carte Sim");
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                throw new TechnicalException("SIM_STATE_PIN_REQUIRED : Carte Sim non dévérouillée (Code Pin)");
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                throw new TechnicalException("SIM_STATE_PUK_REQUIRED : Carte Sim non dévérouillée (Code Puk)");
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                throw new TechnicalException("SIM_STATE_NETWORK_LOCKED : Carte Sim non dévérouillée (Code reseau)");
            case TelephonyManager.SIM_STATE_NOT_READY:
                throw new TechnicalException("SIM_STATE_NOT_READY : Carte non préte");
            case TelephonyManager.SIM_STATE_PERM_DISABLED:
                throw new TechnicalException("SIM_STATE_PERM_DISABLED : Carte désactivé de manière permanente");
            case TelephonyManager.SIM_STATE_CARD_IO_ERROR:
                throw new TechnicalException("SIM_STATE_CARD_IO_ERROR : Carte en erreur");
            case TelephonyManager.SIM_STATE_CARD_RESTRICTED:
                throw new TechnicalException("SIM_STATE_CARD_RESTRICTED : Carte restreinte");
            default:
                throw new TechnicalException(telMgr.getSimState() + " : état inconnu pour la carte SIM");
        }
    }
}
