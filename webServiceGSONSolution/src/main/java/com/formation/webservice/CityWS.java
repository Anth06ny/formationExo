package com.formation.webservice;

import com.formation.webservice.bean.CityBean;
import com.formation.webservice.bean.ResultBean;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Anthony on 15/11/2014.
 */
public class CityWS {

    private final static String API_LOGIN = "login=webserviceexemple";
    private final static String API_KEY = "apikey=sof940dd26cf107eabf8bf6827f87c3ca8e8d82546";

    private final static String CITY_REQ = "http://www.citysearch-api.com/fr/city?&" + API_LOGIN + "&" + API_KEY;
    private final static String CP_PARAM = "cp";

    public static List<CityBean> getCity(String postalCode) throws Exception {
        if (postalCode == null) {
            throw new Exception("Le code postal n'a pas été remplie");
        }
        String urlString = CITY_REQ + "&" + CP_PARAM + "=" + postalCode;
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
            InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
            Gson gson = new Gson();
            ResultBean result = gson.fromJson(inputStreamReader, ResultBean.class);
            if (result == null) {
                throw new Exception("result est nulle");
            } else if (result.getErrors() != null) {
                throw new Exception(result.getErrors().getMessage());
            } else if (result.getCityBean() == null) {
                throw new Exception("result.getCityBean() est nulle");
            } else {
                return Arrays.asList(result.getCityBean());
            }
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
            }
        }
    }

    /* -------------------------
    // Private
    //------------------------- */

    private static String convertStreamToString(InputStreamReader is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
