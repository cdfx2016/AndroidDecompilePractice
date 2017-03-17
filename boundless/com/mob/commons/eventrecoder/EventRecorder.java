package com.mob.commons.eventrecoder;

import android.content.Context;
import android.text.TextUtils;
import com.easemob.util.HanziToPinyin.Token;
import com.mob.commons.e;
import com.mob.tools.MobLog;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;

public final class EventRecorder {
    private static Context a;
    private static File b;
    private static FileOutputStream c;

    public static final synchronized void prepare(Context context) {
        synchronized (EventRecorder.class) {
            a = context.getApplicationContext();
            a(new Runnable() {
                public void run() {
                    try {
                        EventRecorder.b = new File(EventRecorder.a.getFilesDir(), ".mrecord");
                        if (!EventRecorder.b.exists()) {
                            EventRecorder.b.createNewFile();
                        }
                        EventRecorder.c = new FileOutputStream(EventRecorder.b, true);
                    } catch (Throwable th) {
                        MobLog.getInstance().w(th);
                    }
                }
            });
        }
    }

    public static final synchronized void addBegin(String str, String str2) {
        synchronized (EventRecorder.class) {
            a(str + Token.SEPARATOR + str2 + " 0\n");
        }
    }

    private static final void a(Runnable runnable) {
        e.a(new File(a.getFilesDir(), "comm/locks/.mrlock"), runnable);
    }

    public static final synchronized void addEnd(String str, String str2) {
        synchronized (EventRecorder.class) {
            a(str + Token.SEPARATOR + str2 + " 1\n");
        }
    }

    private static final void a(final String str) {
        a(new Runnable() {
            public void run() {
                try {
                    EventRecorder.c.write(str.getBytes("utf-8"));
                    EventRecorder.c.flush();
                } catch (Throwable th) {
                    MobLog.getInstance().w(th);
                }
            }
        });
    }

    public static final synchronized String checkRecord(final String str) {
        String str2;
        synchronized (EventRecorder.class) {
            final LinkedList linkedList = new LinkedList();
            a(new Runnable() {
                public void run() {
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(EventRecorder.b), "utf-8"));
                        for (Object readLine = bufferedReader.readLine(); !TextUtils.isEmpty(readLine); readLine = bufferedReader.readLine()) {
                            String[] split = readLine.split(Token.SEPARATOR);
                            if (str.equals(split[0])) {
                                if ("0".equals(split[2])) {
                                    linkedList.add(split[1]);
                                } else if ("1".equals(split[2])) {
                                    int indexOf = linkedList.indexOf(split[1]);
                                    if (indexOf != -1) {
                                        linkedList.remove(indexOf);
                                    }
                                }
                            }
                        }
                        bufferedReader.close();
                    } catch (Throwable th) {
                        MobLog.getInstance().d(th);
                    }
                }
            });
            if (linkedList.size() > 0) {
                str2 = (String) linkedList.get(0);
            } else {
                str2 = null;
            }
        }
        return str2;
    }

    public static final synchronized void clear() {
        synchronized (EventRecorder.class) {
            a(new Runnable() {
                public void run() {
                    try {
                        EventRecorder.c.close();
                        EventRecorder.b.delete();
                        EventRecorder.b = new File(EventRecorder.a.getFilesDir(), ".mrecord");
                        EventRecorder.b.createNewFile();
                        EventRecorder.c = new FileOutputStream(EventRecorder.b, true);
                    } catch (Throwable th) {
                        MobLog.getInstance().w(th);
                    }
                }
            });
        }
    }
}
