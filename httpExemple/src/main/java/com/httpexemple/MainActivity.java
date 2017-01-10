package com.httpexemple;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.formation.utils.ConnectivityUtils;
import com.formation.utils.OkHttpUtils;
import com.formation.utils.ToastUtils;

public class MainActivity extends Activity implements View.OnClickListener {

    private EditText et;
    private Button btLoad;
    private TextView tvResultat;
    private WebView webviewResultat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et = (EditText) findViewById(R.id.et);
        btLoad = (Button) findViewById(R.id.bt_load);
        tvResultat = (TextView) findViewById(R.id.tv_resultat);

        webviewResultat = (WebView) findViewById(R.id.webview_resultat);

        //Reglages de la webView
        WebSettings webviewSettings = webviewResultat.getSettings();
        webviewSettings.setJavaScriptEnabled(true);

        webviewResultat.setWebViewClient(new WebViewClient());

        btLoad.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideKeypad();
    }

    private void hideKeypad() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
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

            if (!ConnectivityUtils.isConnected(this)) {
                ToastUtils.showToastOnUIThread(this, "Pas de connexion internet", Toast.LENGTH_LONG);
            }
            else {
                String url = et.getText().length() == 0 ? et.getHint().toString() : et.getText().toString();

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
        private ProgressDialog progressDialog;

        public LoadPageAT(String url) {
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MainActivity.this, "", "Chargement...");
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                return OkHttpUtils.sendGetOkHttpRequest(url);
            }
            catch (Exception e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            tvResultat.setText(s);
            progressDialog.cancel();
        }
    }
}
