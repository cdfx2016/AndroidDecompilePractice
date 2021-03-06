package com.xiaomi.xmpush.thrift;

import com.xiaomi.mipush.sdk.Constants;
import java.io.Serializable;
import java.util.BitSet;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.thrift.meta_data.c;
import org.apache.thrift.meta_data.e;
import org.apache.thrift.meta_data.g;
import org.apache.thrift.protocol.b;
import org.apache.thrift.protocol.d;
import org.apache.thrift.protocol.f;
import org.apache.thrift.protocol.h;
import org.apache.thrift.protocol.j;

public class af implements Serializable, Cloneable, org.apache.thrift.a<af, a> {
    private static final b A = new b("target", (byte) 12, (short) 2);
    private static final b B = new b("id", (byte) 11, (short) 3);
    private static final b C = new b("appId", (byte) 11, (short) 4);
    private static final b D = new b("appVersion", (byte) 11, (short) 5);
    private static final b E = new b("packageName", (byte) 11, (short) 6);
    private static final b F = new b(Constants.EXTRA_KEY_TOKEN, (byte) 11, (short) 7);
    private static final b G = new b("deviceId", (byte) 11, (short) 8);
    private static final b H = new b("aliasName", (byte) 11, (short) 9);
    private static final b I = new b("sdkVersion", (byte) 11, (short) 10);
    private static final b J = new b("regId", (byte) 11, (short) 11);
    private static final b K = new b("pushSdkVersionName", (byte) 11, (short) 12);
    private static final b L = new b("pushSdkVersionCode", (byte) 8, (short) 13);
    private static final b M = new b("appVersionCode", (byte) 8, (short) 14);
    private static final b N = new b("androidId", (byte) 11, (short) 15);
    private static final b O = new b("imei", (byte) 11, (short) 16);
    private static final b P = new b("serial", (byte) 11, (short) 17);
    private static final b Q = new b("imeiMd5", (byte) 11, (short) 18);
    private static final b R = new b("spaceId", (byte) 8, (short) 19);
    private static final b S = new b("reason", (byte) 8, (short) 20);
    private static final b T = new b("connectionAttrs", (byte) 13, (short) 100);
    private static final b U = new b("cleanOldRegInfo", (byte) 2, (short) 101);
    private static final b V = new b("oldRegId", (byte) 11, (short) 102);
    public static final Map<a, org.apache.thrift.meta_data.b> x;
    private static final j y = new j("XmPushActionRegistration");
    private static final b z = new b("debug", (byte) 11, (short) 1);
    private BitSet W = new BitSet(4);
    public String a;
    public u b;
    public String c;
    public String d;
    public String e;
    public String f;
    public String g;
    public String h;
    public String i;
    public String j;
    public String k;
    public String l;
    public int m;
    public int n;
    public String o;
    public String p;
    public String q;
    public String r;
    public int s;
    public t t;
    public Map<String, String> u;
    public boolean v = false;
    public String w;

    public enum a {
        DEBUG((short) 1, "debug"),
        TARGET((short) 2, "target"),
        ID((short) 3, "id"),
        APP_ID((short) 4, "appId"),
        APP_VERSION((short) 5, "appVersion"),
        PACKAGE_NAME((short) 6, "packageName"),
        TOKEN((short) 7, Constants.EXTRA_KEY_TOKEN),
        DEVICE_ID((short) 8, "deviceId"),
        ALIAS_NAME((short) 9, "aliasName"),
        SDK_VERSION((short) 10, "sdkVersion"),
        REG_ID((short) 11, "regId"),
        PUSH_SDK_VERSION_NAME((short) 12, "pushSdkVersionName"),
        PUSH_SDK_VERSION_CODE((short) 13, "pushSdkVersionCode"),
        APP_VERSION_CODE((short) 14, "appVersionCode"),
        ANDROID_ID((short) 15, "androidId"),
        IMEI((short) 16, "imei"),
        SERIAL((short) 17, "serial"),
        IMEI_MD5((short) 18, "imeiMd5"),
        SPACE_ID((short) 19, "spaceId"),
        REASON((short) 20, "reason"),
        CONNECTION_ATTRS((short) 100, "connectionAttrs"),
        CLEAN_OLD_REG_INFO((short) 101, "cleanOldRegInfo"),
        OLD_REG_ID((short) 102, "oldRegId");
        
