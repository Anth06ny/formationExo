package anthony.com.smsmmsbomber.utils;

import android.content.Context;
import android.content.SharedPreferences;

import anthony.com.smsmmsbomber.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Anthony on 29/06/2017.
 */

public class SharedPreferenceUtils {

    private static SharedPreferences getSharedPreference(Context c) {
        return c.getSharedPreferences("Register", MODE_PRIVATE);
    }

    /* ---------------------------------
    //Sauvegarde voix
    // -------------------------------- */
    private static final String URL = "URL";

    public static String getSaveURL(Context c) {
        return getSharedPreference(c).getString(URL, c.getResources().getString(R.string.url_server));
    }

    public static void saveURL(Context c, String url) {
        getSharedPreference(c).edit().putString(URL, url).apply();
    }

    /* ---------------------------------
    //Last CampagneID
    // -------------------------------- */
    private static final String LAST_CAMPAGNE_ID = "LAST_CAMPAGNE_ID";

    public static int getSaveLastCampagneId(Context c) {
        return getSharedPreference(c).getInt(LAST_CAMPAGNE_ID, -1);
    }

    public static void saveLastCampagneId(Context c, int id) {
        getSharedPreference(c).edit().putInt(LAST_CAMPAGNE_ID, id).apply();
    }
}
