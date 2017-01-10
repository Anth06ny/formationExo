package com.formation.webservice;

import android.util.Log;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.net.HttpURLConnection;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Anthony on 21/09/2016.
 */
public class OkHttpUtils {

    public static String sendGetOkHttpRequest(String url) throws Exception {
        Log.w("TAG", url);
        //OkHttpClient client = new OkHttpClient();
        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        //Création de la requete
        Request request = new Request.Builder().url(url).build();

        //Execution de la requête
        Response response = client.newCall(request).execute();

        //Analyse du code retour
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw new Exception("Reponse du serveur incorrect : " + response.code());
        }
        else {
            //Résultat de la requete.
            return response.body().string();
        }
    }

    public static void sendGetOkHttpRequestAsync(String url, Callback okHttpCallBack) throws Exception {
        //OkHttpClient client = new OkHttpClient();
        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        //Création de la requete
        Request request = new Request.Builder().url(url).build();

        //Execution de la requête
        client.newCall(request).enqueue(okHttpCallBack);
    }

    public static String sendPostOkHttpRequest(String url, String paramJson) throws Exception {

        //OkHttpClient client = new OkHttpClient();
        OkHttpClient client = getOkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        //Corps de la requête
        RequestBody body = RequestBody.create(JSON, paramJson);
        Request request = new Request.Builder().url(url).post(body).build();
        Response response = client.newCall(request).execute();

        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw new Exception("Reponse du serveur incorrect : " + response.code());
        }
        else {
            return response.body().string();
        }
    }

    private static OkHttpClient getOkHttpClient() {

        if (MyApplication.DEBUG) {

            return new OkHttpClient.Builder()
                    .addNetworkInterceptor(new StethoInterceptor())
                    .build();
        }
        else {
            return new OkHttpClient();
        }
    }
}
