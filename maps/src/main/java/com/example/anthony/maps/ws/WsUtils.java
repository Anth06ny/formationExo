package com.example.anthony.maps.ws;

import android.util.Log;

import com.example.anthony.maps.beans.Station;
import com.example.anthony.maps.beans.metro.Record;
import com.example.anthony.maps.beans.metro.StationMetroBean;
import com.example.anthony.maps.beans.metro.StationMetroResult;
import com.google.android.gms.maps.model.LatLng;
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

    //Url station metro
    private static final String URL_STATION_METRO = "https://data.toulouse-metropole.fr/api/records/1.0/search/?dataset=stations-de-metro&rows=100";

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

    public static ArrayList<StationMetroBean> getStationsMetro() throws Exception {

        Log.w("TAG_URL", URL_STATION_METRO);
        //OkHttpClient client = new OkHttpClient();
        OkHttpClient client = new OkHttpClient();

        //Création de la requete
        Request request = new Request.Builder().url(URL_STATION_METRO).build();

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
            StationMetroResult stationMetroResult = gson.fromJson(inputStreamReader,
                    StationMetroResult.class);

            if (stationMetroResult == null) {
                throw new Exception("listStation à nulle");
            }
            else if (stationMetroResult.getRecords() == null) {
                throw new Exception("stationMetroResult.getRecords() à nulle");
            }

            //On parcourt
            ArrayList<StationMetroBean> stationMetroBeans = new ArrayList<>();
            for (Record record : stationMetroResult.getRecords()) {
                if (record.getFields() == null || record.getFields().getGeo_shape() == null
                        || record.getFields().getGeo_shape().getCoordinates() == null
                        || record.getFields().getGeo_shape().getCoordinates().size() < 2) {
                    Log.w("MON_TAG", "Station incomplete id=" + record.getRecordid());
                    continue;
                }
                else if (record.getFields().getLigne() == null) {
                    Log.w("MON_TAG", "Ligne de station null : id=" + record.getRecordid());
                    continue;
                }
                StationMetroBean stationMetroBean = new StationMetroBean();
                //le nom
                stationMetroBean.setName(record.getFields().getNom());
                //La position
                LatLng latLng = new LatLng(record.getFields().getGeo_shape().getCoordinates().get(1), record.getFields().getGeo_shape().getCoordinates().get(0));
                stationMetroBean.setPosition(latLng);
                //Est ce qu'elle existe déjà, dans ce cas on déclare la ligne multiligne
                if (!exist(stationMetroBeans, stationMetroBean.getName())) {
                    //La ligne
                    if (record.getFields().getLigne().equalsIgnoreCase("a")) {
                        stationMetroBean.setLigne(1);
                    }
                    else if (record.getFields().getLigne().equalsIgnoreCase("b")) {
                        stationMetroBean.setLigne(2);
                    }
                    else if (record.getFields().getLigne().equalsIgnoreCase("c")) {
                        stationMetroBean.setLigne(3);
                    }
                    else {
                        Log.w("MON_TAG", "Ligne de station inconnue : " + record.getFields().getLigne() + " (id=" + record.getRecordid() + ")");
                    }

                    stationMetroBeans.add(stationMetroBean);
                }
            }

            return stationMetroBeans;
        }
    }

    private static boolean exist(ArrayList<StationMetroBean> listStation, String name) {
        for (StationMetroBean stationMetroBean : listStation) {
            if (stationMetroBean.getName().equals(name)) {
                stationMetroBean.setLigne(0);
                return true;
            }
        }
        return false;
    }
}
