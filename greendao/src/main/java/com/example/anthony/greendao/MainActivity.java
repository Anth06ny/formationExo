package com.example.anthony.greendao;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.example.anthony.greendao.adapter.RecycleViewAdapter;
import com.example.anthony.greendao.bean.Classe;
import com.example.anthony.greendao.bean.Eleve;
import com.example.anthony.greendao.bean.Enseignant;
import com.example.anthony.greendao.dao.ClassesBddManager;
import com.example.anthony.greendao.dao.EleveBddManager;
import com.example.anthony.greendao.dao.EnseignantBddManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecycleViewAdapter.RVAdapterCallBack {

    private static final int AFFICHER_ELEVE_DE_LA_CLASSE = 0;
    private static final int AFFICHER_ENSEIGNANT_DE_LELEVE = 1;
    private static final int AFFICHER_CLASSE_DE_LENSEIGNANT = 2;

    private static final String TYPE_AFFICHAGE_EXTRA = "TYPE_AFFICHAGE_EXTRA";
    private static final String ID_EXTRA = "ID_EXTRA";

    //Composant graphique
    private Button bt;
    private RecyclerView rv;
    private RecyclerView.LayoutManager layoutManager;

    //metier
    private RecycleViewAdapter recycleViewAdapter;

    //donnees
    private List<Object> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycleview);

        rv = (RecyclerView) findViewById(R.id.rv);

        list = new ArrayList<>();
        //est ce que la taille en hauteur des cellule de la recycle view va changer ?
        rv.setHasFixedSize(false);
        //A ajouter obligatoirement
        rv.setLayoutManager(layoutManager = new LinearLayoutManager(this));
        rv.setItemAnimator(new DefaultItemAnimator());

        recycleViewAdapter = new RecycleViewAdapter(list, RecycleViewAdapter.TYPE.CLASSE, this);
        rv.setAdapter(recycleViewAdapter);

        //Chargement des données
        long id = -1;
        int typeAffichage = AFFICHER_CLASSE_DE_LENSEIGNANT;
        if (getIntent().getExtras() != null) {
            id = getIntent().getLongExtra(ID_EXTRA, -1);
            typeAffichage = getIntent().getIntExtra(TYPE_AFFICHAGE_EXTRA, AFFICHER_CLASSE_DE_LENSEIGNANT);
        }

        if (id <= 0) {
            //ParDefaut on charge la liste de classe
            list.addAll(ClassesBddManager.getAll());
            setTitle("Toutes les classes");
            recycleViewAdapter.setType(RecycleViewAdapter.TYPE.CLASSE);
        }
        else if (typeAffichage == AFFICHER_CLASSE_DE_LENSEIGNANT) {
            //On a donc un id d'un enseignant
            Enseignant enseignant = EnseignantBddManager.getEnseignant(id);
            list.addAll(enseignant.getClasseList());
            recycleViewAdapter.setType(RecycleViewAdapter.TYPE.CLASSE);

            setTitle("Classe de " + enseignant.getNom());
        }
        else if (typeAffichage == AFFICHER_ELEVE_DE_LA_CLASSE) {
            //On a donc un id d'une classe
            Classe classe = ClassesBddManager.getClasse(id);
            list.addAll(classe.getEleves());
            recycleViewAdapter.setType(RecycleViewAdapter.TYPE.ELEVE);

            setTitle("Eleve de la classe " + classe.getName());
        }
        else if (typeAffichage == AFFICHER_ENSEIGNANT_DE_LELEVE) {
            //On a donc un id d'un élève
            Eleve eleve = EleveBddManager.getEleve(id);
            list.addAll(eleve.getClasse().getEnseignantList());
            recycleViewAdapter.setType(RecycleViewAdapter.TYPE.ENSEIGNANT);

            setTitle("Enseignant de l'élève " + eleve.getNom());
        }
    }

    @Override
    public void onEleveClic(Eleve eleve) {
        //On affiche la liste de ses ensignants
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(TYPE_AFFICHAGE_EXTRA, AFFICHER_ENSEIGNANT_DE_LELEVE);
        intent.putExtra(ID_EXTRA, eleve.getId());

        startActivity(intent);
    }

    @Override
    public void onEnseignantClic(Enseignant enseignant) {
        //La liste de ses classes
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(TYPE_AFFICHAGE_EXTRA, AFFICHER_CLASSE_DE_LENSEIGNANT);
        intent.putExtra(ID_EXTRA, enseignant.getId());

        startActivity(intent);
    }

    @Override
    public void onClasseClic(Classe classe) {
        //liste des eleves
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(TYPE_AFFICHAGE_EXTRA, AFFICHER_ELEVE_DE_LA_CLASSE);
        intent.putExtra(ID_EXTRA, classe.getId());

        startActivity(intent);
    }
}
