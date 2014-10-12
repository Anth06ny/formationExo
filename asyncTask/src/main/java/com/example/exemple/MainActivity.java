package com.example.exemple;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.exemple.adapter.EleveAdapter;
import com.example.exemple.bean.Eleve;
import com.example.exemple.dao.asynctask.ChargementEleveAT;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements OnClickListener {

    //composants graphique
    private ListView lv;
    private Button bt;
    private TextView tv_message;
    private ProgressBar pb, pb_indeterminate;

    //données
    private List<Eleve> eleveList;

    //outils
    private EleveAdapter eleveAdapter;
    private ChargementEleveAT chargementEleveAT = null;

    //--------------------
    // view
    //-------------------

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        eleveList = new ArrayList<Eleve>();
        eleveAdapter = new EleveAdapter(this, eleveList);

        lv = (ListView) findViewById(R.id.lv);
        lv.setAdapter(eleveAdapter);

        bt = (Button) findViewById(R.id.bt);
        bt.setOnClickListener(this);

        tv_message = (TextView) findViewById(R.id.tv_message);
        pb = (ProgressBar) findViewById(R.id.pb);
        pb_indeterminate = (ProgressBar) findViewById(R.id.pb_indeterminate);

    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshVue(false, null);
    }

    //--------------------
    // click
    //-------------------
    @Override
    public void onClick(final View v) {
        //si la tache n'existe pas ou qu'elle est terminée on en recrée une.
        //on ne peut executer qu'une seul fois une AsyncTask
    }

    //--------------------
    // vue
    //-------------------
    private void refreshVue(final boolean loadInProgress, final String erreurMessage) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                //si l'AT est en cours de traitement
                if (loadInProgress) {
                    tv_message.setText("Chargement en cours, veuillez patienter...");
                    tv_message.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.VISIBLE);
                    pb_indeterminate.setVisibility(View.VISIBLE);
                    lv.setVisibility(View.INVISIBLE);
                    bt.setEnabled(false);
                } else if (erreurMessage != null) {
                    tv_message.setText(erreurMessage);
                    tv_message.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.INVISIBLE);
                    pb_indeterminate.setVisibility(View.INVISIBLE);
                    lv.setVisibility(View.INVISIBLE);
                    bt.setEnabled(true);
                }
                //si la liste est vide
                else if (eleveList.isEmpty()) {
                    tv_message.setText("Aucun élève à afficher");
                    tv_message.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.INVISIBLE);
                    pb_indeterminate.setVisibility(View.INVISIBLE);
                    lv.setVisibility(View.INVISIBLE);
                    bt.setEnabled(true);
                } else {
                    tv_message.setVisibility(View.INVISIBLE);
                    pb.setVisibility(View.INVISIBLE);
                    pb_indeterminate.setVisibility(View.INVISIBLE);
                    lv.setVisibility(View.VISIBLE);
                    bt.setEnabled(true);
                }
            }
        });

    }

    //--------------------
    // Retour AsyncTask
    //-------------------

}
