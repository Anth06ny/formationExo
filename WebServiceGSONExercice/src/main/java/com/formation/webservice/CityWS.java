package com.formation.webservice;

import com.formation.webservice.bean.CityBean;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anthony on 15/11/2014.
 */
public class CityWS {

    //TODO mettre son login et clé générés
    private final static String API_LOGIN = "login=";
    private final static String API_KEY = "apikey=";

    //TODO remplir la requete
    private final static String CITY_REQ = "" + API_LOGIN + "&" + API_KEY;
    private final static String CP_PARAM = "cp";

    public static List<CityBean> getCity(String postalCode) throws Exception {

        //TODO créer la requete
        String urlString = "";
        InputStreamReader reader = null;

        try {

            URL url = new URL(urlString);

            // Ouverture de la connexion
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            // Connexion à l'URL
            urlConnection.connect();

            // Si le serveur nous répond avec un code OK
            if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new Exception("Reponse du serveur incorrect : " + urlConnection.getResponseCode());
            }

            //Version on passe par le string
            //String resultString = convertStreamToString(urlConnection.getInputStream());
            // ResultBean resultBean = gson.fromJson(resultString, ResultBean.class);
            //LogUtils.logTemp(convertStreamToString(reader));

            //version optimisée on lit à la volée.
            //TODO parser le résultat et le traiter
            InputStream inputStream = urlConnection.getInputStream();
            Gson gson = new Gson();
            Object result = null;

            if (result == null) {
                throw new Exception("result est nulle");
            }
            else {
                return new ArrayList<>();
            }
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            }
            catch (IOException e) {
            }
        }
    }

    private static String convertStreamToString(InputStreamReader is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
