package com.example.exemple.recycleview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.example.exemple.R;
import com.formation.utils.bean.Eleve;

import java.util.ArrayList;

/**
 * Created by Anthony on 08/01/2016.
 */
public class RecycleViewActivity extends AppCompatActivity implements View.OnClickListener, RecycleViewAdapter.RVAdapterCallBack {

    //Composant graphique
    private Button bt;
    private RecyclerView rv;
    private RecyclerView.LayoutManager layoutManager;

    //metier
    private RecycleViewAdapter recycleViewAdapter;

    //donnees
    private ArrayList<Eleve> eleveList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycleview);

        rv = (RecyclerView) findViewById(R.id.rv);
        bt = (Button) findViewById(R.id.bt);

        eleveList = new ArrayList<>();
        eleveList.add(new Eleve("bob", "bob", true));
        eleveList.add(new Eleve("bob", "bob", true));
        eleveList.add(new Eleve("bob", "bob", true));

        //est ce que la taille de la recycle view va changer ?
        rv.setHasFixedSize(true);
        //A ajouter obligatoirement
        rv.setLayoutManager(layoutManager = new LinearLayoutManager(this));
        rv.setItemAnimator(new DefaultItemAnimator());

        recycleViewAdapter = new RecycleViewAdapter(eleveList, this);
        rv.setAdapter(recycleViewAdapter);

        bt.setOnClickListener(this);
    }

    @Override
    public void onEleveClic(Eleve eleve) {
        //On supprime l'eleve cliqué de la liste, mais pour permettre l'animation on recherche sa position dans la liste
        int index = eleveList.indexOf(eleve);
        eleveList.remove(eleve);
        recycleViewAdapter.notifyItemRemoved(index);
    }

    @Override
    public void onClick(View v) {
        //On ajoute un élève
        Eleve eleve = new Eleve("Nom" + eleveList.size(), "Prénom", eleveList.size() % 2 == 0);
        eleveList.add(0, eleve);
        //Pour permettre l'animation on indique qu'un élément à été ajouté à l'emplacement 0
        recycleViewAdapter.notifyItemInserted(0);
        //recycleViewAdapter.notifyDataSetChanged();
        //Remonte l'assenceur à la position 0
        layoutManager.scrollToPosition(0);
    }
}
