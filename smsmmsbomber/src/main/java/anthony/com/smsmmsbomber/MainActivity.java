package anthony.com.smsmmsbomber;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.formation.utils.exceptions.ExceptionA;

import org.apache.commons.lang3.StringUtils;

import anthony.com.smsmmsbomber.model.CampagneBean;
import anthony.com.smsmmsbomber.model.WSUtils;
import anthony.com.smsmmsbomber.model.dao.TelephoneDaoManager;
import anthony.com.smsmmsbomber.service.SendMessageService;
import anthony.com.smsmmsbomber.utils.Permissionutils;
import anthony.com.smsmmsbomber.utils.SharedPreferenceUtils;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private ProgressDialog waintingDialog;
    private EditText etUrlLoad, etUrlSend, etUrlSendAnswer, etDelay;
    private TextView tvInfo;
    private ImageView iv;

    private CampagneBean campagneBean;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvInfo = findViewById(R.id.tvInfo);
        etUrlSend = findViewById(R.id.etUrlSend);
        etUrlSendAnswer = findViewById(R.id.etUrlSendAnswer);
        etDelay = findViewById(R.id.etDelay);
        iv = findViewById(R.id.iv);
        etUrlLoad = findViewById(R.id.etUrlLoad);

        refreshScreen();
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
        if (v.getId() == R.id.btSaveUrlLoad) {
            SharedPreferenceUtils.saveUrlLoad(this, etUrlLoad.getText().toString());
            Toast.makeText(this, "Url sauvegardée", Toast.LENGTH_SHORT).show();
        }
        else if (v.getId() == R.id.btTest) {
            new MonAT(etUrlLoad.getText().toString()).execute();
        }
        else if (v.getId() == R.id.btSaveUrlSend) {
            SharedPreferenceUtils.saveUrlSendResult(this, etUrlSend.getText().toString());
            Toast.makeText(this, "Url sauvegardée", Toast.LENGTH_SHORT).show();
        }
        else if (v.getId() == R.id.btSaveAnswerUrl) {
            SharedPreferenceUtils.saveUrlSendAnswer(this, etUrlSendAnswer.getText().toString());
            Toast.makeText(this, "Url sauvegardée", Toast.LENGTH_SHORT).show();
        }
        else if (R.id.btStartService == v.getId()) {
            SendMessageService.startservice(this);
        }
        else if (R.id.btStopService == v.getId()) {
            SendMessageService.stopService(this);
        }
        else if (R.id.btResetCampagneId == v.getId()) {
            //On supprime l'ancienne campagne de la base
            TelephoneDaoManager.getDao().deleteAll();
            SharedPreferenceUtils.saveLastCampagneId(this, -1);
            Log.w("TAG_CAMPAGNE", "Reset du capagneId");
        }
        else if (R.id.btSaveDelay == v.getId()) {

            if (StringUtils.isNumeric(etDelay.getText())) {
                SharedPreferenceUtils.saveDelay(this, Integer.parseInt(etDelay.getText().toString()));
                Toast.makeText(this, "Délai sauvegardé. Arretez et redémarrez le service pour la prise en compte", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "Ce n'est pas un delay valide", Toast.LENGTH_SHORT).show();
            }
        }
    }



    /* ---------------------------------
    // private
    // -------------------------------- */

    private void refreshScreen() {

        etUrlLoad.setText(SharedPreferenceUtils.getUrlLoad(this));
        etUrlSendAnswer.setText(SharedPreferenceUtils.getUrlSendAnswer(this));
        etUrlSend.setText(SharedPreferenceUtils.getUrlSendResult(this));
        etDelay.setText(SharedPreferenceUtils.getDelay(this) + "");

        if (campagneBean == null || campagneBean.getTelephoneBeans() == null || campagneBean.getTelephoneBeans().isEmpty()) {
            tvInfo.setText("Aucun numéro chargé");
        }
        else {
            tvInfo.setText(campagneBean.getTelephoneBeans().size() + " numéro(s) chargé(s)\n" + (StringUtils.isNotBlank(campagneBean.getUrlFile()) ? " Mode MMS" : " Mode SMS"));
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
