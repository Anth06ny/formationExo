package anthony.com.smsmmsbomber.utils;

import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.formation.utils.BuildConfig;
import com.formation.utils.exceptions.ExceptionA;
import com.formation.utils.exceptions.TechnicalException;

public class LogUtils {

    public static void w(String tag, String text) {

        //Ajout à CrashLytics
        if (BuildConfig.DEBUG) {
            Log.w(tag, text);
        }
        Crashlytics.log(text);
    }

    public static void logException(ExceptionA exceptionA) {
        if (exceptionA instanceof TechnicalException) {
            Crashlytics.logException(exceptionA);
        }
    }

    public static void logUser(Context c) {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        try {
            Crashlytics.setUserName("EMEI=" + Utils.getDeviceIMEI(c));
            Crashlytics.setUserIdentifier(SharedPreferenceUtils.getUniqueIDGoodFormat(c));
        }
        catch (TechnicalException e) {
            e.printStackTrace();
        }
    }
}
