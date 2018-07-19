package anthony.com.smsmmsbomber.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w("TAG_MMS", "MmsReceiver Action : " + intent.getAction());

        Toast.makeText(context, "Action : " + intent.getAction(), Toast.LENGTH_SHORT).show();
    }
}
