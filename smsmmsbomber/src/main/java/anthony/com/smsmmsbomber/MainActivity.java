package anthony.com.smsmmsbomber;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.formation.utils.exceptions.TechnicalException;
import com.google.gson.Gson;
import com.squareup.otto.Subscribe;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;

import anthony.com.smsmmsbomber.broadcast.MultipleSendSMSBR;
import anthony.com.smsmmsbomber.broadcast.SMSSentListener;
import anthony.com.smsmmsbomber.model.CampagneBean;
import anthony.com.smsmmsbomber.model.TelephoneBean;
import anthony.com.smsmmsbomber.model.WSUtils;
import anthony.com.smsmmsbomber.service.SendMessageService;
import anthony.com.smsmmsbomber.utils.SharedPreferenceUtils;

import static anthony.com.smsmmsbomber.broadcast.MultipleSendSMSBR.SENT_SMS_ACTION_NAME;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private static final int PHOTO_PICKER_ID = 1;

    private Button buttonSend, btCharger, btStopService;
    private TextView tvNbCharger;
    private ProgressDialog waintingDialog;
    private EditText etUrl;
    private TextView tvResultat;
    private ImageView iv;

    private CampagneBean campagneBean;

    //outils
    private MultipleSendSMSBR multipleSendSMSBR;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSend = findViewById(R.id.buttonSend);
        btCharger = findViewById(R.id.btCharger);
        tvNbCharger = findViewById(R.id.tvNbCharger);
        tvResultat = findViewById(R.id.tvResultat);
        btStopService = findViewById(R.id.btStopService);
        iv = findViewById(R.id.iv);
        etUrl = findViewById(R.id.etUrl);

        buttonSend.setOnClickListener(this);
        btCharger.setOnClickListener(this);
        btStopService.setOnClickListener(this);

        etUrl.setText(SharedPreferenceUtils.getSaveURL(this));

        multipleSendSMSBR = new MultipleSendSMSBR();
        //abonnement au broadcast
        registerReceiver(multipleSendSMSBR, new IntentFilter(SENT_SMS_ACTION_NAME));

        refreshScreen();

        MyApplication.getBus().register(this);

        CampagneBean campagneBean = new CampagneBean();
        campagneBean.setMessage("Bonjour la compagnie");
        TelephoneBean telephoneBean = new TelephoneBean();
        telephoneBean.setNumero("06066006");
        ArrayList<TelephoneBean> list = new ArrayList<>();
        list.add(telephoneBean);
        list.add(telephoneBean);
        list.add(telephoneBean);
        campagneBean.setTelephoneBeans(list);
        campagneBean.setVideo(true);
        campagneBean.setUrlFile("https://ljdchost.com/fWb9tHt.gif");

        Log.w("TAG_JSON", new Gson().toJson(campagneBean));
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
    protected void onResume() {
        super.onResume();

        checkPermission();
        makeDefautSmsApp();
        //On lance le service au cas ou il ne soit pas deja lancé
        SendMessageService.startservice(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(multipleSendSMSBR);
        MyApplication.getBus().unregister(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //on boucle
        checkPermission();
    }


    /* ---------------------------------
    // Click
    // -------------------------------- */

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
            else if (campagneBean == null || campagneBean.getTelephoneBeans() == null || campagneBean.getTelephoneBeans().isEmpty()) {
                Toast.makeText(this, "Aucun numéros chargés", Toast.LENGTH_LONG).show();
            }
            else {
                try {
                    if (CampagneBean.isCampagneReady(campagneBean)) {
                        Toast.makeText(this, "Message vide", Toast.LENGTH_LONG).show();
                    }
                    else {
                        SendMessageService.startservice(this);
                    }
                }
                catch (TechnicalException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
        else if (v == btCharger) {
            SharedPreferenceUtils.saveURL(this, etUrl.getText().toString());
            new MonAT(etUrl.getText().toString()).execute();
        }
        else if (btStopService == v) {
            SendMessageService.stopService(this);
        }
    }

    /* ---------------------------------
    // Callback otto
    // -------------------------------- */

    @Subscribe
    public void messageSend(boolean ok) {
        tvResultat.append(new Date().getTime() + " : " + ok + "\n");
    }

    @Subscribe
    public void messageSend(Boolean ok) {
        tvResultat.append(new Date().getTime() + " : " + ok + "\n");
    }





    /* ---------------------------------
    // private
    // -------------------------------- */

    private void makeDefautSmsApp() {
        final String myPackageName = getPackageName();
        if (!Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName)) {
            Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, myPackageName);
            startActivity(intent);
        }
    }

    private void checkPermission() {
        //On check les permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    private void refreshScreen() {

        if (campagneBean == null || campagneBean.getTelephoneBeans() == null || campagneBean.getTelephoneBeans().isEmpty()) {
            tvNbCharger.setText("Aucun numéro chargé");
        }
        else {
            tvNbCharger.setText(campagneBean.getTelephoneBeans().size() + " numéro(s) chargé(s)\n" + (StringUtils.isNotBlank(campagneBean.getUrlFile()) ? " Mode MMS" : " Mode SMS"));
        }

        //Si on a une image
        if (campagneBean != null && campagneBean.getBitmap() != null) {
            iv.setImageBitmap(campagneBean.getBitmap());
        }
    }

    /* ---------------------------------
    // ASYNC TASK Charge la campagne
    // -------------------------------- */
    public class MonAT extends AsyncTask {

        String url;
        Exception exception;
        CampagneBean cb;

        public MonAT(String url) {
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            waintingDialog = ProgressDialog.show(MainActivity.this, "", "Chargement en cours...");
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                cb = WSUtils.getCampagnes(MainActivity.this);
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
            campagneBean = cb;

            if (exception != null) {
                Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
            }

            refreshScreen();
        }
    }
}
