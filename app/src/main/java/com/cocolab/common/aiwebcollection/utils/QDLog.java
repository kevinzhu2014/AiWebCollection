package com.cocolab.common.aiwebcollection.utils;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class QDLog {
    private static boolean isShowLog = true;
    public static void d(String msg) {
        if (isShowLog) {
            Log.d("QDReader", msg);
        }
    }

    public static void d(String tag,String msg) {
        if (isShowLog) {
            Log.d("QDReader " +tag, msg);
        }
    }

    public static void message(String msg) {
        if (isShowLog) {
            Log.d("qdmessage", msg);
            writeLogtoFile("d", "message", msg);
        }
    }

    public static void dSaveLog(String tag, String msg) {
        if (isShowLog) {
            Log.d(tag, msg);
            writeLogtoFile("d", tag, msg);
        }
    }

    public static void exception(Throwable e) {
        if (e == null)
            return;
        e.printStackTrace();
        if (isShowLog) {
            Writer info = new StringWriter();
            PrintWriter printWriter = new PrintWriter(info);
            e.printStackTrace(printWriter);
            printWriter.close();
            writeLogtoFile("d", "Exception", info.toString());
        }
    }

    public static void dSaveStackTrace(String tag) {
        if (isShowLog) {
            String stackTrace = Log.getStackTraceString(new Throwable());
            Log.d(tag, stackTrace);
            writeLogtoFile("d", tag, stackTrace);
        }
    }

    public static void dSaveLogForce(String tag, String msg) {
        writeLogtoFile("d", tag, msg);
    }

    public static void e(String msg) {
        if (msg == null)
            return;
        if (isShowLog) {
            Log.e("QDReader", msg);
        }
    }

    public static void e(String tag, String msg) {
        if (msg == null)
            return;
        if (isShowLog) {
            Log.e(tag, msg);
        }
    }

    public static void eForce(String msg, Throwable e) {
        if (msg == null)
            return;
        Log.e("QDReader", msg, e);
    }

    public static void url(String msg) {
        if (isShowLog) {
            Log.d("QDUrl", msg);
        }
    }

    public static void data(String msg) {
        if (isShowLog) {
            Log.d("QDData", msg);
        }
    }

    public static void w(String msg) {
        if (isShowLog) {
            Log.d("QDReaderW", msg);
        }
    }

    private static void writeLogtoFile(String mylogtype, String tag, String text) {
        // 新建或打开日志文件
    }
}
