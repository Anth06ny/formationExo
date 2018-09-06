package anthony.com.smsmmsbomber.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.crashlytics.android.Crashlytics;
import com.formation.utils.exceptions.ExceptionA;
import com.formation.utils.exceptions.LogicException;
import com.formation.utils.exceptions.TechnicalException;
import com.klinker.android.send_message.Settings;
import com.klinker.android.send_message.Transaction;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import anthony.com.smsmmsbomber.Constants;
import anthony.com.smsmmsbomber.MyApplication;
import anthony.com.smsmmsbomber.R;
import anthony.com.smsmmsbomber.broadcast.ReceptionSMSBR;
import anthony.com.smsmmsbomber.model.AnswerBean;
import anthony.com.smsmmsbomber.model.WSUtils;
import anthony.com.smsmmsbomber.model.dao.AnswerDaoManager;
import anthony.com.smsmmsbomber.model.wsbeans.getscheduleds.GetScheduledAnswerBean;
import anthony.com.smsmmsbomber.model.wsbeans.getscheduleds.PhoneBean;
import anthony.com.smsmmsbomber.utils.LogUtils;
import anthony.com.smsmmsbomber.utils.NotificationUtils;
import anthony.com.smsmmsbomber.utils.OttoEvent;
import anthony.com.smsmmsbomber.utils.Permissionutils;
import anthony.com.smsmmsbomber.utils.SharedPreferenceUtils;
import anthony.com.smsmmsbomber.utils.SmsMmsManager;
import anthony.com.smsmmsbomber.utils.Utils;

import static anthony.com.smsmmsbomber.utils.NotificationUtils.NOTIFICATION_ID;

public class SendMessageService extends Service {

    private SendSmsAT sendSmsAT;
    private Transaction transaction;
    private ReceptionSMSBR receptionSMSBR;
    private Timer timer;

