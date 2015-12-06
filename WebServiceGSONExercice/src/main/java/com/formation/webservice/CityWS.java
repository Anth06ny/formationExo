package com.formation.webservice;

import com.formation.utils.exceptions.LogicException;
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

    private Gson gson;

    public CityWS() throws LogicException {
        gson = new Gson();
    }

    public List<CityBean> getCity(String postalCode) throws LogicException {

        //TODO créer la requete
        String urlString = "";
        InputStreamReader reader = null;

        try {
            reader = getReader(urlString);

            //Version on passe par le string
            //String resultString = convertStreamToString(reader);
            // ResultBean resultBean = gson.fromJson(resultString, ResultBean.class);
            //LogUtils.logTemp(convertStreamToString(reader));

            //version optimisée on lit à la volée.
            //TODO parser le résultat et le traiter
            Object result = null;
            if (result == null) {
                throw new LogicException("result est nulle");
            }
            else {
                return new ArrayList<>();
            }
        }
        catch (LogicException e) {
            throw e;
        }
        catch (Exception e) {
            throw new LogicException("Impossible de récuperer les données", e);
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

    /* -------------------------
    // Private
    //------------------------- */

    /**
     * Recuperer le flux de réponse de la requete
     *
     * @param url
     * @return
     * @throws LogicException
     */
    private InputStreamReader getReader(String url) throws LogicException {

        try {
            // Envoie de la requête
            InputStream inputStream = sendRequest(new URL(url));
            if (inputStream == null) {
                throw new LogicException("inputStream à null");
            }

            // Lecture de l'inputStream dans un reader
            return new InputStreamReader(inputStream, "UTF8");
        }
        catch (Exception e) {
            throw new LogicException("Erreur de parsing url + url=" + url, e);
        }
    }

    /**
     * Envoyer la requete
     *
     * @param url
     * @return
     * @throws Exception
     */
    private InputStream sendRequest(URL url) throws Exception {

        try {
            // Ouverture de la connexion
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            // Connexion à l'URL
            urlConnection.connect();

            // Si le serveur nous répond avec un code OK
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return urlConnection.getInputStream();
            }
        }
        catch (Exception e) {
            throw new Exception("");
        }
        return null;
    }

    private static String convertStreamToString(InputStreamReader is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
