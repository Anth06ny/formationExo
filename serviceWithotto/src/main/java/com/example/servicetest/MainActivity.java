package com.example.servicetest;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.servicetest.services.BackgroundService;
import com.example.servicetest.services.BackgroundServiceEvent;
import com.example.servicetest.services.StopBackgroundServiceEvent;
import com.squareup.otto.Subscribe;

public class MainActivity extends Activity implements OnClickListener {

    private static final int FINE_LOCATION_REQ_CODE = 1;
    private TextView tv;

    //-----------------------
    //View
    //-------------------
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.tv);

        //on s'inscrit pour un retour de otto
        MyApplication.getEventBus().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopService();

        //on se desinscrit d'otto
        MyApplication.getEventBus().unregister(this);
    }

    //-----------------------
    //Service Otto
    //-------------------

    @Subscribe
    public void onBackgroundServiceCallBack(final BackgroundServiceEvent event) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                final String text = "Coordonnées : " + event.getLatitude() + " " + event.getLongitude();
                tv.setText(text);
                Toast.makeText(getBaseContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void stopService() {
        //on detruit le service
        MyApplication.getEventBus().post(new StopBackgroundServiceEvent(true, false));
    }

    //-----------------------
    //View
    //-------------------
    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.startService) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                final Intent intent = new Intent(this, BackgroundService.class);
                startService(intent);
            }
            else {
                Toast.makeText(MainActivity.this, "La permission est nécéssaire pour lancer le service !!",
                        Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_REQ_CODE);
            }
        }
        else if (v.getId() == R.id.stopService) {
            stopService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_REQ_CODE) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                final Intent intent = new Intent(this, BackgroundService.class);
                startService(intent);
            }
            else {
                Toast.makeText(MainActivity.this, "La permission est nécéssaire pour lancer le service !!",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
