package com.mob.commons.deviceinfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Parcelable;
import com.fanyu.boundless.config.Preferences;
import com.mob.commons.a;
import com.mob.commons.c;
import com.mob.commons.e;
import com.mob.commons.f;
import com.mob.tools.MobHandlerThread;
import com.mob.tools.MobLog;
import com.mob.tools.utils.Data;
import com.mob.tools.utils.Hashon;
import com.mob.tools.utils.ReflectHelper;
import com.mob.tools.utils.ResHelper;
import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import org.jivesoftware.smackx.bytestreams.ibb.packet.DataPacketExtension;

public class DeviceInfoCollector implements Callback {
    private static DeviceInfoCollector a;
    private Context b;
    private Hashon c = new Hashon();
    private Handler d;
    private Random e = new Random();

    public static synchronized void startCollector(Context context) {
        synchronized (DeviceInfoCollector.class) {
            if (a == null) {
                a = new DeviceInfoCollector(context);
                a.a();
            }
        }
    }

    private DeviceInfoCollector(Context context) {
        this.b = context.getApplicationContext();
    }

    private void a() {
        MobHandlerThread anonymousClass1 = new MobHandlerThread(this) {
            final /* synthetic */ DeviceInfoCollector a;

            {
                this.a = r1;
            }

            public void run() {
                e.a(new File(ResHelper.getCacheRoot(this.a.b), "comm/locks/.dic_lock"), new Runnable(this) {
                    final /* synthetic */ AnonymousClass1 a;

                    {
                        this.a = r1;
                    }

                    public void run() {
                        this.a.a();
                    }
                });
            }

            private void a() {
                super.run();
            }
        };
        anonymousClass1.start();
        this.d = new Handler(anonymousClass1.getLooper(), this);
        this.d.sendEmptyMessage(1);
        this.d.sendEmptyMessage(2);
        this.d.sendEmptyMessage(3);
        this.d.sendEmptyMessage(5);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean handleMessage(android.os.Message r9) {
        /*
        r8 = this;
        r7 = 4;
        r6 = 0;
        r0 = r9.what;
        switch(r0) {
            case 1: goto L_0x0008;
            case 2: goto L_0x0014;
            case 3: goto L_0x0029;
            case 4: goto L_0x0050;
            case 5: goto L_0x0099;
            default: goto L_0x0007;
        };
    L_0x0007:
        return r6;
    L_0x0008:
        r0 = r8.b;
        r0 = com.mob.commons.a.g(r0);
        if (r0 == 0) goto L_0x0007;
    L_0x0010:
        r8.b();
        goto L_0x0007;
    L_0x0014:
        r0 = r8.b;
        r0 = com.mob.commons.a.l(r0);
        if (r0 == 0) goto L_0x0007;
    L_0x001c:
        r0 = r8.c();
        if (r0 == 0) goto L_0x0025;
    L_0x0022:
        r8.d();
    L_0x0025:
        r8.e();
        goto L_0x0007;
    L_0x0029:
        r0 = r8.b;
        r0 = com.mob.commons.a.h(r0);
        if (r0 == 0) goto L_0x0034;
    L_0x0031:
        r8.f();	 Catch:{ Throwable -> 0x0047 }
    L_0x0034:
        r0 = r8.e;
        r1 = 120; // 0x78 float:1.68E-43 double:5.93E-322;
        r0 = r0.nextInt(r1);
        r0 = r0 + 180;
        r1 = r8.d;
        r0 = r0 * 1000;
        r2 = (long) r0;
        r1.sendEmptyMessageDelayed(r7, r2);
        goto L_0x0007;
    L_0x0047:
        r0 = move-exception;
        r1 = com.mob.tools.MobLog.getInstance();
        r1.w(r0);
        goto L_0x0034;
    L_0x0050:
        r0 = r8.b;
        r0 = com.mob.commons.a.h(r0);
        if (r0 == 0) goto L_0x007c;
    L_0x0058:
        r0 = r8.b;
        r0 = com.mob.commons.a.a(r0);
        r2 = r8.b;
        r2 = com.mob.commons.a.i(r2);
        r2 = (long) r2;
        r4 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r2 = r2 * r4;
        r0 = r0 + r2;
        r2 = r8.b;
        r2 = com.mob.commons.a.a(r2);
        r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r0 < 0) goto L_0x0079;
    L_0x0073:
        r0 = r8.g();	 Catch:{ Throwable -> 0x0090 }
        if (r0 == 0) goto L_0x007c;
    L_0x0079:
        r8.f();	 Catch:{ Throwable -> 0x0090 }
    L_0x007c:
        r0 = r8.e;
        r1 = 120; // 0x78 float:1.68E-43 double:5.93E-322;
        r0 = r0.nextInt(r1);
        r0 = r0 + 180;
        r1 = r8.d;
        r0 = r0 * 1000;
        r2 = (long) r0;
        r1.sendEmptyMessageDelayed(r7, r2);
        goto L_0x0007;
    L_0x0090:
        r0 = move-exception;
        r1 = com.mob.tools.MobLog.getInstance();
        r1.w(r0);
        goto L_0x007c;
    L_0x0099:
        r0 = r8.b;
        r0 = com.mob.commons.a.j(r0);
        if (r0 == 0) goto L_0x0101;
    L_0x00a1:
        r0 = "DeviceHelper";
        r1 = "getInstance";
        r2 = 1;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x0112 }
        r3 = 0;
        r4 = r8.b;	 Catch:{ Throwable -> 0x0112 }
        r2[r3] = r4;	 Catch:{ Throwable -> 0x0112 }
        r1 = com.mob.tools.utils.ReflectHelper.invokeStaticMethod(r0, r1, r2);	 Catch:{ Throwable -> 0x0112 }
        r0 = "getLocation";
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x0112 }
        r3 = 0;
        r4 = 30;
        r4 = java.lang.Integer.valueOf(r4);	 Catch:{ Throwable -> 0x0112 }
        r2[r3] = r4;	 Catch:{ Throwable -> 0x0112 }
        r3 = 1;
        r4 = 0;
        r4 = java.lang.Integer.valueOf(r4);	 Catch:{ Throwable -> 0x0112 }
        r2[r3] = r4;	 Catch:{ Throwable -> 0x0112 }
        r3 = 2;
        r4 = 1;
        r4 = java.lang.Boolean.valueOf(r4);	 Catch:{ Throwable -> 0x0112 }
        r2[r3] = r4;	 Catch:{ Throwable -> 0x0112 }
        r0 = com.mob.tools.utils.ReflectHelper.invokeInstanceMethod(r1, r0, r2);	 Catch:{ Throwable -> 0x0112 }
        r0 = (android.location.Location) r0;	 Catch:{ Throwable -> 0x0112 }
        r2 = 1;
        r8.a(r0, r2);	 Catch:{ Throwable -> 0x0112 }
        r0 = "getLocation";
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x0112 }
        r3 = 0;
        r4 = 15;
        r4 = java.lang.Integer.valueOf(r4);	 Catch:{ Throwable -> 0x0112 }
        r2[r3] = r4;	 Catch:{ Throwable -> 0x0112 }
        r3 = 1;
        r4 = 0;
        r4 = java.lang.Integer.valueOf(r4);	 Catch:{ Throwable -> 0x0112 }
        r2[r3] = r4;	 Catch:{ Throwable -> 0x0112 }
        r3 = 2;
        r4 = 1;
        r4 = java.lang.Boolean.valueOf(r4);	 Catch:{ Throwable -> 0x0112 }
        r2[r3] = r4;	 Catch:{ Throwable -> 0x0112 }
        r0 = com.mob.tools.utils.ReflectHelper.invokeInstanceMethod(r1, r0, r2);	 Catch:{ Throwable -> 0x0112 }
        r0 = (android.location.Location) r0;	 Catch:{ Throwable -> 0x0112 }
        r1 = 2;
        r8.a(r0, r1);	 Catch:{ Throwable -> 0x0112 }
    L_0x0101:
        r0 = r8.d;
        r1 = 5;
        r2 = r8.b;
        r2 = com.mob.commons.a.k(r2);
        r2 = r2 * 1000;
        r2 = (long) r2;
        r0.sendEmptyMessageDelayed(r1, r2);
        goto L_0x0007;
    L_0x0112:
        r0 = move-exception;
        r1 = com.mob.tools.MobLog.getInstance();
        r1.w(r0);
        goto L_0x0101;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mob.commons.deviceinfo.DeviceInfoCollector.handleMessage(android.os.Message):boolean");
    }

    private void b() {
        try {
            HashMap hashMap = new HashMap();
            Object invokeStaticMethod = ReflectHelper.invokeStaticMethod("DeviceHelper", "getInstance", this.b);
            hashMap.put("phonename", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getBluetoothName", new Object[0]));
            hashMap.put("signmd5", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getSignMD5", new Object[0]));
            String MD5 = Data.MD5(this.c.fromHashMap(hashMap));
            String a = f.a(this.b);
            if (a == null || !a.equals(MD5)) {
                HashMap hashMap2 = new HashMap();
                hashMap2.put(MessageEncoder.ATTR_TYPE, "DEVEXT");
                hashMap2.put(DataPacketExtension.ELEMENT_NAME, hashMap);
                hashMap2.put("datetime", Long.valueOf(a.a(this.b)));
                c.a(this.b).a(a.a(this.b), hashMap2);
                f.a(this.b, MD5);
            }
        } catch (Throwable th) {
            MobLog.getInstance().w(th);
        }
    }

    private boolean c() {
        long b = f.b(this.b);
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(b);
        int i = instance.get(1);
        int i2 = instance.get(2);
        int i3 = instance.get(5);
        long a = a.a(this.b);
        Calendar instance2 = Calendar.getInstance();
        instance2.setTimeInMillis(a);
        int i4 = instance2.get(1);
        int i5 = instance2.get(2);
        int i6 = instance2.get(5);
        if (i == i4 && i2 == i5 && i3 == i6) {
            return false;
        }
        return true;
    }

    private void d() {
        synchronized (a) {
            try {
                HashMap hashMap = new HashMap();
                Object invokeStaticMethod = ReflectHelper.invokeStaticMethod("DeviceHelper", "getInstance", this.b);
                hashMap.put("ssid", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getSSID", new Object[0]));
                hashMap.put("bssid", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getBssid", new Object[0]));
                HashMap hashMap2 = new HashMap();
                hashMap2.put(MessageEncoder.ATTR_TYPE, "WIFI_INFO");
                hashMap2.put(DataPacketExtension.ELEMENT_NAME, hashMap);
                long a = a.a(this.b);
                hashMap2.put("datetime", Long.valueOf(a));
                c.a(this.b).a(a.a(this.b), hashMap2);
                f.a(this.b, a);
                f.b(this.b, Data.MD5(this.c.fromHashMap(hashMap)));
            } catch (Throwable th) {
                MobLog.getInstance().w(th);
            }
        }
    }

    private void e() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        this.b.registerReceiver(new BroadcastReceiver(this) {
            final /* synthetic */ DeviceInfoCollector a;

            {
                this.a = r1;
            }

            public void onReceive(Context context, Intent intent) {
                try {
                    if ("android.net.wifi.STATE_CHANGE".equals(intent.getAction())) {
                        Parcelable parcelableExtra = intent.getParcelableExtra("networkInfo");
                        if (parcelableExtra != null && ((NetworkInfo) parcelableExtra).isAvailable()) {
                            HashMap hashMap = new HashMap();
                            Object invokeStaticMethod = ReflectHelper.invokeStaticMethod("DeviceHelper", "getInstance", context);
                            hashMap.put("ssid", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getSSID", new Object[0]));
                            hashMap.put("bssid", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getBssid", new Object[0]));
                            String MD5 = Data.MD5(this.a.c.fromHashMap(hashMap));
                            String c = f.c(context);
                            if ((c == null || !c.equals(MD5)) && a.l(context)) {
                                this.a.d();
                            }
                        }
                    }
                } catch (Throwable th) {
                    MobLog.getInstance().w(th);
                }
            }
        }, intentFilter);
    }

    private void f() throws Throwable {
        int parseInt;
        HashMap hashMap = new HashMap();
        Object invokeStaticMethod = ReflectHelper.invokeStaticMethod("DeviceHelper", "getInstance", this.b);
        try {
            parseInt = Integer.parseInt((String) ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getCarrier", new Object[0]));
        } catch (Throwable th) {
            parseInt = -1;
        }
        hashMap.put("carrier", Integer.valueOf(parseInt));
        hashMap.put("simopname", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getCarrierName", new Object[0]));
        hashMap.put("lac", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getCellLac", new Object[0]));
        hashMap.put("cell", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getCellId", new Object[0]));
        HashMap hashMap2 = new HashMap();
        hashMap2.put(MessageEncoder.ATTR_TYPE, "BSINFO");
        hashMap2.put(DataPacketExtension.ELEMENT_NAME, hashMap);
        hashMap2.put("datetime", Long.valueOf(a.a(this.b)));
        c.a(this.b).a(a.a(this.b), hashMap2);
        f.c(this.b, Data.MD5(this.c.fromHashMap(hashMap)));
        f.b(this.b, a.a(this.b) + (((long) a.i(this.b)) * 1000));
    }

    private boolean g() throws Throwable {
        int parseInt;
        HashMap hashMap = new HashMap();
        Object invokeStaticMethod = ReflectHelper.invokeStaticMethod("DeviceHelper", "getInstance", this.b);
        try {
            parseInt = Integer.parseInt((String) ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getCarrier", new Object[0]));
        } catch (Throwable th) {
            parseInt = -1;
        }
        hashMap.put("carrier", Integer.valueOf(parseInt));
        hashMap.put("simopname", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getCarrierName", new Object[0]));
        hashMap.put("lac", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getCellLac", new Object[0]));
        hashMap.put("cell", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getCellId", new Object[0]));
        String MD5 = Data.MD5(this.c.fromHashMap(hashMap));
        String d = f.d(this.b);
        return d == null || !d.equals(MD5);
    }

    private void a(Location location, int i) {
        if (location != null) {
            HashMap hashMap = new HashMap();
            hashMap.put("accuracy", Float.valueOf(location.getAccuracy()));
            hashMap.put(Preferences.LATITUDE, Double.valueOf(location.getLatitude()));
            hashMap.put("longitude", Double.valueOf(location.getLongitude()));
            hashMap.put("location_type", Integer.valueOf(i));
            HashMap hashMap2 = new HashMap();
            hashMap2.put(MessageEncoder.ATTR_TYPE, "LOCATION");
            hashMap2.put(DataPacketExtension.ELEMENT_NAME, hashMap);
            hashMap2.put("datetime", Long.valueOf(a.a(this.b)));
            c.a(this.b).a(a.a(this.b), hashMap2);
        }
    }
}
