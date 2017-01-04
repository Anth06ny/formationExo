package com.formation.webservice;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by Anthony on 05/04/2016.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Stetho
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }
}
