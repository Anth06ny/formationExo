package com.example.servicetest.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.example.servicetest.MyApplication;
import com.squareup.otto.Subscribe;

import java.util.Timer;
import java.util.TimerTask;

public class BackgroundService extends Service implements LocationListener {

    private Timer timer;
    private LocationManager locationMgr = null;

    //-----------------------
    //View
    //-------------------
    @Override
    public void onCreate() {
        super.onCreate();
        MyApplication.getEventBus().register(this);

        timer = new Timer();
        locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //On teste si le provider existe avant de s'y abonner, sinon ca plante.
        if (locationMgr.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
            locationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, this);
        }

        if (locationMgr.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
            locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
        }
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Toast.makeText(this, "Service start", Toast.LENGTH_SHORT).show();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //faire quelque chose toutes les 6 secondes indéfiniment
                //1er execution au bout de 1seconde
                //ToastUtils.showToastOnUIThread(BackgroundService.this, "test");
            }
        }, 1000, 6000);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //desabonne
        MyApplication.getEventBus().unregister(this);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationMgr.removeUpdates(this);
        }

        this.timer.cancel();
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    //-----------------------
    // LocationListener
    //-------------------

    @Override
    public void onLocationChanged(final Location location) {
        final Double latitude = location.getLatitude();
        final Double longitude = location.getLongitude();

        Toast.makeText(this, "Coordonnées : lat=" + latitude + " Lon=" + longitude, Toast.LENGTH_SHORT).show();

        //Par le biais d'otto on envoie un resultat à toutes activitées qui ecoutent ce type de poste
        MyApplication.getEventBus().post(new BackgroundServiceEvent(latitude, longitude));
    }

    @Override
    public void onStatusChanged(final String provider, final int status, final Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(final String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderDisabled(final String provider) {
        // TODO Auto-generated method stub

    }

    //-----------------------
    // StopEvent
    //-------------------

    /**
     * Permet par le biais d'otto d'arreter le service
     *
     * @param event
     */
    @Subscribe
    public void onBackgroundServiceCallBack(final StopBackgroundServiceEvent event) {
        if (event.stopService) {
            Toast.makeText(this, "Le service s'arrete", Toast.LENGTH_SHORT).show();
            stopSelf();
        }
        else if (event.restartService) {
            //do what you whant
        }
    }
}
