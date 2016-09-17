package com.hybrid.freeopensourceusers.Logging;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;


public class L {
    public static void m(String message) {
        Log.d("Monster", "" + message);
    }

    public static void t(Context context, String message) {
        Toast.makeText(context, message + "", Toast.LENGTH_SHORT).show();
    }
    public static void T(Context context, String message) {
        Toast.makeText(context, message + "", Toast.LENGTH_LONG).show();
    }
}