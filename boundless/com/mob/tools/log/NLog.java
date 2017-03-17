package com.mob.tools.log;

import android.content.Context;
import android.util.Log;
import java.util.HashMap;

public abstract class NLog {
    private static HashMap<String, NLog> loggers = new HashMap();
    private static LogPrinter printer = new LogPrinter();

    protected abstract String getSDKTag();

    static {
        MobUncaughtExceptionHandler.register();
    }

    public NLog() {
        loggers.put(getSDKTag(), this);
        if (loggers.size() == 1) {
            loggers.put("__FIRST__", this);
        }
    }

    public static void setContext(Context context) {
        if (context != null) {
            printer.setContext(context);
            NativeErrorHandler.prepare(context);
        }
    }

    public static void setCollector(String sdkTag, LogCollector collector) {
        printer.setCollector(sdkTag, collector);
    }

    protected static final NLog getInstanceForSDK(final String sdkTag, boolean createNew) {
        NLog instance = (NLog) loggers.get(sdkTag);
        if (instance == null) {
            instance = (NLog) loggers.get("__FIRST__");
        }
        if (instance == null && createNew) {
            return new NLog() {
                protected String getSDKTag() {
                    return sdkTag;
                }
            };
        }
        return instance;
    }

    public final int v(Throwable t) {
        return printer.println(getSDKTag(), 2, 0, Log.getStackTraceString(t));
    }

    public final int v(Object format, Object... args) {
        String message;
        String s = format.toString();
        if (args.length > 0) {
            message = String.format(s, args);
        } else {
            message = s;
        }
        return printer.println(getSDKTag(), 2, 0, message);
    }

    public final int v(Throwable throwable, Object format, Object... args) {
        String s = format.toString();
        StringBuilder stringBuilder = new StringBuilder();
        if (args.length > 0) {
            s = String.format(s, args);
        }
        return printer.println(getSDKTag(), 2, 0, stringBuilder.append(s).append('\n').append(Log.getStackTraceString(throwable)).toString());
    }

    public final int d(Throwable t) {
        return printer.println(getSDKTag(), 3, 0, Log.getStackTraceString(t));
    }

    public final int d(Object format, Object... args) {
        String message;
        String s = format.toString();
        if (args.length > 0) {
            message = String.format(s, args);
        } else {
            message = s;
        }
        return printer.println(getSDKTag(), 3, 0, message);
    }

    public final int d(Throwable throwable, Object format, Object... args) {
        String s = format.toString();
        StringBuilder stringBuilder = new StringBuilder();
        if (args.length > 0) {
            s = String.format(s, args);
        }
        return printer.println(getSDKTag(), 3, 0, stringBuilder.append(s).append('\n').append(Log.getStackTraceString(throwable)).toString());
    }

    public final int i(Throwable t) {
        return printer.println(getSDKTag(), 4, 0, Log.getStackTraceString(t));
    }

    public final int i(Object format, Object... args) {
        String message;
        String s = format.toString();
        if (args.length > 0) {
            message = String.format(s, args);
        } else {
            message = s;
        }
        return printer.println(getSDKTag(), 4, 0, message);
    }

    public final int i(Throwable throwable, Object format, Object... args) {
        String s = format.toString();
        StringBuilder stringBuilder = new StringBuilder();
        if (args.length > 0) {
            s = String.format(s, args);
        }
        return printer.println(getSDKTag(), 4, 0, stringBuilder.append(s).append('\n').append(Log.getStackTraceString(throwable)).toString());
    }

    public final int w(Throwable t) {
        return printer.println(getSDKTag(), 5, 0, Log.getStackTraceString(t));
    }

    public final int w(Object format, Object... args) {
        String message;
        String s = format.toString();
        if (args.length > 0) {
            message = String.format(s, args);
        } else {
            message = s;
        }
        return printer.println(getSDKTag(), 5, 0, message);
    }

    public final int w(Throwable throwable, Object format, Object... args) {
        String s = format.toString();
        StringBuilder stringBuilder = new StringBuilder();
        if (args.length > 0) {
            s = String.format(s, args);
        }
        return printer.println(getSDKTag(), 5, 0, stringBuilder.append(s).append('\n').append(Log.getStackTraceString(throwable)).toString());
    }

    public final int e(Throwable t) {
        return printer.println(getSDKTag(), 6, 0, Log.getStackTraceString(t));
    }

    public final int e(Object format, Object... args) {
        String message;
        String s = format.toString();
        if (args.length > 0) {
            message = String.format(s, args);
        } else {
            message = s;
        }
        return printer.println(getSDKTag(), 6, 0, message);
    }

    public final int e(Throwable throwable, Object format, Object... args) {
        String s = format.toString();
        StringBuilder stringBuilder = new StringBuilder();
        if (args.length > 0) {
            s = String.format(s, args);
        }
        return printer.println(getSDKTag(), 6, 0, stringBuilder.append(s).append('\n').append(Log.getStackTraceString(throwable)).toString());
    }

    public final int crash(Throwable t) {
        return printer.println(getSDKTag(), 6, 1, Log.getStackTraceString(t));
    }

    public final void nativeCrashLog(String log) {
        printer.nativeCrashLog(getSDKTag(), log);
    }
}
