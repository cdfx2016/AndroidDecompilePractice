package com.mob.commons;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.util.MimeTypes;
import com.mob.commons.authorize.DeviceAuthorizer;
import com.mob.tools.MobHandlerThread;
import com.mob.tools.MobLog;
import com.mob.tools.network.KVPair;
import com.mob.tools.network.NetworkHelper.NetworkTimeOut;
import com.mob.tools.utils.Data;
import com.mob.tools.utils.Hashon;
import com.mob.tools.utils.MobRSA;
import com.mob.tools.utils.ReflectHelper;
import com.mob.tools.utils.ResHelper;
import com.mob.tools.utils.SQLiteHelper;
import com.mob.tools.utils.SQLiteHelper.SingleTableDB;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.zip.GZIPOutputStream;
import org.jivesoftware.smack.packet.PrivacyItem.PrivacyRule;
import org.jivesoftware.smackx.bytestreams.ibb.packet.DataPacketExtension;

/* compiled from: DataHeap */
public class c implements Callback {
    private static c a;
    private Context b;
    private Handler c;
    private SingleTableDB d;
    private Hashon e = new Hashon();
    private Random f = new Random();

    public static synchronized c a(Context context) {
        c cVar;
        synchronized (c.class) {
            if (a == null) {
                a = new c(context);
            }
            cVar = a;
        }
        return cVar;
    }

    private c(Context context) {
        this.b = context.getApplicationContext();
        MobHandlerThread mobHandlerThread = new MobHandlerThread();
        mobHandlerThread.start();
        this.c = new Handler(mobHandlerThread.getLooper(), this);
        File file = new File(ResHelper.getCacheRoot(context), "comm/dbs/.dh");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        this.d = SQLiteHelper.getDatabase(file.getAbsolutePath(), "DataHeap_1");
        this.d.addField("time", MimeTypes.BASE_TYPE_TEXT, true);
        this.d.addField(DataPacketExtension.ELEMENT_NAME, MimeTypes.BASE_TYPE_TEXT, true);
        this.c.sendEmptyMessage(1);
    }

    public synchronized void a(long j, HashMap<String, Object> hashMap) {
        Message message = new Message();
        message.what = 2;
        message.obj = new Object[]{Long.valueOf(j), hashMap};
        this.c.sendMessage(message);
    }

    public boolean handleMessage(Message message) {
        switch (message.what) {
            case 1:
                a();
                this.c.sendEmptyMessageDelayed(1, 10000);
                break;
            case 2:
                Object[] objArr = (Object[]) message.obj;
                long longValue = ((Long) ResHelper.forceCast(objArr[0], Long.valueOf(-1))).longValue();
                if (longValue > 0) {
                    b(longValue, (HashMap) objArr[1]);
                    break;
                }
                break;
        }
        return false;
    }

    private void b(final long j, final HashMap<String, Object> hashMap) {
        e.a(new File(ResHelper.getCacheRoot(this.b), "comm/locks/.dhlock"), true, new Runnable(this) {
            final /* synthetic */ c c;

            public void run() {
                try {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("time", String.valueOf(j));
                    contentValues.put(DataPacketExtension.ELEMENT_NAME, Base64.encodeToString(Data.AES128Encode(Data.rawMD5(String.valueOf(ReflectHelper.invokeInstanceMethod(ReflectHelper.invokeStaticMethod("DeviceHelper", "getInstance", this.c.b), "getManufacturer", new Object[0]))), this.c.e.fromHashMap(hashMap).getBytes("utf-8")), 2));
                    SQLiteHelper.insert(this.c.d, contentValues);
                } catch (Throwable th) {
                    MobLog.getInstance().w(th);
                }
            }
        });
    }

    private void a() {
        Object obj;
        try {
            obj = (String) ReflectHelper.invokeInstanceMethod(ReflectHelper.invokeStaticMethod("DeviceHelper", "getInstance", this.b), "getNetworkType", new Object[0]);
        } catch (Throwable th) {
            MobLog.getInstance().w(th);
            obj = null;
        }
        if (obj != null && !PrivacyRule.SUBSCRIPTION_NONE.equals(obj)) {
            e.a(new File(ResHelper.getCacheRoot(this.b), "comm/locks/.dhlock"), true, new Runnable(this) {
                final /* synthetic */ c a;

                {
                    this.a = r1;
                }

                public void run() {
                    ArrayList d = this.a.b();
                    if (d.size() > 0 && this.a.a(d)) {
                        this.a.b(d);
                    }
                }
            });
        }
    }

    private ArrayList<String[]> b() {
        ArrayList<String[]> arrayList = new ArrayList();
        try {
            Cursor query = SQLiteHelper.query(this.d, new String[]{"time", DataPacketExtension.ELEMENT_NAME}, null, null, null);
            if (query != null) {
                if (query.moveToFirst()) {
                    long a = a.a(this.b);
                    do {
                        Object obj = new String[]{query.getString(0), query.getString(1)};
                        long j = -1;
                        try {
                            j = Long.parseLong(obj[0]);
                        } catch (Throwable th) {
                        }
                        if (j <= a) {
                            arrayList.add(obj);
                        }
                    } while (query.moveToNext());
                }
                query.close();
            }
        } catch (Throwable th2) {
            MobLog.getInstance().w(th2);
        }
        return arrayList;
    }

