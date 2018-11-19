package anthony.com.smsmmsbomber.model;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import anthony.com.smsmmsbomber.BuildConfig;
import anthony.com.smsmmsbomber.Constants;
import anthony.com.smsmmsbomber.MyApplication;
import anthony.com.smsmmsbomber.model.wsbeans.GenericAnswerBean;
import anthony.com.smsmmsbomber.model.wsbeans.getboxendpoint.GetBoxEndPointAnswerBean;
import anthony.com.smsmmsbomber.model.wsbeans.getboxendpoint.GetBoxEndPointSendBean;
import anthony.com.smsmmsbomber.model.wsbeans.getscheduleds.GetScheduledAnswerBean;
import anthony.com.smsmmsbomber.model.wsbeans.getscheduleds.PhoneBean;
import anthony.com.smsmmsbomber.model.wsbeans.registerdevice.RegisterDeviceInformationsBean;
import anthony.com.smsmmsbomber.model.wsbeans.registerdevice.RegisterDeviceSendBean;
import anthony.com.smsmmsbomber.model.wsbeans.smssent.SmsSentSendBean;
import anthony.com.smsmmsbomber.model.wsbeans.smssuccessfail.SmsSucessFailSendBean;
import anthony.com.smsmmsbomber.utils.LogUtils;
import anthony.com.smsmmsbomber.utils.Logger;
import anthony.com.smsmmsbomber.utils.SharedPreferenceUtils;
import anthony.com.smsmmsbomber.utils.Utils;
import anthony.com.smsmmsbomber.utils.exceptions.ExceptionA;
import anthony.com.smsmmsbomber.utils.exceptions.TechnicalException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Anthony on 15/11/2014.
 */
public class WSUtils {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final Gson gson = new Gson();

    /**
     * Methode permetant d'enregistrer le device et d'obtenir l'url des requetes
     *
     * @param context
     */
    public static void saveUrlFromBoxEndPoint(Context context) throws ExceptionA {

        String url = Constants.URL_SERVER_CONSOLE + "getBoxEndpoint";
        LogUtils.w("TAG_URL_POST", url);
        GetBoxEndPointSendBean getBoxEndPointSendBean = new GetBoxEndPointSendBean(SharedPreferenceUtils.getUniqueIDGoodFormat(context));

        LogUtils.w("TAG_REQ", "json envoyé : " + gson.toJson(getBoxEndPointSendBean));

        RequestBody body = RequestBody.create(JSON, gson.toJson(getBoxEndPointSendBean));

        //Création de la requete
        Request request = new Request.Builder().url(url).post(body).build();

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
        if (response.code() != Constants.SERVEUR_CODE_ERROR && (response.code() < HttpURLConnection.HTTP_OK || response.code() >= HttpURLConnection
                .HTTP_MULT_CHOICE)) {
            throw new TechnicalException("Erreur serveur : " + response.code() + "\nErreur:" + response.message());
        }
        else {

            GetBoxEndPointAnswerBean answer;
            //Résultat de la requete.
            try {
                if (BuildConfig.DEBUG) {

                    String jsonRecu = response.body().string();
                    Logger.logJson("TAG_JSON_RECU", jsonRecu);
                    answer = gson.fromJson(jsonRecu, GetBoxEndPointAnswerBean.class);
                }
                else {
                    //JSON -> Java (Parser une ArrayList typée)
                    answer = gson.fromJson(new InputStreamReader(response.body().byteStream()), GetBoxEndPointAnswerBean.class);
                }
            }
            catch (Exception e) {
                throw new TechnicalException("Erreur lors du parsing Json", e);
            }

            //On analyse la réponse
            answer.checkError("/GetBoxEndpoint");
            if (StringUtils.isBlank(answer.getEndpoint())) {
                throw new TechnicalException("/GetBoxEndpoint : url vide : " + answer.getEndpoint());
            }
            else if (!answer.getEndpoint().startsWith("https://")) {
                throw new TechnicalException("/GetBoxEndpoint : url invalide : " + answer.getEndpoint());
            }

            if (!StringUtils.endsWithIgnoreCase(answer.getEndpoint(), "api")) {
                answer.setEndpoint(answer.getEndpoint() + "/API");
            }

            //Tout est bon on sauvegarde l'url
            SharedPreferenceUtils.saveUrlLoad(context, answer.getEndpoint());
        }
    }

