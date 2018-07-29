package anthony.com.smsmmsbomber.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

import com.formation.utils.exceptions.ExceptionA;
import com.formation.utils.exceptions.TechnicalException;
import com.klinker.android.send_message.Settings;
import com.klinker.android.send_message.Transaction;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

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

    //Charger la campagne
    //charger l'image
    //Envoyer les sms//mms
    //Gerer les réponses des sms recu
    //Envoyer le rapport

    private SendSmsAT sendSmsAT;
    private Transaction transaction;
    private MultipleSendSMSBR multipleSendSMSBR;

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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

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

                NotificationUtils.createInstantNotification(SendMessageService.this, "Chargemment de la campagne...", null, R.mipmap.ic_ok);
                //Chargement de la campagne
                campagneBean = WSUtils.getCampagnes(SendMessageService.this);
                Log.w("TAG_CAMPAGNE", "Campagne id chargé");

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
                        SmsMmsManager.sendSMS(SendMessageService.this, telephoneBean, parts);
                    }
                    //Mode sms
                    else {
                        SmsMmsManager.sendMMS(transaction, campagneBean, telephoneBean);
                    }
                }
                NotificationUtils.createInstantNotification(SendMessageService.this, campagneBean.getTelephoneBeans().size() + " messages de la campagne id=" +
                        campagneBean.getCampagneId() + " envoyés!", null, R.mipmap.ic_ok);
                //On sauvegarde la derniere campagne
                SharedPreferenceUtils.saveLastCampagneId(SendMessageService.this, campagneBean.getCampagneId());
                Log.w("TAG_CAMPAGNE", "Sauvegarde du campagneId = " + campagneBean.getCampagneId());
            }
            catch (Exception e) {
                this.exception = new TechnicalException("Erreur lors de l'envoie de message", e);
            }
            finally {
                //Envoie du resultat
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
        }
    }
}
