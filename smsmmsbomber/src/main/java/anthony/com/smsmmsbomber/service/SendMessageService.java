package anthony.com.smsmmsbomber.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.formation.utils.exceptions.ExceptionA;
import com.formation.utils.exceptions.LogicException;
import com.formation.utils.exceptions.TechnicalException;
import com.klinker.android.send_message.Settings;
import com.klinker.android.send_message.Transaction;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import anthony.com.smsmmsbomber.Constants;
import anthony.com.smsmmsbomber.R;
import anthony.com.smsmmsbomber.broadcast.MultipleSendSMSBR;
import anthony.com.smsmmsbomber.model.AnswerBean;
import anthony.com.smsmmsbomber.model.WSUtils;
import anthony.com.smsmmsbomber.model.dao.AnswerDaoManager;
import anthony.com.smsmmsbomber.model.wsbeans.getscheduleds.GetScheduledAnswerBean;
import anthony.com.smsmmsbomber.model.wsbeans.getscheduleds.PhoneBean;
import anthony.com.smsmmsbomber.utils.NotificationUtils;
import anthony.com.smsmmsbomber.utils.Permissionutils;
import anthony.com.smsmmsbomber.utils.SharedPreferenceUtils;
import anthony.com.smsmmsbomber.utils.SmsMmsManager;

import static anthony.com.smsmmsbomber.utils.NotificationUtils.NOTIFICATION_ID;

public class SendMessageService extends Service {

    private SendSmsAT sendSmsAT;
    private Transaction transaction;
    private MultipleSendSMSBR multipleSendSMSBR;
    private Timer timer;

