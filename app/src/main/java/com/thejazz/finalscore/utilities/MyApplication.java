package com.thejazz.finalscore.utilities;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;

public class MyApplication extends Application {

    private static MyApplication sInstance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        Stetho.initializeWithDefaults(this);
    }

    public static MyApplication getsInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }
}
