package anthony.com.smsmmsbomber.broadcast;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

import anthony.com.smsmmsbomber.MyApplication;

/**
 * Created by amonteiro on 05/12/2014.
 */
public class MultipleSendSMSBR extends BroadcastReceiver {

    public static final String ACTION_MMS_SENT = "com.example.android.apis.os.MMS_SENT_ACTION";
    public static final String ACTION_MMS_RECEIVED =
            "com.example.android.apis.os.MMS_RECEIVED_ACTION";
    public static final String SENT_SMS_ACTION_NAME = "SMS_SENT";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.w("TAG_SMS", "MultipleSendSMSBR action=" + intent.getAction()) ;

        //Detect l'envoie de sms
        if (intent.getAction().equals(SENT_SMS_ACTION_NAME)) {


            switch (getResultCode()) {
                case Activity.RESULT_OK: // Sms sent
                    MyApplication.getBus().post(true);
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE: // generic failure
                case SmsManager.RESULT_ERROR_NO_SERVICE: // No service
                case SmsManager.RESULT_ERROR_NULL_PDU: // null pdu
                case SmsManager.RESULT_ERROR_RADIO_OFF: //Radio off
                    MyApplication.getBus().post(false);
                    break;
            }
        }
    }


}
