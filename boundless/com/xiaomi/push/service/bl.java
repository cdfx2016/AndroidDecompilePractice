package com.xiaomi.push.service;

import com.xiaomi.push.service.XMPushService.h;

class bl extends h {
    final /* synthetic */ XMPushService b;

    bl(XMPushService xMPushService, int i) {
        this.b = xMPushService;
        super(i);
    }

    public void a() {
        if (this.b.i != null) {
            this.b.i.h();
            this.b.i = null;
        }
    }

    public String b() {
        return "disconnect for disable push";
    }
}