        private static final Map<String, a> x = null;
        private final short y;
        private final String z;

        static {
            x = new HashMap();
            Iterator it = EnumSet.allOf(a.class).iterator();
            while (it.hasNext()) {
                a aVar = (a) it.next();
                x.put(aVar.a(), aVar);
            }
        }

        private a(short s, String str) {
            this.y = s;
            this.z = str;
        }

        public String a() {
            return this.z;
        }
    }

    static {
        Map enumMap = new EnumMap(a.class);
        enumMap.put(a.DEBUG, new org.apache.thrift.meta_data.b("debug", (byte) 2, new c((byte) 11)));
        enumMap.put(a.TARGET, new org.apache.thrift.meta_data.b("target", (byte) 2, new g((byte) 12, u.class)));
        enumMap.put(a.ID, new org.apache.thrift.meta_data.b("id", (byte) 1, new c((byte) 11)));
        enumMap.put(a.APP_ID, new org.apache.thrift.meta_data.b("appId", (byte) 1, new c((byte) 11)));
        enumMap.put(a.APP_VERSION, new org.apache.thrift.meta_data.b("appVersion", (byte) 2, new c((byte) 11)));
        enumMap.put(a.PACKAGE_NAME, new org.apache.thrift.meta_data.b("packageName", (byte) 2, new c((byte) 11)));
        enumMap.put(a.TOKEN, new org.apache.thrift.meta_data.b(Constants.EXTRA_KEY_TOKEN, (byte) 1, new c((byte) 11)));
        enumMap.put(a.DEVICE_ID, new org.apache.thrift.meta_data.b("deviceId", (byte) 2, new c((byte) 11)));
        enumMap.put(a.ALIAS_NAME, new org.apache.thrift.meta_data.b("aliasName", (byte) 2, new c((byte) 11)));
        enumMap.put(a.SDK_VERSION, new org.apache.thrift.meta_data.b("sdkVersion", (byte) 2, new c((byte) 11)));
        enumMap.put(a.REG_ID, new org.apache.thrift.meta_data.b("regId", (byte) 2, new c((byte) 11)));
        enumMap.put(a.PUSH_SDK_VERSION_NAME, new org.apache.thrift.meta_data.b("pushSdkVersionName", (byte) 2, new c((byte) 11)));
        enumMap.put(a.PUSH_SDK_VERSION_CODE, new org.apache.thrift.meta_data.b("pushSdkVersionCode", (byte) 2, new c((byte) 8)));
        enumMap.put(a.APP_VERSION_CODE, new org.apache.thrift.meta_data.b("appVersionCode", (byte) 2, new c((byte) 8)));
        enumMap.put(a.ANDROID_ID, new org.apache.thrift.meta_data.b("androidId", (byte) 2, new c((byte) 11)));
        enumMap.put(a.IMEI, new org.apache.thrift.meta_data.b("imei", (byte) 2, new c((byte) 11)));
        enumMap.put(a.SERIAL, new org.apache.thrift.meta_data.b("serial", (byte) 2, new c((byte) 11)));
        enumMap.put(a.IMEI_MD5, new org.apache.thrift.meta_data.b("imeiMd5", (byte) 2, new c((byte) 11)));
        enumMap.put(a.SPACE_ID, new org.apache.thrift.meta_data.b("spaceId", (byte) 2, new c((byte) 8)));
        enumMap.put(a.REASON, new org.apache.thrift.meta_data.b("reason", (byte) 2, new org.apache.thrift.meta_data.a((byte) 16, t.class)));
        enumMap.put(a.CONNECTION_ATTRS, new org.apache.thrift.meta_data.b("connectionAttrs", (byte) 2, new e((byte) 13, new c((byte) 11), new c((byte) 11))));
        enumMap.put(a.CLEAN_OLD_REG_INFO, new org.apache.thrift.meta_data.b("cleanOldRegInfo", (byte) 2, new c((byte) 2)));
        enumMap.put(a.OLD_REG_ID, new org.apache.thrift.meta_data.b("oldRegId", (byte) 2, new c((byte) 11)));
        x = Collections.unmodifiableMap(enumMap);
        org.apache.thrift.meta_data.b.a(af.class, x);
    }

