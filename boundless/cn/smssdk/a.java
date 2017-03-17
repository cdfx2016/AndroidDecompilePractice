package cn.smssdk;

import android.content.Context;
import android.os.Handler.Callback;
import android.os.Message;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import cn.smssdk.SMSSDK.VerifyCodeReadListener;
import cn.smssdk.contact.OnContactChangeListener;
import cn.smssdk.contact.b;
import cn.smssdk.net.f;
import cn.smssdk.utils.SMSLog;
import cn.smssdk.utils.SPHelper;
import com.mob.tools.utils.DeviceHelper;
import com.mob.tools.utils.R;
import com.mob.tools.utils.ResHelper;
import com.xiaomi.mipush.sdk.Constants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import org.json.JSONObject;

/* compiled from: SMSSDKCore */
public class a implements OnContactChangeListener {
    private Context a;
    private HashSet<EventHandler> b = new HashSet();
    private f c;
    private b d;
    private cn.smssdk.a.a e;
    private cn.smssdk.b.a f;
    private String g;
    private HashMap<Character, ArrayList<String[]>> h;
    private HashMap<String, String> i;
    private ArrayList<HashMap<String, Object>> j;
    private String k;

    public a(Context context, String str, String str2) {
        this.a = context.getApplicationContext();
        this.k = str;
        SMSLog.prepare(this.a, 26, str);
        this.c = f.a(this.a);
        this.c.a(str, str2);
        this.e = cn.smssdk.a.a.a(this.a);
        this.d = b.a(this.a);
        this.f = cn.smssdk.b.a.a(this.a, str);
    }

    public void a() {
        this.d.a((OnContactChangeListener) this);
        this.d.a();
        new Thread(new Runnable(this) {
            final /* synthetic */ a a;

            {
                this.a = r1;
            }

            public void run() {
                this.a.c.c();
            }
        }).start();
    }

