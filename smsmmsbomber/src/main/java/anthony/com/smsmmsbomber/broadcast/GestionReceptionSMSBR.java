package anthony.com.smsmmsbomber.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsMessage;

import org.apache.commons.lang3.StringUtils;

import anthony.com.smsmmsbomber.BuildConfig;
import anthony.com.smsmmsbomber.model.AnswerBean;
import anthony.com.smsmmsbomber.model.dao.AnswerDaoManager;
import anthony.com.smsmmsbomber.utils.LogUtils;

public class GestionReceptionSMSBR extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String SMS_DELIVER = "android.provider.Telephony.SMS_DELIVER";

    @Override
    public void onReceive(Context context, Intent intent) {

        //on recoit un sms
        if (intent.getAction().equals(SMS_RECEIVED) || intent.getAction().equals(SMS_DELIVER)) {
            AnswerBean answerBean = new AnswerBean();
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
                    LogUtils.w("TAG_SMS", "GestionReceptionSMSBR : SMS recu : " + "\nExpediteur=" + expediteur + "\nmessage=" + message);
                }
                if (StringUtils.isNotBlank(expediteur)) {
                    //ON cherche si on a déjà le numéro
                    answerBean.setNumber(expediteur);
                    answerBean.setText(message);
                    AnswerDaoManager.save(answerBean);
                }
            }
            //Le message sera envoyé au serveur lors du prochaine tick
        }
    }

    public static IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SMS_RECEIVED);
        intentFilter.addAction(SMS_DELIVER);
        return intentFilter;
    }
}
