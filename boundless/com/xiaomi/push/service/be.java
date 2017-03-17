package com.xiaomi.push.service;

import com.xiaomi.channel.commonutils.network.d;
import com.xiaomi.push.service.XMPushService.h;

class be extends h {
    final /* synthetic */ XMPushService b;

    be(XMPushService xMPushService, int i) {
        this.b = xMPushService;
        super(i);
    }

    public void a() {
        aa.a(this.b);
        if (d.d(this.b)) {
            this.b.a(true);
        }
    }

    public String b() {
        return "prepare the mi push account.";
    }
}
