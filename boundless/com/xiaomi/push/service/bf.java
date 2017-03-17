package com.xiaomi.push.service;

import com.xiaomi.push.service.XMPushService.f;
import com.xiaomi.push.service.ak.a;

class bf implements a {
    final /* synthetic */ XMPushService a;

    bf(XMPushService xMPushService) {
        this.a = xMPushService;
    }

    public void a() {
        this.a.n();
        if (ak.a().c() <= 0) {
            this.a.a(new f(this.a, 12, null));
        }
    }
}
