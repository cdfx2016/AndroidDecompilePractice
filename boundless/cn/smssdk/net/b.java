package cn.smssdk.net;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import cn.finalteam.toolsfinal.io.FilenameUtils;
import cn.smssdk.utils.SMSLog;
import cn.smssdk.utils.SPHelper;
import com.mob.commons.SMSSDK;
import com.mob.commons.appcollector.PackageCollector;
import com.mob.commons.appcollector.RuntimeCollector;
import com.mob.commons.authorize.DeviceAuthorizer;
import com.mob.commons.deviceinfo.DeviceInfoCollector;
import com.mob.commons.eventrecoder.EventRecorder;
import com.mob.commons.iosbridge.UDPServer;
import com.mob.tools.utils.Data;
import com.mob.tools.utils.DeviceHelper;
import com.mob.tools.utils.Hashon;
import com.mob.tools.utils.ResHelper;
import com.xiaomi.mipush.sdk.Constants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.packet.DiscoverItems.Item;

/* compiled from: Config */
public class b {
    private static b D;
    private static final ArrayList<String> a = new ArrayList(3);
    private String A;
    private String B;
    private String C;
    private String b;
    private String c;
    private Context d;
    private SPHelper e;
    private Hashon f;
    private SparseArray<g> g;
    private final d h = new d();
    private ReentrantLock i;
    private ReentrantReadWriteLock j;
    private Condition k;
    private a l;
    private boolean m;
    private boolean n;
    private boolean o;
    private int p;
    private long q;
    private int r = 1;
    private int s;
    private int t = 1;
    private int u = 1;
    private int v = 1;
    private int w = 1;
    private String x;
    private String y;
    private int z;

    /* compiled from: Config */
    class a extends Thread {
        final /* synthetic */ b a;
        private boolean b = false;
        private boolean c = true;
        private boolean d = true;
        private long e;

        a(b bVar) {
            this.a = bVar;
        }

        public void a() {
            this.e = (long) (this.a.p * 1000);
        }

        public void a(boolean z) {
            this.b = z;
        }

        public boolean b() {
            return this.d;
        }

        public void c() {
            this.d = false;
        }

        public void b(boolean z) {
            this.c = z;
        }

        public void run() {
            while (true) {
                if (this.c || this.e <= 0) {
                    try {
                        d();
                    } catch (Throwable th) {
                        this.d = true;
                        if (this.a.j.writeLock().tryLock()) {
                            this.c = false;
                            this.a.m = false;
                            this.a.k.signalAll();
                            this.a.j.writeLock().unlock();
                        }
                    }
                }
                try {
                    sleep(100);
                    this.e -= 100;
                } catch (InterruptedException e) {
                }
            }
        }

        private void d() throws Throwable {
            EventRecorder.addBegin("SMSSDK", "getConfig");
            CharSequence b = this.a.a(false);
            if (TextUtils.isEmpty(b)) {
                throw new Throwable("duid is empty!");
            }
            if (this.b) {
                c.b();
            }
            HashMap hashMap = new HashMap();
            hashMap.put("appkey", this.a.b);
            hashMap.put("appsecret", this.a.c);
            hashMap.put("duid", b);
            hashMap.put("sdkver", "2.1.3");
            hashMap.put("plat", Integer.valueOf(1));
            HashMap a = this.a.a(this.a.h, hashMap, false, false, 1);
            if (a == null) {
                throw new Throwable("response is empty");
            }
            try {
                this.a.j.writeLock().lock();
                this.a.a(a);
                this.d = false;
            } catch (Throwable th) {
                this.a.j.writeLock().unlock();
            }
            if (!this.d) {
                this.a.e.setConfig(this.a.f.fromHashMap(a));
            }
            this.c = false;
            this.a.m = false;
            if (this.b) {
                this.b = false;
                this.a.k.signalAll();
            }
            this.a.j.writeLock().unlock();
            EventRecorder.addEnd("SMSSDK", "getConfig");
        }
    }

    public static b a(Context context) {
        if (D == null) {
            synchronized (b.class) {
                D = new b(context);
            }
        }
        return D;
    }

