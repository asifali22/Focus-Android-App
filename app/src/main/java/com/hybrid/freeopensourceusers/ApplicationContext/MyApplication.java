package com.hybrid.freeopensourceusers.ApplicationContext;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.hybrid.freeopensourceusers.Sqlite.DatabaseOperations;
import com.hybrid.freeopensourceusers.Sqlite.DatabaseOperations_Session;
import io.fabric.sdk.android.Fabric;


public class MyApplication extends Application {
    // Instance of full application
    private static MyApplication sInstance = null;

    private static DatabaseOperations mDatabase;
    private static DatabaseOperations_Session msDatabase;
    // First thing to be done
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        sInstance = this;

    }

    public synchronized static DatabaseOperations getDatabase(){
        if (mDatabase == null)
            mDatabase = new DatabaseOperations(getAppContext());
        return mDatabase;
    }
    public synchronized  static DatabaseOperations_Session getMsDatabase(){
        if(msDatabase == null)
            msDatabase = new DatabaseOperations_Session(getAppContext());
        return msDatabase;
    }

    // Get Instance
    public static MyApplication getInstance(){
        return sInstance;
    }
    // Get APPLICATION context rather activity context
    public static Context getAppContext(){
        return sInstance.getApplicationContext();
    }
}

