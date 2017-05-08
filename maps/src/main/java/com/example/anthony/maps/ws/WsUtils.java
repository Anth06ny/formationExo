package com.example.anthony.maps.ws;

import android.util.Log;

import com.example.anthony.maps.beans.Station;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Anthony on 04/05/2017.
 */

public class WsUtils {

    private static final String KEY = "2a1b07b2a523f81188fe34e348206a57ffa6f2a7";
    private static final String CONTRAT = "Toulouse";
    private static final String URL = "https://api.jcdecaux.com/vls/v1/stations?contract=" + CONTRAT + "&apiKey=" + KEY;

    private static final Gson gson = new Gson();

    public static ArrayList<Station> getStationsDuServeur() throws Exception {

        Log.w("TAG_URL", URL);
        //OkHttpClient client = new OkHttpClient();
        OkHttpClient client = new OkHttpClient();

        //Création de la requete
        Request request = new Request.Builder().url(URL).build();

        //Execution de la requête
        Response response = client.newCall(request).execute();

        //Analyse du code retour
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw new Exception("Reponse du serveur incorrect : " + response.code());
        }
        else {
            //Résultat de la requete.
            InputStreamReader inputStreamReader = new InputStreamReader(response.body().byteStream());

            //JSON -> Java (Parser une ArrayList typée)
            ArrayList<Station> listStation = gson.fromJson(inputStreamReader,
                    new TypeToken<ArrayList<Station>>() {
                    }.getType());

            return listStation;
        }
    }
}
