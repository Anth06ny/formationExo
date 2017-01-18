package com.example.anthony.greendao.dao;

import com.example.anthony.greendao.MyApplication;
import com.example.anthony.greendao.bean.Classe;
import com.example.anthony.greendao.bean.ClasseEnseignant;
import com.example.anthony.greendao.bean.Enseignant;

import greendaobeans.ClasseEnseignantDao;

/**
 * Created by Anthony on 17/01/2017.
 */
public class ClassesEnseignantBddManager {

    public static void addEnseignantToClasse(Enseignant enseignant, Classe classe) {
        ClasseEnseignant classeEnseignant = new ClasseEnseignant();
        classeEnseignant.setClasseId(classe.getId());
        classeEnseignant.setEnseignantId(enseignant.getId());

        getClasseEnseignantDao().insert(classeEnseignant);
    }

    private static ClasseEnseignantDao getClasseEnseignantDao() {
        return MyApplication.getDaoSession().getClasseEnseignantDao();
    }
}
