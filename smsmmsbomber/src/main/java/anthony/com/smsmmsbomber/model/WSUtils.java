package anthony.com.smsmmsbomber.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.formation.utils.exceptions.TechnicalException;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;

import anthony.com.smsmmsbomber.BuildConfig;
import anthony.com.smsmmsbomber.MyApplication;
import anthony.com.smsmmsbomber.utils.Logger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Anthony on 15/11/2014.
 */
public class WSUtils {

    /**
     * Récupère la liste des commande de l'utilisateur.. Envoie email et token du téléphone
     *
     * @return
     * @throws TechnicalException
     */
    public static CampagneBean getCampagnes(String url) throws TechnicalException {

        Log.w("TAG_URL", url);
        //Création de la requete
        Request request = new Request.Builder().url(url).build();

        //Execution de la requête
        Response response;
        try {
            response = getOkHttpClient().newCall(request).execute();
        }
        catch (IOException e) {
            //On test si google répond pour différencier si c'est internet ou le serveur le probleme
            throw testInternetConnexionOnGoogle(e);
        }

        //Analyse du code retour si non copmris entre 200 et 299
        if (response.code() < HttpURLConnection.HTTP_OK || response.code() >= HttpURLConnection.HTTP_MULT_CHOICE) {
            throw new TechnicalException("Réponse du serveur incorrect : " + response.code() + "\nErreur:" + response.message());
        }
        else {

            CampagneBean campagneBean;

            if (BuildConfig.DEBUG) {
                //Résultat de la requete.
                try {
                    String jsonRecu = response.body().string();
                    Logger.logJson("TAG_JSON_RECU", jsonRecu);
                    campagneBean = new Gson().fromJson(jsonRecu, CampagneBean.class);
                }
                catch (Exception e) {
                    throw new TechnicalException("Erreur lors du .string dans la requete", e);
                }
            }
            else {
                //JSON -> Java (Parser une ArrayList typée)
                campagneBean = new Gson().fromJson(new InputStreamReader(response.body().byteStream()), CampagneBean.class);
            }

            //On télécharge le fichier s'il y en a 1
            if (StringUtils.isNotBlank(campagneBean.getUrlFile())) {
                if (campagneBean.isVideo()) {
                    campagneBean.setVideoFile(downloadVideo(campagneBean.getUrlFile()));
                }
                else {
                    campagneBean.setBitmap(downloadPicture(campagneBean.getUrlFile()));
                }
            }

            return campagneBean;
        }
    }

    public static Bitmap downloadPicture(String url) throws TechnicalException {
        Log.w("TAG_URL", "file: " + url);
        //Création de la requete
        Request request = new Request.Builder().url(url).build();

        //Execution de la requête
        Response response;
        try {
            response = getOkHttpClient().newCall(request).execute();
        }
        catch (IOException e) {
            //On test si google répond pour différencier si c'est internet ou le serveur le probleme
            throw testInternetConnexionOnGoogle(e);
        }

        //Analyse du code retour si non copmris entre 200 et 299
        if (response.code() < HttpURLConnection.HTTP_OK || response.code() >= HttpURLConnection.HTTP_MULT_CHOICE) {
            throw new TechnicalException("Réponse du serveur incorrect : " + response.code() + "\nErreur:" + response.message());
        }
        else {
            InputStream inputStream = null;
            try {
                inputStream = response.body().byteStream();
                return BitmapFactory.decodeStream(inputStream);
            }
            finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static byte[] downloadVideo(String url) throws TechnicalException {
        Log.w("TAG_URL", "file: " + url);
        //Création de la requete
        Request request = new Request.Builder().url(url).build();

        //Execution de la requête
        Response response;
        try {
            response = getOkHttpClient().newCall(request).execute();
        }
        catch (IOException e) {
            //On test si google répond pour différencier si c'est internet ou le serveur le probleme
            throw testInternetConnexionOnGoogle(e);
        }

        //Analyse du code retour si non copmris entre 200 et 299
        if (response.code() < HttpURLConnection.HTTP_OK || response.code() >= HttpURLConnection.HTTP_MULT_CHOICE) {
            throw new TechnicalException("Réponse du serveur incorrect : " + response.code() + "\nErreur:" + response.message());
        }
        else {
            try {
                return response.body().bytes();
            }
            catch (IOException e) {
                throw new TechnicalException("Erreur lors de la récupération du fichier", e);
            }
        }
    }


     /* ---------------------------------
    // private
    // -------------------------------- */

    static TechnicalException testInternetConnexionOnGoogle(IOException e) {
        //On test si google répond pour différencier si c'est internet ou le serveur le probleme
        Request request = request = new Request.Builder().url("www.google.fr").build();
        try {

            new OkHttpClient.Builder().connectTimeout(2, TimeUnit.SECONDS).build().newCall(request).execute();
            //Ca marche -> C'est le serveur le probleme
            return new TechnicalException("L'url ne répond pas.", e);
        }
        catch (IOException e1) {
            //Ca crash encore -> problème d'internet
            return new TechnicalException("Bande passante innexistante.", e1);
        }
    }

    static OkHttpClient getOkHttpClient() throws TechnicalException {
        //On test la connexion à un réseau
        if (!isNetworkAvailable()) {
            throw new TechnicalException("Non connecté à un réseau.");
        }

        return new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .build();
    }

    /**
     * Est ce que le téléphone est relié à un réseau ?
     *
     * @return
     */
    private static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) MyApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
