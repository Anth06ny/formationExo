package com.example.anthony.greendao.dao;

import com.example.anthony.greendao.MyApplication;
import com.example.anthony.greendao.bean.Enseignant;
import com.example.anthony.greendao.bean.EnseignantDao;

/**
 * Created by Anthony on 17/01/2017.
 */
public class EnseignantBddManager {

    public static Enseignant getEnseignant(long id) {
        return getEnseignantDao().load(id);
    }

    public static void insert(Enseignant enseignant) {
        getEnseignantDao().insert(enseignant);
    }

    private static EnseignantDao getEnseignantDao() {
        return MyApplication.getDaoSession().getEnseignantDao();
    }
}
