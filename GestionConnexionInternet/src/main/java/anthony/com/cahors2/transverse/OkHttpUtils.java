package anthony.com.cahors2.transverse;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;

import anthony.com.cahors2.R;
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

    public static boolean isAirplaneModeOn(Context context) {
        return Settings.Global.getInt(context.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }

    public static boolean ping(Context context) throws MyException {

        //Mode avion
        if (isAirplaneModeOn(context)) {
            throw new MyException(R.string.airplane);
        }

        //Non connecté à un réseau
        ConnectivityManager CManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo NInfo = CManager.getActiveNetworkInfo();
        if (NInfo == null || !NInfo.isConnectedOrConnecting()) {
            throw new MyException(R.string.noreseau);
        }

        String adresseClient = context.getString(R.string.ipClient);
        //On tente l'ip du client
        try {
            if (InetAddress.getByName(adresseClient).isReachable(2000)) {
                //Ca marche
                return true;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        //ca ne marche pas, on tente google si
        try {
            if (InetAddress.getByName(context.getString(R.string.google)).isReachable(2000)) {
                //ca marche c'est que c'est un problème du serveur
                throw new MyException(R.string.error_server_client);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        //Ca ne marche pas, internet inexistant ou trop faible
        throw new MyException(R.string.internet_faible);
    }

    public static String sendGetOkHttpRequest(String url) throws Exception {
        Log.w("TAG", url);
        //OkHttpClient client = new OkHttpClient();
        OkHttpClient client = new OkHttpClient.Builder().build();

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

        return new OkHttpClient.Builder()
                .build();
    }
}
