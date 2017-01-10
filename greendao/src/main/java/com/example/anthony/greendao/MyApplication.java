package com.example.anthony.greendao;

import android.app.Application;

public class MyApplication extends Application {

    //private DaoSession daoSession;

    private static MyApplication instance;

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //setupDatabase();
    }

    //    private void setupDatabase() {
    //        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "mytable-db", null);
    //        SQLiteDatabase db = helper.getWritableDatabase();
    //        DaoMaster daoMaster = new DaoMaster(db);
    //        daoSession = daoMaster.newSession();
    //    }
    //
    //    public DaoSession getDaoSession() {
    //        return daoSession;
    //    }
}
