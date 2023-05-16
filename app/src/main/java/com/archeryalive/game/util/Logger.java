
package com.archeryalive.game.util;

import android.util.Log;

/**
 * Created by Zhenjie Yan on 2018/9/12.
 */
public class Logger {

    private static final String TAG = "AndServerSample";
    private static final boolean DEBUG = true;

    public static void i(Object obj) {
        if (DEBUG) {
            Log.i(TAG, obj == null ? "null" : obj.toString());
        }
    }

    public static void d(Object obj) {
        if (DEBUG) {
            Log.d(TAG, obj == null ? "null" : obj.toString());
        }
    }

    public static void v(Object obj) {
        if (DEBUG) {
            Log.v(TAG, obj == null ? "null" : obj.toString());
        }
    }

    public static void w(Object obj) {
        if (DEBUG) {
            Log.w(TAG, obj == null ? "null" : obj.toString());
        }
    }

    public static void e(Object obj) {
        if (DEBUG) {
            Log.e(TAG, obj == null ? "null" : obj.toString());
        }
    }
}