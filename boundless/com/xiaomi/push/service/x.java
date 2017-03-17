package com.xiaomi.push.service;

import com.xiaomi.channel.commonutils.logger.b;
import com.xiaomi.push.service.XMPushService.h;
import com.xiaomi.xmpush.thrift.ab;

final class x extends h {
    final /* synthetic */ XMPushService b;
    final /* synthetic */ ab c;
    final /* synthetic */ String d;

    x(int i, XMPushService xMPushService, ab abVar, String str) {
        this.b = xMPushService;
        this.c = abVar;
        this.d = str;
        super(i);
    }

    public void a() {
        try {
            ab a = s.a(this.b, this.c);
            a.m().a("absent_target_package", this.d);
            aa.a(this.b, a);
        } catch (Exception e) {
            b.a((Throwable) e);
            this.b.a(10, e);
        }
    }

    public String b() {
        return "send app absent ack message for message.";
    }
}
