package com.example.exemple.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Anthony on 12/10/2014.
 */
public class MaBaseSQLite extends SQLiteOpenHelper {

    private static final String NOM_BDD = "mabase.db";
    private static final int VERSION_BDD = 1;

    public MaBaseSQLite(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, NOM_BDD, factory, VERSION_BDD);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //on créé la table à partir de la requête écrite dans la variable CREATE_ELEVE_TABLE
        sqLiteDatabase.execSQL(EleveBDDManager.CREATE_ELEVE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //On peut fait ce qu'on veut ici moi j'ai décidé de supprimer la table et de la recréer
        //comme ça lorsque je change la version les id repartent de 0
        sqLiteDatabase.execSQL("DROP TABLE " + EleveBDDManager.TABLE_ELEVE + ";");
        onCreate(sqLiteDatabase);
    }
}
