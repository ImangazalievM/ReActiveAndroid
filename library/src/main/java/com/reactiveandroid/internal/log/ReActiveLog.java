package com.reactiveandroid.internal.log;

import android.util.Log;

public final class ReActiveLog {

    private static final String TAG = "ReActiveAndroid";
    private static final boolean LOCATION_ENABLED = false;
    private static final int UNNECESSARY_INFO_TRACE_LENGTH = 6;
    private static final int TRACE_LENGTH = 5;

    private static LogLevel currentLogLevel = LogLevel.NONE;

    private ReActiveLog() {
    }

    public static void setLogLevel(LogLevel logLevel) {
        ReActiveLog.currentLogLevel = logLevel;
    }

    public static boolean isLoggingEnabled() {
        return currentLogLevel != LogLevel.NONE;
    }

    public static int v(LogLevel logLevel, String msg) {
        return v(logLevel, TAG, msg);
    }

    public static int v(LogLevel logLevel, String tag, String msg) {
        if (currentLogLevel.log(logLevel)) {
            return Log.v(tag, msg + getTrace());
        }
        return 0;
    }

    public static int d(LogLevel logLevel, String msg) {
        return d(logLevel, TAG, msg);
    }

    public static int d(LogLevel logLevel, String tag, String msg) {
        if (currentLogLevel.log(logLevel)) {
            return Log.d(tag, msg + getTrace());
        }
        return 0;
    }

    public static int i(LogLevel logLevel, String msg) {
        return i(logLevel, TAG, msg);
    }

    public static int i(LogLevel logLevel, String tag, String msg) {
        if (currentLogLevel.log(logLevel)) {
            return Log.i(tag, msg + getTrace());
        }
        return 0;
    }

    public static int w(LogLevel logLevel, String msg) {
        return w(logLevel, TAG, msg);
    }

    public static int w(LogLevel logLevel, String tag, String msg) {
        if (currentLogLevel.log(logLevel)) {
            return Log.w(tag, msg + getTrace());
        }
        return 0;
    }

    public static int w(LogLevel logLevel, String msg, Throwable tr) {
        return w(logLevel, TAG, msg, tr);
    }

    public static int w(LogLevel logLevel, String tag, String msg, Throwable tr) {
        if (currentLogLevel.log(logLevel)) {
            return Log.w(tag, msg + getTrace(), tr);
        }
        return 0;
    }

    public static int e(LogLevel logLevel, String msg) {
        return e(logLevel, TAG, msg);
    }

    public static int e(LogLevel logLevel, String tag, String msg) {
        if (currentLogLevel.log(logLevel)) {
            return Log.e(tag, msg + getTrace());
        }
        return 0;
    }

    public static int e(LogLevel logLevel, String msg, Throwable tr) {
        return e(logLevel, TAG, msg, tr);
    }

    public static int e(LogLevel logLevel, String tag, String msg, Throwable tr) {
        if (currentLogLevel.log(logLevel)) {
            return Log.e(tag, msg + getTrace(), tr);
        }
        return 0;
    }

    private static String getTrace() {
        if (!LOCATION_ENABLED)
            return "";

        final String logClassName = ReActiveLog.class.getName();
        final StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        int foundIndex = -1;

        for (int i = 0; i < traces.length; i++) {
            StackTraceElement trace = traces[i];

            if (trace.getClassName().startsWith(logClassName)) {
                foundIndex = i;
            } else {
                if (foundIndex > 0)
                    break;
            }
        }

        StringBuilder sb = new StringBuilder("\n");

        int startIndex = foundIndex + UNNECESSARY_INFO_TRACE_LENGTH;
        for (int i = startIndex; i < startIndex + TRACE_LENGTH; ++i) {
            if (i >= traces.length)
                break;

            StackTraceElement trace = traces[i];
            sb.append(String.format("    at %s.%s:%s\n", trace.getClassName(), trace.getMethodName(), trace.getLineNumber()));
        }
        sb.delete(sb.length() - 1, sb.length());
        return "\n" + sb.toString();
    }

}