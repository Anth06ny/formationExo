package com.example.anthony.greendao;

import android.app.Application;

import com.example.anthony.greendao.bean.Classe;
import com.example.anthony.greendao.bean.DaoMaster;
import com.example.anthony.greendao.bean.DaoSession;
import com.example.anthony.greendao.bean.Eleve;
import com.example.anthony.greendao.bean.Enseignant;
import com.example.anthony.greendao.dao.ClassesBddManager;
import com.example.anthony.greendao.dao.ClassesEnseignantBddManager;
import com.example.anthony.greendao.dao.EleveBddManager;
import com.example.anthony.greendao.dao.EnseignantBddManager;
import com.facebook.stetho.Stetho;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.Random;

public class MyApplication extends Application {

    private static DaoSession daoSession;

    private static MyApplication instance;

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Stetho.initializeWithDefaults(this);
        setupDatabase();
        if (ClassesBddManager.isEmpty()) {
            fillDataBase();
        }
    }

    private void setupDatabase() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    /**
     * Ajoute des données dans la base pour la remplir
     */
    private void fillDataBase() {
        int nbclasse = 5;
        int nbEleve = 100;
        int nbEnseignant = 20;
        Random random = new Random();

        ArrayList<Classe> classes = new ArrayList<>();
        ArrayList<Enseignant> enseignants = new ArrayList<>();
        ArrayList<Eleve> eleves = new ArrayList<>();
        //Un ensemble de Classe
        for (int i = 0; i < nbclasse; i++) {
            Classe classe = new Classe();
            classe.setName("Classe_" + i);
            ClassesBddManager.insert(classe);
            classes.add(classe);
        }

        //Un ensemble d'élève
        for (int i = 0; i < nbEleve; i++) {
            Eleve eleve = new Eleve();
            eleve.setNom("Eleve_" + i);
            eleve.setPrenom("prenom");
            eleve.setClasseId(classes.get(random.nextInt(nbclasse)).getId());
            eleves.add(eleve);
            EleveBddManager.insert(eleve);
        }

        //Un ensemble d'enseignant
        for (int i = 0; i < nbEnseignant; i++) {
            Enseignant enseignant = new Enseignant();
            enseignant.setPrenom("Enseignant_" + i);
            enseignant.setNom("Enseignant_" + i);
            enseignants.add(enseignant);
            EnseignantBddManager.insert(enseignant);
        }

        //Relation Enseignant * classe
        for (Enseignant enseignant : enseignants) {
            for (Classe classe : classes) {
                //1 chance sur 3 qu'un enseignant s'occupe d'une classe
                if (random.nextInt(3) == 0) {
                    ClassesEnseignantBddManager.addEnseignantToClasse(enseignant, classe);
                }
            }
        }
    }

    public static DaoSession getDaoSession() {
        return daoSession;
    }
}
