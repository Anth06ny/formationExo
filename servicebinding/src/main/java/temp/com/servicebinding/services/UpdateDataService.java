package temp.com.servicebinding.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class UpdateDataService extends Service {

    private IBinder iBinder = null; //l'instance du binder correspondant à notre service

    //Note l'heure de création du service
    private long creationTime;

    //-----------------------
    // Service
    //-------------------
    @Override
    public void onCreate() {
        super.onCreate();

        //au démarrage du service, on créé le binder en envoyant le service
        iBinder = new UpdateDataServiceBinder(this);
        creationTime = System.currentTimeMillis();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return iBinder;
    }

    /* ---------------------------------
    // Method
    // -------------------------------- */

    /**
     *
     * @return Le temps en second d'existence du service
     */
    public long getServiceTimeExecutionInSecond() {

        long difference = System.currentTimeMillis() - creationTime;
        return difference / 1000;
    }

    /* ---------------------------------
    // Binder
    // -------------------------------- */

    public class UpdateDataServiceBinder extends Binder {

        private UpdateDataService updateDataService;

        //on recoit l'instance du service
        public UpdateDataServiceBinder(UpdateDataService updateDataService) {
            super();
            this.updateDataService = updateDataService;
        }

        /* ---------------------------------
        // Getter / Setter
        // -------------------------------- */

        /** @return l'instance du service */
        public UpdateDataService getUpdateDataService() {
            return updateDataService;
        }
    }
}
