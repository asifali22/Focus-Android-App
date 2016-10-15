package com.hybrid.freeopensourceusers.Volley;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.hybrid.freeopensourceusers.ApplicationContext.MyApplication;


public class VolleySingleton {

    // Instance of the this class
    private static VolleySingleton mInstance = null;


    private static RequestQueue mRequestQueue;

    // Constructor
    private VolleySingleton() {
        mRequestQueue = Volley.newRequestQueue(MyApplication.getAppContext());
    }

    // Instance getter
    public static VolleySingleton getInstance() {
        if (mInstance == null) {
            mInstance = new VolleySingleton();
        }
        return mInstance;
    }

    // Request queue getter
    public static synchronized RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

}
