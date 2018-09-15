package anthony.com.smsmmsbomber.broadcast;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.formation.utils.exceptions.TechnicalException;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;

import anthony.com.smsmmsbomber.Constants;
import anthony.com.smsmmsbomber.model.AnswerBean;
import anthony.com.smsmmsbomber.model.dao.AnswerDaoManager;
import anthony.com.smsmmsbomber.model.wsbeans.getscheduleds.PhoneBean;
import anthony.com.smsmmsbomber.utils.LogUtils;

/**
 * Created by amonteiro on 05/12/2014.
 */
public class AccuserEnvoieMMSBR extends com.klinker.android.send_message.MmsSentReceiver {

    public static final ArrayList<AccuserEnvoieMMSBR> list = new ArrayList<>();

    public static final String SENT_MMS_ACTION_NAME = "MMS_SENT";
    public static final String RECEIVED_MMS_ACTION_NAME = "MMS_RECEIVED";
    public static final IntentFilter INTENT_FILTER_MMS = new IntentFilter(SENT_MMS_ACTION_NAME);

    public static void init() {
        INTENT_FILTER_MMS.addAction(RECEIVED_MMS_ACTION_NAME);
    }

    private AnswerBean answerBean;
    private long timeAdd;
    private boolean receivedEventReceived; // Est ce qu'on a recu l'accusé de reception

    public AccuserEnvoieMMSBR() {
        answerBean = new AnswerBean();
    }

    public AccuserEnvoieMMSBR(PhoneBean phoneBean, Context context) {
        answerBean = new AnswerBean(phoneBean);
        addBR(context, this, true);
    }

    @Override
    public void onMessageStatusUpdated(Context context, Intent intent, int resultCode) {
        LogUtils.w("TAG_MMS", "AccuserEnvoieMMSBR action=" + intent.getAction());

        if (intent.getAction().equals(SENT_MMS_ACTION_NAME)) {
            if (StringUtils.isNotBlank(answerBean.getNumber())) {
                answerBean.setSend(resultCode == Activity.RESULT_OK);
                AnswerDaoManager.save(answerBean);
                LogUtils.w("TAG_MMS", "Accusé d'envoie : " + answerBean.toString());
                //on le retire des utilisés;
                addBR(context, this, false);
            }
            else {
                LogUtils.w("TAG_MMS", "Accusé d'envoie : PhoneBean a null");
            }
        }
        else if (intent.getAction().equals(RECEIVED_MMS_ACTION_NAME)) {
            if (StringUtils.isNotBlank(answerBean.getNumber())) {
                //On ne retourne pas les mms en recu en succes
                if (resultCode != Activity.RESULT_OK) {
                    answerBean.setReceived(false);
                    AnswerDaoManager.save(answerBean);
                }

                LogUtils.w("TAG_MMS", "Accusé de reception : " + answerBean.toString());
                //on le retire des utilisées;
                addBR(context, this, false);
            }
            else {
                LogUtils.w("TAG_MMS", "Accusé de reception : PhoneBean a null");
            }
        }
        else {

            LogUtils.w("TAG_MMS", "Action recu inconnu : " + intent.getAction());
            LogUtils.logException(new TechnicalException("Action recu inconnu : " + intent.getAction()));
        }
    }

    public static void createGestionAccuserReceptionBR(Context context, PhoneBean phoneBean) {
        new AccuserEnvoieMMSBR(phoneBean, context);
    }

    public static synchronized void addBR(Context context, AccuserEnvoieMMSBR accuserEnvoieMMSBR, boolean add) {
        try {
            if (add) {
                accuserEnvoieMMSBR.timeAdd = new Date().getTime();
                list.add(accuserEnvoieMMSBR);
                context.registerReceiver(accuserEnvoieMMSBR, INTENT_FILTER_MMS);
            }
            else {
                //si c'est un mms on regarde si on a l'accusé de reception
                //                if (accuserEnvoieMMSBR.answerBean.isMms() && accuserEnvoieMMSBR.answerBean.getReceived() == null) {
                //                    //on a pas l'accusé reception on laisse le BR ouvert
                //                    return;
                //                }

                list.remove(accuserEnvoieMMSBR);
                context.unregisterReceiver(accuserEnvoieMMSBR);
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Parcours la liste et retire tous les broadcast en attente depuis au moins x mins
     */
    public static synchronized void cleanMemory() {
        long now = new Date().getTime();

        for (int i = list.size() - 1; i >= 0; i--) {
            AccuserEnvoieMMSBR ar = list.get(i);
            if (now - ar.timeAdd > Constants.DELETE_BR_DELAY) {
                list.remove(ar);
            }
        }
    }
}