    private b(Context context) {
        this.d = context;
        this.n = false;
        this.e = SPHelper.getInstance(context);
        this.f = new Hashon();
        a.add("852");
        a.add("853");
        a.add("886");
        this.i = new ReentrantLock();
        this.j = new ReentrantReadWriteLock();
        this.k = this.j.writeLock().newCondition();
    }

    public void a(String str, String str2) {
        this.b = str;
        this.c = str2;
    }

    public boolean a(String str) throws Throwable {
        d();
        if (this.t == 0 || ((this.u == 0 && str.equals("86")) || ((this.v == 0 && a.contains(str)) || (this.w == 0 && !a.contains(str) && !str.equals("86"))))) {
            return false;
        }
        return true;
    }

    public boolean a() {
        return this.o;
    }

    public void b() {
        this.o = false;
    }

    private void a(HashMap<String, Object> hashMap) throws Throwable {
        int i = 1;
        long longValue = ((Long) hashMap.get(Item.UPDATE_ACTION)).longValue();
        if (longValue != this.q) {
            this.q = longValue;
            this.p = ((Integer) hashMap.get("expire_at")).intValue();
            if (this.p > 0) {
                this.l.a();
            }
            int intValue = ((Integer) hashMap.get("zonelist_update")).intValue();
            if (intValue > this.s) {
                this.s = intValue;
                this.o = true;
            }
            this.r = ((Integer) hashMap.get("request")).intValue();
            Integer num = (Integer) hashMap.get("sms_toggle");
            this.t = num != null ? num.intValue() : 1;
            num = (Integer) hashMap.get("sms_home");
            if (num != null) {
                intValue = num.intValue();
            } else {
                intValue = 1;
            }
            this.u = intValue;
            num = (Integer) hashMap.get("sms_sp_region");
            if (num != null) {
                intValue = num.intValue();
            } else {
                intValue = 1;
            }
            this.v = intValue;
            num = (Integer) hashMap.get("sms_foreign");
            if (num != null) {
                i = num.intValue();
            }
            this.w = i;
            this.x = (String) hashMap.get("public_key");
            this.y = (String) hashMap.get("modulus");
            num = (Integer) hashMap.get("size");
            this.z = num != null ? num.intValue() : 0;
            if (!(TextUtils.isEmpty(this.x) || TextUtils.isEmpty(this.y) || this.z <= 0)) {
                c.a(this.x, this.y, this.z);
            }
            ArrayList arrayList = (ArrayList) ((HashMap) hashMap.get(Form.TYPE_RESULT)).get("urls");
            if (this.g == null) {
                this.g = new SparseArray();
            } else if (this.g != null && this.g.size() > 0) {
                this.g.clear();
            }
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                HashMap hashMap2 = (HashMap) it.next();
                g gVar = new g(this.d);
                gVar.a(hashMap2);
                gVar.a(this.j);
                this.g.put(gVar.a(), gVar);
            }
        }
    }

    private g a(int i) throws Throwable {
        if (this.b != null && this.b.equalsIgnoreCase("f3fc6baa9ac4")) {
            String valueOf;
            if ("zh".equals(DeviceHelper.getInstance(this.d).getOSLanguage())) {
                valueOf = String.valueOf(new char[]{'所', '填', '写', 'A', 'P', 'P', 'K', 'E', 'Y', '仅', '供', '测', '试', '使', '用', '，', '且', '不', '定', '期', '失', '效', '，', '请', '到', 'm', 'o', 'b', FilenameUtils.EXTENSION_SEPARATOR, 'c', 'o', 'm', '后', '台', '申', '请', '正', '式', 'A', 'P', 'P', 'K', 'E', 'Y'});
            } else {
                valueOf = "This appkey only for demo!Please request a new one for your own App";
            }
            Log.e("SMSSDK WARNING", valueOf);
        }
        d();
        if (this.r != 0) {
            return (g) this.g.get(i);
        }
        throw new Throwable("{\"status\":604,\"detail\":\"" + this.d.getResources().getString(ResHelper.getStringRes(this.d, "smssdk_error_desc_604")) + "\"}");
    }

    private void d() throws Throwable {
        try {
            this.j.writeLock().lock();
            if (this.n) {
                this.j.writeLock().unlock();
                return;
            }
            EventRecorder.prepare(this.d);
            CharSequence checkRecord = EventRecorder.checkRecord("SMSSDK");
            if (checkRecord != null) {
                EventRecorder.clear();
            }
            String config = this.e.getConfig();
            this.l = new a(this);
            EventRecorder.addBegin("SMSSDK", "parseConfig");
            if (!TextUtils.isEmpty(checkRecord) || TextUtils.isEmpty(config)) {
                a(this.f.fromJson("{\"status\":200,\"expire_at\":86400,\"update\":1466077916207,\"zonelist_update\":20151129,\"request\":1,\"sms_toggle\":1,\"sms_home\":1,\"sms_sp_region\":1,\"sms_foreign\":1,\"result\":{\"urls\":[{\"name\":\"uploadCollectData\",\"url\":\"http://upc1299.uz.local:8082/relat/seda\",\"params\":[\"appKey\",\"duid\",\"sdkver\",\"my_phone\",\"collectTime\",\"data\"],\"params_chunk\":\"\",\"encode\":\"RSA\",\"zip\":0,\"request\":1,\"frequency\":\"\"},{\"name\":\"sendTextSMS\",\"url\":\"http://code.sms.mob.com/verify/code\",\"params\":[\"appkey\",\"duid\",\"zone\",\"phone\",\"simserial\",\"my_phone\"],\"params_chunk\":\"\",\"encode\":\"RSA\",\"zip\":0,\"request\":1,\"frequency\":\"\"},{\"name\":\"submitUser\",\"url\":\"http://sdkapi.sms.mob.com/app/submituserinfo\",\"params\":[],\"params_chunk\":\"user_info_001\",\"encode\":\"RSA\",\"zip\":0,\"request\":1,\"frequency\":\"\"},{\"name\":\"logCollect\",\"url\":\"http://log.sms.mob.com/log/collect\",\"params\":[],\"params_chunk\":\"collect_001\",\"encode\":\"AES\",\"zip\":1,\"request\":1,\"frequency\":\"1:2:m\"},{\"name\":\"verifyCode\",\"url\":\"http://code.sms.mob.com/client/verification\",\"params\":[\"zone\",\"phone\",\"code\",\"appkey\",\"duid\"],\"params_chunk\":\"\",\"encode\":\"RSA\",\"zip\":0,\"request\":1,\"frequency\":\"\"},{\"name\":\"sendVoiceSMS\",\"url\":\"http://code.sms.mob.com/voice/verify/code\",\"params\":[\"zone\",\"phone\",\"appkey\",\"duid\"],\"params_chunk\":\"\",\"encode\":\"RSA\",\"zip\":0,\"request\":1,\"frequency\":\"\"},{\"name\":\"getFriend\",\"url\":\"http://addrlist.sms.mob.com/relat/fm\",\"params\":[\"appkey\",\"duid\",\"contactphones\",\"plat\",\"sdkver\"],\"params_chunk\":\"\",\"encode\":\"RSA\",\"zip\":1,\"request\":1,\"frequency\":\"\"},{\"name\":\"uploadContacts\",\"url\":\"http://addrlist.sms.mob.com/relat/apply\",\"params\":[],\"params_chunk\":\"contacts_002\",\"encode\":\"RSA\",\"zip\":1,\"request\":1,\"frequency\":\"\"},{\"name\":\"getZoneList\",\"url\":\"http://sdkapi.sms.mob.com/utils/zonelist\",\"params\":[\"plat\",\"sdkver\",\"token\",\"appkey\",\"duid\"],\"params_chunk\":\"\",\"encode\":\"RSA\",\"zip\":0,\"request\":1,\"frequency\":\"\"},{\"name\":\"logInstall\",\"url\":\"http://log.sms.mob.com/log/install\",\"params\":[],\"params_chunk\":\"install_002\",\"encode\":\"AES\",\"zip\":1,\"request\":1,\"frequency\":\"\"},{\"name\":\"getToken\",\"url\":\"http://sdkapi.sms.mob.com/token/get\",\"params\":[\"appkey\",\"duid\",\"sdkver\",\"plat\",\"aesKey\",\"sign\"],\"params_chunk\":\"\",\"encode\":\"RSA\",\"zip\":0,\"request\":1,\"frequency\":\"\"}]}}"));
            } else {
                a(this.f.fromJson(config));
            }
            String aeskey = this.e.getAeskey();
            if (!TextUtils.isEmpty(aeskey)) {
                c.a(aeskey);
            }
            this.n = true;
            this.l.start();
            EventRecorder.addEnd("SMSSDK", "parseConfig");
            this.j.writeLock().unlock();
        } catch (Throwable th) {
            this.j.writeLock().unlock();
        }
    }

    private String a(boolean z) {
        if (z || TextUtils.isEmpty(this.A)) {
            try {
                this.i.lock();
                if (TextUtils.isEmpty(this.A)) {
                    this.A = DeviceAuthorizer.authorize(this.d, new SMSSDK());
                    this.i.unlock();
                } else {
                    String str = this.A;
                    return str;
                }
            } finally {
                this.i.unlock();
            }
        }
        return this.A;
    }

    private synchronized String b(boolean z) throws Throwable {
        String str;
        this.B = this.e.getToken();
        if (z || TextUtils.isEmpty(this.B) || !c.a()) {
            HashMap hashMap = new HashMap();
            hashMap.put("aesKey", d(a(false)));
            hashMap.put("sign", e());
            this.B = (String) ((HashMap) a(3, hashMap).get(Form.TYPE_RESULT)).get(Constants.EXTRA_KEY_TOKEN);
            if (TextUtils.isEmpty(this.B)) {
                throw new Throwable("get token error!");
            }
            this.e.setToken(this.B);
            str = this.B;
        } else {
            str = this.B;
        }
        return str;
    }

    public HashMap<String, Object> a(int i, HashMap<String, Object> hashMap) throws Throwable {
        a a = a(i);
        HashMap<String, Object> a2 = a(a, hashMap, false, false, 1);
        if (a.a() != 9 || a2 == null) {
            if (a2 != null) {
                a.c();
            }
        } else if (((Integer) a2.get("smart")) == null) {
            a.c();
        }
        return a2;
    }

    private HashMap<String, Object> a(a aVar, HashMap<String, Object> hashMap, boolean z, boolean z2, int i) throws Throwable {
        HashMap<String, Object> hashMap2 = null;
        Object obj;
        if (i > 5) {
            obj = "Server is busy!";
            int stringRes = ResHelper.getStringRes(this.d, "smssdk_error_desc_server_busy");
            if (stringRes > 0) {
                obj = this.d.getString(stringRes);
            }
            HashMap hashMap3 = new HashMap();
            hashMap3.put("description", obj);
            throw new Throwable(this.f.fromHashMap(hashMap3));
        }
        String a = a(z);
        if (!(aVar instanceof g) || aVar.a() == 3) {
            obj = hashMap2;
        } else {
            String b = b(z2);
        }
        try {
            a = aVar.b(a, b, hashMap);
            try {
                hashMap2 = this.f.fromJson(a);
            } catch (Throwable th) {
                SMSLog.getInstance().e(th);
            }
            if (hashMap2 == null || hashMap2.size() <= 0) {
                throw new Throwable("[hashon]Response is empty");
            }
            obj = hashMap2.get("status");
            if (obj == null || !(obj instanceof Integer)) {
                throw new Throwable(a);
            }
            int intValue = ((Integer) obj).intValue();
            return intValue != 200 ? a(intValue, aVar, (HashMap) hashMap, i) : hashMap2;
        } catch (Throwable th2) {
            return a(th2, aVar, (HashMap) hashMap, i);
        }
    }

    private HashMap<String, Object> a(Throwable th, a aVar, HashMap<String, Object> hashMap, int i) throws Throwable {
        HashMap fromJson = this.f.fromJson(th.getMessage());
        Integer num = (Integer) fromJson.get("status");
        if (num == null || num.intValue() == 0) {
            throw th;
        }
        HashMap<String, Object> a = a(num.intValue(), aVar, (HashMap) hashMap, i);
        if (a != null) {
            return a;
        }
        fromJson.put("description", b(num.intValue()));
        fromJson.put("detail", c(num.intValue()));
        throw new Throwable(this.f.fromHashMap(fromJson));
    }

    private HashMap<String, Object> a(int i, a aVar, HashMap<String, Object> hashMap, int i2) throws Throwable {
        int i3 = i2 + 1;
        if (i == 453) {
            a aVar2;
            if (aVar instanceof g) {
                int a = aVar.a();
                try {
                    this.j.writeLock().lock();
                    this.l.b(true);
                    this.l.a(true);
                    this.m = true;
                    while (this.m) {
                        this.k.await();
                    }
                    this.j.writeLock().unlock();
                    if (this.l.b()) {
                        this.l.a(false);
                        c.b();
                        this.l.c();
                        return a(aVar, hashMap, false, false, i3);
                    }
                    if (a > 0) {
                        aVar = a(a);
                    }
                    aVar2 = aVar;
                } catch (Throwable th) {
                    this.l.a(false);
                    c.b();
                    return a(aVar, hashMap, false, false, i3);
                }
            }
            c.b();
            aVar2 = aVar;
            return a(aVar2, hashMap, false, false, i3);
        } else if (i == 419 || i == 420) {
            this.e.setToken("");
            return a(aVar, hashMap, true, true, i3);
        } else if (i == 401 || i == 402) {
            this.e.setToken("");
            return a(aVar, hashMap, false, true, i3);
        } else if (i == 403 || i == 404 || i == 454) {
            return a(aVar, hashMap, false, false, i3);
        } else {
            if (i == 480) {
                hashMap.put("aesKey", d(a(false)));
                return a(aVar, hashMap, false, true, i3);
            }
            throw new Throwable("{status:'" + i + "'}");
        }
    }

    private String b(int i) {
        try {
            int stringRes = ResHelper.getStringRes(this.d, "smssdk_error_desc_" + i);
            if (stringRes > 0) {
                return this.d.getString(stringRes);
            }
        } catch (Throwable th) {
            SMSLog.getInstance().w(th);
        }
        return null;
    }

    private String c(int i) {
        try {
            int stringRes = ResHelper.getStringRes(this.d, "smssdk_error_detail_" + i);
            if (stringRes > 0) {
                return this.d.getString(stringRes);
            }
        } catch (Throwable th) {
            SMSLog.getInstance().w(th);
        }
        return null;
    }

    private String d(String str) throws Throwable {
        String MD5 = Data.MD5(str + System.currentTimeMillis());
        c.a(MD5);
        this.e.setAeskey(MD5);
        return MD5;
    }

    private String e() {
        if (!TextUtils.isEmpty(this.C)) {
            return this.C;
        }
        try {
            this.C = Data.MD5(this.d.getPackageManager().getPackageInfo(this.d.getPackageName(), 64).signatures[0].toByteArray());
            return this.C;
        } catch (NameNotFoundException e) {
            return null;
        }
    }

    public String b(String str) {
        if (!c.a()) {
            try {
                b(true);
            } catch (Throwable th) {
                return null;
            }
        }
        return Data.byteToHex(c.b(str));
    }

    public String c(String str) {
        Object a = e.a().a(str);
        if (!TextUtils.isEmpty(a)) {
            return a;
        }
        if (!c.a()) {
            try {
                b(true);
            } catch (Throwable th) {
                return null;
            }
        }
        return new String(c.a(c.e(str))).trim();
    }

    public void c() {
        a(false);
        new Thread(new Runnable(this) {
            final /* synthetic */ b a;

            {
                this.a = r1;
            }

            public void run() {
                DeviceInfoCollector.startCollector(this.a.d);
                PackageCollector.startCollector(this.a.d);
                RuntimeCollector.startCollector(this.a.d);
                UDPServer.start(this.a.d);
            }
        }).start();
        new Thread(new Runnable(this) {
            final /* synthetic */ b a;

            {
                this.a = r1;
            }

            public void run() {
                RuntimeCollector.startCollector(this.a.d);
            }
        }).start();
        new Thread(new Runnable(this) {
            final /* synthetic */ b a;

            {
                this.a = r1;
            }

            public void run() {
                UDPServer.start(this.a.d);
            }
        }).start();
        new Thread(new Runnable(this) {
            final /* synthetic */ b a;

            {
                this.a = r1;
            }

            public void run() {
                DeviceInfoCollector.startCollector(this.a.d);
            }
        }).start();
    }
}
