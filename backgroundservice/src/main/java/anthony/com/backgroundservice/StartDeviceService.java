package anthony.com.backgroundservice;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class StartDeviceService extends Service {

    private Timer timer;
    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        startForeground(1, NotificationUtils.getNotif(this, "Hello"));

        Log.w("TAG_", "Démmarage service");

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                toastOnUiThread();
            }
        }, 0, 5000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void toastOnUiThread() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.w("TAG_", "Salut");
                Toast.makeText(StartDeviceService.this, "Salut", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.w("TAG_", "Fin du service");

        timer.cancel();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
