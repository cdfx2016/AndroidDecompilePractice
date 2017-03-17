package cn.smssdk.net;

import android.text.TextUtils;
import cn.smssdk.utils.SMSLog;
import com.mob.tools.network.ByteArrayPart;
import com.mob.tools.network.HTTPPart;
import com.mob.tools.network.NetworkHelper;
import com.mob.tools.network.NetworkHelper.NetworkTimeOut;
import com.mob.tools.utils.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/* compiled from: BaseApi */
public abstract class a {
    private static NetworkHelper i;
    private static NetworkTimeOut j;
    protected int a;
    protected String b;
    protected String c;
    protected int d;
    protected boolean e;
    protected boolean f;
    protected boolean g = false;
    protected ReentrantReadWriteLock h;

    protected abstract HashMap<String, Object> a(String str, String str2, HashMap<String, Object> hashMap) throws Throwable;

    public abstract boolean b();

    public int a() {
        return this.a;
    }

    public void a(ReentrantReadWriteLock reentrantReadWriteLock) {
        this.h = reentrantReadWriteLock;
    }

    public String b(String str, String str2, HashMap<String, Object> hashMap) throws Throwable {
        if (b()) {
            throw new Throwable("{\"status\":464}");
        }
        try {
            if (this.h != null) {
                this.h.readLock().lock();
            }
            String a = a(this.c, a(a(str, str2, (HashMap) hashMap), this.e, this.d), str2, this.d, 0);
            return a;
        } finally {
            if (this.h != null) {
                this.h.readLock().unlock();
            }
        }
    }

    private static String a(String str, byte[] bArr, String str2, int i, int i2) throws Throwable {
        if (i2 > 3) {
            throw new Throwable("{'detail':'CRC Error,Network is poor'}");
        }
        if (i == null || j == null) {
            j = new NetworkTimeOut();
            j.connectionTimeout = 180000;
            j.readTimout = 180000;
            i = new NetworkHelper();
        }
        ArrayList a = e.a().a(bArr, str2);
        HTTPPart byteArrayPart = new ByteArrayPart();
        byteArrayPart.append(bArr);
        HashMap hashMap = new HashMap();
        i.rawPost(str, a, byteArrayPart, new HttpResponseCallbackImp(hashMap), j);
        if (hashMap == null || hashMap.size() <= 0) {
            throw new Throwable("[map]Response is empty");
        }
        byte[] bArr2 = (byte[]) hashMap.get("bResp");
        if (bArr2 == null || bArr2.length <= 0) {
            throw new Throwable("[resp]Response is empty");
        }
        String str3 = (String) hashMap.get("hash");
        if (!TextUtils.isEmpty(str3) && !Data.CRC32(bArr2).equals(str3)) {
            return a(str, bArr, str2, i, i2 + 1);
        }
        int intValue;
        String str4;
        Object obj = hashMap.get("httpStatus");
        if (obj != null) {
            intValue = ((Integer) obj).intValue();
        } else {
            intValue = 0;
        }
        if (intValue == 600) {
            str4 = new String(bArr2, "utf-8");
        } else {
            str4 = a(bArr2, i);
        }
        SMSLog.getInstance().d("resp: " + str4, new Object[0]);
        return str4;
    }

    private static byte[] a(HashMap<String, Object> hashMap, boolean z, int i) throws Throwable {
        return c.a((HashMap) hashMap, z, i);
    }

    private static String a(byte[] bArr, int i) throws Throwable {
        return c.a(bArr, i);
    }
}
