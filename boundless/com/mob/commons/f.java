package com.mob.commons;

import android.content.Context;
import com.mob.tools.utils.SharePrefrenceHelper;

/* compiled from: ProcessLevelSPDB */
public class f {
    private static SharePrefrenceHelper a;

    private static synchronized void h(Context context) {
        synchronized (f.class) {
            if (a == null) {
                a = new SharePrefrenceHelper(context.getApplicationContext());
                a.open("mob_commons", 1);
            }
        }
    }

    public static synchronized String a(Context context) {
        String string;
        synchronized (f.class) {
            h(context);
            string = a.getString("key_ext_info");
        }
        return string;
    }

    public static synchronized void a(Context context, String str) {
        synchronized (f.class) {
            h(context);
            a.putString("key_ext_info", str);
        }
    }

    public static synchronized long b(Context context) {
        long j;
        synchronized (f.class) {
            h(context);
            j = a.getLong("wifi_last_time");
        }
        return j;
    }

    public static synchronized void a(Context context, long j) {
        synchronized (f.class) {
            h(context);
            a.putLong("wifi_last_time", Long.valueOf(j));
        }
    }

    public static synchronized String c(Context context) {
        String string;
        synchronized (f.class) {
            h(context);
            string = a.getString("wifi_last_info");
        }
        return string;
    }

    public static synchronized void b(Context context, String str) {
        synchronized (f.class) {
            h(context);
            a.putString("wifi_last_info", str);
        }
    }

    public static synchronized void c(Context context, String str) {
        synchronized (f.class) {
            h(context);
            a.putString("key_cellinfo", str);
        }
    }

    public static synchronized String d(Context context) {
        String string;
        synchronized (f.class) {
            h(context);
            string = a.getString("key_cellinfo");
        }
        return string;
    }

    public static synchronized void b(Context context, long j) {
        synchronized (f.class) {
            h(context);
            a.putLong("key_cellinfo_next_total", Long.valueOf(j));
        }
    }

    public static synchronized void d(Context context, String str) {
        synchronized (f.class) {
            h(context);
            a.putString("key_switches", str);
        }
    }

    public static synchronized String e(Context context) {
        String string;
        synchronized (f.class) {
            h(context);
            string = a.getString("key_switches");
        }
        return string;
    }

    public static synchronized void e(Context context, String str) {
        synchronized (f.class) {
            h(context);
            if (str == null) {
                a.remove("key_data_url");
            } else {
                a.putString("key_data_url", str);
            }
        }
    }

    public static synchronized String f(Context context) {
        String string;
        synchronized (f.class) {
            h(context);
            string = a.getString("key_data_url");
        }
        return string;
    }

    public static synchronized void f(Context context, String str) {
        synchronized (f.class) {
            h(context);
            if (str == null) {
                a.remove("key_conf_url");
            } else {
                a.putString("key_conf_url", str);
            }
        }
    }

    public static synchronized String g(Context context) {
        String string;
        synchronized (f.class) {
            h(context);
            string = a.getString("key_conf_url");
        }
        return string;
    }
}
