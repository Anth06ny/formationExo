package anthony.com.smsmmsbomber.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import anthony.com.smsmmsbomber.utils.LogUtils;

public class MmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.w("TAG_MMS", "MmsReceiver Action : " + intent.getAction());

        Toast.makeText(context, "Action : " + intent.getAction(), Toast.LENGTH_SHORT).show();
    }
}
