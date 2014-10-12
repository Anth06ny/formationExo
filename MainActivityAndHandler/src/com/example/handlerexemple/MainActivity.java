package com.example.handlerexemple;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends CommonActivity implements OnClickListener {

    private Button bt;

    //------------------------
    // View
    //------------------------
    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        // Cette méthode est a appeler APRES la récuperation des extras
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);

        bt = (Button) findViewById(R.id.bt);
        bt.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //------------------------
    // click
    //------------------------

    @Override
    public void onClick(final View v) {
        //lancement de la fenetre d'attente
        new Thread(new Runnable() {

            @Override
            public void run() {
                SystemClock.sleep(5000);
                //fin du lancement de la fenetre d'attente
            }
        }).start();
    }
}
