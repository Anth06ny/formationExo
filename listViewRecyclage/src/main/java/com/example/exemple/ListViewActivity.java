package com.example.exemple;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.formation.utils.adapter.EleveAdapter;
import com.formation.utils.bean.Eleve;

import java.util.ArrayList;

public class ListViewActivity extends AppCompatActivity implements OnClickListener, AdapterView.OnItemClickListener {

    private final static String SAVE_LIST_KEY = "SAVE_LIST_KEY";

    //composants graphiques
    private ListView lv;
    private Button bt;

    //donnees
    private ArrayList<Eleve> eleveList;

    //outils
    private EleveAdapter eleveAdapter;

    //--------------------
    // view
    //-------------------

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        eleveList = new ArrayList<>();

        eleveAdapter = new EleveAdapter(this, eleveList);

        lv = (ListView) findViewById(R.id.lv);
        lv.setAdapter(eleveAdapter);

        bt = (Button) findViewById(R.id.bt);
        bt.setOnClickListener(this);

        lv.setOnItemClickListener(this);
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
    /**
     * on récupère des objets si l'activité a du être recréée
     */
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            ArrayList<Eleve> temp = savedInstanceState.getParcelableArrayList(SAVE_LIST_KEY);
            eleveList.addAll(temp);
        }
    }

    //--------------------
    // view
    //-------------------
    @Override
    public void onClick(final View v) {
        eleveList.add(new Eleve("Bob", "John", false));
        //on previent la liste que les donnees ont changees
        eleveAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final EleveAdapter.ViewHolder viewHolder = (EleveAdapter.ViewHolder) view.getTag();
        if (viewHolder != null) {
            Toast.makeText(this, viewHolder.eleveBean.getNom(), Toast.LENGTH_SHORT).show();
        }
    }
}