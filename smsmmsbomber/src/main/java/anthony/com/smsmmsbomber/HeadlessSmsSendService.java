package anthony.com.smsmmsbomber;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import anthony.com.smsmmsbomber.utils.LogUtils;

public class HeadlessSmsSendService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.w("TAG_", "HeadlessSmsSendService HeadlessSmsSendService created");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