    public af a(int i) {
        this.m = i;
        a(true);
        return this;
    }

    public af a(t tVar) {
        this.t = tVar;
        return this;
    }

    public af a(String str) {
        this.c = str;
        return this;
    }

    public void a(org.apache.thrift.protocol.e eVar) {
        eVar.g();
        while (true) {
            b i = eVar.i();
            if (i.b == (byte) 0) {
                eVar.h();
                z();
                return;
            }
            switch (i.c) {
                case (short) 1:
                    if (i.b != (byte) 11) {
                        h.a(eVar, i.b);
                        break;
                    } else {
                        this.a = eVar.w();
                        break;
                    }
                case (short) 2:
                    if (i.b != (byte) 12) {
                        h.a(eVar, i.b);
                        break;
                    }
                    this.b = new u();
                    this.b.a(eVar);
                    break;
                case (short) 3:
                    if (i.b != (byte) 11) {
                        h.a(eVar, i.b);
                        break;
                    } else {
                        this.c = eVar.w();
                        break;
                    }
                case (short) 4:
                    if (i.b != (byte) 11) {
                        h.a(eVar, i.b);
                        break;
                    } else {
                        this.d = eVar.w();
                        break;
                    }
                case (short) 5:
                    if (i.b != (byte) 11) {
                        h.a(eVar, i.b);
                        break;
                    } else {
                        this.e = eVar.w();
                        break;
                    }
                case (short) 6:
                    if (i.b != (byte) 11) {
                        h.a(eVar, i.b);
                        break;
                    } else {
                        this.f = eVar.w();
                        break;
                    }
                case (short) 7:
                    if (i.b != (byte) 11) {
                        h.a(eVar, i.b);
                        break;
                    } else {
                        this.g = eVar.w();
                        break;
                    }
                case (short) 8:
                    if (i.b != (byte) 11) {
                        h.a(eVar, i.b);
                        break;
                    } else {
                        this.h = eVar.w();
                        break;
                    }
                case (short) 9:
                    if (i.b != (byte) 11) {
                        h.a(eVar, i.b);
                        break;
                    } else {
                        this.i = eVar.w();
                        break;
                    }
                case (short) 10:
                    if (i.b != (byte) 11) {
                        h.a(eVar, i.b);
                        break;
                    } else {
                        this.j = eVar.w();
                        break;
                    }
                case (short) 11:
                    if (i.b != (byte) 11) {
                        h.a(eVar, i.b);
                        break;
                    } else {
                        this.k = eVar.w();
                        break;
                    }
                case (short) 12:
                    if (i.b != (byte) 11) {
                        h.a(eVar, i.b);
                        break;
                    } else {
                        this.l = eVar.w();
                        break;
                    }
                case (short) 13:
                    if (i.b != (byte) 8) {
                        h.a(eVar, i.b);
                        break;
                    }
                    this.m = eVar.t();
                    a(true);
                    break;
                case (short) 14:
                    if (i.b != (byte) 8) {
                        h.a(eVar, i.b);
                        break;
                    }
                    this.n = eVar.t();
                    b(true);
                    break;
                case (short) 15:
                    if (i.b != (byte) 11) {
                        h.a(eVar, i.b);
                        break;
                    } else {
                        this.o = eVar.w();
                        break;
                    }
                case (short) 16:
                    if (i.b != (byte) 11) {
                        h.a(eVar, i.b);
                        break;
                    } else {
                        this.p = eVar.w();
                        break;
                    }
                case (short) 17:
                    if (i.b != (byte) 11) {
                        h.a(eVar, i.b);
                        break;
                    } else {
                        this.q = eVar.w();
                        break;
                    }
                case (short) 18:
                    if (i.b != (byte) 11) {
                        h.a(eVar, i.b);
                        break;
                    } else {
                        this.r = eVar.w();
                        break;
                    }
                case (short) 19:
                    if (i.b != (byte) 8) {
                        h.a(eVar, i.b);
                        break;
                    }
                    this.s = eVar.t();
                    c(true);
                    break;
                case (short) 20:
                    if (i.b != (byte) 8) {
                        h.a(eVar, i.b);
                        break;
                    } else {
                        this.t = t.a(eVar.t());
                        break;
                    }
                case (short) 100:
                    if (i.b != (byte) 13) {
                        h.a(eVar, i.b);
                        break;
                    }
                    d k = eVar.k();
                    this.u = new HashMap(k.c * 2);
                    for (int i2 = 0; i2 < k.c; i2++) {
                        this.u.put(eVar.w(), eVar.w());
                    }
                    eVar.l();
                    break;
                case (short) 101:
                    if (i.b != (byte) 2) {
                        h.a(eVar, i.b);
                        break;
                    }
                    this.v = eVar.q();
                    d(true);
                    break;
                case (short) 102:
                    if (i.b != (byte) 11) {
                        h.a(eVar, i.b);
                        break;
                    } else {
                        this.w = eVar.w();
                        break;
                    }
                default:
                    h.a(eVar, i.b);
                    break;
            }
            eVar.j();
        }
    }

