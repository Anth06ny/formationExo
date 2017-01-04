package com.example.daosqlicontentproviderclient;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by Anthony on 12/10/2014.
 * Permet de gérer la base de donnée des élèves.
 */
public class EleveBDDManager {

    // URI de notre content provider, elle sera utilisé pour accéder au ContentProvider
    public static final Uri DAOURI = Uri
            .parse("content://com.example.exemple.dao.elevecontentorovider");

    public static final String COL_ID = "ID";
    public static final String COL_PRENOM = "Prenom";
    public static final String COL_NOM = "Nom";

    public static ArrayList<Eleve> getEleves(Context context) {
        String columns[] = new String[]{EleveBDDManager.COL_ID, EleveBDDManager.COL_NOM, EleveBDDManager.COL_PRENOM};
        Cursor cur = context.getContentResolver().query(DAOURI, columns, null, null, null);
        return cursorToEleves(cur);
    }

    public static void addEleve(Context context) {
        ContentValues contact = new ContentValues();
        contact.put(COL_PRENOM, "From");
        contact.put(COL_NOM, "Client");
        context.getContentResolver().insert(DAOURI, contact);
    }

    //Cette méthode permet de convertir un cursor en list d'Eleve
    private static ArrayList<Eleve> cursorToEleves(Cursor c) {
        ArrayList<Eleve> eleveListe = new ArrayList<Eleve>();

        if (c != null) {
            //On se place sur le premier élément
            if (c.moveToFirst()) {
                do {
                    Eleve eleveBean = new Eleve(c.getLong(c.getColumnIndex(COL_ID)), c.getString(c.getColumnIndex(COL_NOM)), c.getString(c.getColumnIndex(COL_PRENOM)));
                    eleveListe.add(eleveBean);
                }
                while (c.moveToNext());
            }

            //On ferme le cursor
            c.close();
        }

        //On retourne la liste
        return eleveListe;
    }
}
