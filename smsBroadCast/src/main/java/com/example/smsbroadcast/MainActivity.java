package com.example.smsbroadcast;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

    // Pointeur vers le Boradcast
    private BroadcastReceiver receiver;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.tv);

        // Création du Broadcast en Java
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //do something
                if (intent.getBooleanExtra("state", false)) {
                    tv.setText("ModeAvion désactivé");
                }
                else {
                    tv.setText("ModeAvion activé");
                }
            }
        };

        // S’abonner au BroadCast
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Se désabonner sinon fuite mémoire
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
    }
}
