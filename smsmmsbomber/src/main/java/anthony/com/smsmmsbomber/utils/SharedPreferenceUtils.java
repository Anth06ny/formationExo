package anthony.com.smsmmsbomber.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import org.apache.commons.lang3.StringUtils;

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
// Get AndroidId
// -------------------------------- */
    private static final String AndroidId = "AndroidId";

    public static String getUniqueIDGoodFormat(Context c) {

        SharedPreferences sharedPreferences = getSharedPreference(c);
        String uniqueId = sharedPreferences.getString(AndroidId, "");
        if (StringUtils.isNotBlank(uniqueId)) {
            return uniqueId;
        }

        uniqueId = Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ANDROID_ID);

        uniqueId = StringUtils.substring(uniqueId, -12);
        //On le met en form an-xxxx-xxxx-xxxx
        String goodFormat = "" + uniqueId.charAt(0);
        for (int i = 1; i < uniqueId.length(); i++) {
            if (i % 4 == 0) {
                goodFormat += '-';
            }
            goodFormat += uniqueId.charAt(i);
        }

        //on le sauvegarde
        sharedPreferences.edit().putString(AndroidId, goodFormat).apply();

        return goodFormat;
    }
}
