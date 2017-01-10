package com.example.anthony.maps;

import android.app.Application;

/**
 * Created by Anthony on 10/01/2017.
 */
public class MyApplication extends Application {

    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static MyApplication getInstance() {
        return instance;
    }
}