    public static String getIP() throws ExceptionA {
        String url = Constants.URL_GET_IP;
        LogUtils.w("TAG_URL_GET", url);

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
        if (response.code() != Constants.SERVEUR_CODE_ERROR && (response.code() < HttpURLConnection.HTTP_OK || response.code() >= HttpURLConnection
                .HTTP_MULT_CHOICE)) {
            throw new TechnicalException("/getIP : " + response.code() + "\nErreur:" + response.message());
        }
        else {
            //Résultat de la requete.
            try {
                String ipRecu = response.body().string();
                LogUtils.w("TAG_REQ", "ip:" + ipRecu);
                return ipRecu;
            }
            catch (Exception e) {
                throw new TechnicalException("Erreur : " + e.getMessage(), e);
            }
        }
    }

    /**
     * Enregistre le device aupres de l'url
     *
     * @param context
     * @throws ExceptionA
     */
    public static void registerDevice(Context context, String ip) throws ExceptionA {

        String url = SharedPreferenceUtils.getUrlLoad(context) + "registerDevice";
        LogUtils.w("TAG_URL_POST", url);
        RegisterDeviceSendBean send = new RegisterDeviceSendBean(ip, SharedPreferenceUtils.getUniqueIDGoodFormat(context), Utils.getDeviceIMEI
                (context));
        String json = gson.toJson(send);
        LogUtils.w("TAG_REQ", "json envoyé : " + json);

        RequestBody body = RequestBody.create(JSON, json);

        //Création de la requete
        Request request = new Request.Builder().url(url).post(body).build();

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
        if (response.code() != Constants.SERVEUR_CODE_ERROR && (response.code() < HttpURLConnection.HTTP_OK || response.code() >= HttpURLConnection
                .HTTP_MULT_CHOICE)) {
            throw new TechnicalException("Erreur serveur : " + response.code() + "\nErreur:" + response.message());
        }
        else {

            GenericAnswerBean answer;
            //Résultat de la requete.
            try {
                if (BuildConfig.DEBUG) {

                    String jsonRecu = response.body().string();
                    Logger.logJson("TAG_JSON_RECU", jsonRecu);
                    answer = gson.fromJson(jsonRecu, GenericAnswerBean.class);
                }
                else {
                    //JSON -> Java (Parser une ArrayList typée)
                    answer = gson.fromJson(new InputStreamReader(response.body().byteStream()), GenericAnswerBean.class);
                }
            }
            catch (Exception e) {
                throw new TechnicalException("Erreur lors du parsing Json", e);
            }
            //On analyse la réponse
            answer.checkError("/registerDevice");
        }
    }

    public static void pingServeur(Context context, String ip) throws ExceptionA {

        String url = SharedPreferenceUtils.getUrlLoad(context);
        if (StringUtils.isBlank(url)) {
            url = Constants.URL_SERVER_CONSOLE;
        }
        url += "ping";
        LogUtils.w("TAG_URL_POST", url);
        //Meme format pour l'envoie
        RegisterDeviceSendBean send = new RegisterDeviceSendBean(ip, SharedPreferenceUtils.getUniqueIDGoodFormat(context), "");
        String json = gson.toJson(send);
        LogUtils.w("TAG_REQ", "json envoyé : " + json);

        RequestBody body = RequestBody.create(JSON, json);

        //Création de la requete
        Request request = new Request.Builder().url(url).post(body).build();

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
        if (response.code() != Constants.SERVEUR_CODE_ERROR && (response.code() < HttpURLConnection.HTTP_OK || response.code() >= HttpURLConnection
                .HTTP_MULT_CHOICE)) {
            throw new TechnicalException("Erreur serveur : " + response.code() + "\nErreur:" + response.message());
        }
        else {

            GenericAnswerBean answer;
            try {
                if (BuildConfig.DEBUG) {
                    //Résultat de la requete.

                    String jsonRecu = response.body().string();
                    Logger.logJson("TAG_JSON_RECU", jsonRecu);
                    answer = gson.fromJson(jsonRecu, GenericAnswerBean.class);
                }
                else {
                    //JSON -> Java (Parser une ArrayList typée)
                    answer = gson.fromJson(new InputStreamReader(response.body().byteStream()), GenericAnswerBean.class);
                }
            }
            catch (Exception e) {
                throw new TechnicalException("Erreur lors du parsing Json", e);
            }
            //On analyse la réponse
            answer.checkError("/ping");
        }
    }