    @Override
    public void onCreate() {
        super.onCreate();

        startForeground(NOTIFICATION_ID, NotificationUtils.getNotif(this, "Démarrage du service", null));

        LogUtils.w("TAG_SERVICE", "Démmarage du service");

        receptionSMSBR = new ReceptionSMSBR();
        //on s'abonne
        registerReceiver(receptionSMSBR, ReceptionSMSBR.getIntentFilter());

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
        }, Constants.DELAI_SERVICE, Constants.DELAI_SERVICE);

        MyApplication.getBus().post(OttoEvent.SERVICE_START);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Si on veut envoyer le resultat;
        if (sendSmsAT == null || sendSmsAT.getStatus() == AsyncTask.Status.FINISHED) {
            sendSmsAT = new SendSmsAT();
            sendSmsAT.execute();
        }
        else {
            LogUtils.w("TAG_SERVICE", "Campagne déja en cours d'execution");
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receptionSMSBR);
        timer.cancel();
        LogUtils.w("TAG_SERVICE", "Arret du service");

        //On enleve toute les notification
        NotificationUtils.removeAllNotification(this);
        //On vide la base
        AnswerDaoManager.deleteAll();

        MyApplication.getBus().post(OttoEvent.SERVICE_STOP);
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
        //On enleve toute les notification
        NotificationUtils.removeAllNotification(context);
        //On vide la base
        AnswerDaoManager.deleteAll();
    }

    public static boolean isRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (SendMessageService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /* ---------------------------------
    // Asynctask
    // -------------------------------- */

    public class SendSmsAT extends AsyncTask<Void, String, Void> {

        ExceptionA exception;
        GetScheduledAnswerBean campagneBean;

        @Override
        protected Void doInBackground(Void... voids) {

            String ip;

            try {
                LogUtils.w("TAG_SERVICE", "Lancement d'un campagne...");

                //On verifie les permission
                if (!Permissionutils.isAllPermission(SendMessageService.this)) {
                    throw new ExceptionA("Permission manquante, veuillez accepter les permissions dans l'applications");
                }
                else if (!Permissionutils.isDefautApp(SendMessageService.this)) {
                    throw new ExceptionA("L'application n'est pas définie comme application par defaut pour les SMS, veuillez accepter dans l'application");
                }

                //On récupère notre adresse IP
                ip = WSUtils.getIP();

                //Si on a pas d'url enregistre, on fait un ping sur l'url de la constante
                if (StringUtils.isBlank(SharedPreferenceUtils.getUrlLoad(SendMessageService.this))) {
                    NotificationUtils.createInstantNotification(SendMessageService.this, "Ping 1er lancement...", R.mipmap.ic_ok);
                    WSUtils.pingServeur(SendMessageService.this, ip);
                }

                //Enregistrement du device et recupération de l'url à utiliser
                NotificationUtils.createInstantNotification(SendMessageService.this, "Chargement de l'url à utiliser...", R.mipmap.ic_ok);
                WSUtils.saveUrlFromBoxEndPoint(SendMessageService.this);

                //On enregistre du modem (ici telephone est modem sont liés donc juste 2 appels  à faire)
                WSUtils.registerDevice(SendMessageService.this, ip);

                //on envoie un ping comme quoi on est vivant
                WSUtils.pingServeur(SendMessageService.this, ip);

                //On envoie un autre comme quoi on est vivant aussi
                WSUtils.deviceReady(SendMessageService.this);

                //On verifie le mode avion
                if (Utils.isAirplaneModeOn(SendMessageService.this)) {
                    throw new TechnicalException("Device en mode avion");
                }

                NotificationUtils.createInstantNotification(SendMessageService.this, "Chargemment de la campagne...", R.mipmap.ic_ok);
                //Chargement de la campagne
                campagneBean = WSUtils.getScheduleds(SendMessageService.this);
                LogUtils.w("TAG_CAMPAGNE", "Campagne chargé");
                NotificationUtils.createInstantNotification(SendMessageService.this, "La campagne a été chargé", R.mipmap.ic_ok);

                //on regarde si la campagne contient des fichier
                if (campagneBean.getPhoneList() == null || campagneBean.getPhoneList().isEmpty()) {
                    throw new LogicException("Campagne vide, rien à envoyer");
                }
            }
            catch (ExceptionA e) {
                this.exception = e;
                return null;
            }
            catch (Exception e) {
                exception = new TechnicalException(e);
                return null;
            }

            //Envoie de la campagne
            int i = 0;
            int size = campagneBean.getPhoneList().size();

            boolean cartSimOK = true;
            //Annalyse de la carte sim
            try {
                //ON regare si la carte SIM est en état
                SmsMmsManager.testSimCard(SendMessageService.this);
            }
            catch (TechnicalException e) {
                //Problème avec la carte sim, on parcourt tous les messages pour les mettre en echec
                cartSimOK = false;
                Crashlytics.logException(e);
                for (; i < size; i++) {
                    //On indique les SMS en erreur
                    AnswerBean answerBean = new AnswerBean(campagneBean.getPhoneList().get(i));
                    answerBean.setSend(false);
                    AnswerDaoManager.save(answerBean);
                }
            }

            try {
                if (cartSimOK) {

                    String lastUrl = "";
                    Bitmap bitmap = null;

                    for (; i < size; i++) {
                        if (i % 10 == 0) {
                            NotificationUtils.createInstantNotification(SendMessageService.this, "Envoie de message en cours : " + i + "/" + size, R.mipmap
                                    .ic_sms);
                        }
                        PhoneBean phoneBean = campagneBean.getPhoneList().get(i);
                        try {
                            if (StringUtils.isNotBlank(phoneBean.getUrlFichier())) {

                                //Si ce n'est pas la meme image on telecharge
                                if (StringUtils.isBlank(lastUrl) || !StringUtils.equals(phoneBean.getUrlFichier(), lastUrl)) {
                                    FutureTarget<Bitmap> futureTarget =
                                            Glide.with(SendMessageService.this).asBitmap().load(phoneBean.getUrlFichier()).submit(Integer.MIN_VALUE, Integer.MIN_VALUE);
                                    bitmap = futureTarget.get();
                                    lastUrl = phoneBean.getUrlFichier();
                                }

                                //Mode mms
                                SmsMmsManager.sendMMS(SendMessageService.this, transaction, phoneBean, bitmap);
                            }
                            else {
                                //Mode sms
                                SmsMmsManager.sendSMS(SendMessageService.this, phoneBean, false);
                            }
                        }
                        catch (ExecutionException e) {
                            Crashlytics.logException(new TechnicalException("Erreur au chargement de l'image : " + phoneBean, e));
                            LogUtils.w("TAG_SMS", "Erreur lors de l'envoie d'un sms/mms : " + e.getMessage());
                            e.printStackTrace();
                            //On indique le SMS en erreur
                            AnswerBean answerBean = new AnswerBean(phoneBean);
                            answerBean.setSend(false);
                            AnswerDaoManager.save(answerBean);
                        }
                        catch (Exception e) {
                            LogUtils.w("TAG_SMS", "Erreur lors de l'envoie d'un sms/mms : " + e.getMessage());
                            e.printStackTrace();
                            //On indique le SMS en erreur
                            AnswerBean answerBean = new AnswerBean(phoneBean);
                            answerBean.setSend(false);
                            AnswerDaoManager.save(answerBean);
                        }
                    }
                }
                NotificationUtils.createInstantNotification(SendMessageService.this, campagneBean.getPhoneList().size() + " messages transmis au modem!", R.mipmap.ic_ok);

                //On previent le serveur qu'on a transmis au modem la liste des message
                WSUtils.smssent(SendMessageService.this, campagneBean.getPhoneList());
            }
            catch (Exception e) {
                this.exception = new TechnicalException("Erreur lors de l'envoie de messages\n" + e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (exception != null) {
                exception.printStackTrace();
                NotificationUtils.createInstantNotification(SendMessageService.this, exception.getMessage(), exception instanceof LogicException ? R.mipmap.ic_ok : R
                        .mipmap
                        .ic_error);

                //On envoie à CrashLytics si c'est une exception Technique
                LogUtils.logException(exception);
            }

            //ON lance l'envoie des delivery
            new SendDeliveryFailOrSuccessAT().execute();
            //ON lance l'envoie des sms recu
            new SendAnswerAT().execute();
        }
    }

    /* ---------------------------------
    // AT Envoie d'sms en erreur
    // -------------------------------- */

    public class SendDeliveryFailOrSuccessAT extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {

            //SMS en echec
            try {
                List<AnswerBean> list = AnswerDaoManager.getFailedDelivery();
                if (!list.isEmpty()) {
                    WSUtils.sendSmsSendFail(SendMessageService.this, list, false);
                    //On efface de la base
                    AnswerDaoManager.deleteList(list);
                    NotificationUtils.sendAnswerNotification(SendMessageService.this, "Sms en echec envoyés au serveur", R.mipmap.ic_ok);
                }
                else {
                    LogUtils.w("TAG_SMS", "Aucun SMS en echec a envoyer au serveur");
                }
            }
            catch (ExceptionA exceptionA) {
                exceptionA.printStackTrace();
                NotificationUtils.sendAnswerNotification(SendMessageService.this, "Impossible d'envoyer les accusé d'envoie en erreur.\n" + exceptionA.getMessage(), R.mipmap.ic_error);

                //On envoie à CrashLytics si c'est une exception Technique
                LogUtils.logException(exceptionA);
            }
            catch (Exception e) {
                e.printStackTrace();
                NotificationUtils.sendAnswerNotification(SendMessageService.this, "Impossible d'envoyer les accusé d'envoie en erreur.\n" + e.getMessage(), R.mipmap.ic_error);

                //On envoie à CrashLytics si c'est une exception Technique
                LogUtils.logException(new TechnicalException(e));
            }

            //Accusé récéption
            try {
                List<AnswerBean> list = AnswerDaoManager.getSuccessDelivery();
                if (!list.isEmpty()) {
                    WSUtils.sendSmsSendFail(SendMessageService.this, list, true);
                    //On efface de la base
                    AnswerDaoManager.deleteList(list);
                    NotificationUtils.sendAnswerNotification(SendMessageService.this, "Accusé d'envoie envoyés au serveur", R.mipmap.ic_ok);
                }
                else {
                    LogUtils.w("TAG_SMS", "Aucun SMS en echec a envoyer au serveur");
                }
            }
            catch (ExceptionA exceptionA) {
                exceptionA.printStackTrace();
                NotificationUtils.sendAnswerNotification(SendMessageService.this, "Impossible d'envoyer les accusé d'envoie en succes.\n" + exceptionA.getMessage(), R.mipmap.ic_error);

                //On envoie à CrashLytics si c'est une exception Technique
                LogUtils.logException(exceptionA);
            }
            catch (Exception e) {
                e.printStackTrace();
                NotificationUtils.sendAnswerNotification(SendMessageService.this, "Impossible d'envoyer les accusé d'envoie en succes.\n" + e.getMessage(), R.mipmap.ic_error);

                //On envoie à CrashLytics si c'est une exception Technique
                LogUtils.logException(new TechnicalException(e));
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
                    NotificationUtils.sendAnswerNotification(SendMessageService.this, list.size() + " sms reçu(s) envoyé(s) au serveur", R.mipmap.ic_ok);
                }
                else {
                    LogUtils.w("TAG_SMS", "Aucun SMS reçu a envoyer au serveur");
                }
            }
            catch (ExceptionA exceptionA) {
                exceptionA.printStackTrace();
                NotificationUtils.sendAnswerNotification(SendMessageService.this, "Impossible d'envoyer les sms reçus au serveur.\n" + exceptionA.getMessage(), R.mipmap.ic_error);

                //On envoie à CrashLytics si c'est une exception Technique
                LogUtils.logException(exceptionA);
            }
            return null;
        }
    }
}
