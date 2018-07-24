package anthony.com.smsmmsbomber.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

import com.klinker.android.send_message.Message;
import com.klinker.android.send_message.Transaction;

import java.util.ArrayList;

import anthony.com.smsmmsbomber.broadcast.MultipleSendSMSBR;
import anthony.com.smsmmsbomber.model.CampagneBean;
import anthony.com.smsmmsbomber.model.TelephoneBean;

public class SmsMmsManager {

    public static void sendSMS(final Context context, TelephoneBean telephoneBean, ArrayList<String> parts) {

        SmsManager sms = SmsManager.getDefault();
        Intent intent = new Intent(MultipleSendSMSBR.SENT_SMS_ACTION_NAME);
        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, intent, 0);
        ArrayList<PendingIntent> sendList = new ArrayList<>();
        sendList.add(sentPI);

        sms.sendMultipartTextMessage(telephoneBean.getNumero(), null, parts, sendList, null);
    }

    public static void sendMMS(Transaction transaction, CampagneBean campagneBean, TelephoneBean telephoneBean) {

        Message message = new Message(campagneBean.getMessage(), telephoneBean.getNumero());
        if (campagneBean.isVideo()) {
            message.addVideo(campagneBean.getVideoFile());
        }
        else {
            message.addImage(campagneBean.getBitmap());
        }
        transaction.sendNewMessage(message, Transaction.NO_THREAD_ID);
    }
}
