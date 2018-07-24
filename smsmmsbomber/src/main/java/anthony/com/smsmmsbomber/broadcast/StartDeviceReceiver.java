package anthony.com.smsmmsbomber.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import anthony.com.smsmmsbomber.service.SendMessageService;

public class StartDeviceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SendMessageService.startservice(context);
    }
}
