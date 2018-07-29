package anthony.com.smsmmsbomber;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.formation.utils.exceptions.ExceptionA;
import com.squareup.otto.Subscribe;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;

import anthony.com.smsmmsbomber.model.CampagneBean;
import anthony.com.smsmmsbomber.model.WSUtils;
import anthony.com.smsmmsbomber.model.dao.TelephoneDaoManager;
import anthony.com.smsmmsbomber.service.SendMessageService;
import anthony.com.smsmmsbomber.utils.Permissionutils;
import anthony.com.smsmmsbomber.utils.SharedPreferenceUtils;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private Button buttonSend, btCharger, btStopService, btResetCampagneId;
    private TextView tvNbCharger;
    private ProgressDialog waintingDialog;
    private EditText etUrl;
    private TextView tvResultat;
    private ImageView iv;

    private CampagneBean campagneBean;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSend = findViewById(R.id.buttonSend);
        btCharger = findViewById(R.id.btCharger);
        tvNbCharger = findViewById(R.id.tvNbCharger);
        tvResultat = findViewById(R.id.tvResultat);
        btStopService = findViewById(R.id.btStopService);
        btResetCampagneId = findViewById(R.id.btResetCampagneId);
        iv = findViewById(R.id.iv);
        etUrl = findViewById(R.id.etUrl);

        buttonSend.setOnClickListener(this);
        btCharger.setOnClickListener(this);
        btStopService.setOnClickListener(this);
        btResetCampagneId.setOnClickListener(this);

        etUrl.setText(SharedPreferenceUtils.getSaveURL(this));

        refreshScreen();

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
    protected void onResume() {
        super.onResume();

        //On check les permissions
        Permissionutils.requestAllPermissionIfNot(this);
        Permissionutils.makeDefautSmsApp(this);
        //On lance le service au cas ou il ne soit pas deja lancé
        SendMessageService.startservice(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getBus().unregister(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //on boucle
        //On check les permissions
        Permissionutils.requestAllPermissionIfNot(this);
    }


    /* ---------------------------------
    // Click
    // -------------------------------- */

    @Override
    public void onClick(final View v) {
        if (buttonSend == v) {
            SendMessageService.startservice(this);
        }
        else if (v == btCharger) {
            SharedPreferenceUtils.saveURL(this, etUrl.getText().toString());
            new MonAT(etUrl.getText().toString()).execute();
        }
        else if (btStopService == v) {
            SendMessageService.stopService(this);
        }
        else if (btResetCampagneId == v) {
            //On supprime l'ancienne campagne de la base
            TelephoneDaoManager.getDao().deleteAll();
            SharedPreferenceUtils.saveLastCampagneId(this, -1);
            Log.w("TAG_CAMPAGNE", "Reset du capagneId");
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
        ExceptionA exception;
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
            catch (ExceptionA e) {
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
