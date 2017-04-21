package com.cocolab.common.aiwebcollection.utils;

import android.content.Context;
import android.widget.Toast;

public class QDToast {
    private static boolean isShowLog = true;
    public static void show(Context context, String msg) {
        if (isShowLog) {
            show(context, msg, Toast.LENGTH_SHORT);
        }
    }
    public static void show(Context context, String msg, int duration) {
        if (isShowLog) {
            Toast.makeText(context, msg, duration).show();
        }
    }

}
