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
            //17h
            "",
            "T'inquiete pas j'aurai tous le temps de répondre à tes questions",
            "Normalement avec ton intelligence, tu devrais avoir trouvé la fréquence des messages",
            "mais la grande question c'est...",

            //18h
            "...quand est ce que cela va s'arreter!!!",
            "Normalement si tous ce passe bien avant la fin du weekend",
            "Et tu sais que je suis capable...",
            "... d'avoir prévu suffisament de message pour!!",

            //19h
            "",
            "Oui le précédent  était vide, il faut entretenir le suspense",
            "Il est peut être temps de...",

            //20h
            "te dire comment arreter ça.",
            "sauf si tu veux laisser passer la nuit",
            "juste pour voir si c'est bien automatisé.",
            "Mais je ne serai pas très bavard pendant la nuit",

            //21h
            "tu ne recevras que l'heure.",
            "Donc pour arreter ca...",
            "...il faut que...",
            "... tu m'envoies une photo ...",

            //22h
            " ... de toi en ...",
            "... je suis sur que tu penses que je vais dire un truc pervers!!!",
            "et ben non, juste en fille ( robe / juppe ...)",
            " Ah la la, tous ca pour que je prenne ton numéro !!!",
            "Maline la Drouaud",

            //Departement
            "Bon il est maintenant l'heure d'apprendre les départements", "01	Ain", "02	Aisne", "03	Allier", "04	Alpes", "05	Haute", "06	Alpe",
            "07	Ardèche", "08	Ardennes", "09	Ariège", "10	Aube", "11	Aude", "12	Aveyron", "13	Bouche", "14	Calvados", "15	Cantal", "16	Charente",
            "17	Charent", "18	Cher", "19	Corrèze", "2A	Cors", "2B	Haut", "21	Côt", "22	Côtes", "23	Creuse", "24	Dordogne", "25	Doubs", "26	Drôme",
            "27	Eure", "28	Eur", "29	Finistère", "30	Gard", "31	Haut", "32	Gers", "33	Gironde", "34	Hérault", "35	Ill", "36	Indre", "37	Indr",
            "38	Isère", "39	Jura", "40	Landes", "41	Loi", "42	Loire", "43	Haut", "44	Loir", "45	Loiret", "46	Lot", "47	Lo", "48	Lozère", "49	Main",
            "50	Manche", "51	Marne", "52	Haut", "53	Mayenne", "54	Meurth", "55	Meuse", "56	Morbihan", "57	Moselle", "58	Nièvre", "59	Nord",
            "60	Oise", "61	Orne", "62	Pa", "63	Pu", "64	Pyrénée", "65	Haute", "66	Pyrénée", "67	Ba", "68	Hau", "69	Rhône", "70	Haut", "71	Saôn",
            "72	Sarthe", "73	Savoie", "74	Haut", "75	Paris", "76	Sein", "77	Sein", "78	Yvelines", "79	Deu", "80	Somme", "81	Tarn", "82	Tar",
            "83	Var", "84	Vaucluse", "85	Vendée", "86	Vienne", "87	Haut", "88	Vosges", "89	Yonne", "90	Territoir", "91	Essonne", "92	Haut",
            "93	Sein", "94	Va", "95	Val-d'Oise"

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
