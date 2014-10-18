package com.example.exemple.dao.asynctask;

import android.os.AsyncTask;
import android.os.SystemClock;

import com.example.exemple.bean.Eleve;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChargementEleveAT extends AsyncTask<Void, Integer, String> {

    private final CallBack callBack;
    private final ArrayList<Eleve> eleveList;
    private final Random random;
    private int nbrEleve;

    //--------------------
    // AT
    //-------------------

    public ChargementEleveAT(final CallBack callBack) {
        this.callBack = callBack;
        eleveList = new ArrayList<Eleve>();
        random = new Random();
    }

    @Override
    /**
     * Pre chargement avant l'execution
     * Peut etre sur l'UIThread
     */
    protected void onPreExecute() {
        super.onPreExecute();
        nbrEleve = random.nextInt(10);
    }

    @Override
    /**
     * traitement
     * Jamais sur l'UIThread
     */
    protected String doInBackground(final Void... arg0) {
        //Appel WS avec attente
        if (random.nextInt(3) > 0) {
            for (int i = 0; i < nbrEleve; i++) {
                eleveList.add(new Eleve("Jean" + i, "Pierre" + i, i % 2 == 0));
                publishProgress(i);
                SystemClock.sleep(1000);

            }
            return null;
        }
        else {
            //Simulation de l'echec
            SystemClock.sleep(2000);
            return "Pas de chance cela à échoué";
        }
    };

    @Override
    /**
     * Mise a jour de la valeur d'attente
     * SUR lUIThread
     */
    protected void onProgressUpdate(final Integer... values) {
        super.onProgressUpdate(values);
        if (callBack != null) {
            callBack.updateChargement(nbrEleve, values[0]);
        }
    };

    @Override
    /**
     * traitement post execution
     * Peut etre sur l'UIThread
     */
    protected void onPostExecute(final String messageErreur) {

        if (messageErreur == null) {
            if (callBack != null) {
                callBack.eleveLoad(eleveList);
            }
        }
        else {
            if (callBack != null) {
                callBack.loadFail(messageErreur);
            }
        }
    };

    //--------------------
    // Interface
    //-------------------

    public interface CallBack {
        //succes
        void eleveLoad(List<Eleve> eleve);

        //fail
        void loadFail(String message);

        void updateChargement(int max, int current);
    }

}
