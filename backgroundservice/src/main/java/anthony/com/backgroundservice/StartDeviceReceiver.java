package anthony.com.backgroundservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

public class StartDeviceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "Démarrage", Toast.LENGTH_SHORT).show();
        Log.w("TAG_", "Démarrage");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, StartDeviceService.class));
        }
        else {
            context.startService(new Intent(context, StartDeviceService.class));
        }
    }
}
