package anthony.com.ultimatewarmup;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener, CompoundButton.OnCheckedChangeListener {

    public static final int NB_REPETITION = 6;
    public static final int DUREE = 6;

    private static final String UTERENCE_ID = "UTERENCE_ID";// Truc demander pour le speech
    private static final String UTERENCE_NEXT_ID = "UTERENCE_NEXT_ID";// Truc demander pour le speech

    private Button btSelectAll, btUnselectAll, btStart, btChangerDeVoix;
    private LinearLayout ll;
    private RadioGroup rg;
    private ArrayList<CheckBox> checkBoxes;

    private TextToSpeech t1;
    boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btSelectAll = findViewById(R.id.btSelectAll);
        btUnselectAll = findViewById(R.id.btUnselectAll);
        btChangerDeVoix = findViewById(R.id.btChangerDeVoix);
        btStart = findViewById(R.id.btStart);
        ll = findViewById(R.id.ll);
        rg = findViewById(R.id.rg);

        btSelectAll.setOnClickListener(this);
        btUnselectAll.setOnClickListener(this);
        btStart.setOnClickListener(this);
        btChangerDeVoix.setOnClickListener(this);

        t1 = new TextToSpeech(this, this);
        t1.stop();
        checkBoxes = new ArrayList<>();

        //Les voix

        //Les exo
        for (ExerciceBean exerciceBean : ExerciceBean.values())

        {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setChecked(true);
            checkBox.setText(exerciceBean.textCheckBox);
            checkBox.setTag(exerciceBean);
            checkBox.setPadding(0, 20, 0, 0);

            checkBoxes.add(checkBox);
            ll.addView(checkBox, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            TextView textView = new TextView(this);
            textView.setText(exerciceBean.textExplication);
            textView.setTextColor(getResources().getColor(R.color.colorTextLigth));

            ll.addView(textView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        }

        showExercice(true);
    }

    @Override
    public void onClick(View v) {
        if (v == btSelectAll) {
            for (CheckBox checkBox : checkBoxes) {
                checkBox.setChecked(true);
            }
        }
        else if (v == btUnselectAll) {
            for (CheckBox checkBox : checkBoxes) {
                checkBox.setChecked(false);
            }
        }
        else if (v == btChangerDeVoix) {
            showExercice(rg.getVisibility() == View.VISIBLE);
        }
        else if (v == btStart) {
            if (t1.isSpeaking()) {
                t1.stop();
            }
            else {
                next();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (rg.getVisibility() == View.VISIBLE) {
            showExercice(true);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        t1.stop();
    }


    /* ---------------------------------
    // textToSpeach
    // -------------------------------- */

    @Override

    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            t1.setLanguage(Locale.FRANCE);
            Set<Voice> voices = t1.getVoices();
            if (voices != null) {
                //La voie sauvegarder
                String saveVoicename = SharedPreferenceUtils.getSaveVoice(this);
                if (saveVoicename == null) {
                    saveVoicename = t1.getVoice().getName();
                }

                for (Voice voice : t1.getVoices()) {

                    if (voice.getLocale().getCountry().equals("FR")) {
                        RadioButton rb = new RadioButton(this);
                        rb.setText(voice.getName());
                        rb.setTag(voice);
                        rb.setPadding(0, 0, 20, 0);
                        rb.setOnCheckedChangeListener(this);
                        rg.addView(rb);

                        //Si c'est celle sauvegarder

                        if (voice.getName().equalsIgnoreCase(saveVoicename)) {
                            t1.setVoice(voice);
                            rb.setChecked(true);
                        }
                    }
                }
            }
            t1.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                }

                @Override
                public void onDone(String utteranceId) {
                    if (UTERENCE_NEXT_ID.equals(utteranceId)) {
                        next();
                    }
                }

                @Override
                public void onError(String utteranceId) {
                }
            });
        }
        else {
            Toast.makeText(this, "Erreur d'initialisation de TextToSpeech", Toast.LENGTH_LONG).show();
        }
    }

    /* ---------------------------------
    // Methode
    // -------------------------------- */

    //Switch entre afficher les exo ou les voix
    private void showExercice(boolean showExercice) {
        rg.setVisibility(showExercice ? View.GONE : View.VISIBLE);
        ll.setVisibility(showExercice ? View.VISIBLE : View.GONE);
        btStart.setVisibility(showExercice ? View.VISIBLE : View.GONE);
    }

    private void next() {

        int tempsPreparationExo = 3000;
        int tempPreparationCote = 2000;
        int tempsEntreChaqueseconde = 700;
        int tempsEntreRepetition = 1000;

        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isChecked()) {
                isRunning = true;
                ExerciceBean exerciceBean = (ExerciceBean) checkBox.getTag();
                //Nom exo
                t1.speak(exerciceBean.textCheckBox, TextToSpeech.QUEUE_ADD, null, UTERENCE_ID);
                t1.playSilentUtterance(tempsPreparationExo, TextToSpeech.QUEUE_ADD, UTERENCE_ID);

                if (exerciceBean.nomCote1 != null) {
                    //Nom du cote
                    t1.speak(exerciceBean.nomCote1, TextToSpeech.QUEUE_ADD, null, UTERENCE_ID);
                    t1.playSilentUtterance(tempPreparationCote, TextToSpeech.QUEUE_ADD, UTERENCE_ID);
                }

                for (int i = 1; i <= exerciceBean.nbRepetition; i++) {
                    //1 à 6 seconde
                    for (int j = 1; j <= exerciceBean.nbRepetition; j++) {
                        t1.speak("" + j, TextToSpeech.QUEUE_ADD, null, UTERENCE_ID);
                        t1.playSilentUtterance(tempsEntreChaqueseconde, TextToSpeech.QUEUE_ADD, UTERENCE_ID);
                    }
                    t1.playSilentUtterance(tempsEntreRepetition, TextToSpeech.QUEUE_ADD, UTERENCE_ID);
                }

                if (exerciceBean.nomCote2 != null) {
                    //Changement de coté
                    t1.speak(exerciceBean.nomCote2, TextToSpeech.QUEUE_ADD, null, UTERENCE_ID);
                    t1.playSilentUtterance(tempPreparationCote, TextToSpeech.QUEUE_ADD, UTERENCE_ID);

                    for (int i = 0; i < exerciceBean.nbRepetition; i++) {
                        //1 à 6 seconde
                        for (int j = 1; j <= exerciceBean.nbRepetition; j++) {
                            t1.speak("" + j, TextToSpeech.QUEUE_ADD, null, UTERENCE_ID);
                            t1.playSilentUtterance(tempsEntreChaqueseconde, TextToSpeech.QUEUE_ADD, UTERENCE_ID);
                        }
                        t1.playSilentUtterance(tempsEntreRepetition, TextToSpeech.QUEUE_ADD, UTERENCE_ID);
                    }
                }
                //pour passer à l'exo suivant
                t1.playSilentUtterance(100, TextToSpeech.QUEUE_ADD, UTERENCE_NEXT_ID);

                //C'est fini je décoche la check box
                checkBox.setChecked(false);
                return;
            }
        }

        Toast.makeText(this, "Aucun exercice coché", Toast.LENGTH_SHORT).show();
    }

    /* ---------------------------------
    // Calback radioButton
    // -------------------------------- */

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            Voice voice = (Voice) buttonView.getTag();
            if (voice != null) {
                t1.setVoice(voice);
            }
            SharedPreferenceUtils.saveVoice(this, voice.getName());

            //Le temps que l'annimation se fasse
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showExercice(true);
                }
            }, 500);
        }
    }

    /* ---------------------------------
    // AsyncTask
    // -------------------------------- */

    public class MonAT extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            return null;
        }
    }
}
