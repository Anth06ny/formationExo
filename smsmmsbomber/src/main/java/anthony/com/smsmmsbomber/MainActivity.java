package anthony.com.smsmmsbomber;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.formation.utils.exceptions.ExceptionA;
import com.formation.utils.exceptions.LogicException;
import com.formation.utils.exceptions.TechnicalException;
import com.squareup.otto.Subscribe;

import org.apache.commons.lang3.StringUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

import anthony.com.smsmmsbomber.broadcast.MultipleSendSMSBR;
import anthony.com.smsmmsbomber.broadcast.SMSSentListener;
import anthony.com.smsmmsbomber.model.CampagneBean;
import anthony.com.smsmmsbomber.model.WSUtils;
import anthony.com.smsmmsbomber.utils.SmsMmsManager;

import static anthony.com.smsmmsbomber.broadcast.MultipleSendSMSBR.SENT_SMS_ACTION_NAME;

public class MainActivity extends Activity implements OnClickListener {

    private static final int PHOTO_PICKER_ID = 1;

    private Button buttonSend, btCharger, btSendMMS;
    private TextView tvNbCharger;
    private ProgressDialog waintingDialog;
    private EditText editTextSMS, etUrl;
    private TextView tvResultat;
    private ImageView iv;
    private Button load;

    //data
    CampagneBean campagneBean;
    public static Uri imageUri;
    Bitmap selectedImage;

    //outils
    private MultipleSendSMSBR multipleSendSMSBR;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSend = findViewById(R.id.buttonSend);
        btSendMMS = findViewById(R.id.btSendMMS);
        btCharger = findViewById(R.id.btCharger);
        editTextSMS = findViewById(R.id.editTextSMS);
        tvNbCharger = findViewById(R.id.tvNbCharger);
        tvResultat = findViewById(R.id.tvResultat);
        iv = findViewById(R.id.iv);
        load = findViewById(R.id.load);
        etUrl = findViewById(R.id.etUrl);

        campagneBean = null;

        buttonSend.setOnClickListener(this);
        btSendMMS.setOnClickListener(this);
        btCharger.setOnClickListener(this);
        load.setOnClickListener(this);

        multipleSendSMSBR = new MultipleSendSMSBR();
        //abonnement au broadcast
        registerReceiver(multipleSendSMSBR, new IntentFilter(SENT_SMS_ACTION_NAME));

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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        makeDefautSmsApp();
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
        //On boucle tant qu'on n'a pas la permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_PICKER_ID && resultCode == Activity.RESULT_OK) {
            try {
                imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                iv.setImageBitmap(selectedImage);
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
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
        else if (v == load) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(Intent.createChooser(intent, "Complete action using"), PHOTO_PICKER_ID);
        }
        else if (v == btSendMMS) {
            new SendMMSAT("Ca marche").execute();
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

    public void refreshScreen() {
        if (campagneBean == null || campagneBean.getTelephoneBeans() == null || campagneBean.getTelephoneBeans().isEmpty()) {
            tvNbCharger.setText("Aucun numéro chargé");
        }
        else {
            tvNbCharger.setText(campagneBean.getTelephoneBeans().size() + " numéro(s) chargé(s)\n" + (StringUtils.isNotBlank(campagneBean.getUrlFile()) ? " Mode MMS" : " Mode SMS"));
        }
    }

    /* ---------------------------------
    // ASYNC TASK CHarge les numéros
    // -------------------------------- */
    public class MonAT extends AsyncTask {

        String url;
        Exception exception;
        CampagneBean campagneBean;

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
                campagneBean = WSUtils.getCampagnes(url);
                //On télécharge l'image s'il y en a une

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

            refreshScreen();
        }
    }

    public class SendSmsAT extends AsyncTask {

        String message;
        ExceptionA exception;

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
            try {
                SmsMmsManager.sendCampagne(campagneBean);
            }
            catch (LogicException e) {
                e.printStackTrace();
                this.exception = e;
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
                tvResultat.append("Erreur lors de l'envoie : " + exception.getMessage());
            }
            else {
                Toast.makeText(MainActivity.this, "Envoie terminé", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class SendMMSAT extends AsyncTask {

        String message;
        Exception exception;

        public SendMMSAT(String message) {
            this.message = message;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            waintingDialog = ProgressDialog.show(MainActivity.this, "", "Envoie en cours...");
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {

            }
            catch (Exception e) {
                this.exception = exception;
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
                Toast.makeText(MainActivity.this, "Echec : " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(MainActivity.this, "Envoie terminé", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void makeDefautSmsApp() {
        final String myPackageName = getPackageName();
        if (!Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName)) {
            Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, myPackageName);
            startActivity(intent);
        }
    }
}
