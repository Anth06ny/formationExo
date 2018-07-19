package anthony.com.smsmmsbomber.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.telephony.SmsManager;

import com.formation.utils.exceptions.LogicException;
import com.klinker.android.send_message.Message;
import com.klinker.android.send_message.Settings;
import com.klinker.android.send_message.Transaction;

import java.util.ArrayList;

import anthony.com.smsmmsbomber.broadcast.MultipleSendSMSBR;
import anthony.com.smsmmsbomber.model.CampagneBean;
import anthony.com.smsmmsbomber.model.TelephoneBean;

public class SmsMmsManager {

    public static void sendCampagne(CampagneBean campagneBean) throws LogicException {
        if (campagneBean == null || campagneBean.getTelephoneBeans() == null || campagneBean.getTelephoneBeans().isEmpty()) {
            throw new LogicException("Aucun message à envoyer");
        }
        //Mode sms
        else if (campagneBean.getBitmap() == null) {

        }
        //Mode mms
        else {

        }
    }

    public static void sendSMS(final Context context, ArrayList<TelephoneBean> phonesNumber, String message) {

        SmsManager sms = SmsManager.getDefault();
        //On divise notre messege en plusieurs SMS en fonction du format
        ArrayList<String> parts = sms.divideMessage(message);

        for (TelephoneBean telephoneBean : phonesNumber) {
            Intent intent = new Intent(MultipleSendSMSBR.SENT_SMS_ACTION_NAME);
            PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, intent, 0);
            ArrayList<PendingIntent> sendList = new ArrayList<>();
            sendList.add(sentPI);

            sms.sendMultipartTextMessage(telephoneBean.getNumero(), null, parts, sendList, null);
        }
    }

    public static void sendMMS(final Context context, String phoneNumber, String messageText, Bitmap bitmap) throws Exception {

        Settings settings = new Settings();
        settings.setUseSystemSending(true);
        Transaction transaction = new Transaction(context, settings);
        Message message = new Message("HelloBob", "+33686874310");
        //Message message = new Message("HelloBob", "+33628473080");
        message.setImage(bitmap);
        transaction.sendNewMessage(message, Transaction.NO_THREAD_ID);
    }
}
