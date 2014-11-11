package com.example.handlerexemplesolution;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.formation.utils.PopupsManager;

public class CommonActivity extends Activity {

    private Dialog progressDialog;
    private Handler handler;

    //------------------------
    // View
    //------------------------

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_common_layout);
        handler = getHandler();

        progressDialog = PopupsManager.createProgressPopup(this, getString(R.string.loading));
    }

    /**
     * Override of the setContentView Method to 'inject' the layout passed as parameter into a frameLayout, with this layout organization, UpActivity will
     * provide facilities to put/remove progressBar whenever needed + generic behaviour for Back and Home button / Title of the screen
     *
     * @see android.app.Activity#setContentView(int)
     */
    @Override
    public void setContentView(final int layoutResID) {
        // called by activity implementing UPactivity
        final View v = getLayoutInflater().inflate(layoutResID, null);
        setContentView(v);

    }

    /**
     * Override of the setContentView Method to 'inject' the layout passed as parameter into a frameLayout, with this layout organization, UpActivity will
     * provide facilities to put/remove progressBar whenever needed + generic behaviour for Back and Home button / Title of the screen
     *
     * @see android.app.Activity#setContentView(View)
     */
    @Override
    public void setContentView(final View v) {
        // called by activity implementing UPactivity
        final FrameLayout container = (FrameLayout) findViewById(R.id.container);
        container.removeAllViews();
        container.addView(v);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler = null;
    }

    //------------------------
    // Update
    //------------------------
    /**
     * Update the screen title (this could occur during an async update of the underlying screen)
     *
     * @param title
     */
    public void updateScreenTitle(final String title) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                ((TextView) findViewById(R.id.screenTitle)).setText(title);
            }
        });
    }

    //------------------------
    // Handler ProgressBar
    //------------------------

    private static final int MSG_START_PROGRESS = 0;
    private static final int MSG_STOP_PROGRESS = 1;

    private int progressCount = 0;

    /** Handler pemettant de gerer certains evenements. */
    private Handler getHandler() {
        return new Handler() {

            @Override
            public void handleMessage(final Message msg) {

                switch (msg.what) {
                    case MSG_START_PROGRESS:
                        if (!progressDialog.isShowing()) {
                            progressDialog.show();
                        }
                        break;
                    case MSG_STOP_PROGRESS:
                        if (progressDialog.isShowing()) {
                            progressDialog.cancel();
                        }
                        break;

                    default:
                        break;
                }
            }
        };
    }

    /**
     * Afficher l'icone de progression. A utiliser typiquement lorsque des appels réseau sont en cours. Le nombre d'appel est comptabilisé et il faudra un même
     * nombre d'appel à stopProgress pour retirer l'icone
     */
    public void startProgress() {
        progressCount++;
        handler.sendEmptyMessage(MSG_START_PROGRESS);
    }

    /**
     * Enlever l'icone de progression. cf startProgress : l'icone ne sera retiré que s'il y a un même nombre d'appel qu'à startProgress
     */
    public void stopProgress() {
        if (progressCount == 0) {
            Log.e("Debug", "nombre d'appels de stop/start progress incorrect ...");
            return;
        }
        progressCount--;
        if (progressCount == 0) {
            handler.sendEmptyMessage(MSG_STOP_PROGRESS);
        }
    }

    public void forceStopProgress() {
        progressCount = 0;
        handler.sendEmptyMessage(MSG_STOP_PROGRESS);
    }

}
