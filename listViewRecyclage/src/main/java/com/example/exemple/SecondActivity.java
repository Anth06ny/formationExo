package com.example.exemple;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.example.exemple.adapter.EleveAdapter;
import com.example.exemple.bean.Eleve;

import java.util.ArrayList;

public class SecondActivity extends Activity implements OnClickListener {

    private final static String SAVE_LIST_KEY = "SAVE_LIST_KEY";

    //composants graphiques
    private ListView lv;
    private Button bt;

    //donn�es
    private ArrayList<Eleve> eleveList;

    //outils
    private EleveAdapter eleveAdapter;

    //--------------------
    // view
    //-------------------

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        eleveList = new ArrayList<Eleve>();

        eleveAdapter = new EleveAdapter(this, eleveList);

        lv = (ListView) findViewById(R.id.lv);
        lv.setAdapter(eleveAdapter);

        bt = (Button) findViewById(R.id.bt);
        bt.setOnClickListener(this);

    }

    @Override
    /**
     * on sauvegarde des objets si l'activité doit être recréée
     */
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(SAVE_LIST_KEY, eleveList);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    /**
     * on récupère des objets si l'activité a du être recréée
     */
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            ArrayList<Eleve> temp = savedInstanceState.getParcelableArrayList(SAVE_LIST_KEY);
            eleveList.addAll(temp);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.second, menu);
        return true;
    }

    //--------------------
    // view
    //-------------------
    @Override
    public void onClick(final View v) {
        eleveList.add(new Eleve("Bob", "John", false));
        //on previent la liste que les donn�es ont chang�es
        eleveAdapter.notifyDataSetChanged();
    }
}