    public void a(EventHandler eventHandler) {
        synchronized (this.b) {
            if (eventHandler != null) {
                if (!this.b.contains(eventHandler)) {
                    this.b.add(eventHandler);
                    eventHandler.onRegister();
                }
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void b(cn.smssdk.EventHandler r3) {
        /*
        r2 = this;
        r1 = r2.b;
        monitor-enter(r1);
        if (r3 == 0) goto L_0x000d;
    L_0x0005:
        r0 = r2.b;	 Catch:{ all -> 0x0019 }
        r0 = r0.contains(r3);	 Catch:{ all -> 0x0019 }
        if (r0 != 0) goto L_0x000f;
    L_0x000d:
        monitor-exit(r1);	 Catch:{ all -> 0x0019 }
    L_0x000e:
        return;
    L_0x000f:
        r3.onUnregister();	 Catch:{ all -> 0x0019 }
        r0 = r2.b;	 Catch:{ all -> 0x0019 }
        r0.remove(r3);	 Catch:{ all -> 0x0019 }
        monitor-exit(r1);	 Catch:{ all -> 0x0019 }
        goto L_0x000e;
    L_0x0019:
        r0 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x0019 }
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: cn.smssdk.a.b(cn.smssdk.EventHandler):void");
    }

    public void b() {
        synchronized (this.b) {
            Iterator it = this.b.iterator();
            while (it.hasNext()) {
                ((EventHandler) it.next()).onUnregister();
            }
            this.b.clear();
        }
    }

    public void a(final int i, final Object obj) {
        new Thread(this) {
            final /* synthetic */ a c;

            public void run() {
                synchronized (this.c.b) {
                    Iterator it = this.c.b.iterator();
                    while (it.hasNext()) {
                        ((EventHandler) it.next()).beforeEvent(i, obj);
                    }
                }
                this.c.b(i, obj);
            }
        }.start();
    }

    private void b(int i, Object obj) {
        switch (i) {
            case 1:
                e();
                return;
            case 2:
                b(obj);
                return;
            case 3:
                c(obj);
                return;
            case 4:
                a(obj);
                return;
            case 5:
                d(obj);
                return;
            case 6:
                g();
                return;
            case 7:
                f();
                return;
            case 8:
                e(obj);
                return;
            default:
                return;
        }
    }

    private void a(int i, int i2, Object obj) {
        if (obj != null && (obj instanceof Throwable)) {
            Throwable th = (Throwable) obj;
            Object message = th.getMessage();
            if (!TextUtils.isEmpty(message)) {
                try {
                    JSONObject jSONObject = new JSONObject(message);
                    int optInt = jSONObject.optInt("status");
                    if (TextUtils.isEmpty(jSONObject.optString("detail")) && ((optInt > 400 && optInt <= 500) || optInt > 600)) {
                        int stringRes = R.getStringRes(this.a, "smssdk_error_desc_" + optInt);
                        if (stringRes > 0) {
                            String string = this.a.getResources().getString(stringRes);
                            JSONObject jSONObject2 = new JSONObject();
                            jSONObject2.put("status", optInt);
                            jSONObject2.put("detail", string);
                            obj = new Throwable(jSONObject2.toString(), th);
                        }
                    }
                } catch (Throwable th2) {
                    SMSLog.getInstance().w(th2);
                }
            }
            SMSLog.getInstance().w(th);
        }
        synchronized (this.b) {
            Iterator it = this.b.iterator();
            while (it.hasNext()) {
                ((EventHandler) it.next()).afterEvent(i, i2, obj);
            }
        }
    }

    private void a(Object obj) {
        Object a;
        int i = 0;
        try {
            a = this.d.a(((Boolean) obj).booleanValue());
            i = -1;
        } catch (Throwable th) {
            a = th;
        }
        a(4, i, a);
    }

    private void b(Object obj) {
        Object valueOf;
        int i;
        try {
            String substring;
            if (this.i == null || this.i.size() <= 0) {
                h();
            }
            Object[] objArr = (Object[]) obj;
            String str = (String) objArr[0];
            String str2 = (String) objArr[1];
            String str3 = (String) objArr[2];
            String str4 = (String) objArr[3];
            if (str.startsWith("+")) {
                substring = str.substring(1);
            } else {
                substring = str;
            }
            if (!a(str2, substring)) {
                SMSLog.getInstance().d("phone num error", new Object[0]);
            }
            OnSendMessageHandler onSendMessageHandler = (OnSendMessageHandler) objArr[4];
            if (onSendMessageHandler == null || !onSendMessageHandler.onSendMessage(substring, str2)) {
                valueOf = Boolean.valueOf(this.c.a(substring, str2, str3, str4));
                i = -1;
                a(2, i, valueOf);
                return;
            }
            throw new UserInterruptException();
        } catch (Throwable th) {
            valueOf = th;
            i = 0;
        }
    }

    private void c(Object obj) {
        int i = 0;
        Object b;
        try {
            if (this.i == null || this.i.size() <= 0) {
                h();
            }
            String[] strArr = (String[]) obj;
            String str = strArr[0];
            String str2 = strArr[1];
            String str3 = strArr[2];
            if (str.startsWith("+")) {
                str = str.substring(1);
            }
            if (a(str2, str)) {
                b = this.c.b(str3, str, str2);
                i = -1;
                a(3, i, b);
                return;
            }
            throw new Throwable("phone num error");
        } catch (Throwable th) {
            b = th;
        }
    }

    private void e() {
        Object h;
        int i = 0;
        try {
            h = h();
            i = -1;
        } catch (Throwable th) {
            h = th;
        }
        a(1, i, h);
    }

    private void d(Object obj) {
        int i;
        Object obj2;
        try {
            String[] strArr = (String[]) obj;
            this.e.a(strArr[0], strArr[1], strArr[2], strArr[3], strArr[4]);
            i = -1;
            obj2 = null;
        } catch (Throwable th) {
            Throwable th2 = th;
            i = 0;
        }
        a(5, i, obj2);
    }

    private void f() {
        this.e.a(new Callback(this) {
            final /* synthetic */ a a;

            {
                this.a = r1;
            }

            public boolean handleMessage(Message message) {
                int i;
                if (message.what == 1) {
                    i = -1;
                } else {
                    i = 0;
                }
                this.a.a(7, i, message.obj);
                return false;
            }
        });
    }

    private void g() {
        this.e.b(new Callback(this) {
            final /* synthetic */ a a;

            {
                this.a = r1;
            }

            public boolean handleMessage(Message message) {
                int i;
                if (message.what == 1) {
                    i = -1;
                } else {
                    i = 0;
                }
                this.a.a(6, i, message.obj);
                return false;
            }
        });
    }

    public void onContactChange(boolean z) {
        this.d.b(new Runnable(this) {
            final /* synthetic */ a a;

            {
                this.a = r1;
            }

            public void run() {
                this.a.e.a(0, new Callback(this) {
                    final /* synthetic */ AnonymousClass5 a;

                    {
                        this.a = r1;
                    }

                    public boolean handleMessage(Message message) {
                        if (message.arg1 > 0) {
                            this.a.a.a(7, -1, (Object) Integer.valueOf(message.arg1));
                        }
                        return false;
                    }
                });
            }
        }, null);
    }

    public HashMap<Character, ArrayList<String[]>> c() {
        String appLanguage = DeviceHelper.getInstance(this.a).getAppLanguage();
        SMSLog.getInstance().d("appLanguage:" + appLanguage, new Object[0]);
        if (!(appLanguage == null || appLanguage.equals(this.g))) {
            this.g = appLanguage;
            this.h = null;
        }
        if (this.h != null && this.h.size() > 0) {
            return this.h;
        }
        HashMap hashMap = null;
        for (char c = 'A'; c <= 'Z'; c = (char) (c + 1)) {
            int stringArrayRes = ResHelper.getStringArrayRes(this.a, "smssdk_country_group_" + Character.toLowerCase(c));
            if (stringArrayRes > 0) {
                Object obj;
                String[] stringArray = this.a.getResources().getStringArray(stringArrayRes);
                if (stringArray != null) {
                    obj = null;
                    for (String split : stringArray) {
                        Object split2 = split.split(Constants.ACCEPT_TIME_SEPARATOR_SP);
                        if (obj == null) {
                            obj = new ArrayList();
                        }
                        obj.add(split2);
                    }
                } else {
                    obj = null;
                }
                if (obj != null) {
                    if (hashMap == null) {
                        hashMap = new LinkedHashMap();
                    }
                    hashMap.put(Character.valueOf(c), obj);
                }
            }
        }
        this.h = hashMap;
        return this.h;
    }

    public String[] a(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        for (Entry value : c().entrySet()) {
            ArrayList arrayList = (ArrayList) value.getValue();
            int size = arrayList == null ? 0 : arrayList.size();
            for (int i = 0; i < size; i++) {
                String[] strArr = (String[]) arrayList.get(i);
                if (strArr != null && strArr.length > 2 && str.equals(strArr[2])) {
                    return strArr;
                }
            }
        }
        return null;
    }

    public String[] b(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        for (Entry value : c().entrySet()) {
            ArrayList arrayList = (ArrayList) value.getValue();
            int size = arrayList == null ? 0 : arrayList.size();
            for (int i = 0; i < size; i++) {
                String[] strArr = (String[]) arrayList.get(i);
                if (strArr.length < 4) {
                    SMSLog.getInstance().d("MCC not found in the country: " + strArr[0], new Object[0]);
                } else {
                    String str2 = strArr[3];
                    if (str2.indexOf("|") >= 0) {
                        for (Object equals : str2.split("\\|")) {
                            if (str.equals(equals)) {
                                return strArr;
                            }
                        }
                    } else if (str.equals(str2)) {
                        return strArr;
                    }
                }
            }
        }
        return null;
    }

    public void a(SmsMessage smsMessage, VerifyCodeReadListener verifyCodeReadListener) {
        this.f.a(verifyCodeReadListener);
        this.f.a(smsMessage);
    }

    private void e(Object obj) {
        int i;
        Object obj2;
        Object[] objArr = (Object[]) obj;
        String str = (String) objArr[0];
        String str2 = (String) objArr[1];
        String str3 = (String) objArr[2];
        if (str2.startsWith("+")) {
            str2 = str2.substring(1);
        }
        try {
            if (this.i == null || this.i.size() <= 0) {
                h();
            }
            if (!a(str, str2)) {
                SMSLog.getInstance().d("phone num error", new Object[0]);
            }
            this.c.a(str, str2, str3);
            i = -1;
            obj2 = null;
        } catch (Throwable th) {
            Throwable th2 = th;
            i = 0;
        }
        a(8, i, obj2);
    }

    public void d() {
        SPHelper.getInstance(this.a).setWarnWhenReadContact(true);
    }

    private boolean a(String str, String str2) throws Throwable {
        int stringRes;
        if (TextUtils.isEmpty(str)) {
            stringRes = R.getStringRes(this.a, "smssdk_error_desc_603");
            if (stringRes <= 0) {
                return false;
            }
            throw new Throwable("{\"status\":603,\"detail\":\"" + this.a.getResources().getString(stringRes) + "\"}");
        } else if (this.i == null || this.i.size() <= 0) {
            if (str2 != "86") {
                stringRes = R.getStringRes(this.a, "smssdk_error_desc_604");
                if (stringRes > 0) {
                    throw new Throwable("{\"status\":604,\"detail\":\"" + this.a.getResources().getString(stringRes) + "\"}");
                }
            } else if (str.length() != 11) {
                stringRes = R.getStringRes(this.a, "smssdk_error_desc_603");
                if (stringRes > 0) {
                    throw new Throwable("{\"status\":603,\"detail\":\"" + this.a.getResources().getString(stringRes) + "\"}");
                }
            }
            return false;
        } else {
            String str3 = (String) this.i.get(str2);
            if (TextUtils.isEmpty(str3)) {
                int stringRes2 = R.getStringRes(this.a, "smssdk_error_desc_604");
                if (stringRes2 > 0) {
                    throw new Throwable("{\"status\":604,\"detail\":\"" + this.a.getResources().getString(stringRes2) + "\"}");
                }
            }
            if (Pattern.compile(str3).matcher(str).matches()) {
                return true;
            }
            stringRes = R.getStringRes(this.a, "smssdk_error_desc_603");
            if (stringRes <= 0) {
                return false;
            }
            throw new Throwable("{\"status\":603,\"detail\":\"" + this.a.getResources().getString(stringRes) + "\"}");
        }
    }

    private ArrayList<HashMap<String, Object>> h() throws Throwable {
        if (this.j == null || this.c.a()) {
            this.j = this.c.b();
        }
        Iterator it = this.j.iterator();
        while (it.hasNext()) {
            HashMap hashMap = (HashMap) it.next();
            String str = (String) hashMap.get("zone");
            String str2 = (String) hashMap.get("rule");
            if (!(TextUtils.isEmpty(str) || TextUtils.isEmpty(str2))) {
                if (this.i == null) {
                    this.i = new HashMap();
                }
                this.i.put(str, str2);
            }
        }
        return this.j;
    }
}
