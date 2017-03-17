package com.xiaomi.push.service;

import com.xiaomi.channel.commonutils.logger.b;
import com.xiaomi.push.service.XMPushService.h;
import com.xiaomi.xmpush.thrift.ab;

final class t extends h {
    final /* synthetic */ XMPushService b;
    final /* synthetic */ ab c;

    t(int i, XMPushService xMPushService, ab abVar) {
        this.b = xMPushService;
        this.c = abVar;
        super(i);
    }

    public void a() {
        try {
            aa.a(this.b, aa.a(this.c.j(), this.c.h()));
        } catch (Exception e) {
            b.a((Throwable) e);
            this.b.a(10, e);
        }
    }

    public String b() {
        return "send app absent message.";
    }
}