    public void a(boolean z) {
        this.W.set(0, z);
    }

    public boolean a() {
        return this.a != null;
    }

    public boolean a(af afVar) {
        if (afVar == null) {
            return false;
        }
        boolean a = a();
        boolean a2 = afVar.a();
        if ((a || a2) && (!a || !a2 || !this.a.equals(afVar.a))) {
            return false;
        }
        a = b();
        a2 = afVar.b();
        if ((a || a2) && (!a || !a2 || !this.b.a(afVar.b))) {
            return false;
        }
        a = c();
        a2 = afVar.c();
        if ((a || a2) && (!a || !a2 || !this.c.equals(afVar.c))) {
            return false;
        }
        a = e();
        a2 = afVar.e();
        if ((a || a2) && (!a || !a2 || !this.d.equals(afVar.d))) {
            return false;
        }
        a = f();
        a2 = afVar.f();
        if ((a || a2) && (!a || !a2 || !this.e.equals(afVar.e))) {
            return false;
        }
        a = g();
        a2 = afVar.g();
        if ((a || a2) && (!a || !a2 || !this.f.equals(afVar.f))) {
            return false;
        }
        a = i();
        a2 = afVar.i();
        if ((a || a2) && (!a || !a2 || !this.g.equals(afVar.g))) {
            return false;
        }
        a = j();
        a2 = afVar.j();
        if ((a || a2) && (!a || !a2 || !this.h.equals(afVar.h))) {
            return false;
        }
        a = k();
        a2 = afVar.k();
        if ((a || a2) && (!a || !a2 || !this.i.equals(afVar.i))) {
            return false;
        }
        a = l();
        a2 = afVar.l();
        if ((a || a2) && (!a || !a2 || !this.j.equals(afVar.j))) {
            return false;
        }
        a = m();
        a2 = afVar.m();
        if ((a || a2) && (!a || !a2 || !this.k.equals(afVar.k))) {
            return false;
        }
        a = n();
        a2 = afVar.n();
        if ((a || a2) && (!a || !a2 || !this.l.equals(afVar.l))) {
            return false;
        }
        a = o();
        a2 = afVar.o();
        if ((a || a2) && (!a || !a2 || this.m != afVar.m)) {
            return false;
        }
        a = p();
        a2 = afVar.p();
        if ((a || a2) && (!a || !a2 || this.n != afVar.n)) {
            return false;
        }
        a = q();
        a2 = afVar.q();
        if ((a || a2) && (!a || !a2 || !this.o.equals(afVar.o))) {
            return false;
        }
        a = r();
        a2 = afVar.r();
        if ((a || a2) && (!a || !a2 || !this.p.equals(afVar.p))) {
            return false;
        }
        a = s();
        a2 = afVar.s();
        if ((a || a2) && (!a || !a2 || !this.q.equals(afVar.q))) {
            return false;
        }
        a = t();
        a2 = afVar.t();
        if ((a || a2) && (!a || !a2 || !this.r.equals(afVar.r))) {
            return false;
        }
        a = u();
        a2 = afVar.u();
        if ((a || a2) && (!a || !a2 || this.s != afVar.s)) {
            return false;
        }
        a = v();
        a2 = afVar.v();
        if ((a || a2) && (!a || !a2 || !this.t.equals(afVar.t))) {
            return false;
        }
        a = w();
        a2 = afVar.w();
        if ((a || a2) && (!a || !a2 || !this.u.equals(afVar.u))) {
            return false;
        }
        a = x();
        a2 = afVar.x();
        if ((a || a2) && (!a || !a2 || this.v != afVar.v)) {
            return false;
        }
        a = y();
        a2 = afVar.y();
        return !(a || a2) || (a && a2 && this.w.equals(afVar.w));
    }

