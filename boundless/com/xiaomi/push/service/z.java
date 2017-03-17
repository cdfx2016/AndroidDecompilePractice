package com.xiaomi.push.service;

import com.xiaomi.channel.commonutils.logger.b;
import com.xiaomi.push.service.XMPushService.h;
import com.xiaomi.xmpush.thrift.ab;

final class z extends h {
    final /* synthetic */ XMPushService b;
    final /* synthetic */ ab c;
    final /* synthetic */ boolean d;
    final /* synthetic */ boolean e;
    final /* synthetic */ boolean f;

    z(int i, XMPushService xMPushService, ab abVar, boolean z, boolean z2, boolean z3) {
        this.b = xMPushService;
        this.c = abVar;
        this.d = z;
        this.e = z2;
        this.f = z3;
        super(i);
    }

    public void a() {
        try {
            aa.a(this.b, s.a(this.b, this.c, this.d, this.e, this.f));
        } catch (Exception e) {
            b.a((Throwable) e);
            this.b.a(10, e);
        }
    }

    public String b() {
        return "send wrong message ack for message.";
    }
}
