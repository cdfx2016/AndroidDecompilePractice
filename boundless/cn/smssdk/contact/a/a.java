package cn.smssdk.contact.a;

import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.Groups;
import cn.smssdk.contact.c;
import cn.smssdk.utils.SMSLog;
import com.fanyu.boundless.config.Preferences;
import com.mob.tools.utils.Hashon;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/* compiled from: Contact */
public class a implements Serializable {
    private String a;
    private h b;
    private i c;
    private e d;
    private k e;
    private ArrayList<d> f;
    private m g;
    private j h;
    private ArrayList<q> i;
    private ArrayList<g> j;
    private ArrayList<c> k;
    private ArrayList<l> l;
    private ArrayList<n> m;
    private ArrayList<p> n;
    private ArrayList<o> o;
    private f p;

    public a(String str) {
        try {
            HashMap fromJson = new Hashon().fromJson(str);
            if (fromJson != null) {
                Iterator it;
                HashMap hashMap = (HashMap) fromJson.get(Preferences.sbry);
                if (hashMap != null) {
                    this.b = (h) b.a(hashMap);
                }
                hashMap = (HashMap) fromJson.get(Preferences.NICKNAME);
                if (hashMap != null) {
                    this.c = (i) b.a(hashMap);
                }
                hashMap = (HashMap) fromJson.get("group");
                if (hashMap != null) {
                    this.d = (e) b.a(hashMap);
                }
                hashMap = (HashMap) fromJson.get("organization");
                if (hashMap != null) {
                    this.e = (k) b.a(hashMap);
                }
                ArrayList arrayList = (ArrayList) fromJson.get("event");
                if (arrayList != null) {
                    if (this.f == null) {
                        this.f = new ArrayList();
                    }
                    it = arrayList.iterator();
                    while (it.hasNext()) {
                        this.f.add((d) b.a((HashMap) it.next()));
                    }
                }
                hashMap = (HashMap) fromJson.get("photo");
                if (hashMap != null) {
                    this.g = (m) b.a(hashMap);
                }
                hashMap = (HashMap) fromJson.get("note");
                if (hashMap != null) {
                    this.h = (j) b.a(hashMap);
                }
                arrayList = (ArrayList) fromJson.get("websites");
                if (arrayList != null) {
                    if (this.i == null) {
                        this.i = new ArrayList();
                    }
                    it = arrayList.iterator();
                    while (it.hasNext()) {
                        this.i.add((q) b.a((HashMap) it.next()));
                    }
                }
                arrayList = (ArrayList) fromJson.get("ims");
                if (arrayList != null) {
                    if (this.j == null) {
                        this.j = new ArrayList();
                    }
                    it = arrayList.iterator();
                    while (it.hasNext()) {
                        this.j.add((g) b.a((HashMap) it.next()));
                    }
                }
                arrayList = (ArrayList) fromJson.get("emails");
                if (arrayList != null) {
                    if (this.k == null) {
                        this.k = new ArrayList();
                    }
                    it = arrayList.iterator();
                    while (it.hasNext()) {
                        this.k.add((c) b.a((HashMap) it.next()));
                    }
                }
                arrayList = (ArrayList) fromJson.get("phones");
                if (arrayList != null) {
                    if (this.l == null) {
                        this.l = new ArrayList();
                    }
                    it = arrayList.iterator();
                    while (it.hasNext()) {
                        this.l.add((l) b.a((HashMap) it.next()));
                    }
                }
                arrayList = (ArrayList) fromJson.get("postals");
                if (arrayList != null) {
                    if (this.m == null) {
                        this.m = new ArrayList();
                    }
                    it = arrayList.iterator();
                    while (it.hasNext()) {
                        this.m.add((n) b.a((HashMap) it.next()));
                    }
                }
                arrayList = (ArrayList) fromJson.get("sipAddresses");
                if (arrayList != null) {
                    if (this.n == null) {
                        this.n = new ArrayList();
                    }
                    it = arrayList.iterator();
                    while (it.hasNext()) {
                        this.n.add((p) b.a((HashMap) it.next()));
                    }
                }
                arrayList = (ArrayList) fromJson.get("relations");
                if (arrayList != null) {
                    if (this.o == null) {
                        this.o = new ArrayList();
                    }
                    it = arrayList.iterator();
                    while (it.hasNext()) {
                        this.o.add((o) b.a((HashMap) it.next()));
                    }
                }
                hashMap = (HashMap) fromJson.get("identity");
                if (hashMap != null) {
                    this.p = (f) b.a(hashMap);
                }
            }
        } catch (Throwable th) {
            SMSLog.getInstance().w(th);
        }
    }

    public a(c cVar, String str) {
        this.a = str;
        a(cVar);
    }

