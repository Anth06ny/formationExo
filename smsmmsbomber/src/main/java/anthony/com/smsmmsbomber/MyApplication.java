package anthony.com.smsmmsbomber;

import android.app.Application;

import com.squareup.otto.Bus;

import org.greenrobot.greendao.database.Database;

import anthony.com.smsmmsbomber.model.DaoMaster;
import anthony.com.smsmmsbomber.model.DaoSession;
import anthony.com.smsmmsbomber.service.SendMessageService;

/**
 * Created by Anthony on 05/04/2016.
 */
public class MyApplication extends Application {

    /**
     * Annalyser l'envoie de message
     */

    private static MyApplication instance;
    private static Bus bus;
    private static DaoSession daoSession;

    @Override
    public void onCreate() {

        super.onCreate();
        instance = this;
        bus = new Bus();
        setupDatabase();

        if (!BuildConfig.DEBUG) {
            SendMessageService.startservice(this);
        }
    }

    private void setupDatabase() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public static Bus getBus() {
        return bus;
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public static DaoSession getDaoSession() {
        return daoSession;
    }
}
