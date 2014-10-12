package com.example.fragmentfromscratch;

import android.app.Application;

public class MyApplication extends Application {

    private static MyApplication instance;

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    //-------------------------
    // getter setter
    //-------------------------

    /**
     * Detect si on est en simple ou double affichage
     * @return
     */
    public boolean isTwoPane() {
        return getResources().getBoolean(R.bool.twoPane);
    }
}