    private boolean a(ArrayList<String[]> arrayList) {
        try {
            b a = b.a(this.b);
            ArrayList a2 = a.a();
            if (a2.isEmpty()) {
                return false;
            }
            HashMap hashMap = new HashMap();
            Object invokeStaticMethod = ReflectHelper.invokeStaticMethod("DeviceHelper", "getInstance", this.b);
            hashMap.put("plat", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getPlatformCode", new Object[0]));
            hashMap.put("device", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getDeviceKey", new Object[0]));
            hashMap.put("mac", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getMacAddress", new Object[0]));
            hashMap.put("model", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getModel", new Object[0]));
            hashMap.put("duid", DeviceAuthorizer.authorize(this.b, null));
            hashMap.put("imei", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getIMEI", new Object[0]));
            hashMap.put("serialno", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getSerialno", new Object[0]));
            hashMap.put("networktype", ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getDetailNetworkTypeForStatic", new Object[0]));
            ArrayList arrayList2 = new ArrayList();
            byte[] rawMD5 = Data.rawMD5(String.valueOf(ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getManufacturer", new Object[0])));
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                arrayList2.add(this.e.fromJson(new String(Data.AES128Decode(rawMD5, Base64.decode(((String[]) it.next())[1], 2)), "utf-8").trim()));
            }
            hashMap.put("datas", arrayList2);
            arrayList2 = new ArrayList();
            arrayList2.add(new KVPair("appkey", ((MobProduct) a2.get(0)).getProductAppkey()));
            arrayList2.add(new KVPair("m", a(this.e.fromHashMap(hashMap))));
            ArrayList arrayList3 = new ArrayList();
            arrayList3.add(new KVPair("User-Identity", a.a(a2)));
            NetworkTimeOut networkTimeOut = new NetworkTimeOut();
            networkTimeOut.readTimout = DefaultLoadControl.DEFAULT_MAX_BUFFER_MS;
            networkTimeOut.connectionTimeout = DefaultLoadControl.DEFAULT_MAX_BUFFER_MS;
            boolean equals = "200".equals(String.valueOf(this.e.fromJson(a.httpPost(b(this.b), arrayList2, null, arrayList3, networkTimeOut)).get("status")));
            if (equals) {
                return equals;
            }
            f.e(this.b, null);
            return equals;
        } catch (Throwable th) {
            f.e(this.b, null);
            MobLog.getInstance().w(th);
            return false;
        }
    }

    private String a(String str) throws Throwable {
        OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        dataOutputStream.writeLong(this.f.nextLong());
        dataOutputStream.writeLong(this.f.nextLong());
        dataOutputStream.flush();
        dataOutputStream.close();
        byte[] toByteArray = byteArrayOutputStream.toByteArray();
        OutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
        GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream2);
        gZIPOutputStream.write(str.getBytes("utf-8"));
        gZIPOutputStream.flush();
        gZIPOutputStream.close();
        byte[] AES128Encode = Data.AES128Encode(toByteArray, byteArrayOutputStream2.toByteArray());
        toByteArray = new MobRSA(1024).encode(toByteArray, new BigInteger("ceeef5035212dfe7c6a0acdc0ef35ce5b118aab916477037d7381f85c6b6176fcf57b1d1c3296af0bb1c483fe5e1eb0ce9eb2953b44e494ca60777a1b033cc07", 16), new BigInteger("191737288d17e660c4b61440d5d14228a0bf9854499f9d68d8274db55d6d954489371ecf314f26bec236e58fac7fffa9b27bcf923e1229c4080d49f7758739e5bd6014383ed2a75ce1be9b0ab22f283c5c5e11216c5658ba444212b6270d629f2d615b8dfdec8545fb7d4f935b0cc10b6948ab4fc1cb1dd496a8f94b51e888dd", 16));
        OutputStream byteArrayOutputStream3 = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream2 = new DataOutputStream(byteArrayOutputStream3);
        dataOutputStream2.writeInt(toByteArray.length);
        dataOutputStream2.write(toByteArray);
        dataOutputStream2.writeInt(AES128Encode.length);
        dataOutputStream2.write(AES128Encode);
        dataOutputStream2.flush();
        dataOutputStream2.close();
        return Base64.encodeToString(byteArrayOutputStream3.toByteArray(), 2);
    }

    private void b(ArrayList<String[]> arrayList) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                String[] strArr = (String[]) it.next();
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append('\'').append(strArr[0]).append('\'');
            }
            SQLiteHelper.delete(this.d, "time in (" + stringBuilder.toString() + ")", null);
        } catch (Throwable th) {
            MobLog.getInstance().w(th);
        }
    }

    private static String b(Context context) {
        String str = null;
        try {
            str = f.f(context);
        } catch (Throwable th) {
            MobLog.getInstance().w(th);
        }
        return TextUtils.isEmpty(str) ? "http://c.data.mob.com/v2/cdata" : str;
    }
}
