package anthony.com.smsmmsbomber.broadcast;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import anthony.com.smsmmsbomber.model.AnswerBean;
import anthony.com.smsmmsbomber.model.dao.AnswerDaoManager;
import anthony.com.smsmmsbomber.model.wsbeans.getscheduleds.PhoneBean;
import anthony.com.smsmmsbomber.utils.LogUtils;

/**
 * Created by amonteiro on 05/12/2014.
 */
public class AccuserReceptionSMSBR extends BroadcastReceiver {

    public static final ArrayList<AccuserReceptionSMSBR> list = new ArrayList<>();

    public static final IntentFilter INTENT_FILTER_SMS = new IntentFilter(AccuserReceptionSMSBR.SENT_SMS_ACTION_NAME);

    public static final String SENT_SMS_ACTION_NAME = "SMS_SENT";
    public static final String DELIVERED_SMS_ACTION_NAME = "SMS_DELIVERED";

    private AnswerBean answerBean;

    public AccuserReceptionSMSBR() {
        answerBean = new AnswerBean();
    }

    public AccuserReceptionSMSBR(PhoneBean phoneBean, Context context) {
        answerBean = new AnswerBean(phoneBean);
        addBR(context, this, true);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        LogUtils.w("TAG_SMS", "AccuserReceptionSMSBR action=" + intent.getAction());

        if (StringUtils.isNotBlank(answerBean.getNumber())) {
            answerBean.setSend(getResultCode() == Activity.RESULT_OK);
            AnswerDaoManager.save(answerBean);
            Log.w("TAG_SMS", "Accusé d'envoie : " + answerBean.toString());

            //on le retire des utilisés;
            addBR(context, this, false);
        }
        else {
            Log.w("TAG_SMS", "Accusé d'envoie : PhoneBean a null");
        }
    }

       /* ---------------------------------
       // Static gestion liste de Broadcast
       // -------------------------------- */

    public static void createGestionAccuserReceptionBR(Context context, PhoneBean phoneBean) {
        new AccuserReceptionSMSBR(phoneBean, context);
    }

    public static synchronized void addBR(Context context, AccuserReceptionSMSBR accuserReceptionSMSBR, boolean add) {
        try {
            if (add) {
                list.add(accuserReceptionSMSBR);
                context.registerReceiver(accuserReceptionSMSBR, INTENT_FILTER_SMS);
            }
            else {
                list.remove(accuserReceptionSMSBR);
                context.unregisterReceiver(accuserReceptionSMSBR);
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
