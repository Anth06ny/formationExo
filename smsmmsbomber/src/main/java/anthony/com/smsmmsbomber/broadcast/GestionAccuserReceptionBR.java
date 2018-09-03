package anthony.com.smsmmsbomber.broadcast;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import anthony.com.smsmmsbomber.model.AnswerBean;
import anthony.com.smsmmsbomber.model.dao.AnswerDaoManager;
import anthony.com.smsmmsbomber.model.wsbeans.getscheduleds.PhoneBean;
import anthony.com.smsmmsbomber.utils.LogUtils;
import anthony.com.smsmmsbomber.utils.SmsMmsManager;

/**
 * Created by amonteiro on 05/12/2014.
 */
public class GestionAccuserReceptionBR extends BroadcastReceiver {

    public static final String SENT_SMS_ACTION_NAME = "SMS_SENT";
    public static final String SENT_MMS_ACTION_NAME = "MMS_SENT";
    public static final String DELIVERED_SMS_ACTION_NAME = "SMS_DELIVERED";

    private PhoneBean phoneBean;

    public GestionAccuserReceptionBR() {

    }

    public GestionAccuserReceptionBR(PhoneBean phoneBean) {
        this.phoneBean = phoneBean;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        LogUtils.w("TAG_SMS", "GestionAccuserReceptionBR action=" + intent.getAction());

        if (phoneBean != null) {
            AnswerBean answerBean = new AnswerBean();
            answerBean.setNumber(phoneBean.getNumber());
            answerBean.setOutbox(SmsMmsManager.outboxFormat(phoneBean.getId() + ""));
            answerBean.setSend(getResultCode() == Activity.RESULT_OK);
            AnswerDaoManager.save(answerBean);
            Log.w("TAG_SMS", "Accusé d'envoie : " + answerBean.toString());
        }
        else {
            Log.w("TAG_SMS", "Accusé d'envoie : PhoneBean a null");
        }

        //on le retire des utilisés;
        context.unregisterReceiver(this);
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
