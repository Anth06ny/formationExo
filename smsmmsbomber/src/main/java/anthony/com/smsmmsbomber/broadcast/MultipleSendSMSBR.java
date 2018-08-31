package anthony.com.smsmmsbomber.broadcast;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import org.apache.commons.lang3.StringUtils;

import anthony.com.smsmmsbomber.model.AnswerBean;
import anthony.com.smsmmsbomber.model.dao.AnswerDaoManager;
import anthony.com.smsmmsbomber.utils.LogUtils;
import anthony.com.smsmmsbomber.utils.SmsMmsManager;

/**
 * Created by amonteiro on 05/12/2014.
 */
public class MultipleSendSMSBR extends BroadcastReceiver {

    public static final String ACTION_MMS_SENT = "com.example.android.apis.os.MMS_SENT_ACTION";
    public static final String ACTION_MMS_RECEIVED =
            "com.example.android.apis.os.MMS_RECEIVED_ACTION";
    public static final String SENT_SMS_ACTION_NAME = "SMS_SENT";
    public static final String SENT_MMS_ACTION_NAME = "MMS_SENT";
    public static final String DELIVERED_SMS_ACTION_NAME = "SMS_DELIVERED";        //Recu
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String SMS_DELIVER = "android.provider.Telephony.SMS_DELIVER";

    @Override
    public void onReceive(Context context, Intent intent) {

        LogUtils.w("TAG_SMS", "MultipleSendSMSBR action=" + intent.getAction());
        AnswerBean answerBean = new AnswerBean();
        //On récupere le sms concerné s'il y en a un
        if (intent.getExtras() != null) {
            answerBean.setNumber(intent.getStringExtra(SmsMmsManager.NUMBER_EXTRA));
            answerBean.setOutbox(intent.getStringExtra(SmsMmsManager.SMS_OUT_BOX));
        }

        //Detect l'envoie de sms
        if (intent.getAction().equals(SENT_SMS_ACTION_NAME) || intent.getAction().equals(ACTION_MMS_SENT) || intent.getAction().equals(SENT_MMS_ACTION_NAME)) {
            answerBean.setSend(getResultCode() == Activity.RESULT_OK);
            LogUtils.w("TAG_SMS", getResultCode() == Activity.RESULT_OK ? "SMS envoyé" : "non envoyé");
        }
        //detect l'accuse reception d'un sms
        else if (intent.getAction().equals(DELIVERED_SMS_ACTION_NAME)) {
            LogUtils.w("TAG_SMS", getResultCode() == Activity.RESULT_OK ? "SMS recu" : "sms non recu");
        }
        //on recoit un sms
        else if (intent.getAction().equals(SMS_RECEIVED) || intent.getAction().equals(SMS_DELIVER)) {
            SmsMmsManager.receiveSMS(intent, answerBean);
            //Le message sera envoyé au serveur lors du prochaine tick
        }

        LogUtils.w("TAG_SMS", answerBean.toString());

        //On le sauvegarde s'il y a un numéro ou si il y a une réponse
        //POUR LE MOMENT ON NE GARDE PAS LES ACCUSé D'ENVOIE
        if (StringUtils.isNotBlank(answerBean.getNumber()) || answerBean.getSend() != null) {
            //On cherche parmi les sms envoyés celui qui a le même numéro
            AnswerDaoManager.save(answerBean);
        }
    }

    public static IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SENT_SMS_ACTION_NAME);
        intentFilter.addAction(DELIVERED_SMS_ACTION_NAME);
        intentFilter.addAction(ACTION_MMS_RECEIVED);
        intentFilter.addAction(ACTION_MMS_SENT);

        return intentFilter;
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
}
