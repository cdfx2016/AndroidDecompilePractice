package cn.smssdk.contact;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.RawContacts;
import android.text.TextUtils;
import android.widget.Toast;
import cn.smssdk.contact.a.a;
import cn.smssdk.contact.a.c;
import cn.smssdk.contact.a.d;
import cn.smssdk.contact.a.e;
import cn.smssdk.contact.a.g;
import cn.smssdk.contact.a.h;
import cn.smssdk.contact.a.i;
import cn.smssdk.contact.a.j;
import cn.smssdk.contact.a.k;
import cn.smssdk.contact.a.l;
import cn.smssdk.contact.a.m;
import cn.smssdk.contact.a.n;
import cn.smssdk.contact.a.o;
import cn.smssdk.contact.a.q;
import cn.smssdk.utils.SMSLog;
import cn.smssdk.utils.SPHelper;
import com.fanyu.boundless.config.Preferences;
import com.mob.tools.FakeActivity;
import com.mob.tools.utils.DeviceHelper;
import com.mob.tools.utils.UIHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/* compiled from: ContactHelper */
public class b {
    private static b a;
    private static ContentObserver b;
    private Context c;
    private ContentResolver d;
    private c e;
    private OnContactChangeListener f;
    private d g;
    private String h;

    public static b a(Context context) {
        if (a == null) {
            a = new b(context);
        }
        return a;
    }

    private b(Context context) {
        this.c = context.getApplicationContext();
        this.d = context.getContentResolver();
        this.e = new c(context, this.d);
        c();
        this.g = new d(context, this);
        this.h = new File(context.getFilesDir(), ".cb").getAbsolutePath();
    }

    private void c() {
        if (b == null) {
            b = new ContentObserver(this, new Handler()) {
                final /* synthetic */ b a;

                public void onChange(boolean z) {
                    this.a.g.a();
                    if (this.a.f != null) {
                        this.a.f.onContactChange(z);
                    }
                }
            };
        }
        try {
            this.d.unregisterContentObserver(b);
            this.d.registerContentObserver(Contacts.CONTENT_URI, true, b);
        } catch (Throwable th) {
            SMSLog.getInstance().w(th);
        }
    }

    public void a() {
        a(new Runnable(this) {
            final /* synthetic */ b a;

            {
                this.a = r1;
            }

            public void run() {
                this.a.g.a();
            }
        }, null);
    }

    public void a(Runnable runnable, Runnable runnable2) {
        try {
            if (!DeviceHelper.getInstance(this.c).checkPermission("android.permission.READ_CONTACTS")) {
                e();
                if (runnable2 != null) {
                    runnable2.run();
                }
            } else if (!SPHelper.getInstance(this.c).isWarnWhenReadContact()) {
                SPHelper.getInstance(this.c).setAllowReadContact();
                if (runnable != null) {
                    runnable.run();
                }
            } else if (SPHelper.getInstance(this.c).isAllowReadContact()) {
                if (runnable != null) {
                    runnable.run();
                }
            } else if (a.a()) {
                a.a(runnable, runnable2);
            } else {
                a aVar = new a();
                a.a(runnable, runnable2);
                aVar.showForResult(this.c, null, new FakeActivity(this) {
                    final /* synthetic */ b a;

                    {
                        this.a = r1;
                    }

                    public void onResult(HashMap<String, Object> hashMap) {
                        Iterator it;
                        Runnable runnable;
                        if ("true".equals(String.valueOf(hashMap.get("res")))) {
                            SPHelper.getInstance(this.a.c).setAllowReadContact();
                            it = ((ArrayList) hashMap.get("okActions")).iterator();
                            while (it.hasNext()) {
                                runnable = (Runnable) it.next();
                                if (runnable != null) {
                                    runnable.run();
                                }
                            }
                            return;
                        }
                        it = ((ArrayList) hashMap.get("cancelActions")).iterator();
                        while (it.hasNext()) {
                            runnable = (Runnable) it.next();
                            if (runnable != null) {
                                runnable.run();
                            }
                        }
                    }
                });
            }
        } catch (Throwable th) {
            e();
            SMSLog.getInstance().w(th);
            if (runnable2 != null) {
                runnable2.run();
            }
        }
    }

