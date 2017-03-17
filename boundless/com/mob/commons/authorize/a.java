package com.mob.commons.authorize;

import android.content.Context;
import android.util.Base64;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.mob.commons.MobProduct;
import com.mob.commons.e;
import com.mob.tools.MobLog;
import com.mob.tools.network.KVPair;
import com.mob.tools.network.NetworkHelper;
import com.mob.tools.network.NetworkHelper.NetworkTimeOut;
import com.mob.tools.utils.Data;
import com.mob.tools.utils.Hashon;
import com.mob.tools.utils.ReflectHelper;
import com.mob.tools.utils.ResHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/* compiled from: Authorizer */
public final class a {
    public final String a(final Context context, final MobProduct mobProduct) {
        final String[] strArr = new String[1];
        e.a(new File(ResHelper.getCacheRoot(context), "comm/locks/.globalLock"), true, new Runnable(this) {
            final /* synthetic */ a d;

            public void run() {
                strArr[0] = this.d.b(context, mobProduct);
            }
        });
        return strArr[0];
    }

    private final File b(Context context) {
        File file = new File(ResHelper.getCacheRoot(context), "comm/dbs/.duid");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        return file;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final java.lang.String b(android.content.Context r9, com.mob.commons.MobProduct r10) {
        /*
        r8 = this;
        r2 = 0;
        r0 = r8.b(r9);	 Catch:{ Throwable -> 0x00c5 }
        r1 = r0.exists();	 Catch:{ Throwable -> 0x00c5 }
        if (r1 == 0) goto L_0x00cd;
    L_0x000b:
        r1 = r0.isFile();	 Catch:{ Throwable -> 0x00c5 }
        if (r1 == 0) goto L_0x00cd;
    L_0x0011:
        r3 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x00a8, all -> 0x00bd }
        r3.<init>(r0);	 Catch:{ Throwable -> 0x00a8, all -> 0x00bd }
        r1 = new java.io.ObjectInputStream;	 Catch:{ Throwable -> 0x00a8, all -> 0x00bd }
        r1.<init>(r3);	 Catch:{ Throwable -> 0x00a8, all -> 0x00bd }
        r0 = r1.readObject();	 Catch:{ Throwable -> 0x00ed }
        r0 = (java.util.HashMap) r0;	 Catch:{ Throwable -> 0x00ed }
        if (r1 == 0) goto L_0x0026;
    L_0x0023:
        r1.close();	 Catch:{ Throwable -> 0x00e4 }
    L_0x0026:
        if (r0 != 0) goto L_0x00f7;
    L_0x0028:
        r0 = r8.c(r9, r10);
        r4 = r0;
    L_0x002d:
        if (r4 == 0) goto L_0x00f5;
    L_0x002f:
        r0 = "duid";
        r0 = r4.get(r0);
        r0 = (java.lang.String) r0;
        if (r0 == 0) goto L_0x00a7;
    L_0x0039:
        r1 = "deviceInfo";
        r1 = r4.get(r1);	 Catch:{ Throwable -> 0x00e9 }
        r1 = (java.util.HashMap) r1;	 Catch:{ Throwable -> 0x00e9 }
        r1 = r8.a(r9, r1);	 Catch:{ Throwable -> 0x00e9 }
        if (r1 == 0) goto L_0x00f2;
    L_0x0047:
        r1 = 1;
        r3 = r8.a(r9, r10, r4, r1);	 Catch:{ Throwable -> 0x00e9 }
        if (r3 == 0) goto L_0x00f2;
    L_0x004e:
        r0 = "appInfo";
        r0 = r4.get(r0);	 Catch:{ Throwable -> 0x00d9 }
        r0 = (java.util.HashMap) r0;	 Catch:{ Throwable -> 0x00d9 }
        if (r0 != 0) goto L_0x00ef;
    L_0x0058:
        r0 = new java.util.HashMap;	 Catch:{ Throwable -> 0x00d9 }
        r0.<init>();	 Catch:{ Throwable -> 0x00d9 }
        r1 = "appInfo";
        r4.put(r1, r0);	 Catch:{ Throwable -> 0x00d9 }
        r1 = r0;
    L_0x0063:
        r0 = "DeviceHelper";
        r5 = "getInstance";
        r6 = 1;
        r6 = new java.lang.Object[r6];	 Catch:{ Throwable -> 0x00d0 }
        r7 = 0;
        r6[r7] = r9;	 Catch:{ Throwable -> 0x00d0 }
        r0 = com.mob.tools.utils.ReflectHelper.invokeStaticMethod(r0, r5, r6);	 Catch:{ Throwable -> 0x00d0 }
        r5 = "getPackageName";
        r6 = 0;
        r6 = new java.lang.Object[r6];	 Catch:{ Throwable -> 0x00d0 }
        r0 = com.mob.tools.utils.ReflectHelper.invokeInstanceMethod(r0, r5, r6);	 Catch:{ Throwable -> 0x00d0 }
        r0 = (java.lang.String) r0;	 Catch:{ Throwable -> 0x00d0 }
        r2 = r0;
    L_0x007d:
        r0 = r1.get(r2);	 Catch:{ Throwable -> 0x00d9 }
        r0 = (java.util.HashMap) r0;	 Catch:{ Throwable -> 0x00d9 }
        if (r0 != 0) goto L_0x008d;
    L_0x0085:
        r0 = new java.util.HashMap;	 Catch:{ Throwable -> 0x00d9 }
        r0.<init>();	 Catch:{ Throwable -> 0x00d9 }
        r1.put(r2, r0);	 Catch:{ Throwable -> 0x00d9 }
    L_0x008d:
        r1 = r10.getProductTag();	 Catch:{ Throwable -> 0x00d9 }
        r0 = r0.get(r1);	 Catch:{ Throwable -> 0x00d9 }
        r0 = (java.lang.String) r0;	 Catch:{ Throwable -> 0x00d9 }
        if (r0 == 0) goto L_0x00a3;
    L_0x0099:
        r1 = r10.getProductAppkey();	 Catch:{ Throwable -> 0x00d9 }
        r0 = r0.equals(r1);	 Catch:{ Throwable -> 0x00d9 }
        if (r0 != 0) goto L_0x00a6;
    L_0x00a3:
        r8.a(r9, r10, r4);	 Catch:{ Throwable -> 0x00d9 }
    L_0x00a6:
        r0 = r3;
    L_0x00a7:
        return r0;
    L_0x00a8:
        r0 = move-exception;
        r1 = r2;
    L_0x00aa:
        r3 = com.mob.tools.MobLog.getInstance();	 Catch:{ all -> 0x00eb }
        r3.w(r0);	 Catch:{ all -> 0x00eb }
        if (r1 == 0) goto L_0x00cd;
    L_0x00b3:
        r1.close();	 Catch:{ Throwable -> 0x00b9 }
        r0 = r2;
        goto L_0x0026;
    L_0x00b9:
        r0 = move-exception;
        r0 = r2;
        goto L_0x0026;
    L_0x00bd:
        r0 = move-exception;
        r1 = r2;
    L_0x00bf:
        if (r1 == 0) goto L_0x00c4;
    L_0x00c1:
        r1.close();	 Catch:{ Throwable -> 0x00e7 }
    L_0x00c4:
        throw r0;	 Catch:{ Throwable -> 0x00c5 }
    L_0x00c5:
        r0 = move-exception;
        r1 = com.mob.tools.MobLog.getInstance();
        r1.w(r0);
    L_0x00cd:
        r0 = r2;
        goto L_0x0026;
    L_0x00d0:
        r0 = move-exception;
        r5 = com.mob.tools.MobLog.getInstance();	 Catch:{ Throwable -> 0x00d9 }
        r5.w(r0);	 Catch:{ Throwable -> 0x00d9 }
        goto L_0x007d;
    L_0x00d9:
        r0 = move-exception;
        r1 = r0;
        r0 = r3;
    L_0x00dc:
        r2 = com.mob.tools.MobLog.getInstance();
        r2.w(r1);
        goto L_0x00a7;
    L_0x00e4:
        r1 = move-exception;
        goto L_0x0026;
    L_0x00e7:
        r1 = move-exception;
        goto L_0x00c4;
    L_0x00e9:
        r1 = move-exception;
        goto L_0x00dc;
    L_0x00eb:
        r0 = move-exception;
        goto L_0x00bf;
    L_0x00ed:
        r0 = move-exception;
        goto L_0x00aa;
    L_0x00ef:
        r1 = r0;
        goto L_0x0063;
    L_0x00f2:
        r3 = r0;
        goto L_0x004e;
    L_0x00f5:
        r0 = r2;
        goto L_0x00a7;
    L_0x00f7:
        r4 = r0;
        goto L_0x002d;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mob.commons.authorize.a.b(android.content.Context, com.mob.commons.MobProduct):java.lang.String");
    }

    private final boolean a(Context context, HashMap<String, String> hashMap) {
        Object obj = null;
        if (hashMap == null) {
            return true;
        }
        Object invokeStaticMethod;
        Object obj2;
        try {
            invokeStaticMethod = ReflectHelper.invokeStaticMethod("DeviceHelper", "getInstance", context);
        } catch (Throwable th) {
            MobLog.getInstance().w(th);
            invokeStaticMethod = null;
        }
        Object obj3 = hashMap.get("adsid");
        if (invokeStaticMethod != null) {
            try {
                obj2 = (String) ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getAdvertisingID", new Object[0]);
            } catch (Throwable th2) {
                MobLog.getInstance().w(th2);
            }
            if (obj2 == null && ((obj3 == null && obj2 != null) || (obj3 != null && !obj3.equals(obj2)))) {
                return true;
            }
            obj3 = hashMap.get("imei");
            if (invokeStaticMethod != null) {
                try {
                    obj2 = (String) ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getIMEI", new Object[0]);
                } catch (Throwable th22) {
                    MobLog.getInstance().w(th22);
                }
                if (obj3 != null || !obj3.equals(r0)) {
                    return true;
                }
                obj3 = hashMap.get("serialno");
                if (invokeStaticMethod != null) {
                    try {
                        obj2 = (String) ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getSerialno", new Object[0]);
                    } catch (Throwable th222) {
                        MobLog.getInstance().w(th222);
                    }
                    if (obj3 != null || !obj3.equals(r0)) {
                        return true;
                    }
                    obj3 = hashMap.get("mac");
                    if (invokeStaticMethod != null) {
                        try {
                            obj2 = (String) ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getMacAddress", new Object[0]);
                        } catch (Throwable th2222) {
                            MobLog.getInstance().w(th2222);
                        }
                        if (obj3 != null || !obj3.equals(r0)) {
                            return true;
                        }
                        obj3 = hashMap.get("model");
                        if (invokeStaticMethod != null) {
                            try {
                                obj2 = (String) ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getModel", new Object[0]);
                            } catch (Throwable th22222) {
                                MobLog.getInstance().w(th22222);
                            }
                            if (obj3 != null || !obj3.equals(r0)) {
                                return true;
                            }
                            obj3 = hashMap.get("factory");
                            if (invokeStaticMethod != null) {
                                try {
                                    obj2 = (String) ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getManufacturer", new Object[0]);
                                } catch (Throwable th222222) {
                                    MobLog.getInstance().w(th222222);
                                }
                                if (obj3 != null || !obj3.equals(r0)) {
                                    return true;
                                }
                                obj3 = hashMap.get("androidid");
                                if (invokeStaticMethod != null) {
                                    try {
                                        obj2 = (String) ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getAndroidID", new Object[0]);
                                    } catch (Throwable th2222222) {
                                        MobLog.getInstance().w(th2222222);
                                    }
                                    if (obj3 != null || !obj3.equals(r0)) {
                                        return true;
                                    }
                                    obj3 = hashMap.get("sysver");
                                    if (invokeStaticMethod != null) {
                                        try {
                                            obj = (String) ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getOSVersionName", new Object[0]);
                                        } catch (Throwable th22222222) {
                                            MobLog.getInstance().w(th22222222);
                                        }
                                    }
                                    if (obj3 == null || !obj3.equals(r2)) {
                                        return true;
                                    }
                                    return false;
                                }
                                obj2 = null;
                                if (obj3 != null) {
                                }
                                return true;
                            }
                            obj2 = null;
                            if (obj3 != null) {
                            }
                            return true;
                        }
                        obj2 = null;
                        if (obj3 != null) {
                        }
                        return true;
                    }
                    obj2 = null;
                    if (obj3 != null) {
                    }
                    return true;
                }
                obj2 = null;
                if (obj3 != null) {
                }
                return true;
            }
            obj2 = null;
            if (obj3 != null) {
            }
            return true;
        }
        obj2 = null;
        if (obj2 == null) {
        }
        obj3 = hashMap.get("imei");
        if (invokeStaticMethod != null) {
            obj2 = (String) ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getIMEI", new Object[0]);
            if (obj3 != null) {
            }
            return true;
        }
        obj2 = null;
        if (obj3 != null) {
        }
        return true;
    }

    private final HashMap<String, Object> c(Context context, MobProduct mobProduct) {
        int parseInt;
        Throwable th;
        Object invokeStaticMethod = ReflectHelper.invokeStaticMethod("DeviceHelper", "getInstance", context);
        try {
            parseInt = ResHelper.parseInt((String) ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getCarrier", new Object[0]));
        } catch (Throwable th2) {
            MobLog.getInstance().w(th2);
            return null;
        }
        HashMap hashMap = new HashMap();
        hashMap.put("adsid", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getAdvertisingID", new Object[0]));
        hashMap.put("imei", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getIMEI", new Object[0]));
        hashMap.put("serialno", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getSerialno", new Object[0]));
        hashMap.put("androidid", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getAndroidID", new Object[0]));
        hashMap.put("mac", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getMacAddress", new Object[0]));
        hashMap.put("model", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getModel", new Object[0]));
        hashMap.put("factory", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getManufacturer", new Object[0]));
        hashMap.put("carrier", Integer.valueOf(parseInt));
        hashMap.put("screensize", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getScreenSize", new Object[0]));
        hashMap.put("sysver", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getOSVersionName", new Object[0]));
        hashMap.put("plat", Integer.valueOf(1));
        Hashon hashon = new Hashon();
        String encodeToString = Base64.encodeToString(Data.AES128Encode("sdk.commonap.sdk", hashon.fromHashMap(hashMap)), 2);
        ArrayList arrayList = new ArrayList();
        arrayList.add(new KVPair("m", encodeToString));
        NetworkTimeOut networkTimeOut = new NetworkTimeOut();
        networkTimeOut.readTimout = DefaultLoadControl.DEFAULT_MAX_BUFFER_MS;
        networkTimeOut.connectionTimeout = DefaultLoadControl.DEFAULT_MAX_BUFFER_MS;
        HashMap fromJson = hashon.fromJson(new NetworkHelper().httpPost("http://devs.data.mob.com:80/dinfo", arrayList, null, null, networkTimeOut));
        if (fromJson == null) {
            return null;
        }
        encodeToString = (String) fromJson.get("duid");
        if (encodeToString == null || encodeToString.length() <= 0) {
            return null;
        }
        HashMap<String, Object> hashMap2 = new HashMap();
        ObjectOutputStream objectOutputStream;
        try {
            hashMap2.put("duid", encodeToString);
            hashMap.put("carrier", String.valueOf(parseInt));
            hashMap2.put("deviceInfo", hashMap);
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(b(context)));
            try {
                objectOutputStream.writeObject(hashMap2);
                if (objectOutputStream != null) {
                    try {
                        objectOutputStream.flush();
                        objectOutputStream.close();
                    } catch (Throwable th3) {
                    }
                }
            } catch (Throwable th4) {
                th2 = th4;
                try {
                    MobLog.getInstance().w(th2);
                    if (objectOutputStream != null) {
                        try {
                            objectOutputStream.flush();
                            objectOutputStream.close();
                        } catch (Throwable th5) {
                        }
                    }
                    return hashMap2;
                } catch (Throwable th6) {
                    th2 = th6;
                    if (objectOutputStream != null) {
                        try {
                            objectOutputStream.flush();
                            objectOutputStream.close();
                        } catch (Throwable th7) {
                        }
                    }
                    throw th2;
                }
            }
        } catch (Throwable th8) {
            th2 = th8;
            objectOutputStream = null;
            if (objectOutputStream != null) {
                objectOutputStream.flush();
                objectOutputStream.close();
            }
            throw th2;
        }
        return hashMap2;
    }

    private final String a(Context context, MobProduct mobProduct, HashMap<String, Object> hashMap, boolean z) {
        int parseInt;
        Throwable th;
        Throwable th2;
        Object invokeStaticMethod = ReflectHelper.invokeStaticMethod("DeviceHelper", "getInstance", context);
        try {
            parseInt = ResHelper.parseInt((String) ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getCarrier", new Object[0]));
        } catch (Throwable th3) {
            MobLog.getInstance().w(th3);
            return null;
        }
        HashMap hashMap2 = new HashMap();
        hashMap2.put("adsid", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getAdvertisingID", new Object[0]));
        hashMap2.put("imei", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getIMEI", new Object[0]));
        hashMap2.put("serialno", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getSerialno", new Object[0]));
        hashMap2.put("androidid", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getAndroidID", new Object[0]));
        hashMap2.put("mac", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getMacAddress", new Object[0]));
        hashMap2.put("model", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getModel", new Object[0]));
        hashMap2.put("factory", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getManufacturer", new Object[0]));
        hashMap2.put("carrier", Integer.valueOf(parseInt));
        hashMap2.put("screensize", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getScreenSize", new Object[0]));
        hashMap2.put("sysver", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getOSVersionName", new Object[0]));
        hashMap2.put("plat", Integer.valueOf(1));
        Hashon hashon = new Hashon();
        String encodeToString = Base64.encodeToString(Data.AES128Encode("sdk.commonap.sdk", hashon.fromHashMap(hashMap2)), 2);
        ArrayList arrayList = new ArrayList();
        arrayList.add(new KVPair("m", encodeToString));
        NetworkTimeOut networkTimeOut = new NetworkTimeOut();
        networkTimeOut.readTimout = DefaultLoadControl.DEFAULT_MAX_BUFFER_MS;
        networkTimeOut.connectionTimeout = DefaultLoadControl.DEFAULT_MAX_BUFFER_MS;
        HashMap fromJson = hashon.fromJson(new NetworkHelper().httpPost("http://devs.data.mob.com:80/dinfo", arrayList, null, null, networkTimeOut));
        if (fromJson == null) {
            return null;
        }
        encodeToString = (String) fromJson.get("duid");
        if (encodeToString == null || encodeToString.length() <= 0) {
            return null;
        }
        hashMap.put("duid", encodeToString);
        hashMap2.put("carrier", String.valueOf(parseInt));
        hashMap.put("deviceInfo", hashMap2);
        if (!z) {
            return encodeToString;
        }
        ObjectOutputStream objectOutputStream;
        try {
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(b(context)));
            try {
                objectOutputStream.writeObject(hashMap);
                if (objectOutputStream == null) {
                    return encodeToString;
                }
                try {
                    objectOutputStream.flush();
                    objectOutputStream.close();
                    return encodeToString;
                } catch (Throwable th4) {
                    return encodeToString;
                }
            } catch (Throwable th5) {
                th2 = th5;
                try {
                    MobLog.getInstance().w(th2);
                    if (objectOutputStream != null) {
                        return encodeToString;
                    }
                    try {
                        objectOutputStream.flush();
                        objectOutputStream.close();
                        return encodeToString;
                    } catch (Throwable th6) {
                        return encodeToString;
                    }
                } catch (Throwable th7) {
                    th3 = th7;
                    if (objectOutputStream != null) {
                        try {
                            objectOutputStream.flush();
                            objectOutputStream.close();
                        } catch (Throwable th8) {
                        }
                    }
                    throw th3;
                }
            }
        } catch (Throwable th9) {
            th3 = th9;
            objectOutputStream = null;
            if (objectOutputStream != null) {
                objectOutputStream.flush();
                objectOutputStream.close();
            }
            throw th3;
        }
    }

    private final void a(Context context, MobProduct mobProduct, HashMap<String, Object> hashMap) {
        Throwable th;
        ObjectOutputStream objectOutputStream = null;
        try {
            String str = (String) hashMap.get("duid");
            Object invokeStaticMethod = ReflectHelper.invokeStaticMethod("DeviceHelper", "getInstance", context);
            ArrayList arrayList = new ArrayList();
            arrayList.add(new KVPair("product", mobProduct.getProductTag()));
            arrayList.add(new KVPair("appkey", mobProduct.getProductAppkey()));
            arrayList.add(new KVPair("duid", str));
            arrayList.add(new KVPair("apppkg", String.valueOf(ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getPackageName", new Object[0]))));
            arrayList.add(new KVPair("appver", String.valueOf(ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getAppVersion", new Object[0]))));
            arrayList.add(new KVPair("sdkver", String.valueOf(mobProduct.getSdkver())));
            arrayList.add(new KVPair("network", String.valueOf(ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getDetailNetworkTypeForStatic", new Object[0]))));
            NetworkTimeOut networkTimeOut = new NetworkTimeOut();
            networkTimeOut.readTimout = DefaultLoadControl.DEFAULT_MAX_BUFFER_MS;
            networkTimeOut.connectionTimeout = DefaultLoadControl.DEFAULT_MAX_BUFFER_MS;
            HashMap fromJson = new Hashon().fromJson(new NetworkHelper().httpPost("http://devs.data.mob.com:80/dsign", arrayList, null, null, networkTimeOut));
            if ("true".equals(String.valueOf(fromJson.get("reup"))) && a(context, mobProduct, hashMap, false) == null) {
            }
            if ("200".equals(String.valueOf(fromJson.get("status")))) {
                ((HashMap) ((HashMap) hashMap.get("appInfo")).get((String) ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getPackageName", new Object[0]))).put(mobProduct.getProductTag(), mobProduct.getProductAppkey());
                ObjectOutputStream objectOutputStream2;
                try {
                    objectOutputStream2 = new ObjectOutputStream(new FileOutputStream(b(context)));
                    try {
                        objectOutputStream2.writeObject(hashMap);
                        if (objectOutputStream2 != null) {
                            try {
                                objectOutputStream2.flush();
                                objectOutputStream2.close();
                            } catch (Throwable th2) {
                            }
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        try {
                            MobLog.getInstance().w(th);
                            if (objectOutputStream2 != null) {
                                try {
                                    objectOutputStream2.flush();
                                    objectOutputStream2.close();
                                } catch (Throwable th4) {
                                }
                            }
                        } catch (Throwable th5) {
                            th = th5;
                            objectOutputStream = objectOutputStream2;
                            if (objectOutputStream != null) {
                                try {
                                    objectOutputStream.flush();
                                    objectOutputStream.close();
                                } catch (Throwable th6) {
                                }
                            }
                            throw th;
                        }
                    }
                } catch (Throwable th7) {
                    th = th7;
                    if (objectOutputStream != null) {
                        objectOutputStream.flush();
                        objectOutputStream.close();
                    }
                    throw th;
                }
            }
        } catch (Throwable th8) {
            MobLog.getInstance().w(th8);
        }
    }

    public final String a(final Context context) {
        final String[] strArr = new String[1];
        e.a(new File(ResHelper.getCacheRoot(context), "comm/locks/.globalLock"), true, new Runnable(this) {
            final /* synthetic */ a c;

            public void run() {
                ObjectInputStream objectInputStream;
                HashMap hashMap;
                Throwable th;
                try {
                    File a = this.c.b(context);
                    if (a.exists() && a.isFile()) {
                        try {
                            objectInputStream = new ObjectInputStream(new FileInputStream(a));
                            try {
                                hashMap = (HashMap) objectInputStream.readObject();
                                if (objectInputStream != null) {
                                    try {
                                        objectInputStream.close();
                                    } catch (Throwable th2) {
                                    }
                                }
                            } catch (Throwable th3) {
                                th = th3;
                                try {
                                    MobLog.getInstance().w(th);
                                    if (objectInputStream != null) {
                                        try {
                                            objectInputStream.close();
                                            hashMap = null;
                                        } catch (Throwable th4) {
                                            hashMap = null;
                                        }
                                        if (hashMap == null) {
                                            hashMap = this.c.c(context);
                                        }
                                        strArr[0] = (String) hashMap.get("duid");
                                    }
                                    hashMap = null;
                                    if (hashMap == null) {
                                        hashMap = this.c.c(context);
                                    }
                                    strArr[0] = (String) hashMap.get("duid");
                                } catch (Throwable th5) {
                                    th = th5;
                                    if (objectInputStream != null) {
                                        try {
                                            objectInputStream.close();
                                        } catch (Throwable th6) {
                                        }
                                    }
                                    throw th;
                                }
                            }
                        } catch (Throwable th7) {
                            th = th7;
                            objectInputStream = null;
                            if (objectInputStream != null) {
                                objectInputStream.close();
                            }
                            throw th;
                        }
                        if (hashMap == null) {
                            hashMap = this.c.c(context);
                        }
                        strArr[0] = (String) hashMap.get("duid");
                    }
                } catch (Throwable th8) {
                    MobLog.getInstance().w(th8);
                }
                hashMap = null;
                if (hashMap == null) {
                    hashMap = this.c.c(context);
                }
                strArr[0] = (String) hashMap.get("duid");
            }
        });
        return strArr[0];
    }

    private HashMap<String, Object> c(Context context) {
        Throwable th;
        HashMap<String, Object> hashMap = new HashMap();
        ObjectOutputStream objectOutputStream = null;
        try {
            String str;
            String str2;
            Object invokeStaticMethod = ReflectHelper.invokeStaticMethod("DeviceHelper", "getInstance", context);
            String str3 = (String) ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getModel", new Object[0]);
            String str4 = str3 == null ? "" : str3;
            str3 = (String) ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getIMEI", new Object[0]);
            if (str3 == null) {
                str = "";
            } else {
                str = str3;
            }
            str3 = (String) ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getMacAddress", new Object[0]);
            if (str3 == null) {
                str2 = "";
            } else {
                str2 = str3;
            }
            str3 = (String) ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getSerialno", new Object[0]);
            if (str3 == null) {
                str3 = "";
            }
            hashMap.put("duid", Data.byteToHex(Data.SHA1(str4 + ":" + str + ":" + str2 + ":" + str3)));
            HashMap hashMap2 = new HashMap();
            hashMap2.put("adsid", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getAdvertisingID", new Object[0]));
            hashMap2.put("imei", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getIMEI", new Object[0]));
            hashMap2.put("serialno", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getSerialno", new Object[0]));
            hashMap2.put("androidid", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getAndroidID", new Object[0]));
            hashMap2.put("mac", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getMacAddress", new Object[0]));
            hashMap2.put("model", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getModel", new Object[0]));
            hashMap2.put("factory", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getManufacturer", new Object[0]));
            hashMap2.put("carrier", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getCarrier", new Object[0]));
            hashMap2.put("screensize", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getScreenSize", new Object[0]));
            hashMap2.put("sysver", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getOSVersionName", new Object[0]));
            hashMap2.put("plat", Integer.valueOf(1));
            hashMap.put("deviceInfo", hashMap2);
            ObjectOutputStream objectOutputStream2 = new ObjectOutputStream(new FileOutputStream(b(context)));
            try {
                objectOutputStream2.writeObject(hashMap);
                if (objectOutputStream2 != null) {
                    try {
                        objectOutputStream2.flush();
                        objectOutputStream2.close();
                    } catch (Throwable th2) {
                    }
                }
            } catch (Throwable th3) {
                th = th3;
                objectOutputStream = objectOutputStream2;
                if (objectOutputStream != null) {
                    objectOutputStream.flush();
                    objectOutputStream.close();
                }
                throw th;
            }
        } catch (Throwable th4) {
            th = th4;
            MobLog.getInstance().w(th);
            if (objectOutputStream != null) {
                objectOutputStream.flush();
                objectOutputStream.close();
            }
            return hashMap;
        }
        return hashMap;
    }
}
