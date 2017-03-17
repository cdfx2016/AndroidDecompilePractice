package com.mob.commons.logcollector;

import android.content.Context;
import android.content.Intent;
import com.mob.tools.MobLog;
import com.mob.tools.log.LogCollector;

public abstract class LogsCollector implements LogCollector {
    private c a;
    private boolean b;

    protected abstract String getAppkey();

    protected abstract String getSDKTag();

    protected abstract int getSDKVersion();

    public LogsCollector(Context context) {
        this.a = c.a(context);
        this.a.a(getSDKVersion(), getSDKTag(), getAppkey());
        try {
            if (context.getPackageManager().getPackageInfo("cn.sharesdk.log", 64) != null) {
                this.b = true;
            }
        } catch (Throwable th) {
        }
        this.b = false;
    }

    public final void log(String str, int i, int i2, String str2, String str3) {
        a(i, str3);
        if (str != null && str.equals(getSDKTag())) {
            if ("SHARESDK".equals(str) && (!str3.contains("com.mob.") || !str3.contains("cn.sharesdk."))) {
                return;
            }
            if (i2 == 1) {
                this.a.b(getSDKVersion(), i2, str, getAppkey(), str3);
            } else if (i2 == 2) {
                this.a.a(getSDKVersion(), i2, str, getAppkey(), str3);
            } else if (i == 5) {
                this.a.a(getSDKVersion(), i2, str, getAppkey(), str3);
            }
        }
    }

    final int a(int i, String str) {
        if (this.a.a() != null && this.b) {
            try {
                Intent intent = new Intent();
                intent.setAction("cn.sharesdk.log");
                intent.putExtra("package", this.a.a().getPackageName());
                intent.putExtra("priority", i);
                intent.putExtra("msg", str);
                this.a.a().sendBroadcast(intent);
            } catch (Throwable th) {
                MobLog.getInstance().w(th);
            }
        }
        return 0;
    }
}