    public void b(final Runnable runnable, final Runnable runnable2) {
        a(new Runnable(this) {
            final /* synthetic */ b b;

            public void run() {
                new Thread(this) {
                    final /* synthetic */ AnonymousClass4 a;

                    {
                        this.a = r1;
                    }

                    public void run() {
                        try {
                            this.a.b.b(true);
                            if (runnable != null) {
                                runnable.run();
                            }
                        } catch (Throwable th) {
                            SMSLog.getInstance().w(th);
                        }
                    }
                }.start();
            }
        }, new Runnable(this) {
            final /* synthetic */ b b;

            public void run() {
                if (runnable2 != null) {
                    runnable2.run();
                }
            }
        });
    }

    private synchronized void b(boolean z) throws Throwable {
        SMSLog.getInstance().d(">>>>>>>>> ContactHelper.onRebuild", new Object[0]);
        if (z || !new File(this.h).exists()) {
            Object obj;
            Iterator it;
            SMSLog.getInstance().d(">>>>>>>>> ContactHelper.onRebuild.start", new Object[0]);
            ArrayList arrayList = new ArrayList();
            if (VERSION.SDK_INT <= 10) {
                obj = "_id";
            } else {
                String str = "name_raw_contact_id";
            }
            Uri uri = VERSION.SDK_INT <= 9 ? RawContacts.CONTENT_URI : Contacts.CONTENT_URI;
            String[] strArr = new String[]{obj};
            SMSLog.getInstance().d(">>>>>>>>> query: " + uri, new Object[0]);
            ArrayList a = this.e.a(uri, strArr, null, null, "sort_key asc");
            if (a != null) {
                SMSLog.getInstance().d(">>>>>>>>> found: " + a.size() + " ids", new Object[0]);
                it = a.iterator();
                while (it.hasNext()) {
                    String str2 = (String) ((HashMap) it.next()).get(obj);
                    if (str2 != null) {
                        arrayList.add(new a(this.e, str2));
                    }
                }
            }
            SMSLog.getInstance().d(">>>>>>>>> ContactHelper.onRebuild.buffercontacts", new Object[0]);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(this.h)));
            objectOutputStream.writeInt(arrayList.size());
            it = arrayList.iterator();
            while (it.hasNext()) {
                byte[] bytes = ((a) it.next()).toString().getBytes("utf-8");
                objectOutputStream.writeInt(bytes.length);
                objectOutputStream.write(bytes);
            }
            objectOutputStream.flush();
            objectOutputStream.close();
            SMSLog.getInstance().d(">>>>>>>>> ContactHelper.onRebuild.buffercontacts.finish", new Object[0]);
        } else {
            SMSLog.getInstance().d(">>>>>>>>> ContactHelper.onRebuild.quickfinish", new Object[0]);
        }
    }

    private ArrayList<a> d() {
        int i = 0;
        SMSLog.getInstance().d(">>>>>>>>> ContactHelper.getContacts", new Object[0]);
        if (this.h == null) {
            return new ArrayList();
        }
        File file = new File(this.h);
        try {
            if (!file.exists()) {
                b(false);
            } else if (file.length() <= 28) {
                b(true);
            }
            ObjectInputStream objectInputStream = new ObjectInputStream(new GZIPInputStream(new FileInputStream(this.h)));
            int readInt = objectInputStream.readInt();
            SMSLog.getInstance().d(">>>>>>>>> found: " + readInt + " contacts", new Object[0]);
            ArrayList<a> arrayList = new ArrayList(readInt);
            while (i < readInt) {
                byte[] bArr = new byte[objectInputStream.readInt()];
                objectInputStream.readFully(bArr);
                arrayList.add(new a(new String(bArr, "utf-8")));
                i++;
            }
            objectInputStream.close();
            SMSLog.getInstance().d(">>>>>>>>> ContactHelper.getContacts.finish", new Object[0]);
            return arrayList;
        } catch (Throwable th) {
            if (file.exists()) {
                file.delete();
            }
            SMSLog.getInstance().w(th);
            return new ArrayList();
        }
    }

    public ArrayList<HashMap<String, Object>> a(boolean z) throws Throwable {
        if (!DeviceHelper.getInstance(this.c).checkPermission("android.permission.READ_CONTACTS")) {
            return null;
        }
        ArrayList d = d();
        if (d == null) {
            return null;
        }
        ArrayList<HashMap<String, Object>> arrayList = new ArrayList();
        Iterator it = d.iterator();
        while (it.hasNext()) {
            CharSequence b;
            ArrayList arrayList2;
            CharSequence g;
            ArrayList arrayList3;
            Iterator it2;
            ArrayList arrayList4;
            CharSequence b2;
            a aVar = (a) it.next();
            HashMap hashMap = new HashMap();
            h a = aVar.a();
            if (a != null) {
                HashMap hashMap2;
                ArrayList arrayList5;
                b = a.b();
                if (!TextUtils.isEmpty(b)) {
                    hashMap.put("prefixname", b);
                }
                b = a.c();
                if (!TextUtils.isEmpty(b)) {
                    hashMap.put("suffixname", b);
                }
                b = a.d();
                if (!TextUtils.isEmpty(b)) {
                    hashMap.put("lastname", b);
                }
                b = a.e();
                if (!TextUtils.isEmpty(b)) {
                    hashMap.put("firstname", b);
                }
                b = a.f();
                if (!TextUtils.isEmpty(b)) {
                    hashMap.put("displayname", b);
                }
                b = a.i();
                if (TextUtils.isEmpty(b)) {
                    arrayList2 = null;
                } else {
                    hashMap2 = new HashMap();
                    hashMap2.put("key", "phonetic");
                    arrayList5 = new ArrayList();
                    arrayList5.add(b);
                    hashMap2.put("vals", arrayList5);
                    if (null == null) {
                        arrayList2 = new ArrayList();
                        hashMap.put("others", arrayList2);
                    } else {
                        arrayList2 = null;
                    }
                    arrayList2.add(hashMap2);
                }
                g = a.g();
                if (!TextUtils.isEmpty(g)) {
                    HashMap hashMap3 = new HashMap();
                    hashMap3.put("key", "phoneticfirstname");
                    ArrayList arrayList6 = new ArrayList();
                    arrayList6.add(g);
                    hashMap3.put("vals", arrayList6);
                    if (arrayList2 == null) {
                        arrayList2 = new ArrayList();
                        hashMap.put("others", arrayList2);
                    }
                    arrayList2.add(hashMap3);
                }
                CharSequence h = a.h();
                if (!TextUtils.isEmpty(h)) {
                    hashMap2 = new HashMap();
                    hashMap2.put("key", "phoneticlastname");
                    arrayList5 = new ArrayList();
                    arrayList5.add(h);
                    hashMap2.put("vals", arrayList5);
                    if (arrayList2 == null) {
                        arrayList2 = new ArrayList();
                        hashMap.put("others", arrayList2);
                    }
                    arrayList2.add(hashMap2);
                }
                arrayList3 = arrayList2;
            } else {
                arrayList3 = null;
            }
            i b3 = aVar.b();
            if (b3 != null) {
                b = b3.b();
                if (!TextUtils.isEmpty(b)) {
                    hashMap.put(Preferences.NICKNAME, b);
                }
            }
            k d2 = aVar.d();
            if (d2 != null) {
                g = d2.b();
                if (!TextUtils.isEmpty(g)) {
                    hashMap.put("company", g);
                }
                b = d2.c();
                if (!TextUtils.isEmpty(b)) {
                    hashMap.put("position", b);
                }
            }
            arrayList2 = aVar.j();
            if (arrayList2 != null) {
                it2 = arrayList2.iterator();
                arrayList4 = null;
                while (it2.hasNext()) {
                    l lVar = (l) it2.next();
                    b2 = lVar.b();
                    if (!TextUtils.isEmpty(b2)) {
                        if (arrayList4 == null) {
                            arrayList4 = new ArrayList();
                            hashMap.put("phones", arrayList4);
                        }
                        HashMap hashMap4 = new HashMap();
                        hashMap4.put("phone", b2);
                        hashMap4.put(MessageEncoder.ATTR_TYPE, Integer.valueOf(lVar.c()));
                        hashMap4.put("desc", lVar.d());
                        arrayList4.add(hashMap4);
                    }
                }
            }
            arrayList2 = aVar.i();
            if (arrayList2 != null) {
                it2 = arrayList2.iterator();
                arrayList4 = null;
                while (it2.hasNext()) {
                    c cVar = (c) it2.next();
                    b2 = cVar.b();
                    if (!TextUtils.isEmpty(b2)) {
                        if (arrayList4 == null) {
                            arrayList4 = new ArrayList();
                            hashMap.put("mails", arrayList4);
                        }
                        hashMap4 = new HashMap();
                        hashMap4.put("email", b2);
                        hashMap4.put(MessageEncoder.ATTR_TYPE, Integer.valueOf(cVar.c()));
                        hashMap4.put("desc", cVar.d());
                        arrayList4.add(hashMap4);
                    }
                }
            }
            arrayList2 = aVar.k();
            if (arrayList2 != null) {
                it2 = arrayList2.iterator();
                arrayList4 = null;
                while (it2.hasNext()) {
                    n nVar = (n) it2.next();
                    b2 = nVar.b();
                    if (!TextUtils.isEmpty(b2)) {
                        if (arrayList4 == null) {
                            arrayList4 = new ArrayList();
                            hashMap.put("addresses", arrayList4);
                        }
                        hashMap4 = new HashMap();
                        hashMap4.put("address", b2);
                        hashMap4.put(MessageEncoder.ATTR_TYPE, Integer.valueOf(nVar.c()));
                        hashMap4.put("desc", nVar.d());
                        arrayList4.add(hashMap4);
                    }
                }
            }
            arrayList2 = aVar.l();
            if (arrayList2 != null) {
                it2 = arrayList2.iterator();
                arrayList4 = null;
                while (it2.hasNext()) {
                    d dVar = (d) it2.next();
                    b2 = dVar.b();
                    if (!TextUtils.isEmpty(b2)) {
                        if (arrayList4 == null) {
                            arrayList4 = new ArrayList();
                            hashMap.put("specialdate", arrayList4);
                        }
                        hashMap4 = new HashMap();
                        hashMap4.put("date", b2);
                        hashMap4.put(MessageEncoder.ATTR_TYPE, Integer.valueOf(dVar.c()));
                        hashMap4.put("desc", dVar.d());
                        arrayList4.add(hashMap4);
                    }
                }
            }
            arrayList2 = aVar.h();
            if (arrayList2 != null) {
                it2 = arrayList2.iterator();
                arrayList4 = null;
                while (it2.hasNext()) {
                    g gVar = (g) it2.next();
                    b2 = gVar.b();
                    if (!TextUtils.isEmpty(b2)) {
                        if (arrayList4 == null) {
                            arrayList4 = new ArrayList();
                            hashMap.put("ims", arrayList4);
                        }
                        hashMap4 = new HashMap();
                        hashMap4.put("val", b2);
                        hashMap4.put(MessageEncoder.ATTR_TYPE, Integer.valueOf(gVar.c()));
                        hashMap4.put("desc", gVar.d());
                        arrayList4.add(hashMap4);
                    }
                }
            }
            e c = aVar.c();
            if (c != null) {
                b = c.b();
                if (!TextUtils.isEmpty(b)) {
                    hashMap.put("group", b);
                }
            }
            j f = aVar.f();
            if (f != null) {
                b = f.b();
                if (!TextUtils.isEmpty(b)) {
                    hashMap.put("remarks", b);
                }
            }
            arrayList2 = aVar.g();
            if (arrayList2 != null) {
                it2 = arrayList2.iterator();
                arrayList4 = null;
                while (it2.hasNext()) {
                    q qVar = (q) it2.next();
                    b2 = qVar.b();
                    if (!TextUtils.isEmpty(b2)) {
                        if (arrayList4 == null) {
                            arrayList4 = new ArrayList();
                            hashMap.put("websites", arrayList4);
                        }
                        hashMap4 = new HashMap();
                        hashMap4.put("val", b2);
                        hashMap4.put(MessageEncoder.ATTR_TYPE, Integer.valueOf(qVar.c()));
                        hashMap4.put("desc", qVar.d());
                        arrayList4.add(hashMap4);
                    }
                }
            }
            arrayList2 = aVar.m();
            if (arrayList2 != null) {
                it2 = arrayList2.iterator();
                arrayList4 = null;
                while (it2.hasNext()) {
                    o oVar = (o) it2.next();
                    b2 = oVar.b();
                    if (!TextUtils.isEmpty(b2)) {
                        if (arrayList4 == null) {
                            arrayList4 = new ArrayList();
                            hashMap.put("relations", arrayList4);
                        }
                        hashMap4 = new HashMap();
                        hashMap4.put("val", b2);
                        hashMap4.put(MessageEncoder.ATTR_TYPE, Integer.valueOf(oVar.c()));
                        hashMap4.put("desc", oVar.d());
                        arrayList4.add(hashMap4);
                    }
                }
            }
            if (z) {
                m e = aVar.e();
                if (e != null) {
                    CharSequence b4 = e.b();
                    if (!TextUtils.isEmpty(b4)) {
                        HashMap hashMap5 = new HashMap();
                        hashMap5.put("key", "avatar");
                        arrayList4 = new ArrayList();
                        arrayList4.add(b4);
                        hashMap5.put("vals", arrayList4);
                        if (arrayList3 == null) {
                            arrayList3 = new ArrayList();
                            hashMap.put("others", arrayList3);
                        }
                        arrayList3.add(hashMap5);
                    }
                }
            }
            arrayList.add(hashMap);
        }
        return arrayList;
    }

    public String[] b() {
        ArrayList d = d();
        if (d == null) {
            return null;
        }
        Iterator it;
        HashSet hashSet = new HashSet();
        Iterator it2 = d.iterator();
        while (it2.hasNext()) {
            d = ((a) it2.next()).j();
            if (d != null) {
                it = d.iterator();
                while (it.hasNext()) {
                    CharSequence b = ((l) it.next()).b();
                    if (!TextUtils.isEmpty(b)) {
                        hashSet.add(b);
                    }
                }
            }
        }
        String[] strArr = new String[hashSet.size()];
        it = hashSet.iterator();
        int i = 0;
        while (it.hasNext()) {
            strArr[i] = (String) it.next();
            i++;
        }
        return strArr.length > 0 ? strArr : null;
    }

    public void a(OnContactChangeListener onContactChangeListener) {
        this.f = onContactChangeListener;
    }

    private void e() {
        if (SPHelper.getInstance(this.c).isWarnWhenReadContact()) {
            UIHandler.sendEmptyMessage(1, new Callback(this) {
                final /* synthetic */ b a;

                {
                    this.a = r1;
                }

                public boolean handleMessage(Message message) {
                    if (message.what == 1) {
                        CharSequence valueOf;
                        if ("zh".equals(DeviceHelper.getInstance(this.a.c).getOSLanguage())) {
                            valueOf = String.valueOf(new char[]{'应', '用', '无', '权', '限', '读', '取', '通', '讯', '录'});
                        } else {
                            valueOf = "no permission to read contacts";
                        }
                        Toast.makeText(this.a.c, valueOf, 0).show();
                    }
                    return false;
                }
            });
        }
    }
}
