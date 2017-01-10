package com.formation.webservice;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by Anthony on 05/04/2016.
 */
public class MyApplication extends Application {

    public static final boolean DEBUG = true;

    @Override
    public void onCreate() {
        super.onCreate();

        if (DEBUG) {
            Stetho.initializeWithDefaults(this);
        }
    }
}
