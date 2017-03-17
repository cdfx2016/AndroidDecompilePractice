package com.mob.commons.logcollector;

import android.content.Context;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import com.mob.tools.MobLog;
import com.mob.tools.SSDKHandlerThread;
import com.mob.tools.log.NLog;
import com.mob.tools.network.KVPair;
import com.mob.tools.network.NetworkHelper;
import com.mob.tools.network.NetworkHelper.NetworkTimeOut;
import com.mob.tools.utils.Data;
import com.mob.tools.utils.FileLocker;
import com.mob.tools.utils.Hashon;
import com.mob.tools.utils.ReflectHelper;
import com.mob.tools.utils.ResHelper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.GZIPOutputStream;
import org.jivesoftware.smack.packet.PrivacyItem.PrivacyRule;
import org.jivesoftware.smackx.Form;

/* compiled from: LogsManager */
public class c extends SSDKHandlerThread {
    private static c a;
    private static String b = "http://api.exc.mob.com:80";
    private HashMap<String, Integer> c;
    private Context d;
    private NetworkHelper e = new NetworkHelper();
    private d f;
    private File g;
    private FileLocker h;

    private c(Context context) {
        this.d = context.getApplicationContext();
        this.f = d.a(context);
        this.c = new HashMap();
        this.h = new FileLocker();
        this.g = new File(context.getFilesDir(), ".lock");
        if (!this.g.exists()) {
            try {
                this.g.createNewFile();
            } catch (Throwable e) {
                MobLog.getInstance().w(e);
            }
        }
        NLog.setContext(context);
        startThread();
    }

    public Context a() {
        return this.d;
    }

    public static synchronized c a(Context context) {
        c cVar;
        synchronized (c.class) {
            if (a == null) {
                a = new c(context);
            }
            cVar = a;
        }
        return cVar;
    }

    public void a(int i, String str, String str2) {
        Message message = new Message();
        message.what = 100;
        message.arg1 = i;
        message.obj = new Object[]{str, str2};
        this.handler.sendMessage(message);
    }

    public void a(int i, int i2, String str, String str2, String str3) {
        Message message = new Message();
        message.what = 101;
        message.arg1 = i;
        message.arg2 = i2;
        message.obj = new Object[]{str, str2, str3};
        this.handler.sendMessage(message);
    }

    private void a(Message message) {
        this.handler.sendMessageDelayed(message, 1000);
    }

    public void b(int i, int i2, String str, String str2, String str3) {
        a(i, i2, str, str2, str3);
        try {
            this.handler.wait();
        } catch (Throwable th) {
        }
    }

    protected void onMessage(Message message) {
        switch (message.what) {
            case 100:
                b(message);
                return;
            case 101:
                c(message);
                return;
            default:
                return;
        }
    }

    private void b(Message message) {
        try {
            int i = message.arg1;
            Object[] objArr = (Object[]) message.obj;
            String str = (String) objArr[0];
            String str2 = (String) objArr[1];
            b(i, str, str2);
            a(i, str, str2, null);
        } catch (Throwable th) {
            MobLog.getInstance().w(th);
        }
    }

