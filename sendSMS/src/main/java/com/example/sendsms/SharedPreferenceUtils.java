package com.example.sendsms;

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
    private static final String URL = "URL";

    public static String getSaveUrl(Context c) {
        return getSharedPreference(c).getString(URL, null);
    }

    public static void saveUrl(Context c, String voice) {
        getSharedPreference(c).edit().putString(URL, voice).apply();
    }
}
