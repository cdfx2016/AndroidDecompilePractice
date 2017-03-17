package com.xiaomi.push.service;

import com.xiaomi.channel.commonutils.logger.b;
import com.xiaomi.push.service.XMPushService.h;
import com.xiaomi.xmpush.thrift.ab;

final class y extends h {
    final /* synthetic */ XMPushService b;
    final /* synthetic */ ab c;
    final /* synthetic */ String d;
    final /* synthetic */ String e;

    y(int i, XMPushService xMPushService, ab abVar, String str, String str2) {
        this.b = xMPushService;
        this.c = abVar;
        this.d = str;
        this.e = str2;
        super(i);
    }

    public void a() {
        try {
            ab a = s.a(this.b, this.c);
            a.h.a("error", this.d);
            a.h.a("reason", this.e);
            aa.a(this.b, a);
        } catch (Exception e) {
            b.a((Throwable) e);
            this.b.a(10, e);
        }
    }

    public String b() {
        return "send wrong message ack for message.";
    }
}
