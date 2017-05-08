package com.example.anthony.maps.ws;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Anthony on 21/09/2016.
 */
public class OkHttpUtils {

    public static Response sendGetOkHttpRequest(String url) throws Exception {
        Log.w("TAG_URL", url);
        //OkHttpClient client = new OkHttpClient();
        OkHttpClient client = new OkHttpClient();

        //Création de la requete
        Request request = new Request.Builder().url(url).build();

        //Execution de la requête
        return client.newCall(request).execute();


    }
}
