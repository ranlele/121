package com.fit.run.utils;

import android.util.Log;


public class RunLog {
    private static final String TAG = RunLog.class.getSimpleName();
    private static final boolean DEBUG = true;

    /**
     * @param msg
     */
    public static void e(String msg) {
        if (DEBUG) {
            Log.e(TAG, msg);
        }
    }


    /**
     * @param msg
     */
    public static void d(String msg) {
        if (DEBUG) {
            Log.d(TAG, msg);
        }
    }

    /**
     * @param msg
     */
    public static void i(String msg) {
        if (DEBUG) {
            Log.i(TAG, msg);
        }
    }

    /**
     * @param msg
     */
    public static void v(String msg) {
        if (DEBUG) {
            Log.v(TAG, msg);
        }
    }

    /**
     * @param msg
     */
    public static void w(String msg) {
        if (DEBUG) {
            Log.w(TAG, msg);
        }
    }

    /**
     * @param msg
     */
    public static void wtf(String msg) {
        if (DEBUG) {
            Log.wtf(TAG, msg);
        }
    }
}
