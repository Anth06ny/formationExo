package temp.com.servicebinding.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by amonteiro on 30/01/2015.
 */
public class SMSSpamer extends Service {

    private Timer timer;
    private SimpleDateFormat simpleDateFormat;

    private String[] messageSoir = new String[] {

            "Normalement si tous ce passe bien avant la fin du weekend",

            //18h
            "",
            "Oui le précédent  était vide, il faut entretenir le suspense",
            "Il est peut être temps de...",

            //19h
            "te dire comment arreter ça.",
            "sauf si tu veux laisser passer la nuit",
            "juste pour voir si c'est bien automatisé.",
            "Mais je ne serai pas très bavard pendant la nuit",

            //20h
            "tu ne recevras que l'heure, et un petit bonus.",
            "Donc pour arreter ca...",
            "...il faut que...",
            "... tu m'envoies une photo ...",

            //21h
            " ... de toi en ...",
            "... je suis sur que tu penses que je vais dire un truc pervers!!!",
            "et ben non, simplement une photo de toi en tenant une pancarte avec écrie 'Anthone' plus un texte gentil" + " à mon attention.",
            " Ah la la, tous ca pour que je prenne ton numéro !!!",

            //22h
            "Maline la Drouaud",

            //Departement
            "Bon il est maintenant l'heure d'apprendre les départements", "01	Ain", "02	Aisne", "03	Allier", "04	Alpes-de-Haute-Provence",
            "05	Hautes-Alpes", "06	Alpes-Maritimes", "07	Ardèche", "08	Ardennes", "09	Ariège", "10	Aube", "11	Aude", "12	Aveyron",
            "13	Bouches-du-Rhône", "14	Calvados", "15	Cantal", "16	Charente", "17	Charente-Maritime", "18	Cher", "19	Corrèze", "2A	Corse-du-Sud",
            "2B	Haute-Corse", "21	Côte-d'Or", "22	Côtes-d'Armor", "23	Creuse", "24	Dordogne", "25	Doubs", "26	Drôme", "27	Eure", "28	Eure-et-Loir",
            "29	Finistère", "30	Gard", "31	Haute-Garonne", "32	Gers", "33	Gironde", "34	Hérault", "35	Ille-et-Vilaine", "36	Indre",
            "37	Indre-et-Loire", "38	Isère", "39	Jura", "40	Landes", "41	Loir-et-Cher", "42	Loire", "43	Haute-Loire", "44	Loire-Atlantique",
            "45	Loiret", "46	Lot", "47	Lot-et-Garonne", "48	Lozère", "49	Maine-et-Loire", "50	Manche", "51	Marne", "52	Haute-Marne", "53	Mayenne",
            "54	Meurthe-et-Moselle", "55	Meuse", "56	Morbihan", "57	Moselle", "58	Nièvre", "59	Nord", "60	Oise", "61	Orne", "62	Pas-de-Calais",
            "63	Puy-de-Dôme", "64	Pyrénées-Atlantiques", "65	Hautes-Pyrénées", "66	Pyrénées-Orientales", "67	Bas-Rhin", "68	Haut-Rhin", "69	Rhône",
            "70	Haute-Saône", "71	Saône-et-Loire", "72	Sarthe", "73	Savoie", "74	Haute-Savoie", "75	Paris", "76	Seine-Maritime", "77	Seine-et-Marne",
            "78	Yvelines", "79	Deux-Sèvres", "80	Somme", "81	Tarn", "82	Tarn-et-Garonne", "83	Var", "84	Vaucluse", "85	Vendée", "86	Vienne",
            "87	Haute-Vienne", "88	Vosges", "89	Yonne", "90	Territoire de Belfort", "91	Essonne", "92	Hauts-de-Seine", "93	Seine-Saint-Denis",
            "94	Val-de-Marne", "95	Val-d'Oise", "971	Guadeloupe", "972	Martinique", "973	Guyane", "974	La Réunion", "976	Mayotte"

    };

    private String messageAccueil = "Bonjour apprentie Drouaud\n, il est ";

    private IBinder iBinder = null; //l'instance du binder correspondant à notre service

    public static int smsSend = 0;

    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();

        timer = new Timer();
        simpleDateFormat = new SimpleDateFormat("HH\'h\'mm");

        handler = new Handler();

        //au démarrage du service, on créé le binder en envoyant le service
        iBinder = new SMSSpamerBinder(this);
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                if (smsSend >= messageSoir.length) {
                    this.cancel();
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isQuater()) {
                            SMSSentListener.sendSMS(SMSSpamer.this, "0626696682", getNextMessage());
                            SMSSpamer.smsSend++;
                        }
                        else {
                            Toast.makeText(SMSSpamer.this, "not quater", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }, 1000, 60000);

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return iBinder;
    }

    private String getNextMessage() {

        String start = messageAccueil + simpleDateFormat.format(new Date()) + ".\n\n";
        start += messageSoir[smsSend];

        return start;

    }

    private boolean isQuater() {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(new Date());

        return cal1.get(Calendar.MINUTE) % 15 == 0;

    }

    /* ---------------------------------
    // Binder
    // -------------------------------- */

    public class SMSSpamerBinder extends Binder {

        private SMSSpamer smsSpamer;

        //on recoit l'instance du service
        public SMSSpamerBinder(SMSSpamer smsSpamer) {
            super();
            this.smsSpamer = smsSpamer;
        }

        /* ---------------------------------
        // Getter / Setter
        // -------------------------------- */

        /** @return l'instance du service */
        public SMSSpamer getSMSSpamer() {
            return smsSpamer;
        }
    }
}
