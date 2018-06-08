package com.example.sendsms;

import android.app.Application;

import com.squareup.otto.Bus;

/**
 * Created by Anthony on 05/04/2016.
 */
public class MyApplication extends Application {

    private static MyApplication instance;
    private static Bus bus;

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        instance = this;
        bus = new Bus();
    }

    public static Bus getBus() {
        return bus;
    }
}
