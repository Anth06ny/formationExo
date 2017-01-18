package com.example.anthony.greendao.dao;

import com.example.anthony.greendao.MyApplication;
import com.example.anthony.greendao.bean.Classe;
import com.example.anthony.greendao.bean.ClasseEnseignant;
import com.example.anthony.greendao.bean.Enseignant;

import java.util.List;

import greendaobeans.ClasseDao;

/**
 * Created by Anthony on 17/01/2017.
 */
public class ClassesBddManager {

    public static void insert(Classe classe) {
        getClasseDao().insert(classe);
    }

    public static Classe getClasse(long id) {
        return getClasseDao().load(id);
    }

    public static List<Classe> getAll() {
        return getClasseDao().loadAll();
    }

    public static boolean isEmpty() {
        return getClasseDao().count() == 0;
    }

    private static void addEnseignantToClasse(Enseignant enseignant, Classe classe) {
        ClasseEnseignant classeEnseignant = new ClasseEnseignant();
        classeEnseignant.setClasseId(classe.getId());
        classeEnseignant.setEnseignantId(enseignant.getId());

        MyApplication.getDaoSession().getClasseEnseignantDao().insert(classeEnseignant);
    }

    private static ClasseDao getClasseDao() {
        return MyApplication.getDaoSession().getClasseDao();
    }
}
