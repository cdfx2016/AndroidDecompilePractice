package com.xiaomi.xmpush.thrift;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.thrift.meta_data.b;
import org.apache.thrift.meta_data.c;
import org.apache.thrift.meta_data.g;
import org.apache.thrift.protocol.e;
import org.apache.thrift.protocol.f;
import org.apache.thrift.protocol.h;
import org.apache.thrift.protocol.j;

public class ab implements Serializable, Cloneable, org.apache.thrift.a<ab, a> {
    public static final Map<a, b> i;
    private static final j j = new j("XmPushActionContainer");
    private static final org.apache.thrift.protocol.b k = new org.apache.thrift.protocol.b(MessageEncoder.ATTR_ACTION, (byte) 8, (short) 1);
    private static final org.apache.thrift.protocol.b l = new org.apache.thrift.protocol.b("encryptAction", (byte) 2, (short) 2);
    private static final org.apache.thrift.protocol.b m = new org.apache.thrift.protocol.b("isRequest", (byte) 2, (short) 3);
    private static final org.apache.thrift.protocol.b n = new org.apache.thrift.protocol.b("pushAction", (byte) 11, (short) 4);
    private static final org.apache.thrift.protocol.b o = new org.apache.thrift.protocol.b("appid", (byte) 11, (short) 5);
    private static final org.apache.thrift.protocol.b p = new org.apache.thrift.protocol.b("packageName", (byte) 11, (short) 6);
    private static final org.apache.thrift.protocol.b q = new org.apache.thrift.protocol.b("target", (byte) 12, (short) 7);
    private static final org.apache.thrift.protocol.b r = new org.apache.thrift.protocol.b("metaInfo", (byte) 12, (short) 8);
    public a a;
    public boolean b = true;
    public boolean c = true;
    public ByteBuffer d;
    public String e;
    public String f;
    public u g;
    public r h;
    private BitSet s = new BitSet(2);

    public enum a {
        ACTION((short) 1, MessageEncoder.ATTR_ACTION),
        ENCRYPT_ACTION((short) 2, "encryptAction"),
        IS_REQUEST((short) 3, "isRequest"),
        PUSH_ACTION((short) 4, "pushAction"),
        APPID((short) 5, "appid"),
        PACKAGE_NAME((short) 6, "packageName"),
        TARGET((short) 7, "target"),
        META_INFO((short) 8, "metaInfo");
        
        private static final Map<String, a> i = null;
        private final short j;
        private final String k;

        static {
            i = new HashMap();
            Iterator it = EnumSet.allOf(a.class).iterator();
            while (it.hasNext()) {
                a aVar = (a) it.next();
                i.put(aVar.a(), aVar);
            }
        }

        private a(short s, String str) {
            this.j = s;
            this.k = str;
        }

        public String a() {
            return this.k;
        }
    }

    static {
        Map enumMap = new EnumMap(a.class);
        enumMap.put(a.ACTION, new b(MessageEncoder.ATTR_ACTION, (byte) 1, new org.apache.thrift.meta_data.a((byte) 16, a.class)));
        enumMap.put(a.ENCRYPT_ACTION, new b("encryptAction", (byte) 1, new c((byte) 2)));
        enumMap.put(a.IS_REQUEST, new b("isRequest", (byte) 1, new c((byte) 2)));
        enumMap.put(a.PUSH_ACTION, new b("pushAction", (byte) 1, new c((byte) 11)));
        enumMap.put(a.APPID, new b("appid", (byte) 2, new c((byte) 11)));
        enumMap.put(a.PACKAGE_NAME, new b("packageName", (byte) 2, new c((byte) 11)));
        enumMap.put(a.TARGET, new b("target", (byte) 1, new g((byte) 12, u.class)));
        enumMap.put(a.META_INFO, new b("metaInfo", (byte) 2, new g((byte) 12, r.class)));
        i = Collections.unmodifiableMap(enumMap);
        b.a(ab.class, i);
    }

    public a a() {
        return this.a;
    }

    public ab a(a aVar) {
        this.a = aVar;
        return this;
    }

    public ab a(r rVar) {
        this.h = rVar;
        return this;
    }

    public ab a(u uVar) {
        this.g = uVar;
        return this;
    }

    public ab a(String str) {
        this.e = str;
        return this;
    }

    public ab a(ByteBuffer byteBuffer) {
        this.d = byteBuffer;
        return this;
    }

