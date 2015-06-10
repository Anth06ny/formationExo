package com.example.exemple;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;

public class MainActivity extends Activity implements OnClickListener {

    private TextView tv_heure;
    private Button bt_go;

    //----------------
    // view
    //----------------

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_heure = (TextView) findViewById(R.id.tv_heure);
        bt_go = (Button) findViewById(R.id.bt_go);

        bt_go.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("TempsDebug", "MainActivity.onResume");
        SystemClock.sleep(1000);
        tv_heure.setText("Heure : " + new Date().getTime());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //----------------
    // click
    //----------------

    @Override
    public void onClick(final View v) {

        final Intent intent = new Intent(this, SecondActivity.class);
        intent.putExtra(Constante.EXTRA_SECOND_ACTIVITY_MSG, "Hello from MainActivity");
        startActivity(intent);

    }

}
