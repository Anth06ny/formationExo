package com.example.servicebinding2;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.servicebinding2.services.UpdateDataService;

public class MainActivity extends Activity implements OnClickListener {

    //composant graphique
    private TextView tv;

    // ServiceConnection permet de gérer l'état du lien entre l'activité et le service.
    private ServiceConnection serviceConnection;

    //L'instance du service
    private UpdateDataService updateDataService;

    //-----------------------
    //View
    //-------------------
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tv.setText("Activité prête");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Sinon impossible de tuer l'activité
        if (serviceConnection != null) {
            unbindService(serviceConnection);
        }
        updateDataService = null;
        serviceConnection = null;
    }

    public void updateTextView(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText(text);
            }
        });
    }

    /**
     * met à jour l'état du service sur le TextView
     */
    public void refreshServiceValue() {
        if (updateDataService != null) {
            updateTextView(updateDataService.getServiceTimeExecutionInSecond() + "");
        }
        else {
            updateTextView("Aucun service bindé");
        }
    }

    /* ---------------------------------
    // Service
    // -------------------------------- */

    /**
     * Lance ou récupère l'instance du service
     */
    private void bindToService() {

        if (serviceConnection == null) {

            serviceConnection = new ServiceConnection() {

                //le service s'est déconnecté
                public void onServiceDisconnected(ComponentName name) {
                    updateDataService = null;
                    if (MainActivity.this != null) {
                        Toast.makeText(MainActivity.this, "Déconnecté du service", Toast.LENGTH_LONG).show();
                        updateTextView("Le service s'est arreté");
                    }
                }

                //le service se connecte
                public void onServiceConnected(ComponentName arg0, IBinder binder) {
                    //on récupère l'instance du service dans l'activité
                    updateDataService = ((UpdateDataService.UpdateDataServiceBinder) binder).getUpdateDataService();

                    Toast.makeText(MainActivity.this, "Service bindé", Toast.LENGTH_LONG).show();

                    refreshServiceValue();
                }
            };
        }
        //démarre le service s'il n'est pas démarré
        //Le binding du service est configuré avec "BIND_AUTO_CREATE" ce qui normalement
        //démarre le service s'il n'est pas démarré, la différence ici est que le fait de
        //démarrer le service par "startService" fait que si l'activité est détruite, le service
        //reste en vie
        startService(new Intent(this, UpdateDataService.class));

        Intent intent = new Intent(this, UpdateDataService.class);
        //lance le binding du service
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        updateTextView("Démarrage du service");
    }

    private void stopService() {
        updateTextView("Arret du service");
        //on detruit le service
        if (updateDataService != null) {
            updateDataService.stopSelf();
            updateDataService = null;
        }
        if (serviceConnection != null) {
            unbindService(serviceConnection);
            serviceConnection = null;
        }
    }

    /* ---------------------------------
    // Evenements
    // -------------------------------- */

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.startService) {
            bindToService();
        }
        else if (v.getId() == R.id.stopService) {
            stopService();
        }
        else if (v.getId() == R.id.updateTime) {
            refreshServiceValue();
        }
        else if (v.getId() == R.id.newActivity) {
            //On crée une nouvelle Activité ou on récuperera le même service déjà lancé
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}
