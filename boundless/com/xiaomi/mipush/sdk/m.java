package com.xiaomi.mipush.sdk;

import com.xiaomi.channel.commonutils.android.e;
import com.xiaomi.channel.commonutils.string.d;
import com.xiaomi.xmpush.thrift.ae;
import java.util.HashMap;
import org.apache.thrift.a;

final class m implements Runnable {
    m() {
    }

    public void run() {
        if (e.c(MiPushClient.sContext) != null) {
            a aeVar = new ae();
            aeVar.b(a.a(MiPushClient.sContext).c());
            aeVar.c("client_info_update");
            aeVar.a(MiPushClient.generatePacketID());
            aeVar.a(new HashMap());
            aeVar.i().put(Constants.EXTRA_KEY_IMEI_MD5, d.a(e.c(MiPushClient.sContext)));
            int b = e.b();
            if (b >= 0) {
                aeVar.i().put("space_id", Integer.toString(b));
            }
            u.a(MiPushClient.sContext).a(aeVar, com.xiaomi.xmpush.thrift.a.Notification, false, null);
        }
    }
}