    private void c(Message message) {
        int c;
        String MD5;
        try {
            int i = message.arg1;
            Object[] objArr = (Object[]) message.obj;
            String str = (String) objArr[0];
            String str2 = (String) objArr[1];
            String str3 = (String) objArr[2];
            int i2 = 1;
            if (message.arg2 == 0) {
                i2 = 2;
            } else if (message.arg2 == 2) {
                i2 = 1;
            }
            String f = this.f.f();
            if (!TextUtils.isEmpty(f)) {
                ArrayList arrayList = (ArrayList) new Hashon().fromJson(f).get("fakelist");
                if (arrayList != null && arrayList.size() > 0) {
                    Iterator it = arrayList.iterator();
                    while (it.hasNext()) {
                        f = (String) it.next();
                        if (!TextUtils.isEmpty(f) && str3.contains(f)) {
                            return;
                        }
                    }
                }
            }
            c = this.f.c();
            int d = this.f.d();
            int e = this.f.e();
            if (3 != i2 || -1 != e) {
                if (1 != i2 || -1 != c) {
                    if (2 != i2 || -1 != d) {
                        MD5 = Data.MD5(str3);
                        this.h.setLockFile(this.g.getAbsolutePath());
                        if (this.h.lock(false)) {
                            f.a(this.d, System.currentTimeMillis() - this.f.a(), str3, i2, MD5);
                        }
                        this.h.release();
                        this.c.remove(MD5);
                        if (3 == i2 && 1 == e) {
                            a(i, str, str2, new String[]{String.valueOf(3)});
                        } else if (1 == i2 && 1 == c) {
                            a(i, str, str2, new String[]{String.valueOf(1)});
                        } else if (2 == i2 && 1 == d) {
                            a(i, str, str2, new String[]{String.valueOf(2)});
                        }
                    }
                }
            }
        } catch (Throwable th) {
            MobLog.getInstance().w(th);
        }
    }

    private String b() {
        return b + "/errconf";
    }