    public static void deviceReady(Context context) throws ExceptionA {
        String url = SharedPreferenceUtils.getUrlLoad(context) + "deviceReady";
        LogUtils.w("TAG_URL_POST", url);
        //Meme format pour l'envoie
        RegisterDeviceInformationsBean send = new RegisterDeviceInformationsBean(Utils.getDeviceIMEI(context));
        String json = gson.toJson(send);
        LogUtils.w("TAG_REQ", "json envoyé : " + json);

        RequestBody body = RequestBody.create(JSON, json);

        //Création de la requete
        Request request = new Request.Builder().url(url).post(body).build();

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
        if (response.code() != Constants.SERVEUR_CODE_ERROR && (response.code() < HttpURLConnection.HTTP_OK || response.code() >= HttpURLConnection
                .HTTP_MULT_CHOICE)) {
            throw new TechnicalException("Erreur serveur : " + response.code() + "\nErreur:" + response.message());
        }
        else {

            GenericAnswerBean answer;
            try {
                if (BuildConfig.DEBUG) {
                    //Résultat de la requete.

                    String jsonRecu = response.body().string();
                    Logger.logJson("TAG_JSON_RECU", jsonRecu);
                    answer = gson.fromJson(jsonRecu, GenericAnswerBean.class);
                }
                else {
                    //JSON -> Java (Parser une ArrayList typée)
                    answer = gson.fromJson(new InputStreamReader(response.body().byteStream()), GenericAnswerBean.class);
                }
            }
            catch (Exception e) {
                throw new TechnicalException("Erreur lors du parsing Json", e);
            }
            //On analyse la réponse
            answer.checkError("/deviceReady");
        }
    }

    /**
     * Récupère la liste des commande de l'utilisateur.. Envoie email et token du téléphone
     *
     * @return
     * @throws TechnicalException
     */
    public static GetScheduledAnswerBean getScheduleds(Context context) throws ExceptionA {
        String url = SharedPreferenceUtils.getUrlLoad(context) + "getScheduleds";
        LogUtils.w("TAG_URL_POST", url);
        //Meme format pour l'envoie
        RegisterDeviceSendBean send = new RegisterDeviceSendBean("", SharedPreferenceUtils.getUniqueIDGoodFormat(context), "");
        String json = gson.toJson(send);
        LogUtils.w("TAG_REQ", "json envoyé : " + json);

        RequestBody body = RequestBody.create(JSON, json);

        //Création de la requete
        Request request = new Request.Builder().url(url).post(body).build();

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
        if (response.code() != Constants.SERVEUR_CODE_ERROR && (response.code() < HttpURLConnection.HTTP_OK || response.code() >= HttpURLConnection
                .HTTP_MULT_CHOICE)) {
            throw new TechnicalException("Erreur serveur : " + response.code() + "\nErreur:" + response.message());
        }
        else {

            GetScheduledAnswerBean answer;
            //Résultat de la requete.
            try {
                if (BuildConfig.DEBUG) {

                    String jsonRecu = response.body().string();
                    Logger.logJson("TAG_JSON_RECU", jsonRecu);
                    answer = gson.fromJson(jsonRecu, GetScheduledAnswerBean.class);
                }
                else {
                    //JSON -> Java (Parser une ArrayList typée)
                    answer = gson.fromJson(new InputStreamReader(response.body().byteStream()), GetScheduledAnswerBean.class);
                }
            }
            catch (Exception e) {
                throw new TechnicalException("Erreur lors du parsing Json", e);
            }
            //On analyse la réponse
            answer.checkError("/getScheduleds");

            return answer;
        }
    }

