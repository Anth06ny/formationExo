package anthony.com.smsmmsbomber.utils;

import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import anthony.com.smsmmsbomber.BuildConfig;
import anthony.com.smsmmsbomber.MainActivity;
import anthony.com.smsmmsbomber.MyApplication;
import anthony.com.smsmmsbomber.utils.exceptions.ExceptionA;
import anthony.com.smsmmsbomber.utils.exceptions.TechnicalException;

public class LogUtils {

    public static void w(String tag, String text) {

        //Ajout à CrashLytics
        if (BuildConfig.DEBUG) {
            Log.w(tag, text);
        }
        if (MainActivity.LOG_ON) {
            MyApplication.getBus().post(text);
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
