package com.formation.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Anthony on 11/11/2014.
 */
public class HTTPUtils {



    /**
     * Retourne la page http en parametre
     * @param myurl
     * @return
     * @throws java.io.IOException
     */
    public static String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        URL url = new URL(myurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try {
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");

            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("DEBUG_HTTP", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            return readIt(is);

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
                conn.disconnect();
            }
        }
    }

    /**
     * @param stream
     * @return Le string contenut dans le stream.
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    private static String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line + "\n");
        }
        return builder.toString();
    }

}
