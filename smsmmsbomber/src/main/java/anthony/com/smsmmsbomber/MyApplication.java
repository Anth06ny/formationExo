package anthony.com.smsmmsbomber;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.klinker.android.send_message.Settings;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import org.greenrobot.greendao.database.Database;

import anthony.com.smsmmsbomber.model.DaoMaster;
import anthony.com.smsmmsbomber.model.DaoSession;
import anthony.com.smsmmsbomber.service.SendMessageService;
import io.fabric.sdk.android.Fabric;

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
    private static String versionAppli;

    @Override
    public void onCreate() {

        super.onCreate();
        instance = this;
        Fabric.with(this, new Crashlytics());
        bus = new Bus(ThreadEnforcer.ANY);
        setupDatabase();

        //Pour le reglage de la lib de mms
        Settings settings = new Settings();
        settings.setUseSystemSending(true);

        //Version de l'application
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionAppli = "v0.0.2." + pInfo.versionCode;   //v0.0.0.2.xxx ou xxx correspond au numerond de la version mobile
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (!BuildConfig.DEBUG) {
            SendMessageService.startservice(this);
        }
        else {
            Stetho.initializeWithDefaults(this);
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

    public static String getVersionAppli() {
        return versionAppli;
    }
}
