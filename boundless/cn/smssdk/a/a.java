package cn.smssdk.a;

import android.content.Context;
import android.os.Handler.Callback;
import android.os.Message;
import cn.smssdk.contact.b;
import cn.smssdk.net.f;
import cn.smssdk.utils.SMSLog;
import cn.smssdk.utils.SPHelper;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/* compiled from: SocialHelper */
public final class a {
    private static a a;
    private f b;
    private SPHelper c;
    private b d;
    private b e;

    public static a a(Context context) {
        if (a == null) {
            a = new a(context);
        }
        return a;
    }

    private a(Context context) {
        this.b = f.a(context);
        this.c = SPHelper.getInstance(context);
        this.d = new b(context, this);
        this.e = b.a(context);
    }

    public void a(String str, String str2, String str3, String str4, String str5) throws Throwable {
        this.b.a(str, str2, str3, str4, str5);
    }

    public void a(final Callback callback) {
        if (a()) {
            c(new Callback(this) {
                final /* synthetic */ a b;

                public boolean handleMessage(Message message) {
                    ArrayList a;
                    Message message2 = new Message();
                    try {
                        a = this.b.a(this.b.b.a((String[]) message.obj));
                        this.b.c.setBufferedNewFriends(a);
                        this.b.c.setRequestNewFriendsTime();
                    } catch (Throwable th) {
                        message2.what = 0;
                        message2.obj = th;
                    }
                    message2.what = 1;
                    message2.obj = Integer.valueOf(a.size());
                    callback.handleMessage(message2);
                    return false;
                }
            });
            return;
        }
        Message message = new Message();
        message.what = 1;
        try {
            message.obj = Integer.valueOf(this.c.getBufferedNewFriends().size());
        } catch (Throwable th) {
            SMSLog.getInstance().w(th);
            message.obj = Integer.valueOf(0);
        }
        callback.handleMessage(message);
    }

    private boolean a() {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(System.currentTimeMillis());
        int i = instance.get(1);
        int i2 = instance.get(2);
        int i3 = instance.get(5);
        instance.setTimeInMillis(this.c.getLastRequestNewFriendsTime());
        int i4 = instance.get(1);
        int i5 = instance.get(2);
        int i6 = instance.get(5);
        if (i == i4 && i2 == i5 && i3 == i6) {
            return false;
        }
        return true;
    }

    protected ArrayList<HashMap<String, Object>> a(ArrayList<HashMap<String, Object>> arrayList) throws Throwable {
        ArrayList bufferedFriends;
        ArrayList bufferedNewFriends;
        try {
            bufferedFriends = this.c.getBufferedFriends();
        } catch (Throwable th) {
            SMSLog.getInstance().w(th);
            bufferedFriends = new ArrayList();
        }
        HashMap hashMap = new HashMap();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            HashMap hashMap2 = (HashMap) it.next();
            Object obj = hashMap2.get("phone");
            if (obj != null) {
                Object obj2;
                Iterator it2 = bufferedFriends.iterator();
                while (it2.hasNext()) {
                    if (obj.equals(((HashMap) it2.next()).get("phone"))) {
                        obj2 = null;
                        break;
                    }
                }
                int i = 1;
                if (obj2 != null) {
                    hashMap.put(obj, hashMap2);
                }
            }
        }
        try {
            bufferedNewFriends = this.c.getBufferedNewFriends();
        } catch (Throwable th2) {
            SMSLog.getInstance().w(th2);
            bufferedNewFriends = new ArrayList();
        }
        Iterator it3 = bufferedNewFriends.iterator();
        while (it3.hasNext()) {
            hashMap2 = (HashMap) it3.next();
            Object obj3 = hashMap2.get("phone");
            if (obj3 != null) {
                hashMap.put(obj3, hashMap2);
            }
        }
        ArrayList<HashMap<String, Object>> arrayList2 = new ArrayList();
        for (Entry value : hashMap.entrySet()) {
            arrayList2.add(value.getValue());
        }
        return arrayList2;
    }

    public void b(final Callback callback) {
        c(new Callback(this) {
            final /* synthetic */ a b;

            public boolean handleMessage(Message message) {
                ArrayList a;
                ArrayList a2;
                Message message2 = new Message();
                try {
                    a = this.b.b.a((String[]) message.obj);
                    a2 = this.b.a(a);
                    this.b.c.setBufferedFriends(a);
                    this.b.c.setBufferedNewFriends(new ArrayList());
                } catch (Throwable th) {
                    message2.what = 0;
                    message2.obj = th;
                }
                Iterator it = a2.iterator();
                while (it.hasNext()) {
                    Object obj = ((HashMap) it.next()).get("phone");
                    if (obj != null) {
                        Iterator it2 = a.iterator();
                        while (it2.hasNext()) {
                            HashMap hashMap = (HashMap) it2.next();
                            if (obj.equals(hashMap.get("phone"))) {
                                hashMap.put("isnew", Boolean.valueOf(true));
                                break;
                            }
                        }
                    }
                }
                message2.what = 1;
                message2.obj = a;
                callback.handleMessage(message2);
                return false;
            }
        });
    }

    public void a(int i, Callback callback) {
        this.d.a(i, callback);
    }

    private void c(final Callback callback) {
        Object bufferedContactPhones;
        final Message message = new Message();
        try {
            bufferedContactPhones = this.c.getBufferedContactPhones();
        } catch (Throwable th) {
            SMSLog.getInstance().w(th);
            bufferedContactPhones = null;
        }
        if (bufferedContactPhones == null || bufferedContactPhones.length <= 0) {
            this.e.a(new Runnable(this) {
                final /* synthetic */ a c;

                public void run() {
                    new Thread(this) {
                        final /* synthetic */ AnonymousClass3 a;

                        {
                            this.a = r1;
                        }

                        public void run() {
                            Object b = this.a.c.e.b();
                            try {
                                this.a.c.c.setBufferedContactPhones(b);
                            } catch (Throwable th) {
                                SMSLog.getInstance().w(th);
                            }
                            message.obj = b;
                            callback.handleMessage(message);
                        }
                    }.start();
                }
            }, new Runnable(this) {
                final /* synthetic */ a c;

                public void run() {
                    new Thread(this) {
                        final /* synthetic */ AnonymousClass4 a;

                        {
                            this.a = r1;
                        }

                        public void run() {
                            callback.handleMessage(message);
                        }
                    }.start();
                }
            });
            return;
        }
        message.obj = bufferedContactPhones;
        callback.handleMessage(message);
    }
}
