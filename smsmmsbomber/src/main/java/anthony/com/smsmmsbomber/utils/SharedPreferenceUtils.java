package anthony.com.smsmmsbomber.utils;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import com.formation.utils.exceptions.TechnicalException;

import org.apache.commons.lang3.StringUtils;

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

    public static String getUrlLoad(Context c) throws TechnicalException {

        return getSharedPreference(c).getString(URL_LOAD, "");
    }

    public static void saveUrlLoad(Context c, String url) {
        if (!StringUtils.endsWith(url, "/")) {
            url += "/";
        }
        getSharedPreference(c).edit().putString(URL_LOAD, url).apply();
    }

    /* ---------------------------------
// Get AndroidId
// -------------------------------- */
    private static final String AndroidId = "AndroidId";

    public static String getUniqueIDGoodFormat(Context c) throws TechnicalException {

        if (ActivityCompat.checkSelfPermission(c, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return "PasLaPermission";
        }

        SharedPreferences sharedPreferences = getSharedPreference(c);
        String uniqueId = sharedPreferences.getString(AndroidId, "");
        if (StringUtils.isNotBlank(uniqueId)) {
            return uniqueId;
        }

        //Change de temps en temps
        //uniqueId = Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ANDROID_ID);
        uniqueId = Utils.getDeviceIMEI(c);

        if (uniqueId == null || uniqueId.length() < 12) {
            throw new TechnicalException("L'IMEI est trop petit : " + uniqueId);
        }

        uniqueId = StringUtils.substring(uniqueId, -12);
        //On le met en form an-xxxx-xxxx-xxxx
        String goodFormat = "an-" + uniqueId.charAt(0);
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
