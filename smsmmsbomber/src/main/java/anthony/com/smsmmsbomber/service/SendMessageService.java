package anthony.com.smsmmsbomber.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.SmsManager;
import android.util.Log;

import com.formation.utils.exceptions.ExceptionA;
import com.formation.utils.exceptions.LogicException;
import com.formation.utils.exceptions.TechnicalException;
import com.klinker.android.send_message.Settings;
import com.klinker.android.send_message.Transaction;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import anthony.com.smsmmsbomber.MyApplication;
import anthony.com.smsmmsbomber.R;
import anthony.com.smsmmsbomber.broadcast.MultipleSendSMSBR;
import anthony.com.smsmmsbomber.model.CampagneBean;
import anthony.com.smsmmsbomber.model.TelephoneBean;
import anthony.com.smsmmsbomber.model.WSUtils;
import anthony.com.smsmmsbomber.model.dao.TelephoneDaoManager;
import anthony.com.smsmmsbomber.utils.NotificationUtils;
import anthony.com.smsmmsbomber.utils.Permissionutils;
import anthony.com.smsmmsbomber.utils.SharedPreferenceUtils;
import anthony.com.smsmmsbomber.utils.SmsMmsManager;

import static anthony.com.smsmmsbomber.utils.NotificationUtils.NOTIFICATION_ID;

public class SendMessageService extends Service {

    public static final int TEMPS_ATTENTE_ENVOIE_RESULTAT = 30000;

    private SendSmsAT sendSmsAT;
    private Transaction transaction;
    private MultipleSendSMSBR multipleSendSMSBR;
    private Timer timer;

