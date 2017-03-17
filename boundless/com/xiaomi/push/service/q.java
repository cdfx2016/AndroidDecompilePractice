package com.xiaomi.push.service;

import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.push.service.XMPushService.h;
import com.xiaomi.push.service.ak.b;
import com.xiaomi.push.service.ak.c;
import java.util.Collection;

public class q extends h {
    private XMPushService b;
    private byte[] c;
    private String d;
    private String e;
    private String f;

    public q(XMPushService xMPushService, String str, String str2, String str3, byte[] bArr) {
        super(9);
        this.b = xMPushService;
        this.d = str;
        this.c = bArr;
        this.e = str2;
        this.f = str3;
    }

    public void a() {
        n a;
        Collection c;
        b bVar;
        n a2 = o.a(this.b);
        if (a2 == null) {
            try {
                a = o.a(this.b, this.d, this.e, this.f);
            } catch (Throwable e) {
                com.xiaomi.channel.commonutils.logger.b.a(e);
                a = a2;
            } catch (Throwable e2) {
                com.xiaomi.channel.commonutils.logger.b.a(e2);
            }
            if (a != null) {
                com.xiaomi.channel.commonutils.logger.b.d("no account for mipush");
                r.a(this.b, ErrorCode.ERROR_AUTHERICATION_ERROR, "no account.");
            }
            c = ak.a().c("5");
            if (c.isEmpty()) {
                bVar = (b) c.iterator().next();
            } else {
                bVar = a.a(this.b);
                aa.a(this.b, bVar);
                ak.a().a(bVar);
            }
            if (this.b.f()) {
                this.b.a(true);
                return;
            }
            try {
                if (bVar.m == c.binded) {
                    aa.a(this.b, this.d, this.c);
                    return;
                } else if (bVar.m == c.unbind) {
                    XMPushService xMPushService = this.b;
                    XMPushService xMPushService2 = this.b;
                    xMPushService2.getClass();
                    xMPushService.a(new a(xMPushService2, bVar));
                    return;
                } else {
                    return;
                }
            } catch (Exception e3) {
                com.xiaomi.channel.commonutils.logger.b.a((Throwable) e3);
                this.b.a(10, e3);
                return;
            }
        }
        a = a2;
        if (a != null) {
            c = ak.a().c("5");
            if (c.isEmpty()) {
                bVar = (b) c.iterator().next();
            } else {
                bVar = a.a(this.b);
                aa.a(this.b, bVar);
                ak.a().a(bVar);
            }
            if (this.b.f()) {
                this.b.a(true);
                return;
            } else if (bVar.m == c.binded) {
                aa.a(this.b, this.d, this.c);
                return;
            } else if (bVar.m == c.unbind) {
                XMPushService xMPushService3 = this.b;
                XMPushService xMPushService22 = this.b;
                xMPushService22.getClass();
                xMPushService3.a(new a(xMPushService22, bVar));
                return;
            } else {
                return;
            }
        }
        com.xiaomi.channel.commonutils.logger.b.d("no account for mipush");
        r.a(this.b, ErrorCode.ERROR_AUTHERICATION_ERROR, "no account.");
    }

    public String b() {
        return "register app";
    }
}