    private void b(int i, String str, String str2) throws Throwable {
        ArrayList arrayList = new ArrayList();
        Object invokeStaticMethod = ReflectHelper.invokeStaticMethod("DeviceHelper", "getInstance", this.d);
        arrayList.add(new KVPair("key", str2));
        arrayList.add(new KVPair("sdk", str));
        arrayList.add(new KVPair("apppkg", String.valueOf(ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getPackageName", new Object[0]))));
        arrayList.add(new KVPair("appver", String.valueOf(ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getAppVersion", new Object[0]))));
        arrayList.add(new KVPair("sdkver", String.valueOf(i)));
        arrayList.add(new KVPair("plat", String.valueOf(ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getPlatformCode", new Object[0]))));
        try {
            NetworkTimeOut networkTimeOut = new NetworkTimeOut();
            networkTimeOut.readTimout = 10000;
            networkTimeOut.connectionTimeout = 10000;
            MobLog.getInstance().i("get server config == %s", this.e.httpPost(b(), arrayList, null, null, networkTimeOut));
            HashMap fromJson = new Hashon().fromJson(r0);
            if ("-200".equals(String.valueOf(fromJson.get("status")))) {
                MobLog.getInstance().i("error log server config response fail !!", new Object[0]);
                return;
            }
            invokeStaticMethod = fromJson.get(Form.TYPE_RESULT);
            if (invokeStaticMethod != null && (invokeStaticMethod instanceof HashMap)) {
                HashMap hashMap;
                fromJson = (HashMap) invokeStaticMethod;
                if (fromJson.containsKey("timestamp")) {
                    this.f.a(System.currentTimeMillis() - ResHelper.parseLong(String.valueOf(fromJson.get("timestamp"))));
                }
                if ("1".equals(String.valueOf(fromJson.get("enable")))) {
                    this.f.a(true);
                } else {
                    this.f.a(false);
                }
                Object obj = fromJson.get("upconf");
                if (obj != null && (obj instanceof HashMap)) {
                    hashMap = (HashMap) obj;
                    String valueOf = String.valueOf(hashMap.get("crash"));
                    String valueOf2 = String.valueOf(hashMap.get("sdkerr"));
                    String valueOf3 = String.valueOf(hashMap.get("apperr"));
                    this.f.a(Integer.parseInt(valueOf));
                    this.f.b(Integer.parseInt(valueOf2));
                    this.f.c(Integer.parseInt(valueOf3));
                }
                if (fromJson.containsKey("requesthost") && fromJson.containsKey("requestport")) {
                    obj = String.valueOf(fromJson.get("requesthost"));
                    Object valueOf4 = String.valueOf(fromJson.get("requestport"));
                    if (!(TextUtils.isEmpty(obj) || TextUtils.isEmpty(valueOf4))) {
                        b = "http://" + obj + ":" + valueOf4;
                    }
                }
                invokeStaticMethod = fromJson.get("filter");
                if (invokeStaticMethod != null && (invokeStaticMethod instanceof ArrayList)) {
                    ArrayList arrayList2 = (ArrayList) invokeStaticMethod;
                    if (arrayList2.size() > 0) {
                        hashMap = new HashMap();
                        hashMap.put("fakelist", arrayList2);
                        this.f.a(new Hashon().fromHashMap(hashMap));
                    }
                }
            }
        } catch (Throwable th) {
            MobLog.getInstance().d(th);
        }
    }

    private String c() {
        return b + "/errlog";
    }

    private void a(int i, String str, String str2, String[] strArr) {
        try {
            if (this.f.b()) {
                if (PrivacyRule.SUBSCRIPTION_NONE.equals((String) ReflectHelper.invokeInstanceMethod(ReflectHelper.invokeStaticMethod("DeviceHelper", "getInstance", this.d), "getDetailNetworkTypeForStatic", new Object[0]))) {
                    throw new IllegalStateException("network is disconnected!");
                }
                ArrayList a = f.a(this.d, strArr);
                for (int i2 = 0; i2 < a.size(); i2++) {
                    e eVar = (e) a.get(i2);
                    HashMap c = c(i, str, str2);
                    c.put("errmsg", eVar.a);
                    if (a(a(new Hashon().fromHashMap(c)), true)) {
                        f.a(this.d, eVar.b);
                    }
                }
            }
        } catch (Throwable th) {
            MobLog.getInstance().i(th);
        }
    }

    private HashMap<String, Object> c(int i, String str, String str2) throws Throwable {
        HashMap<String, Object> hashMap = new HashMap();
        Object invokeStaticMethod = ReflectHelper.invokeStaticMethod("DeviceHelper", "getInstance", this.d);
        hashMap.put("key", str2);
        hashMap.put("plat", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getPlatformCode", new Object[0]));
        hashMap.put("sdk", str);
        hashMap.put("sdkver", Integer.valueOf(i));
        hashMap.put("appname", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getAppName", new Object[0]));
        hashMap.put("apppkg", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getPackageName", new Object[0]));
        hashMap.put("appver", String.valueOf(ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getAppVersion", new Object[0])));
        hashMap.put("deviceid", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getDeviceKey", new Object[0]));
        hashMap.put("model", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getModel", new Object[0]));
        hashMap.put("mac", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getMacAddress", new Object[0]));
        hashMap.put("udid", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getDeviceId", new Object[0]));
        hashMap.put("sysver", String.valueOf(ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getOSVersionInt", new Object[0])));
        hashMap.put("networktype", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getDetailNetworkTypeForStatic", new Object[0]));
        return hashMap;
    }

    private String a(String str) throws Throwable {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(str.getBytes());
        OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);
        byte[] bArr = new byte[1024];
        while (true) {
            int read = byteArrayInputStream.read(bArr, 0, 1024);
            if (read != -1) {
                gZIPOutputStream.write(bArr, 0, read);
            } else {
                gZIPOutputStream.flush();
                gZIPOutputStream.close();
                byte[] toByteArray = byteArrayOutputStream.toByteArray();
                byteArrayOutputStream.flush();
                byteArrayOutputStream.close();
                byteArrayInputStream.close();
                return Base64.encodeToString(toByteArray, 2);
            }
        }
    }

    private boolean a(String str, boolean z) throws Throwable {
        try {
            if (PrivacyRule.SUBSCRIPTION_NONE.equals((String) ReflectHelper.invokeInstanceMethod(ReflectHelper.invokeStaticMethod("DeviceHelper", "getInstance", this.d), "getDetailNetworkTypeForStatic", new Object[0]))) {
                throw new IllegalStateException("network is disconnected!");
            }
            ArrayList arrayList = new ArrayList();
            arrayList.add(new KVPair("m", str));
            NetworkTimeOut networkTimeOut = new NetworkTimeOut();
            networkTimeOut.readTimout = 10000;
            networkTimeOut.connectionTimeout = 10000;
            this.e.httpPost(c(), arrayList, null, null, networkTimeOut);
            return true;
        } catch (Throwable th) {
            MobLog.getInstance().i(th);
            return false;
        }
    }
}