    public int b(af afVar) {
        if (!getClass().equals(afVar.getClass())) {
            return getClass().getName().compareTo(afVar.getClass().getName());
        }
        int compareTo = Boolean.valueOf(a()).compareTo(Boolean.valueOf(afVar.a()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (a()) {
            compareTo = org.apache.thrift.b.a(this.a, afVar.a);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        compareTo = Boolean.valueOf(b()).compareTo(Boolean.valueOf(afVar.b()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (b()) {
            compareTo = org.apache.thrift.b.a(this.b, afVar.b);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        compareTo = Boolean.valueOf(c()).compareTo(Boolean.valueOf(afVar.c()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (c()) {
            compareTo = org.apache.thrift.b.a(this.c, afVar.c);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        compareTo = Boolean.valueOf(e()).compareTo(Boolean.valueOf(afVar.e()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (e()) {
            compareTo = org.apache.thrift.b.a(this.d, afVar.d);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        compareTo = Boolean.valueOf(f()).compareTo(Boolean.valueOf(afVar.f()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (f()) {
            compareTo = org.apache.thrift.b.a(this.e, afVar.e);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        compareTo = Boolean.valueOf(g()).compareTo(Boolean.valueOf(afVar.g()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (g()) {
            compareTo = org.apache.thrift.b.a(this.f, afVar.f);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        compareTo = Boolean.valueOf(i()).compareTo(Boolean.valueOf(afVar.i()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (i()) {
            compareTo = org.apache.thrift.b.a(this.g, afVar.g);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        compareTo = Boolean.valueOf(j()).compareTo(Boolean.valueOf(afVar.j()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (j()) {
            compareTo = org.apache.thrift.b.a(this.h, afVar.h);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        compareTo = Boolean.valueOf(k()).compareTo(Boolean.valueOf(afVar.k()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (k()) {
            compareTo = org.apache.thrift.b.a(this.i, afVar.i);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        compareTo = Boolean.valueOf(l()).compareTo(Boolean.valueOf(afVar.l()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (l()) {
            compareTo = org.apache.thrift.b.a(this.j, afVar.j);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        compareTo = Boolean.valueOf(m()).compareTo(Boolean.valueOf(afVar.m()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (m()) {
            compareTo = org.apache.thrift.b.a(this.k, afVar.k);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        compareTo = Boolean.valueOf(n()).compareTo(Boolean.valueOf(afVar.n()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (n()) {
            compareTo = org.apache.thrift.b.a(this.l, afVar.l);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        compareTo = Boolean.valueOf(o()).compareTo(Boolean.valueOf(afVar.o()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (o()) {
            compareTo = org.apache.thrift.b.a(this.m, afVar.m);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        compareTo = Boolean.valueOf(p()).compareTo(Boolean.valueOf(afVar.p()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (p()) {
            compareTo = org.apache.thrift.b.a(this.n, afVar.n);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        compareTo = Boolean.valueOf(q()).compareTo(Boolean.valueOf(afVar.q()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (q()) {
            compareTo = org.apache.thrift.b.a(this.o, afVar.o);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        compareTo = Boolean.valueOf(r()).compareTo(Boolean.valueOf(afVar.r()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (r()) {
            compareTo = org.apache.thrift.b.a(this.p, afVar.p);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        compareTo = Boolean.valueOf(s()).compareTo(Boolean.valueOf(afVar.s()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (s()) {
            compareTo = org.apache.thrift.b.a(this.q, afVar.q);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        compareTo = Boolean.valueOf(t()).compareTo(Boolean.valueOf(afVar.t()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (t()) {
            compareTo = org.apache.thrift.b.a(this.r, afVar.r);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        compareTo = Boolean.valueOf(u()).compareTo(Boolean.valueOf(afVar.u()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (u()) {
            compareTo = org.apache.thrift.b.a(this.s, afVar.s);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        compareTo = Boolean.valueOf(v()).compareTo(Boolean.valueOf(afVar.v()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (v()) {
            compareTo = org.apache.thrift.b.a(this.t, afVar.t);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        compareTo = Boolean.valueOf(w()).compareTo(Boolean.valueOf(afVar.w()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (w()) {
            compareTo = org.apache.thrift.b.a(this.u, afVar.u);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        compareTo = Boolean.valueOf(x()).compareTo(Boolean.valueOf(afVar.x()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (x()) {
            compareTo = org.apache.thrift.b.a(this.v, afVar.v);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        compareTo = Boolean.valueOf(y()).compareTo(Boolean.valueOf(afVar.y()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (y()) {
            compareTo = org.apache.thrift.b.a(this.w, afVar.w);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        return 0;
    }

    public af b(int i) {
        this.n = i;
        b(true);
        return this;
    }

    public af b(String str) {
        this.d = str;
        return this;
    }

    public void b(org.apache.thrift.protocol.e eVar) {
        z();
        eVar.a(y);
        if (this.a != null && a()) {
            eVar.a(z);
            eVar.a(this.a);
            eVar.b();
        }
        if (this.b != null && b()) {
            eVar.a(A);
            this.b.b(eVar);
            eVar.b();
        }
        if (this.c != null) {
            eVar.a(B);
            eVar.a(this.c);
            eVar.b();
        }
        if (this.d != null) {
            eVar.a(C);
            eVar.a(this.d);
            eVar.b();
        }
        if (this.e != null && f()) {
            eVar.a(D);
            eVar.a(this.e);
            eVar.b();
        }
        if (this.f != null && g()) {
            eVar.a(E);
            eVar.a(this.f);
            eVar.b();
        }
        if (this.g != null) {
            eVar.a(F);
            eVar.a(this.g);
            eVar.b();
        }
        if (this.h != null && j()) {
            eVar.a(G);
            eVar.a(this.h);
            eVar.b();
        }
        if (this.i != null && k()) {
            eVar.a(H);
            eVar.a(this.i);
            eVar.b();
        }
        if (this.j != null && l()) {
            eVar.a(I);
            eVar.a(this.j);
            eVar.b();
        }
        if (this.k != null && m()) {
            eVar.a(J);
            eVar.a(this.k);
            eVar.b();
        }
        if (this.l != null && n()) {
            eVar.a(K);
            eVar.a(this.l);
            eVar.b();
        }
        if (o()) {
            eVar.a(L);
            eVar.a(this.m);
            eVar.b();
        }
        if (p()) {
            eVar.a(M);
            eVar.a(this.n);
            eVar.b();
        }
        if (this.o != null && q()) {
            eVar.a(N);
            eVar.a(this.o);
            eVar.b();
        }
        if (this.p != null && r()) {
            eVar.a(O);
            eVar.a(this.p);
            eVar.b();
        }
        if (this.q != null && s()) {
            eVar.a(P);
            eVar.a(this.q);
            eVar.b();
        }
        if (this.r != null && t()) {
            eVar.a(Q);
            eVar.a(this.r);
            eVar.b();
        }
        if (u()) {
            eVar.a(R);
            eVar.a(this.s);
            eVar.b();
        }
        if (this.t != null && v()) {
            eVar.a(S);
            eVar.a(this.t.a());
            eVar.b();
        }
        if (this.u != null && w()) {
            eVar.a(T);
            eVar.a(new d((byte) 11, (byte) 11, this.u.size()));
            for (Entry entry : this.u.entrySet()) {
                eVar.a((String) entry.getKey());
                eVar.a((String) entry.getValue());
            }
            eVar.d();
            eVar.b();
        }
        if (x()) {
            eVar.a(U);
            eVar.a(this.v);
            eVar.b();
        }
        if (this.w != null && y()) {
            eVar.a(V);
            eVar.a(this.w);
            eVar.b();
        }
        eVar.c();
        eVar.a();
    }

    public void b(boolean z) {
        this.W.set(1, z);
    }

    public boolean b() {
        return this.b != null;
    }

    public af c(int i) {
        this.s = i;
        c(true);
        return this;
    }

    public af c(String str) {
        this.e = str;
        return this;
    }

    public void c(boolean z) {
        this.W.set(2, z);
    }

    public boolean c() {
        return this.c != null;
    }

    public /* synthetic */ int compareTo(Object obj) {
        return b((af) obj);
    }

    public af d(String str) {
        this.f = str;
        return this;
    }

    public String d() {
        return this.d;
    }

    public void d(boolean z) {
        this.W.set(3, z);
    }

    public af e(String str) {
        this.g = str;
        return this;
    }

    public boolean e() {
        return this.d != null;
    }

    public boolean equals(Object obj) {
        return (obj != null && (obj instanceof af)) ? a((af) obj) : false;
    }

    public af f(String str) {
        this.h = str;
        return this;
    }

    public boolean f() {
        return this.e != null;
    }

    public af g(String str) {
        this.l = str;
        return this;
    }

    public boolean g() {
        return this.f != null;
    }

    public af h(String str) {
        this.o = str;
        return this;
    }

    public String h() {
        return this.g;
    }

    public int hashCode() {
        return 0;
    }

    public af i(String str) {
        this.p = str;
        return this;
    }

    public boolean i() {
        return this.g != null;
    }

    public af j(String str) {
        this.q = str;
        return this;
    }

    public boolean j() {
        return this.h != null;
    }

    public af k(String str) {
        this.r = str;
        return this;
    }

    public boolean k() {
        return this.i != null;
    }

    public boolean l() {
        return this.j != null;
    }

    public boolean m() {
        return this.k != null;
    }

    public boolean n() {
        return this.l != null;
    }

    public boolean o() {
        return this.W.get(0);
    }

    public boolean p() {
        return this.W.get(1);
    }

    public boolean q() {
        return this.o != null;
    }

    public boolean r() {
        return this.p != null;
    }

    public boolean s() {
        return this.q != null;
    }

    public boolean t() {
        return this.r != null;
    }

    public String toString() {
        Object obj = null;
        StringBuilder stringBuilder = new StringBuilder("XmPushActionRegistration(");
        Object obj2 = 1;
        if (a()) {
            stringBuilder.append("debug:");
            if (this.a == null) {
                stringBuilder.append("null");
            } else {
                stringBuilder.append(this.a);
            }
            obj2 = null;
        }
        if (b()) {
            if (obj2 == null) {
                stringBuilder.append(", ");
            }
            stringBuilder.append("target:");
            if (this.b == null) {
                stringBuilder.append("null");
            } else {
                stringBuilder.append(this.b);
            }
        } else {
            obj = obj2;
        }
        if (obj == null) {
            stringBuilder.append(", ");
        }
        stringBuilder.append("id:");
        if (this.c == null) {
            stringBuilder.append("null");
        } else {
            stringBuilder.append(this.c);
        }
        stringBuilder.append(", ");
        stringBuilder.append("appId:");
        if (this.d == null) {
            stringBuilder.append("null");
        } else {
            stringBuilder.append(this.d);
        }
        if (f()) {
            stringBuilder.append(", ");
            stringBuilder.append("appVersion:");
            if (this.e == null) {
                stringBuilder.append("null");
            } else {
                stringBuilder.append(this.e);
            }
        }
        if (g()) {
            stringBuilder.append(", ");
            stringBuilder.append("packageName:");
            if (this.f == null) {
                stringBuilder.append("null");
            } else {
                stringBuilder.append(this.f);
            }
        }
        stringBuilder.append(", ");
        stringBuilder.append("token:");
        if (this.g == null) {
            stringBuilder.append("null");
        } else {
            stringBuilder.append(this.g);
        }
        if (j()) {
            stringBuilder.append(", ");
            stringBuilder.append("deviceId:");
            if (this.h == null) {
                stringBuilder.append("null");
            } else {
                stringBuilder.append(this.h);
            }
        }
        if (k()) {
            stringBuilder.append(", ");
            stringBuilder.append("aliasName:");
            if (this.i == null) {
                stringBuilder.append("null");
            } else {
                stringBuilder.append(this.i);
            }
        }
        if (l()) {
            stringBuilder.append(", ");
            stringBuilder.append("sdkVersion:");
            if (this.j == null) {
                stringBuilder.append("null");
            } else {
                stringBuilder.append(this.j);
            }
        }
        if (m()) {
            stringBuilder.append(", ");
            stringBuilder.append("regId:");
            if (this.k == null) {
                stringBuilder.append("null");
            } else {
                stringBuilder.append(this.k);
            }
        }
        if (n()) {
            stringBuilder.append(", ");
            stringBuilder.append("pushSdkVersionName:");
            if (this.l == null) {
                stringBuilder.append("null");
            } else {
                stringBuilder.append(this.l);
            }
        }
        if (o()) {
            stringBuilder.append(", ");
            stringBuilder.append("pushSdkVersionCode:");
            stringBuilder.append(this.m);
        }
        if (p()) {
            stringBuilder.append(", ");
            stringBuilder.append("appVersionCode:");
            stringBuilder.append(this.n);
        }
        if (q()) {
            stringBuilder.append(", ");
            stringBuilder.append("androidId:");
            if (this.o == null) {
                stringBuilder.append("null");
            } else {
                stringBuilder.append(this.o);
            }
        }
        if (r()) {
            stringBuilder.append(", ");
            stringBuilder.append("imei:");
            if (this.p == null) {
                stringBuilder.append("null");
            } else {
                stringBuilder.append(this.p);
            }
        }
        if (s()) {
            stringBuilder.append(", ");
            stringBuilder.append("serial:");
            if (this.q == null) {
                stringBuilder.append("null");
            } else {
                stringBuilder.append(this.q);
            }
        }
        if (t()) {
            stringBuilder.append(", ");
            stringBuilder.append("imeiMd5:");
            if (this.r == null) {
                stringBuilder.append("null");
            } else {
                stringBuilder.append(this.r);
            }
        }
        if (u()) {
            stringBuilder.append(", ");
            stringBuilder.append("spaceId:");
            stringBuilder.append(this.s);
        }
        if (v()) {
            stringBuilder.append(", ");
            stringBuilder.append("reason:");
            if (this.t == null) {
                stringBuilder.append("null");
            } else {
                stringBuilder.append(this.t);
            }
        }
        if (w()) {
            stringBuilder.append(", ");
            stringBuilder.append("connectionAttrs:");
            if (this.u == null) {
                stringBuilder.append("null");
            } else {
                stringBuilder.append(this.u);
            }
        }
        if (x()) {
            stringBuilder.append(", ");
            stringBuilder.append("cleanOldRegInfo:");
            stringBuilder.append(this.v);
        }
        if (y()) {
            stringBuilder.append(", ");
            stringBuilder.append("oldRegId:");
            if (this.w == null) {
                stringBuilder.append("null");
            } else {
                stringBuilder.append(this.w);
            }
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    public boolean u() {
        return this.W.get(2);
    }

    public boolean v() {
        return this.t != null;
    }

    public boolean w() {
        return this.u != null;
    }

    public boolean x() {
        return this.W.get(3);
    }

    public boolean y() {
        return this.w != null;
    }

    public void z() {
        if (this.c == null) {
            throw new f("Required field 'id' was not present! Struct: " + toString());
        } else if (this.d == null) {
            throw new f("Required field 'appId' was not present! Struct: " + toString());
        } else if (this.g == null) {
            throw new f("Required field 'token' was not present! Struct: " + toString());
        }
    }
}
