package anthony.com.smsmmsbomber.broadcast;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.util.Log;

import anthony.com.smsmmsbomber.BuildConfig;
import anthony.com.smsmmsbomber.model.TelephoneBean;
import anthony.com.smsmmsbomber.model.dao.TelephoneDaoManager;
import anthony.com.smsmmsbomber.utils.SmsMmsManager;

/**
 * Created by amonteiro on 05/12/2014.
 */
public class MultipleSendSMSBR extends BroadcastReceiver {

    public static final String ACTION_MMS_SENT = "com.example.android.apis.os.MMS_SENT_ACTION";
    public static final String ACTION_MMS_RECEIVED =
            "com.example.android.apis.os.MMS_RECEIVED_ACTION";
    public static final String SENT_SMS_ACTION_NAME = "SMS_SENT";
    public static final String DELIVERED_SMS_ACTION_NAME = "SMS_DELIVERED";        //Recu
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.w("TAG_SMS", "MultipleSendSMSBR action=" + intent.getAction());
        TelephoneBean telephoneBean = null;
        //On récupere le sms concerné s'il y en a un
        if (intent.getExtras() != null) {
            long idExtra = intent.getLongExtra("id", 0);
            Log.w("TAG_SMS", "id=" + idExtra);
            if (idExtra > 0) {
                telephoneBean = TelephoneDaoManager.getTelephone(idExtra);
            }
        }

        //Detect l'envoie de sms
        if (intent.getAction().equals(SENT_SMS_ACTION_NAME)) {
            if (telephoneBean != null) {
                telephoneBean.setSend(getResultCode() == Activity.RESULT_OK);
            }
            if (BuildConfig.DEBUG) {
                Log.w("TAG_SMS", getResultCode() == Activity.RESULT_OK ? "SMS envoyé" : "non envoyé");
            }
        }
        //detect l'accuse reception d'un sms
        else if (intent.getAction().equals(DELIVERED_SMS_ACTION_NAME)) {
            if (telephoneBean != null) {
                telephoneBean.setReceived(getResultCode() == Activity.RESULT_OK);
            }
            if (BuildConfig.DEBUG) {
                Log.w("TAG_SMS", getResultCode() == Activity.RESULT_OK ? "SMS recu" : "sms non recu");
            }
        }
        //on recoit un sms
        else if (intent.getAction().equals(SMS_RECEIVED)) {
            SmsMmsManager.receiveSMS(intent);
            //Le message sera envoyé au serveur lors du prochaine tick
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
