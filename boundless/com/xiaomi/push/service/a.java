package com.xiaomi.push.service;

import com.xiaomi.push.service.XMPushService.h;
import com.xiaomi.slim.b;
import com.xiaomi.smack.packet.c;

class a extends h {
    private XMPushService b = null;
    private c[] c;
    private b[] d;

    public a(XMPushService xMPushService, b[] bVarArr) {
        super(4);
        this.b = xMPushService;
        this.d = bVarArr;
    }

    public a(XMPushService xMPushService, c[] cVarArr) {
        super(4);
        this.b = xMPushService;
        this.c = cVarArr;
    }

    public void a() {
        try {
            if (this.c != null) {
                this.b.a(this.c);
            } else {
                this.b.a(this.d);
            }
        } catch (Exception e) {
            com.xiaomi.channel.commonutils.logger.b.a((Throwable) e);
            this.b.a(10, e);
        }
    }

    public String b() {
        return "batch send message.";
    }
}
