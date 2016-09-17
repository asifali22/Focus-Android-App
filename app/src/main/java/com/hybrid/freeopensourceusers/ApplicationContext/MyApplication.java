package com.hybrid.freeopensourceusers.ApplicationContext;

import android.app.Application;
import android.content.Context;

import com.hybrid.freeopensourceusers.Sqlite.DatabaseOperations;


public class MyApplication extends Application {
    // Instance of full application
    private static MyApplication sInstance = null;

    private static DatabaseOperations mDatabase;
    // First thing to be done
    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

    }

    public synchronized static DatabaseOperations getDatabase(){
        if (mDatabase == null)
            mDatabase = new DatabaseOperations(getAppContext());
        return mDatabase;
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

