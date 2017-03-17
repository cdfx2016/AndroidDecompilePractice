package com.mob.tools.log;

import android.content.Context;
import android.text.TextUtils;
import java.util.HashMap;

public class LogPrinter {
    private HashMap<String, LogCollector> collectors = new HashMap();
    private String packageName = "";
    private String scope = "";

    public void setContext(Context context) {
        this.packageName = context.getPackageName();
        if (TextUtils.isEmpty(this.packageName)) {
            this.packageName = "";
        } else {
            this.scope = this.packageName;
        }
    }

    public void setCollector(String sdkTag, LogCollector collector) {
        this.collectors.put(sdkTag, collector);
    }

    public int println(String sdkTag, int priority, int level, String msg) {
        Thread t = Thread.currentThread();
        String message = processMessage(t, msg);
        String scope = getScope(t);
        LogCollector collector = (LogCollector) this.collectors.get(sdkTag);
        if (collector != null) {
            collector.log(sdkTag, priority, level, scope, message);
        }
        return 0;
    }

    public void nativeCrashLog(String sdkTag, String log) {
        LogCollector collector = (LogCollector) this.collectors.get(sdkTag);
        if (collector != null) {
            collector.log(sdkTag, 6, 2, this.scope, log);
        }
    }

    private String processMessage(Thread t, String msg) {
        return String.format("%s %s", new Object[]{t.getName(), msg});
    }

    private String getScope(Thread t) {
        StackTraceElement[] stackTrace = t.getStackTrace();
        if (stackTrace == null || stackTrace.length <= 0) {
            return this.scope;
        }
        StackTraceElement trace = stackTrace[stackTrace.length - 1];
        String fileName = trace.getFileName();
        if (fileName == null || fileName.length() <= 0) {
            fileName = trace.getClassName();
        } else {
            fileName = this.scope + "/" + fileName;
        }
        int lineNum = trace.getLineNumber();
        String source = String.valueOf(lineNum);
        if (lineNum < 0) {
            source = trace.getMethodName();
            if (source == null || source.length() <= 0) {
                source = "Unknown Source";
            }
        }
        return fileName + "(" + source + ")";
    }
}
