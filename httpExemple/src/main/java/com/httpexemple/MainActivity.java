package com.httpexemple;

import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.formation.utils.HTTPUtils;
import com.formation.utils.PopupsManager;
import com.formation.utils.ToastUtils;

import java.io.IOException;

public class MainActivity extends Activity implements View.OnClickListener {

    private EditText et;
    private Button btLoad;
    private TextView tvResultat;
    private WebView webviewResultat;

    private Dialog waintingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et = (EditText) findViewById(R.id.et);
        btLoad = (Button) findViewById(R.id.bt_load);
        tvResultat = (TextView) findViewById(R.id.tv_resultat);

        webviewResultat = (WebView) findViewById(R.id.webview_resultat);

        WebSettings webviewSettings = webviewResultat.getSettings();
        webviewSettings.setUseWideViewPort(true);
        webviewSettings.setJavaScriptEnabled(true);
        webviewSettings.setBuiltInZoomControls(true);
        webviewSettings.setLoadWithOverviewMode(true);
        webviewSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);

        waintingDialog = PopupsManager.createProgressPopup(this, getString(R.string.loading));

        btLoad.setOnClickListener(this);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2014-11-11 13:02:58 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == btLoad) {

            if (!HTTPUtils.isInternetConnexion(this)) {
                ToastUtils.showToastOnUIThread(this, "Pas de connexion internet", Toast.LENGTH_LONG);
            }
            else {
                String url = et.getText().length() == 0 ? et.getHint().toString() : et.getText().toString();

                waintingDialog.show();

                //utilise le mechanisme des handler on peut lancer l'appel sur le main thread
                webviewResultat.loadUrl(url);

                //mais pas pour HttpURLConnection
                new LoadPageAT(url).execute();
            }
        }
    }

    /**
     * AsyncTask pour charger une url en dehors du main thread
     */
    public class LoadPageAT extends AsyncTask<Void, Void, String> {

        private String url;

        public LoadPageAT(String url) {
            this.url = url;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                return HTTPUtils.downloadUrl(url);
            }
            catch (IOException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            tvResultat.setText(s);

            waintingDialog.cancel();

        }
    }

}
