package com.xiaomi.push.service;

import com.xiaomi.channel.commonutils.logger.b;
import com.xiaomi.push.service.XMPushService.h;
import com.xiaomi.xmpush.thrift.ab;

final class u extends h {
    final /* synthetic */ XMPushService b;
    final /* synthetic */ ab c;

    u(int i, XMPushService xMPushService, ab abVar) {
        this.b = xMPushService;
        this.c = abVar;
        super(i);
    }

    public void a() {
        try {
            aa.a(this.b, s.a(this.b, this.c));
        } catch (Exception e) {
            b.a((Throwable) e);
            this.b.a(10, e);
        }
    }

    public String b() {
        return "send ack message for message.";
    }
}
