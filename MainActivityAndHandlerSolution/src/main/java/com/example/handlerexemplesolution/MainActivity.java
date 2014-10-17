package com.example.handlerexemplesolution;

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

        // Cette méthode est a appeler APRES la récuperation des extras
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
        startProgress();
        new Thread(new Runnable() {

            @Override
            public void run() {
                SystemClock.sleep(5000);
                stopProgress();
            }
        }).start();
    }
}