    public ab a(boolean z) {
        this.b = z;
        b(true);
        return this;
    }

    public void a(e eVar) {
        eVar.g();
        while (true) {
            org.apache.thrift.protocol.b i = eVar.i();
            if (i.b == (byte) 0) {
                eVar.h();
                if (!d()) {
                    throw new f("Required field 'encryptAction' was not found in serialized data! Struct: " + toString());
                } else if (e()) {
                    o();
                    return;
                } else {
                    throw new f("Required field 'isRequest' was not found in serialized data! Struct: " + toString());
                }
            }
            switch (i.c) {
                case (short) 1:
                    if (i.b != (byte) 8) {
                        h.a(eVar, i.b);
                        break;
                    } else {
                        this.a = a.a(eVar.t());
                        break;
                    }
                case (short) 2:
                    if (i.b != (byte) 2) {
                        h.a(eVar, i.b);
                        break;
                    }
                    this.b = eVar.q();
                    b(true);
                    break;
                case (short) 3:
                    if (i.b != (byte) 2) {
                        h.a(eVar, i.b);
                        break;
                    }
                    this.c = eVar.q();
                    d(true);
                    break;
                case (short) 4:
                    if (i.b != (byte) 11) {
                        h.a(eVar, i.b);
                        break;
                    } else {
                        this.d = eVar.x();
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
                    if (i.b != (byte) 12) {
                        h.a(eVar, i.b);
                        break;
                    }
                    this.g = new u();
                    this.g.a(eVar);
                    break;
                case (short) 8:
                    if (i.b != (byte) 12) {
                        h.a(eVar, i.b);
                        break;
                    }
                    this.h = new r();
                    this.h.a(eVar);
                    break;
                default:
                    h.a(eVar, i.b);
                    break;
            }
            eVar.j();
        }
    }

    public boolean a(ab abVar) {
        if (abVar == null) {
            return false;
        }
        boolean b = b();
        boolean b2 = abVar.b();
        if (((b || b2) && (!b || !b2 || !this.a.equals(abVar.a))) || this.b != abVar.b || this.c != abVar.c) {
            return false;
        }
        b = g();
        b2 = abVar.g();
        if ((b || b2) && (!b || !b2 || !this.d.equals(abVar.d))) {
            return false;
        }
        b = i();
        b2 = abVar.i();
        if ((b || b2) && (!b || !b2 || !this.e.equals(abVar.e))) {
            return false;
        }
        b = k();
        b2 = abVar.k();
        if ((b || b2) && (!b || !b2 || !this.f.equals(abVar.f))) {
            return false;
        }
        b = l();
        b2 = abVar.l();
        if ((b || b2) && (!b || !b2 || !this.g.a(abVar.g))) {
            return false;
        }
        b = n();
        b2 = abVar.n();
        return !(b || b2) || (b && b2 && this.h.a(abVar.h));
    }

    public int b(ab abVar) {
        if (!getClass().equals(abVar.getClass())) {
            return getClass().getName().compareTo(abVar.getClass().getName());
        }
        int compareTo = Boolean.valueOf(b()).compareTo(Boolean.valueOf(abVar.b()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (b()) {
            compareTo = org.apache.thrift.b.a(this.a, abVar.a);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        compareTo = Boolean.valueOf(d()).compareTo(Boolean.valueOf(abVar.d()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (d()) {
            compareTo = org.apache.thrift.b.a(this.b, abVar.b);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        compareTo = Boolean.valueOf(e()).compareTo(Boolean.valueOf(abVar.e()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (e()) {
            compareTo = org.apache.thrift.b.a(this.c, abVar.c);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        compareTo = Boolean.valueOf(g()).compareTo(Boolean.valueOf(abVar.g()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (g()) {
            compareTo = org.apache.thrift.b.a(this.d, abVar.d);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        compareTo = Boolean.valueOf(i()).compareTo(Boolean.valueOf(abVar.i()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (i()) {
            compareTo = org.apache.thrift.b.a(this.e, abVar.e);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        compareTo = Boolean.valueOf(k()).compareTo(Boolean.valueOf(abVar.k()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (k()) {
            compareTo = org.apache.thrift.b.a(this.f, abVar.f);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        compareTo = Boolean.valueOf(l()).compareTo(Boolean.valueOf(abVar.l()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (l()) {
            compareTo = org.apache.thrift.b.a(this.g, abVar.g);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        compareTo = Boolean.valueOf(n()).compareTo(Boolean.valueOf(abVar.n()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (n()) {
            compareTo = org.apache.thrift.b.a(this.h, abVar.h);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        return 0;
    }

    public ab b(String str) {
        this.f = str;
        return this;
    }

    public void b(e eVar) {
        o();
        eVar.a(j);
        if (this.a != null) {
            eVar.a(k);
            eVar.a(this.a.a());
            eVar.b();
        }
        eVar.a(l);
        eVar.a(this.b);
        eVar.b();
        eVar.a(m);
        eVar.a(this.c);
        eVar.b();
        if (this.d != null) {
            eVar.a(n);
            eVar.a(this.d);
            eVar.b();
        }
        if (this.e != null && i()) {
            eVar.a(o);
            eVar.a(this.e);
            eVar.b();
        }
        if (this.f != null && k()) {
            eVar.a(p);
            eVar.a(this.f);
            eVar.b();
        }
        if (this.g != null) {
            eVar.a(q);
            this.g.b(eVar);
            eVar.b();
        }
        if (this.h != null && n()) {
            eVar.a(r);
            this.h.b(eVar);
            eVar.b();
        }
        eVar.c();
        eVar.a();
    }

    public void b(boolean z) {
        this.s.set(0, z);
    }

    public boolean b() {
        return this.a != null;
    }

    public ab c(boolean z) {
        this.c = z;
        d(true);
        return this;
    }

    public boolean c() {
        return this.b;
    }

    public /* synthetic */ int compareTo(Object obj) {
        return b((ab) obj);
    }

    public void d(boolean z) {
        this.s.set(1, z);
    }

    public boolean d() {
        return this.s.get(0);
    }

    public boolean e() {
        return this.s.get(1);
    }

    public boolean equals(Object obj) {
        return (obj != null && (obj instanceof ab)) ? a((ab) obj) : false;
    }

    public byte[] f() {
        a(org.apache.thrift.b.c(this.d));
        return this.d.array();
    }

    public boolean g() {
        return this.d != null;
    }

    public String h() {
        return this.e;
    }

    public int hashCode() {
        return 0;
    }

    public boolean i() {
        return this.e != null;
    }

    public String j() {
        return this.f;
    }

    public boolean k() {
        return this.f != null;
    }

    public boolean l() {
        return this.g != null;
    }

    public r m() {
        return this.h;
    }

    public boolean n() {
        return this.h != null;
    }

    public void o() {
        if (this.a == null) {
            throw new f("Required field 'action' was not present! Struct: " + toString());
        } else if (this.d == null) {
            throw new f("Required field 'pushAction' was not present! Struct: " + toString());
        } else if (this.g == null) {
            throw new f("Required field 'target' was not present! Struct: " + toString());
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("XmPushActionContainer(");
        stringBuilder.append("action:");
        if (this.a == null) {
            stringBuilder.append("null");
        } else {
            stringBuilder.append(this.a);
        }
        stringBuilder.append(", ");
        stringBuilder.append("encryptAction:");
        stringBuilder.append(this.b);
        stringBuilder.append(", ");
        stringBuilder.append("isRequest:");
        stringBuilder.append(this.c);
        stringBuilder.append(", ");
        stringBuilder.append("pushAction:");
        if (this.d == null) {
            stringBuilder.append("null");
        } else {
            org.apache.thrift.b.a(this.d, stringBuilder);
        }
        if (i()) {
            stringBuilder.append(", ");
            stringBuilder.append("appid:");
            if (this.e == null) {
                stringBuilder.append("null");
            } else {
                stringBuilder.append(this.e);
            }
        }
        if (k()) {
            stringBuilder.append(", ");
            stringBuilder.append("packageName:");
            if (this.f == null) {
                stringBuilder.append("null");
            } else {
                stringBuilder.append(this.f);
            }
        }
        stringBuilder.append(", ");
        stringBuilder.append("target:");
        if (this.g == null) {
            stringBuilder.append("null");
        } else {
            stringBuilder.append(this.g);
        }
        if (n()) {
            stringBuilder.append(", ");
            stringBuilder.append("metaInfo:");
            if (this.h == null) {
                stringBuilder.append("null");
            } else {
                stringBuilder.append(this.h);
            }
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}
