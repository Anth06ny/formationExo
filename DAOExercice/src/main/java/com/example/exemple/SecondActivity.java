package com.example.exemple;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.example.exemple.adapter.EleveAdapter;
import com.example.exemple.dao.Eleve;
import com.example.exemple.dao.EleveBDDManager;

import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends Activity implements OnClickListener {

    private final static String SAVE_LIST_KEY = "SAVE_LIST_KEY";

    //composants graphiques
    private ListView lv;
    private Button bt;

    //donn�es
    private ArrayList<Eleve> eleveBeanList;

    //outils
    private EleveAdapter eleveAdapter;

    //--------------------
    // view
    //-------------------

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        eleveBeanList = new ArrayList<Eleve>();
        eleveAdapter = new EleveAdapter(this, eleveBeanList);

        lv = (ListView) findViewById(R.id.lv);
        lv.setAdapter(eleveAdapter);

        bt = (Button) findViewById(R.id.bt);
        bt.setOnClickListener(this);

        loadEleveFromBDD();

        eleveAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.second, menu);
        return true;
    }

    //--------------------
    // evenement
    //-------------------

    @Override
    public void onClick(final View v) {
        Eleve eleveBean = new Eleve();
        eleveBean.setNom("Bob");
        eleveBean.setPrenom("John");

        //on le sauvegarde en abse
        saveEleveInBDD(eleveBean);
        eleveBeanList.add(eleveBean);
        //on previent la liste que les donn�es ont chang�es
        eleveAdapter.notifyDataSetChanged();
    }

    //--------------------
    // bdd
    //-------------------

    private void loadEleveFromBDD() {

        List<Eleve> temp = EleveBDDManager.getAllEleve();
        eleveBeanList.addAll(temp);
    }

    private void saveEleveInBDD(Eleve eleveBean) {
        EleveBDDManager.insertOrUpdate(eleveBean);
    }
}
