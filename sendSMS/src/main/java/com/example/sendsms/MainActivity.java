package com.example.sendsms;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sendsms.broadcast.MultipleSendSMSBR;
import com.example.sendsms.broadcast.SMSSentListener;
import com.formation.utils.exceptions.TechnicalException;
import com.squareup.otto.Subscribe;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;

import static com.example.sendsms.broadcast.MultipleSendSMSBR.SENT_SMS_ACTION_NAME;

public class MainActivity extends Activity implements OnClickListener {

    private Button buttonSend, btCharger;
    private TextView tvNbCharger;
    private ProgressDialog waintingDialog;
    private EditText editTextSMS, etUrl;
    private TextView tvResultat;

    //data
    ArrayList<TelephoneBean> telephoneBeans;

    //outils
    private MultipleSendSMSBR multipleSendSMSBR;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSend = findViewById(R.id.buttonSend);
        btCharger = findViewById(R.id.btCharger);
        editTextSMS = findViewById(R.id.editTextSMS);
        tvNbCharger = findViewById(R.id.tvNbCharger);
        tvResultat = findViewById(R.id.tvResultat);
        etUrl = findViewById(R.id.etUrl);

        telephoneBeans = new ArrayList<>();

        buttonSend.setOnClickListener(this);
        btCharger.setOnClickListener(this);

        multipleSendSMSBR = new MultipleSendSMSBR(telephoneBeans);
        //abonnement au broadcast
        registerReceiver(multipleSendSMSBR, new IntentFilter(SENT_SMS_ACTION_NAME));

        refreshScreen();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        }

        MyApplication.getBus().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (waintingDialog != null) {
            waintingDialog.dismiss();
            waintingDialog = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(multipleSendSMSBR);
        MyApplication.getBus().unregister(this);
    }

    @Subscribe
    public void messageSend(boolean ok) {
        tvResultat.append(new Date().getTime() + " : " + ok + "\n");
    }

    @Subscribe
    public void messageSend(Boolean ok) {
        tvResultat.append(new Date().getTime() + " : " + ok + "\n");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //On boucle tant qu'on n'a pas la permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        }
    }

    @Override
    public void onClick(final View v) {
        if (buttonSend == v) {

            //            final String phoneNo = textPhoneNo.getText().toString();
            //            final String sms = textSMS.getText().toString();
            //
            //            SMSSentListener.sendSMS(this, phoneNo, sms);
            if (!SMSSentListener.canSendSMS(this)) {
                Toast.makeText(this, "Le device ne permet pas l'envoie de SMS", Toast.LENGTH_LONG).show();
            }
            else if (telephoneBeans.isEmpty()) {
                Toast.makeText(this, "Aucun numéros chargés", Toast.LENGTH_LONG).show();
            }
            else if (StringUtils.isBlank(editTextSMS.getText())) {
                Toast.makeText(this, "Message vide", Toast.LENGTH_LONG).show();
            }
            else {
                tvResultat.setText("");
                new SendSmsAT(editTextSMS.getText().toString()).execute();
            }
        }
        else if (v == btCharger) {
            new MonAT(etUrl.getText().toString()).execute();
        }
    }

    public void refreshScreen() {
        tvNbCharger.setText(telephoneBeans.size() + " numéro(s) chargé(s)");
    }

    /* ---------------------------------
    // ASYNC TASK CHarge les numéros
    // -------------------------------- */
    public class MonAT extends AsyncTask {

        String url;
        Exception exception;

        public MonAT(String url) {
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            waintingDialog = ProgressDialog.show(MainActivity.this, "", "Chargement en cours...");
            telephoneBeans.clear();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                telephoneBeans.addAll(WSUtils.getPhones(url));
            }
            catch (TechnicalException e) {
                exception = e;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (waintingDialog != null) {
                waintingDialog.dismiss();
                waintingDialog = null;
            }

            if (exception != null) {
                Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(MainActivity.this, telephoneBeans.size() + " numéros chargés", Toast.LENGTH_SHORT).show();
            }

            refreshScreen();
        }
    }

    public class SendSmsAT extends AsyncTask {

        String message;

        public SendSmsAT(String message) {
            this.message = message;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            waintingDialog = ProgressDialog.show(MainActivity.this, "", "Envoie en cours...");
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            MultipleSendSMSBR.sendSMS(MainActivity.this, telephoneBeans, message);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (waintingDialog != null) {
                waintingDialog.dismiss();
                waintingDialog = null;
            }

            Toast.makeText(MainActivity.this, "Envoie terminé", Toast.LENGTH_SHORT).show();
        }
    }
}