    public static void smssent(Context context, ArrayList<PhoneBean> phoneList) throws ExceptionA {
        String url = SharedPreferenceUtils.getUrlLoad(context) + "smsSent";
        LogUtils.w("TAG_URL_POST", url);
        SmsSentSendBean send = new SmsSentSendBean(Utils.getDeviceIMEI(context), phoneList);

        String json = gson.toJson(send);
        LogUtils.w("TAG_REQ", "json envoyé : " + json);

        RequestBody body = RequestBody.create(JSON, json);

        //Création de la requete
        Request request = new Request.Builder().url(url).post(body).build();

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
        if (response.code() != Constants.SERVEUR_CODE_ERROR && (response.code() < HttpURLConnection.HTTP_OK || response.code() >= HttpURLConnection
                .HTTP_MULT_CHOICE)) {
            throw new TechnicalException("Erreur serveur : " + response.code() + "\nErreur:" + response.message());
        }
        else {

            GenericAnswerBean answer;
            //Résultat de la requete.
            try {
                if (BuildConfig.DEBUG) {

                    String jsonRecu = response.body().string();
                    Logger.logJson("TAG_JSON_RECU", jsonRecu);
                    answer = gson.fromJson(jsonRecu, GenericAnswerBean.class);
                }
                else {
                    //JSON -> Java (Parser une ArrayList typée)
                    answer = gson.fromJson(new InputStreamReader(response.body().byteStream()), GenericAnswerBean.class);
                }
            }
            catch (Exception e) {
                throw new TechnicalException("Erreur lors du parsing Json", e);
            }
            //On analyse la réponse
            answer.checkError("/smsSent");
        }
    }

    //    public static Bitmap downloadPicture(String url) throws TechnicalException {
    //        Log.w("TAG_URL", "file: " + url);
    //        //Création de la requete
    //        Request request = new Request.Builder().url(url).build();
    //
    //        //Execution de la requête
    //        Response response;
    //        try {
    //            response = getOkHttpClient().newCall(request).execute();
    //        }
    //        catch (IOException e) {
    //            //On test si google répond pour différencier si c'est internet ou le serveur le probleme
    //            throw testInternetConnexionOnGoogle(e);
    //        }
    //
    //        //Analyse du code retour si non copmris entre 200 et 299
    //        if (response.code() < HttpURLConnection.HTTP_OK || response.code() >= HttpURLConnection.HTTP_MULT_CHOICE) {
    //            throw new TechnicalException("Réponse du serveur incorrect : " + response.code() + "\nErreur:" + response.message());
    //        }
    //        else {
    //            InputStream inputStream = null;
    //            try {
    //                inputStream = response.body().byteStream();
    //                return BitmapFactory.decodeStream(inputStream);
    //            }
    //            finally {
    //                if (inputStream != null) {
    //                    try {
    //                        inputStream.close();
    //                    }
    //                    catch (IOException e) {
    //                        e.printStackTrace();
    //                    }
    //                }
    //            }
    //        }
    //    }

    //    public static byte[] downloadVideo(String url) throws TechnicalException {
    //        Log.w("TAG_URL", "file: " + url);
    //        //Création de la requete
    //        Request request = new Request.Builder().url(url).build();
    //
    //        //Execution de la requête
    //        Response response;
    //        try {
    //            response = getOkHttpClient().newCall(request).execute();
    //        }
    //        catch (IOException e) {
    //            //On test si google répond pour différencier si c'est internet ou le serveur le probleme
    //            throw testInternetConnexionOnGoogle(e);
    //        }
    //
    //        //Analyse du code retour si non copmris entre 200 et 299
    //        if (response.code() < HttpURLConnection.HTTP_OK || response.code() >= HttpURLConnection.HTTP_MULT_CHOICE) {
    //            throw new TechnicalException("Réponse du serveur incorrect : " + response.code() + "\nErreur:" + response.message());
    //        }
    //        else {
    //            try {
    //                return response.body().bytes();
    //            }
    //            catch (IOException e) {
    //                throw new TechnicalException("Erreur lors de la récupération du fichier", e);
    //            }
    //        }
    //    }

