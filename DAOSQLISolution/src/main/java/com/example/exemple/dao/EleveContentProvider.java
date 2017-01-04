package com.example.exemple.dao;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.exemple.R;

/**
 * Created by Anthony on 22/09/2016.
 */
public class EleveContentProvider extends ContentProvider {

    private MaBaseSQLite maBaseSQLite;

    @Override
    public boolean onCreate() {
        maBaseSQLite = new MaBaseSQLite(getContext());
        return false;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        //Le type mime défini aussi dans le manifest.
        return getContext().getResources().getString(R.string.type_mime);
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //Effectuer une requete
        long id = getId(uri);
        SQLiteDatabase db = maBaseSQLite.getReadableDatabase();
        if (id < 0) {
            return db.query(EleveBDDManager.TABLE_ELEVE,
                    projection, selection, selectionArgs, null, null,
                    sortOrder);
        }
        else {
            return db.query(EleveBDDManager.TABLE_ELEVE,
                    projection, EleveBDDManager.COL_ID + "=" + id, null, null, null,
                    null);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = maBaseSQLite.getWritableDatabase();
        try {
            long id = db.insertOrThrow(EleveBDDManager.TABLE_ELEVE, null, values);

            if (id == -1) {
                throw new RuntimeException(String.format(
                        "%s : Failed to insert [%s] for unknown reasons.", "DAOSQLISolution", values, uri));
            }
            else {
                return ContentUris.withAppendedId(uri, id);
            }
        }
        finally {
            db.close();
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        long id = getId(uri);
        SQLiteDatabase db = maBaseSQLite.getWritableDatabase();
        try {
            if (id < 0) {
                return db.delete(
                        EleveBDDManager.TABLE_ELEVE,
                        selection, selectionArgs);
            }
            else {
                return db.delete(
                        EleveBDDManager.TABLE_ELEVE,
                        EleveBDDManager.COL_ID + "=" + id, selectionArgs);
            }
        }
        finally {
            db.close();
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        long id = getId(uri);
        SQLiteDatabase db = maBaseSQLite.getWritableDatabase();

        try {
            if (id < 0) {
                return db.update(EleveBDDManager.TABLE_ELEVE, values, selection, selectionArgs);
            }
            else {
                return db.update(EleveBDDManager.TABLE_ELEVE,
                        values, EleveBDDManager.COL_ID + "=" + id, null);
            }
        }
        finally {
            db.close();
        }
    }

    private long getId(Uri uri) {
        String lastPathSegment = uri.getLastPathSegment();
        if (lastPathSegment != null) {
            try {
                return Long.parseLong(lastPathSegment);
            }
            catch (NumberFormatException e) {
                Log.e("TAG", "Number Format Exception : " + e);
            }
        }
        return -1;
    }
}
