package anthony.com.ultimatewarmup;

import android.content.Context;
import android.content.SharedPreferences;

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
    private static final String VOIX_KEY = "VOIX_KEY";

    public static String getSaveVoice(Context c) {
        return getSharedPreference(c).getString(VOIX_KEY, null);
    }

    public static void saveVoice(Context c, String voice) {
        getSharedPreference(c).edit().putString(VOIX_KEY, voice).apply();
    }
}
