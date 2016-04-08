package com.example.exemple;

import android.app.Application;

import com.example.exemple.dao.MaBaseSQLite;

/**
 * Created by Anthony on 05/04/2016.
 */
public class MyApplication extends Application {

    private static MaBaseSQLite maBaseSQLite;

    public static MaBaseSQLite getMaBaseSQLite() {
        return maBaseSQLite;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        maBaseSQLite = new MaBaseSQLite(this);
    }
}
