package com.example.anthony.greendao.dao;

import com.example.anthony.greendao.MyApplication;
import com.example.anthony.greendao.bean.Classe;
import com.example.anthony.greendao.bean.ClasseDao;

import java.util.List;

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

    private static ClasseDao getClasseDao() {
        return MyApplication.getDaoSession().getClasseDao();
    }
}
