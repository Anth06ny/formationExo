package anthony.com.cahors2;

import android.app.Application;

import com.squareup.otto.Bus;

/**
 * Created by Anthony on 15/05/2018.
 */

public class MyApplication extends Application {

    private static Bus bus;

    public static Bus getBus() {
        return bus;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bus = new Bus();
    }
}
