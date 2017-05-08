package temp.com.servicebinding;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.formation.utils.ToastUtils;

import temp.com.servicebinding.services.UpdateDataService;

public class MainActivity extends Activity implements OnClickListener {

    //composant graphique
    private TextView tv;

    // ServiceConnection permet de gérer l'état du lien entre l'activité et le service.
    private ServiceConnection serviceConnection;

    //L'instance du service
    private UpdateDataService updateDataService;

    private Intent serviceIntent;

    //-----------------------
    //View
    //-------------------
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);

        serviceIntent = new Intent(this, UpdateDataService.class);

        serviceConnection = new ServiceConnection() {

            //le service s'est déconnecté
            public void onServiceDisconnected(ComponentName name) {
                updateDataService = null;
                if (MainActivity.this != null) {
                    ToastUtils.showToast(MainActivity.this, "Déconnecté du service", Toast.LENGTH_LONG);
                    updateTextView("Le service s'est arreté");
                }
            }

            //le service se connecte
            public void onServiceConnected(ComponentName arg0, IBinder binder) {
                //on récupère l'instance du service dans l'activité
                updateDataService = ((UpdateDataService.UpdateDataServiceBinder) binder).getUpdateDataService();

                ToastUtils.showToast(MainActivity.this, "Service bindé", Toast.LENGTH_LONG);

                refreshServiceValue();
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Je me bind au service au cas ou il existe
        bindService(serviceIntent, serviceConnection, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(serviceConnection);
        updateDataService = null;
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
    // Evenements
    // -------------------------------- */

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.startService) {
            startService(serviceIntent);
            bindService(serviceIntent, serviceConnection, 0);
        }
        else if (v.getId() == R.id.stopService) {
            updateTextView("Arret du service");
            if (updateDataService != null) {
                updateDataService.stopSelf();
                updateDataService = null;
            }
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
