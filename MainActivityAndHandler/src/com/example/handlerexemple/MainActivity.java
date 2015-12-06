package com.example.handlerexemple;

import android.os.Bundle;
import android.os.SystemClock;
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
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);

        bt = (Button) findViewById(R.id.bt);
        bt.setOnClickListener(this);

    }

    //------------------------
    // click
    //------------------------

    @Override
    public void onClick(final View v) {
        //lancement de la fenetre d'attente
        //TODO : Utiliser les methodes de la main activity pour afficher une fenetre d'attente

        new Thread(new Runnable() {

            @Override
            public void run() {
                SystemClock.sleep(5000);
                //fin du lancement de la fenetre d'attente
                //TODO : Utiliser les methodes de la main activity pour stopper la fenetre d'attente
            }
        }).start();
    }
}