    @Override
    public void onCreate() {
        super.onCreate();

        startForeground(NOTIFICATION_ID, NotificationUtils.getNotif(this, "Démarrage du service",  null));

        Log.w("TAG_SERVICE", "Démmarage du service");

        multipleSendSMSBR = new MultipleSendSMSBR();
        //on s'abonne
        registerReceiver(multipleSendSMSBR, MultipleSendSMSBR.getIntentFilter());

        //Transaction pour l'envoie de mms
        Settings settings = new Settings();
        settings.setUseSystemSending(true);
        transaction = new Transaction(this, settings);

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                onStartCommand(null, 0, 0);
            }
        }, 0, Constants.DELAI_SERVICE);
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
        GetScheduledAnswerBean campagneBean;

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
                //Si on a pas d'url enregistre, on fait un ping sur l'url de la constante
                else if (StringUtils.isBlank(SharedPreferenceUtils.getUrlLoad(SendMessageService.this))) {
                    NotificationUtils.createInstantNotification(SendMessageService.this, "Ping 1er lancement...",  R.mipmap.ic_ok);
                    WSUtils.pingServeur(SendMessageService.this);
                }

                //Enregistrement du device et recupération de l'url à utiliser
                NotificationUtils.createInstantNotification(SendMessageService.this, "Chargement de l'url à utiliser...",  R.mipmap.ic_ok);
                WSUtils.saveUrlFromBoxEndPoint(SendMessageService.this);

                //On enregistre du modem (ici telephone est modem sont liés donc juste 2 appels  à faire)
                WSUtils.registerDevice(SendMessageService.this);

                //on envoie un ping comme quoi on est vivant
                WSUtils.pingServeur(SendMessageService.this);

                //On envoie un autre comme quoi on est vivant aussi
                WSUtils.deviceReady(SendMessageService.this);

                NotificationUtils.createInstantNotification(SendMessageService.this, "Chargemment de la campagne...",  R.mipmap.ic_ok);
                //Chargement de la campagne
                campagneBean = WSUtils.getScheduleds(SendMessageService.this);
                Log.w("TAG_CAMPAGNE", "Campagne chargé");
                NotificationUtils.createInstantNotification(SendMessageService.this, "La campagne a été chargé",  R.mipmap.ic_ok);

                //on regarde si la campagne contient des fichier
                if (campagneBean.getPhoneList() == null || campagneBean.getPhoneList().isEmpty()) {
                    throw new LogicException("Campagne vide, rien à envoyer");
                }
            }
            catch (ExceptionA e) {
                this.exception = e;
                return null;
            }

            try {
                //Envoie de la campagne
                int i = 0;
                int size = campagneBean.getPhoneList().size();
                String lastUrl = "";
                Bitmap bitmap = null;

                //On divise notre messege en plusieurs SMS en fonction du format

                for (; i < size; i++) {
                    if (i % 10 == 0) {
                        NotificationUtils.createInstantNotification(SendMessageService.this, "Envoie de message en cours : " + i + "/" + size,  R.mipmap
                                .ic_sms);
                    }

                    PhoneBean phoneBean = campagneBean.getPhoneList().get(i);
                    if (StringUtils.isNotBlank(phoneBean.getUrlFichier())) {

                        //Si ce n'est pas la meme image on telecharge
                        if (StringUtils.isBlank(lastUrl) || !StringUtils.equals(phoneBean.getUrlFichier(), lastUrl)) {
                            FutureTarget<Bitmap> futureTarget =
                                    Glide.with(SendMessageService.this).asBitmap().load(phoneBean.getUrlFichier()).submit(Integer.MIN_VALUE, Integer.MIN_VALUE);
                            bitmap = futureTarget.get();
                            lastUrl = phoneBean.getUrlFichier();
                        }

                        //Mode mms
                        SmsMmsManager.sendMMS(transaction, phoneBean, bitmap);
                    }
                    else {
                        //Mode sms
                        SmsMmsManager.sendSMS(SendMessageService.this, phoneBean, true, false);
                    }
                }

                NotificationUtils.createInstantNotification(SendMessageService.this, campagneBean.getPhoneList().size() + " messages envoyés!",  R.mipmap.ic_ok);

                //On previent le serveur qu'on a transmis au modem la liste des message
                WSUtils.smssent(SendMessageService.this, campagneBean.getPhoneList());
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
                NotificationUtils.createInstantNotification(SendMessageService.this, exception.getMessage(), R.mipmap.ic_error);
            }

            //ON lance l'envoie des delivery en echec
            new SendDeliveryFailAT().execute();
            //ON lance l'envoie des sms recu
            new SendDeliveryFailAT().execute();
        }
    }

    /* ---------------------------------
    // AT Envoie d'sms en erreur
    // -------------------------------- */

    public class SendDeliveryFailAT extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                List<AnswerBean> list = AnswerDaoManager.getFailedDelivery();
                if (!list.isEmpty()) {
                    WSUtils.sendSmsSendFail(SendMessageService.this, list);
                    //On efface de la base
                    AnswerDaoManager.deleteList(list);
                    NotificationUtils.sendAnswerNotification(SendMessageService.this, "Sms en echec envoyés au serveur", R.mipmap.ic_ok);
                }
            }
            catch (ExceptionA exceptionA) {
                exceptionA.printStackTrace();
                NotificationUtils.sendAnswerNotification(SendMessageService.this, "Impossible d'envoyer les accusé d'envoie en erreur.\n" + exceptionA.getMessage(), R.mipmap.ic_error);
            }
            return null;
        }
    }

    /* ---------------------------------
    // AT Envoie d'sms reçu
    // -------------------------------- */

    public class SendAnswerAT extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                List<AnswerBean> list = AnswerDaoManager.getSmsReceived();
                if (!list.isEmpty()) {
                    WSUtils.sendSmsReceive(SendMessageService.this, list);
                    //On efface de la base
                    AnswerDaoManager.deleteList(list);
                    NotificationUtils.sendAnswerNotification(SendMessageService.this, "Sms reçu envoyés au serveur", R.mipmap.ic_ok);
                }
            }
            catch (ExceptionA exceptionA) {
                exceptionA.printStackTrace();
                NotificationUtils.sendAnswerNotification(SendMessageService.this, "Impossible d'envoyer les accusés d'envoie en erreur.\n" + exceptionA.getMessage(), R.mipmap.ic_error);
            }
            return null;
        }
    }
}
