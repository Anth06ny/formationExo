package anthony.com.smsmmsbomber;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class HeadlessSmsSendService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.w("TAG_", "HeadlessSmsSendService HeadlessSmsSendService created");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
