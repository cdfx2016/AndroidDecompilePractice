package cn.smssdk.contact.a;

import cn.smssdk.utils.SMSLog;
import com.mob.tools.utils.Hashon;
import java.io.Serializable;
import java.util.HashMap;

/* compiled from: ContactObject */
public abstract class b implements Serializable {
    private static final HashMap<String, Class<? extends b>> a = new HashMap();
    private HashMap<String, Object> b;

    static {
        a.put("vnd.android.cursor.item/name", h.class);
        a.put("vnd.android.cursor.item/nickname", i.class);
        a.put("vnd.android.cursor.item/group_membership", e.class);
        a.put("vnd.android.cursor.item/organization", k.class);
        a.put("vnd.android.cursor.item/contact_event", d.class);
        a.put("vnd.android.cursor.item/photo", m.class);
        a.put("vnd.android.cursor.item/note", j.class);
        a.put("vnd.android.cursor.item/website", q.class);
        a.put("vnd.android.cursor.item/im", g.class);
        a.put("vnd.android.cursor.item/email_v2", c.class);
        a.put("vnd.android.cursor.item/phone_v2", l.class);
        a.put("vnd.android.cursor.item/postal-address_v2", n.class);
        a.put("vnd.android.cursor.item/relation", o.class);
        a.put("vnd.android.cursor.item/sip_address", p.class);
        a.put("vnd.android.cursor.item/identity", f.class);
    }

    public static b a(HashMap<String, Object> hashMap) {
        Class cls = (Class) a.get((String) hashMap.get("mimetype"));
        if (cls != null) {
            try {
                b bVar = (b) cls.newInstance();
                bVar.b((HashMap) hashMap);
                return bVar;
            } catch (Throwable th) {
                SMSLog.getInstance().w(th);
            }
        }
        return null;
    }

    protected void b(HashMap<String, Object> hashMap) {
        this.b = hashMap;
    }

    protected byte[] a(String str) {
        return (byte[]) this.b.get(str);
    }

    protected String b(String str) {
        return (String) this.b.get(str);
    }

    protected int a(String str, int i) {
        Object obj = this.b.get(str);
        if (obj instanceof Integer) {
            return ((Integer) obj).intValue();
        }
        try {
            return Integer.parseInt((String) obj);
        } catch (Throwable th) {
            SMSLog.getInstance().w(th);
            return i;
        }
    }

    public String toString() {
        return this.b == null ? "" : new Hashon().fromHashMap(this.b);
    }

    protected HashMap<String, Object> a() {
        return this.b;
    }
}
