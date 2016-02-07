package com.example.schedulerproject;

import android.util.Log;

/**
 * Created by 현욱 on 2016-01-26.
 */
public class LogManager {
    private static final String TAG = "Scheduler";
    private static final boolean DEBUG = true;
    public static void logPrint(String text) {
        if(DEBUG) {
            Log.v(TAG, text);
        }
    }
}