    @Override
    public void onCreate() {
        super.onCreate();

        startForeground(NOTIFICATION_ID, NotificationUtils.getNotif(this, "Démarrage du service", null, null));

        Log.w("TAG_SERVICE", "Démmarage du service");

        multipleSendSMSBR = new MultipleSendSMSBR();
        //on s'abonne
        registerReceiver(multipleSendSMSBR, MultipleSendSMSBR.getIntentFilter());

        //Transaction pour l'envoie de mms
        Settings settings = new Settings();
        settings.setUseSystemSending(true);
        transaction = new Transaction(this, settings);

        int delai = SharedPreferenceUtils.getDelay(this) * 1000;

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                onStartCommand(null, 0, 0);
            }
        }, 0, delai);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Si on veut envoyer le resultat;
        if (sendSmsAT == null || sendSmsAT.getStatus() == AsyncTask.Status.FINISHED) {
            sendSmsAT = new SendSmsAT();
            sendSmsAT.execute();
        }
        else {
            Log.w("TAG_SERVICE", "Campagne déja en cours d'execution");
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(multipleSendSMSBR);
        timer.cancel();
        Log.w("TAG_SERVICE", "Arret du service");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /* ---------------------------------
    // static
    // -------------------------------- */
    public static void startservice(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, SendMessageService.class));
        }
        else {
            context.startService(new Intent(context, SendMessageService.class));
        }
    }

    public static void stopService(Context context) {
        context.stopService(new Intent(context, SendMessageService.class));
    }

    /* ---------------------------------
    // Asynctask
    // -------------------------------- */

    public class SendSmsAT extends AsyncTask<Void, String, Void> {

        ExceptionA exception;
        CampagneBean campagneBean;

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Log.w("TAG_SERVICE", "Lancement d'un campagne...");

                //On verifie les permission
                if (!Permissionutils.isAllPermission(SendMessageService.this)) {
                    throw new ExceptionA("Permission manquante, veuillez accepter les permissions dans l'applications");
                }
                else if (!Permissionutils.isDefautApp(SendMessageService.this)) {
                    throw new ExceptionA("L'application n'est pas définie comme application par defaut pour les SMS, veuillez accepter dans l'application");
                }
                else if (StringUtils.isBlank(SharedPreferenceUtils.getUrlLoad(SendMessageService.this))) {
                    throw new ExceptionA("Veuillez definir dans l'application une url ou charger la campagne");
                }

                NotificationUtils.createInstantNotification(SendMessageService.this, "Chargemment de la campagne...", null, R.mipmap.ic_ok);
                //Chargement de la campagne
                campagneBean = WSUtils.getCampagnes(SendMessageService.this);
                Log.w("TAG_CAMPAGNE", "Campagne chargé : " + campagneBean.getCampagneId());

                //Si elle a deja été envoyé
                if (SharedPreferenceUtils.getLastCampagneId(SendMessageService.this) >= campagneBean.getCampagneId()) {
                    NotificationUtils.createInstantNotification(SendMessageService.this, "La campagne " + campagneBean.getCampagneId() + " a déjà été envoyée",
                            null, R.mipmap
                                    .ic_ok);
                    return null;
                }
                else {
                    //On supprime l'ancienne campagne de la base
                    TelephoneDaoManager.delete(SharedPreferenceUtils.getLastCampagneId(SendMessageService.this));
                }

                NotificationUtils.createInstantNotification(SendMessageService.this, "La campagne " + campagneBean.getCampagneId() + " a été chargé",
                        null, R.mipmap.ic_ok);

                //On télécharge le fichier s'il y en a 1
                if (StringUtils.isNotBlank(campagneBean.getUrlFile())) {
                    Log.w("TAG_CAMPAGNE", "Téléchargement du ficher : " + campagneBean.getUrlFile());
                    if (campagneBean.isVideo()) {
                        campagneBean.setVideoFile(WSUtils.downloadVideo(campagneBean.getUrlFile()));
                    }
                    else {
                        campagneBean.setBitmap(WSUtils.downloadPicture(campagneBean.getUrlFile()));
                    }
                }
            }
            catch (ExceptionA e) {
                this.exception = e;
                return null;
            }

            //Analyse de la campagne
            try {
                if (!CampagneBean.isCampagneReady(campagneBean)) {
                    NotificationUtils.createInstantNotification(SendMessageService.this, "Rien à envoyer", null, R.mipmap.ic_ok);
                    return null;
                }

                //on la sauvegarde en base
                TelephoneDaoManager.save(campagneBean);
                Log.w("TAG_BBD", "Sauvegarde en base de la campagne");
            }
            catch (ExceptionA e) {
                this.exception = e;
                return null;
            }

            try {
                //Envoie de la campagne
                int i = 0;
                int size = campagneBean.getTelephoneBeans().size();
                boolean isSms = StringUtils.isBlank(campagneBean.getUrlFile());
                SmsManager sms = SmsManager.getDefault();
                //On divise notre messege en plusieurs SMS en fonction du format
                ArrayList<String> parts = sms.divideMessage(campagneBean.getMessage());

                for (; i < size; i++) {

                    if (i % 10 == 0) {
                        if (isSms) {
                            NotificationUtils.createInstantNotification(SendMessageService.this, "Envoie de SMS en cours : " + i + "/" + size, null, R.mipmap
                                    .ic_sms);
                        }
                        else if (campagneBean.isVideo()) {
                            NotificationUtils.createInstantNotification(SendMessageService.this, "Envoie de MMS en cours : " + i + "/" + size, campagneBean.getBitmap(), R.mipmap.ic_mms);
                        }
                        else {
                            NotificationUtils.createInstantNotification(SendMessageService.this, "Envoie de MMS en cours : " + i + "/" + size, campagneBean.getBitmap(), R.mipmap.ic_video);
                        }
                    }

                    TelephoneBean telephoneBean = campagneBean.getTelephoneBeans().get(i);
                    //Mode mms
                    if (isSms) {
                        SmsMmsManager.sendSMS(SendMessageService.this, telephoneBean, parts, campagneBean.isAccuserEnvoie(), campagneBean.isAccuserReception());
                    }
                    //Mode sms
                    else {
                        SmsMmsManager.sendMMS(transaction, campagneBean, telephoneBean);
                    }
                }

                String message = campagneBean.getTelephoneBeans().size() + " messages de la campagne id=" +
                        campagneBean.getCampagneId() + " envoyés!";

                //On sauvegarde la derniere campagne
                SharedPreferenceUtils.saveLastCampagneId(SendMessageService.this, campagneBean.getCampagneId());
                Log.w("TAG_CAMPAGNE", "Sauvegarde du campagneId = " + campagneBean.getCampagneId());

                NotificationUtils.createInstantNotification(SendMessageService.this, message, null, R.mipmap.ic_ok);

                if (StringUtils.isBlank(SharedPreferenceUtils.getUrlSendResult(SendMessageService.this))) {
                    message += "\n Pas d'url pour l'envoie des résultats";
                    NotificationUtils.createInstantNotification(SendMessageService.this, message, null, R.mipmap.ic_ok);
                    return null;
                }
                else {
                    //Si c'est un sms et qu'on attend l'accusé reception et qu'on a une url d'envoie
                    if (campagneBean.isAccuserEnvoie() || campagneBean.isAccuserReception() && StringUtils.isBlank(campagneBean.getUrlFile())) {
                        message += "\nEnvoie des résultats dans 30 secondes";
                        NotificationUtils.createInstantNotification(SendMessageService.this, message, null, R.mipmap.ic_ok);
                        SystemClock.sleep(TEMPS_ATTENTE_ENVOIE_RESULTAT);
                    }

                    //Envoie des resultats
                    //ON fait en sorte de recherger les résultats
                    MyApplication.getDaoSession().clear();
                    campagneBean.setTelephoneBeans(new ArrayList<>(TelephoneDaoManager.getTelephoneFromCampagneId(campagneBean.getCampagneId())));
                    NotificationUtils.createInstantNotification(SendMessageService.this, "Envoie du resultat...", null, R.mipmap.ic_ok);
                    WSUtils.sendCampagneBean(SendMessageService.this, campagneBean);
                    NotificationUtils.createInstantNotification(SendMessageService.this, message + "\nResultats envoyés" + campagneBean, null, R.mipmap.ic_ok);
                }
            }
            catch (Exception e) {
                this.exception = new TechnicalException("Erreur lors de l'envoie de message", e);
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (exception != null) {
                exception.printStackTrace();
                NotificationUtils.createInstantNotification(SendMessageService.this, exception.getMessage(), campagneBean != null ? campagneBean.getBitmap() :
                        null, R.mipmap.ic_error);
            }

            //ON lance l'envoie des sms recu
            new SendAnswerAT().execute();
        }
    }

    public class SendAnswerAT extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {

            try {
                if (StringUtils.isBlank(SharedPreferenceUtils.getUrlSendAnswer(SendMessageService.this))) {
                    throw new LogicException("Pas d'url ou envoyer les sms reçu");
                }
                //ON envoie les réponse
                List<TelephoneBean> list = TelephoneDaoManager.getTelephoneWithAnswer();
                if (!list.isEmpty()) {
                    WSUtils.sendAnswer(SendMessageService.this, list);

                    //On sauvegarde l'envoie
                    TelephoneDaoManager.deleteList(list);

                    NotificationUtils.sendAnswerNotification(SendMessageService.this, "Les " + list.size() + "sms reçus ont été envoyés au serveur", R.mipmap.ic_ok);
                }
                else {
                    NotificationUtils.sendAnswerNotification(SendMessageService.this, "Aucun sms reçu", R.mipmap.ic_ok);
                }
            }
            catch (ExceptionA exceptionA) {
                exceptionA.printStackTrace();
                NotificationUtils.sendAnswerNotification(SendMessageService.this, "Impossible d'envoyer les sms reçu.\n" + exceptionA.getMessage(), R.mipmap.ic_error);
            }

            return null;
        }
    }
}
