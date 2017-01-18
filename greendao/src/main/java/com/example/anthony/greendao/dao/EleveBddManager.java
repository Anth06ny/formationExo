package com.example.anthony.greendao.dao;

import com.example.anthony.greendao.MyApplication;
import com.example.anthony.greendao.bean.Eleve;

import greendaobeans.EleveDao;

/**
 * Created by Anthony on 17/01/2017.
 */
public class EleveBddManager {

    public static void insert(Eleve eleve) {
        getEleveDao().insert(eleve);
    }

    public static Eleve getEleve(long id) {
        return getEleveDao().load(id);
    }

    private static EleveDao getEleveDao() {
        return MyApplication.getDaoSession().getEleveDao();
    }
}