    /**
     * Envoie les sms en succes ou en echec d'accusé d'envoie
     *
     * @param context
     * @param answerBeanList
     * @param success        true si c'est en success et false si c'est en echec
     * @throws ExceptionA
     */
    public static void sendSmsSendFail(Context context, List<AnswerBean> answerBeanList, boolean success) throws ExceptionA {
        String url = SharedPreferenceUtils.getUrlLoad(context) + (success ? "smsSentSuccess" : "smsSentFailed");

        LogUtils.w("TAG_URL", url);

        // SmsSucessFailSendBean send = new SmsSucessFailSendBean(SharedPreferenceUtils.getUniqueIDGoodFormat(context), answerBeanList);

        String json = gson.toJson(answerBeanList);
        LogUtils.w("TAG_REQ", "json envoyé : " + json);
        RequestBody body = RequestBody.create(JSON, json);

        //Création de la requete
        Request request = new Request.Builder().url(url).post(body).build();

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
        if (response.code() != Constants.SERVEUR_CODE_ERROR && (response.code() < HttpURLConnection.HTTP_OK || response.code() >= HttpURLConnection
                .HTTP_MULT_CHOICE)) {
            throw new TechnicalException("Erreur serveur : " + response.code() + "\nErreur:" + response.message());
        }
        else {

            GenericAnswerBean answer;
            //Résultat de la requete.
            try {
                if (BuildConfig.DEBUG) {

                    String jsonRecu = response.body().string();
                    Logger.logJson("TAG_JSON_RECU", jsonRecu);
                    answer = gson.fromJson(jsonRecu, GenericAnswerBean.class);
                }
                else {
                    //JSON -> Java (Parser une ArrayList typée)
                    answer = gson.fromJson(new InputStreamReader(response.body().byteStream()), GenericAnswerBean.class);
                }
            }
            catch (Exception e) {
                throw new TechnicalException("Erreur lors du parsing Json", e);
            }
            //On analyse la réponse
            answer.checkError("/smsSentFailed");
        }
    }

    public static void sendSmsReceive(Context context, List<AnswerBean> answerBeanList) throws ExceptionA {
        String url = SharedPreferenceUtils.getUrlLoad(context) + "smsReceived";

        LogUtils.w("TAG_URL", url);

        SmsSucessFailSendBean send = new SmsSucessFailSendBean(SharedPreferenceUtils.getUniqueIDGoodFormat(context), answerBeanList);
        String json = gson.toJson(send);
        LogUtils.w("TAG_REQ", "json envoyé : " + json);

        RequestBody body = RequestBody.create(JSON, json);

        //Création de la requete
        Request request = new Request.Builder().url(url).post(body).build();

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
        if (response.code() != Constants.SERVEUR_CODE_ERROR && (response.code() < HttpURLConnection.HTTP_OK || response.code() >= HttpURLConnection
                .HTTP_MULT_CHOICE)) {
            throw new TechnicalException("Erreur serveur : " + response.code() + "\nErreur:" + response.message());
        }
        else {

            GenericAnswerBean answer;
            //Résultat de la requete.
            try {
                if (BuildConfig.DEBUG) {

                    String jsonRecu = response.body().string();
                    Logger.logJson("TAG_JSON_RECU", jsonRecu);
                    answer = gson.fromJson(jsonRecu, GenericAnswerBean.class);
                }

                else {
                    //JSON -> Java (Parser une ArrayList typée)
                    answer = gson.fromJson(new InputStreamReader(response.body().byteStream()), GenericAnswerBean.class);
                }
            }
            catch (Exception e) {
                throw new TechnicalException("Erreur lors du parsing Json", e);
            }

            //On analyse la réponse
            answer.checkError("/smsReceived");
        }
    }



     /* ---------------------------------
    // private
    // -------------------------------- */

    static TechnicalException testInternetConnexionOnGoogle(IOException e) {
        //On test si google répond pour différencier si c'est internet ou le serveur le probleme
        Request request = request = new Request.Builder().url("https://www.google.fr").build();
        try {

            new OkHttpClient.Builder().connectTimeout(2, TimeUnit.SECONDS).build().newCall(request).execute();
            //Ca marche -> C'est le serveur le probleme
            return new TechnicalException("L'url ne répond pas", e);
        }
        catch (Exception e1) {
            //Ca crash encore -> problème d'internet
            return new TechnicalException("Bande passante insufisante", e1);
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