    private void a(c cVar) {
        if (this.a != null) {
            String str = "raw_contact_id=" + this.a;
            ArrayList a = cVar.a(Data.CONTENT_URI, null, str, null, null);
            if (a != null) {
                Iterator it = a.iterator();
                while (it.hasNext()) {
                    HashMap hashMap = (HashMap) it.next();
                    b a2 = b.a(hashMap);
                    if (a2 != null) {
                        if (a2 instanceof h) {
                            this.b = (h) a2;
                        } else if (a2 instanceof i) {
                            this.c = (i) a2;
                        } else if (a2 instanceof e) {
                            str = "_id=" + hashMap.get("data1");
                            a = cVar.a(Groups.CONTENT_URI, null, str, null, null);
                            if (a != null && a.size() > 0) {
                                hashMap = (HashMap) a.get(0);
                                hashMap.put("mimetype", "vnd.android.cursor.item/group_membership");
                                this.d = (e) b.a(hashMap);
                            }
                        } else if (a2 instanceof k) {
                            this.e = (k) a2;
                        } else if (a2 instanceof d) {
                            if (this.f == null) {
                                this.f = new ArrayList();
                            }
                            this.f.add((d) a2);
                        } else if (a2 instanceof m) {
                            this.g = (m) a2;
                        } else if (a2 instanceof j) {
                            this.h = (j) a2;
                        } else if (a2 instanceof q) {
                            if (this.i == null) {
                                this.i = new ArrayList();
                            }
                            this.i.add((q) a2);
                        } else if (a2 instanceof g) {
                            if (this.j == null) {
                                this.j = new ArrayList();
                            }
                            this.j.add((g) a2);
                        } else if (a2 instanceof c) {
                            if (this.k == null) {
                                this.k = new ArrayList();
                            }
                            this.k.add((c) a2);
                        } else if (a2 instanceof l) {
                            if (this.l == null) {
                                this.l = new ArrayList();
                            }
                            this.l.add((l) a2);
                        } else if (a2 instanceof n) {
                            if (this.m == null) {
                                this.m = new ArrayList();
                            }
                            this.m.add((n) a2);
                        } else if (a2 instanceof o) {
                            if (this.m == null) {
                                this.o = new ArrayList();
                            }
                            this.o.add((o) a2);
                        } else if (a2 instanceof p) {
                            if (this.n == null) {
                                this.n = new ArrayList();
                            }
                            this.n.add((p) a2);
                        } else if (a2 instanceof f) {
                            this.p = (f) a2;
                        }
                    }
                }
            }
        }
    }

    public h a() {
        return this.b;
    }

    public i b() {
        return this.c;
    }

    public e c() {
        return this.d;
    }

    public k d() {
        return this.e;
    }

    public m e() {
        return this.g;
    }

    public j f() {
        return this.h;
    }

    public ArrayList<q> g() {
        return this.i;
    }

    public ArrayList<g> h() {
        return this.j;
    }

    public ArrayList<c> i() {
        return this.k;
    }

    public ArrayList<l> j() {
        return this.l;
    }

    public ArrayList<n> k() {
        return this.m;
    }

    public ArrayList<d> l() {
        return this.f;
    }

    public ArrayList<o> m() {
        return this.o;
    }

    public String toString() {
        return new Hashon().fromHashMap(n());
    }

    public HashMap<String, Object> n() {
        ArrayList arrayList;
        Iterator it;
        HashMap<String, Object> hashMap = new HashMap();
        if (this.b != null) {
            hashMap.put(Preferences.sbry, this.b.a());
        }
        if (this.c != null) {
            hashMap.put(Preferences.NICKNAME, this.c.a());
        }
        if (this.d != null) {
            hashMap.put("group", this.d.a());
        }
        if (this.e != null) {
            hashMap.put("organization", this.e.a());
        }
        if (this.f != null) {
            arrayList = new ArrayList();
            it = this.f.iterator();
            while (it.hasNext()) {
                arrayList.add(((d) it.next()).a());
            }
            hashMap.put("events", arrayList);
        }
        if (this.g != null) {
            hashMap.put("photo", this.g.a());
        }
        if (this.h != null) {
            hashMap.put("note", this.h.a());
        }
        if (this.i != null) {
            arrayList = new ArrayList();
            it = this.i.iterator();
            while (it.hasNext()) {
                arrayList.add(((q) it.next()).a());
            }
            hashMap.put("websites", arrayList);
        }
        if (this.j != null) {
            arrayList = new ArrayList();
            it = this.j.iterator();
            while (it.hasNext()) {
                arrayList.add(((g) it.next()).a());
            }
            hashMap.put("ims", arrayList);
        }
        if (this.k != null) {
            arrayList = new ArrayList();
            it = this.k.iterator();
            while (it.hasNext()) {
                arrayList.add(((c) it.next()).a());
            }
            hashMap.put("emails", arrayList);
        }
        if (this.l != null) {
            arrayList = new ArrayList();
            it = this.l.iterator();
            while (it.hasNext()) {
                arrayList.add(((l) it.next()).a());
            }
            hashMap.put("phones", arrayList);
        }
        if (this.m != null) {
            arrayList = new ArrayList();
            it = this.m.iterator();
            while (it.hasNext()) {
                arrayList.add(((n) it.next()).a());
            }
            hashMap.put("postals", arrayList);
        }
        if (this.n != null) {
            arrayList = new ArrayList();
            it = this.n.iterator();
            while (it.hasNext()) {
                arrayList.add(((p) it.next()).a());
            }
            hashMap.put("sipAddresses", arrayList);
        }
        if (this.o != null) {
            arrayList = new ArrayList();
            it = this.o.iterator();
            while (it.hasNext()) {
                arrayList.add(((o) it.next()).a());
            }
            hashMap.put("relations", arrayList);
        }
        if (this.p != null) {
            hashMap.put("identity", this.p.a());
        }
        return hashMap;
    }
}
