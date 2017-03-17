package com.xiaomi.push.service;

import com.xiaomi.push.service.XMPushService.h;

class ba extends h {
    final /* synthetic */ XMPushService b;

    ba(XMPushService xMPushService, int i) {
        this.b = xMPushService;
        super(i);
    }

    public void a() {
        if (this.b.i != null) {
            this.b.i.b(15, null);
            this.b.i = null;
        }
    }

    public String b() {
        return "disconnect for service destroy.";
    }
}
