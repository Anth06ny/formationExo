package anthony.com.smsmmsbomber.utils;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.formation.utils.exceptions.TechnicalException;
import com.klinker.android.send_message.Message;
import com.klinker.android.send_message.Transaction;

import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;

import anthony.com.smsmmsbomber.Constants;
import anthony.com.smsmmsbomber.MyApplication;
import anthony.com.smsmmsbomber.broadcast.AccuserEnvoieMMSBR;
import anthony.com.smsmmsbomber.broadcast.AccuserReceptionSMSBR;
import anthony.com.smsmmsbomber.model.AnswerBean;
import anthony.com.smsmmsbomber.model.dao.AnswerDaoManager;
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
        AccuserEnvoieMMSBR.createGestionAccuserReceptionBR(context, phoneBean);
        Intent intennt = new Intent(AccuserEnvoieMMSBR.SENT_MMS_ACTION_NAME);
        transaction.setExplicitBroadcastForSentMms(intennt);

        Intent intennt2 = new Intent(AccuserEnvoieMMSBR.RECEIVED_MMS_ACTION_NAME);
        transaction.setExplicitBroadcastForDeliveredSms(intennt2);
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

    /* ---------------------------------
    // Gestion de la base de donnée des MMS
    // -------------------------------- */

    public static void printLast10MMS(Context c) {

        Uri uri = Uri.parse("content://mms/sent");
        Cursor query = c.getContentResolver().query(uri, null, null, null, "date DESC");

        String result = "";
        for (int i = 0; i < 5 && query.moveToNext(); i++) {
            for (int iColonne = 0; iColonne < query.getColumnCount(); iColonne++) {
                result += "-" + query.getColumnName(iColonne) + " : " + query.getString(iColonne) + "\n";
            }
            result += "-dest : " + getMMSSender(c, query.getString(query.getColumnIndex("_id"))) + "\n";
            result += "-------------------------------------\n";
        }
        query.close();

        Log.w("TAG_BDD", result);
    }

    /**
     * Supprime de la bdd des mms, les mms en erreur et les met dans la bdd de l'appli
     *
     * @param c
     */
    public static void saveAndDeleteAllMmsSentInError(Context c) {

        ContentResolver contentResolver = c.getContentResolver();
        final String[] projection = new String[]{"_id", "date", "m_id"};
        Uri uri = Uri.parse("content://mms/sent");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, Constants.DELAY_MMS_ERROR);   //On retire des minutes

        //Les message en erreur de plus de 5min
        String selection = "date < " + (calendar.getTimeInMillis() / 1000) + " AND " + Telephony.Mms.MESSAGE_ID + " IS null";
        Cursor query = contentResolver.query(uri, projection, selection, null, "date DESC");

        ArrayList<AnswerBean> list = new ArrayList<>();
        if (query != null) {
            while (query.moveToNext()) {

                long msgId = query.getLong(query.getColumnIndex("_id"));
                String number = getMMSSender(c, msgId + "");

                AnswerBean answerBean = null;
                if (StringUtils.isNotBlank(number)) {
                    AccuserEnvoieMMSBR br = AccuserEnvoieMMSBR.getBR(number);
                    if (br != null) {
                        //on a trouvé le br en attente
                        answerBean = br.getAnswerBean();
                        //On desactive le BR
                        AccuserEnvoieMMSBR.addBR(c, br, false);
                    }
                }
                if (answerBean == null) {
                    answerBean = new AnswerBean();
                    answerBean.setMms(true);
                    answerBean.setNumber(number);
                }

                answerBean.setMsgId(msgId);
                answerBean.setSend(false);

                list.add(answerBean);
            }
            query.close();
            for (AnswerBean answerBean : list) {
                //On efface et on met sur la prochaine vague
                deleteMMS(c, answerBean.getMsgId() + "");
                AnswerDaoManager.save(answerBean);
            }
        }
    }

    public static void deleteAllMMSInError() {
        Uri uri = Uri.parse("content://mms/sent");
        LogUtils.w("TAG_BDD", "delete : " + MyApplication.getInstance().getContentResolver().delete(uri, Telephony.Mms.MESSAGE_ID + " IS null", null));
    }

    public static String getMMSSender(Context c, String mmsId) {

        ContentResolver contentResolver = c.getContentResolver();
        final String[] projection = new String[]{"address"};

        //Detail du mms
        Uri uri = Uri.parse(MessageFormat.format("content://mms/{0}/addr", mmsId));
        String selection = "msg_id=" + mmsId;
        Cursor query = contentResolver.query(uri, projection, selection, null, "_id DESC");

        //On ne récupère que le 1er, le 2eme correspond à l'expediteur
        String res = null;
        if (query.moveToFirst()) {
            res = query.getString(query.getColumnIndex("address"));
        }
        query.close();

        return res;
    }

    public static void deleteMMS(Context c, String mmsId) {

        Uri uri = Uri.parse("content://mms/sent");
        LogUtils.w("TAG_BDD", "delete : " + c.getContentResolver().delete(uri, "_id = " + mmsId, null));
    }
}
