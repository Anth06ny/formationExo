package anthony.com.smsmmsbomber.broadcast;

import android.app.Activity;
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
public class AccuserReceptionMMSBR extends com.klinker.android.send_message.MmsSentReceiver {

    public static final ArrayList<AccuserReceptionMMSBR> list = new ArrayList<>();

    public static final String SENT_MMS_ACTION_NAME = "MMS_SENT";
    public static final String RECEIVED_MMS_ACTION_NAME = "MMS_RECEIVED";
    public static final IntentFilter INTENT_FILTER_MMS2 = new IntentFilter(SENT_MMS_ACTION_NAME);

    static {
        INTENT_FILTER_MMS2.addAction(RECEIVED_MMS_ACTION_NAME);
    }
    //public static final IntentFilter INTENT_FILTER_MMS = new IntentFilter(MMS_SENT);

    private AnswerBean answerBean;

    public AccuserReceptionMMSBR() {
        answerBean = new AnswerBean();
    }

    public AccuserReceptionMMSBR(PhoneBean phoneBean, Context context) {
        answerBean = new AnswerBean(phoneBean);
        addBR(context, this, true);
    }

    @Override
    public void onMessageStatusUpdated(Context context, Intent intent, int resultCode) {
        LogUtils.w("TAG_MMS", "AccuserReceptionMMSBR action=" + intent.getAction());


        if (StringUtils.isNotBlank(answerBean.getNumber())) {
            answerBean.setSend(resultCode == Activity.RESULT_OK);
            AnswerDaoManager.save(answerBean);
            Log.w("TAG_MMS", "Accusé d'envoie : " + answerBean.toString());

            //on le retire des utilisés;
            addBR(context, this, false);
        }
        else {
            Log.w("TAG_SMS", "Accusé d'envoie : PhoneBean a null");
        }
    }

    public static void createGestionAccuserReceptionBR(Context context, PhoneBean phoneBean) {
        new AccuserReceptionMMSBR(phoneBean, context);
    }

    public static synchronized void addBR(Context context, AccuserReceptionMMSBR accuserReceptionMMSBR, boolean add) {
        try {
            if (add) {
                list.add(accuserReceptionMMSBR);
                context.registerReceiver(accuserReceptionMMSBR, INTENT_FILTER_MMS2);
            }
            else {
                list.remove(accuserReceptionMMSBR);
                context.unregisterReceiver(accuserReceptionMMSBR);
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
