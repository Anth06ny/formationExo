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
    //Sauvegarde url de chargement
    // -------------------------------- */
    private static final String URL_LOAD = "URL_LOAD";

    public static String getUrlLoad(Context c) {
        return getSharedPreference(c).getString(URL_LOAD, c.getResources().getString(R.string.url_server));
    }

    public static void saveUrlLoad(Context c, String url) {
        getSharedPreference(c).edit().putString(URL_LOAD, url).apply();
    }

    /* ---------------------------------
//Sauvegarde url envoie resultat
// -------------------------------- */
    private static final String URL_SEND_RESULT = "URL_SEND_RESULT";

    public static String getUrlSendResult(Context c) {
        return getSharedPreference(c).getString(URL_SEND_RESULT, "");
    }

    public static void saveUrlSendResult(Context c, String url) {
        getSharedPreference(c).edit().putString(URL_SEND_RESULT, url).apply();
    }

    /* ---------------------------------
//Sauvegarde url envoie resultat
// -------------------------------- */
    private static final String URL_SEND_ANSWER = "URL_SEND_ANSWER";

    public static String getUrlSendAnswer(Context c) {
        return getSharedPreference(c).getString(URL_SEND_ANSWER, "");
    }

    public static void saveUrlSendAnswer(Context c, String url) {
        getSharedPreference(c).edit().putString(URL_SEND_ANSWER, url).apply();
    }

    /* ---------------------------------
    //Last CampagneID
    // -------------------------------- */
    private static final String LAST_CAMPAGNE_ID = "LAST_CAMPAGNE_ID";

    public static int getLastCampagneId(Context c) {
        return getSharedPreference(c).getInt(LAST_CAMPAGNE_ID, -1);
    }

    public static void saveLastCampagneId(Context c, int id) {
        getSharedPreference(c).edit().putInt(LAST_CAMPAGNE_ID, id).apply();
    }

    /* ---------------------------------
   //Last CampagneID
   // -------------------------------- */
    private static final String TIME_DELAY = "TIME_DELAY";

    public static int getDelay(Context c) {
        return getSharedPreference(c).getInt(TIME_DELAY, 30);
    }

    public static void saveDelay(Context c, int id) {
        getSharedPreference(c).edit().putInt(TIME_DELAY, id).apply();
    }
}
