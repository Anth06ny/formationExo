package com.example.exemple.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.exemple.bean.Eleve;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anthony on 12/10/2014.
 * Permet de gérer la base de donnée des élèves.
 */
public class EleveBDDManager {

    public static final String TABLE_ELEVE = "Eleve";

    private static final String COL_ID = "ID";
    private static final String COL_PRENOM = "Prenom";
    private static final String COL_NOM = "Nom";

    public static final String CREATE_ELEVE_TABLE = "CREATE TABLE " + TABLE_ELEVE + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_PRENOM + " TEXT NOT NULL, " + COL_NOM + " TEXT NOT NULL);";

    private SQLiteDatabase bdd;
    private MaBaseSQLite maBaseSQLite;

    public EleveBDDManager(Context context) {
        //On créer la BDD et sa table
        maBaseSQLite = new MaBaseSQLite(context, null);
    }

    /* *****************************
     * Gestion session ***
     *****************************   */
    private void open() {
        bdd = maBaseSQLite.getWritableDatabase();
    }

    private void close() {
        bdd.close();
    }

    /* *****************************
    *           Acces BDD        ***
    *****************************   */

    public void insertEleve(Eleve eleve) {
        open();
        //Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = new ContentValues();
        //on lui ajoute une valeur associée à une clé (qui est le nom de la colonne dans laquelle on veut mettre la
        // valeur)
        values.put(COL_PRENOM, eleve.getPrenom());
        values.put(COL_NOM, eleve.getNom());
        //on insère l'objet dans la BDD via le ContentValues
        eleve.setId(bdd.insert(TABLE_ELEVE, null, values));
        if (eleve.getId() == -1) {
            //gestion erreur
        }
        close();
    }

    public int updateEleve(Eleve eleve) {
        open();
        //La mise à jour d'un élève dans la BDD fonctionne plus ou moins comme une insertion
        //il faut simplement préciser quel élève on doit mettre à jour grâce à l'ID
        ContentValues values = new ContentValues();
        values.put(COL_PRENOM, eleve.getPrenom());
        values.put(COL_NOM, eleve.getNom());
        int result = bdd.update(TABLE_ELEVE, values, COL_ID + " = " + eleve.getId(), null);
        close();
        return result;
    }

    public int removeEleveWithID(int id) {
        open();
        //Suppression d'un élève de la BDD grâce à l'ID
        int result = bdd.delete(TABLE_ELEVE, COL_ID + " = " + id, null);
        close();
        return result;
    }

    public List<Eleve> getAllEleves() {
        open();
        //Récupère dans un Cursor tous les élèves
        Cursor c = bdd.query(TABLE_ELEVE, new String[] { COL_ID, COL_PRENOM, COL_NOM }, null, null, null, null, null);
        List<Eleve> result = cursorToEleves(c);
        close();
        return result;
    }

    public List<Eleve> getElevesWithPrenom(String prenom) {
        open();
        //Récupère dans un Cursor tous les élèves correspondant au prénom
        Cursor c = bdd.query(TABLE_ELEVE, new String[] { COL_ID, COL_PRENOM, COL_NOM }, COL_PRENOM + " LIKE \"" + prenom + "\"", null, null, null,
                null);
        List<Eleve> result = cursorToEleves(c);
        close();
        return result;
    }

    /* *****************************
    *          Currseur       ***
    *****************************   */

    //Cette méthode permet de convertir un cursor en list d'Eleve
    private List<Eleve> cursorToEleves(Cursor c) {
        ArrayList<Eleve> eleveListe = new ArrayList<Eleve>();

        if (c != null) {
            //On se place sur le premier élément
            if (c.moveToFirst()) {
                do {
                    Eleve eleveBean = new Eleve(c.getString(c.getColumnIndex(COL_NOM)), c.getString(c.getColumnIndex(COL_PRENOM)), false);
                    eleveListe.add(eleveBean);
                } while (c.moveToNext());
            }

            //On ferme le cursor
            c.close();
        }

        //On retourne la liste
        return eleveListe;
    }

    /* *****************************
     *******  Gestion session   ***
     *****************************   */

    public SQLiteDatabase getBDD() {
        return bdd;
    }
}
