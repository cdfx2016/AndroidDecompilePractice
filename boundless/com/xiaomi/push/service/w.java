package com.xiaomi.push.service;

import com.xiaomi.channel.commonutils.logger.b;
import com.xiaomi.push.service.XMPushService.h;
import com.xiaomi.xmpush.thrift.ab;

final class w extends h {
    final /* synthetic */ XMPushService b;
    final /* synthetic */ ab c;

    w(int i, XMPushService xMPushService, ab abVar) {
        this.b = xMPushService;
        this.c = abVar;
        super(i);
    }

    public void a() {
        try {
            ab a = s.a(this.b, this.c);
            a.m().a("miui_message_unrecognized", "1");
            aa.a(this.b, a);
        } catch (Exception e) {
            b.a((Throwable) e);
            this.b.a(10, e);
        }
    }

    public String b() {
        return "send ack message for unrecognized new miui message.";
    }
}
