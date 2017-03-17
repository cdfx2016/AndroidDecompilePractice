package com.xiaomi.push.service;

import com.xiaomi.channel.commonutils.logger.b;
import com.xiaomi.push.service.XMPushService.h;
import com.xiaomi.xmpush.thrift.a;
import com.xiaomi.xmpush.thrift.ae;
import com.xiaomi.xmpush.thrift.aq;
import java.util.ArrayList;
import java.util.Iterator;

class bj extends h {
    final /* synthetic */ ArrayList b;
    final /* synthetic */ bi c;

    bj(bi biVar, int i, ArrayList arrayList) {
        this.c = biVar;
        this.b = arrayList;
        super(i);
    }

    public void a() {
        String packageName = this.c.a.getPackageName();
        String a = this.c.a(packageName);
        ArrayList a2 = av.a(this.b, packageName, a);
        if (a2 != null) {
            Iterator it = a2.iterator();
            while (it.hasNext()) {
                ae aeVar = (ae) it.next();
                aeVar.a("uploadWay", "longXMPushService");
                this.c.a.a(packageName, aq.a(aa.a(packageName, a, aeVar, a.Notification)), true);
            }
            return;
        }
        b.d("Get a null XmPushActionNotification when TinyDataHelper.transToTriftObj() in XMPushService.");
    }

    public String b() {
        return "Send tiny data.";
    }
}
