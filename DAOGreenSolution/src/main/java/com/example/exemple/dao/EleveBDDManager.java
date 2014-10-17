package com.example.exemple.dao;

import android.content.Context;

import com.example.exemple.MyApplication;

import java.util.ArrayList;
import java.util.List;

import greendao.Eleve;
import greendao.EleveDao;

/**
 * Classe de gestion de la table eleve
 */
public class EleveBDDManager {

    public static void insertOrUpdate(Context context, Eleve eleve) {
        getEleveDao(context).insertOrReplace(eleve);
    }

    public static void clearEleve(Context context) {
        getEleveDao(context).deleteAll();
    }

    public static void deleteEleveWithId(Context context, long id) {
        getEleveDao(context).delete(getEleveForId(context, id));
    }

    public static Eleve getEleveForId(Context context, long id) {
        return getEleveDao(context).load(id);
    }

    public static ArrayList<Eleve> getAllEleve(Context context) {
        return (ArrayList<Eleve>) getEleveDao(context).loadAll();
    }

    /**
     * Retourne une liste d'élève en fonction du prénom
     * @param context
     * @return
     */
    public static List<Eleve> getEleveByPrenom(Context context, String prenom) {
        return getEleveDao(context).queryBuilder().where(EleveDao.Properties.Prenom.eq(prenom)).list();
    }

    private static EleveDao getEleveDao(Context c) {
        return MyApplication.getInstance().getDaoSession().getEleveDao();
    }
}
