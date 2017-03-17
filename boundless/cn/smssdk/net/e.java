package cn.smssdk.net;

import android.content.Context;
import android.text.TextUtils;
import cn.finalteam.toolsfinal.io.IOUtils;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;
import com.fanyu.boundless.config.Preferences;
import com.mob.tools.network.KVPair;
import com.mob.tools.utils.Data;
import com.mob.tools.utils.DeviceHelper;
import com.mob.tools.utils.Hashon;
import com.xiaomi.mipush.sdk.Constants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/* compiled from: ParamsBuilder */
public class e {
    private static String a;
    private static boolean b;
    private static DeviceHelper c;
    private static HashMap<String, Object> d;
    private static b e;
    private static boolean h;
    private static boolean i;
    private static e j;
    private Hashon f = new Hashon();
    private HashMap<String, String> g;

    public static e a() {
        if (j == null) {
            synchronized (e.class) {
                j = new e();
            }
        }
        return j;
    }

    private e() {
    }

    public static void a(Context context, String str) {
        String simSerialNumber;
        String str2 = null;
        a = str;
        c = DeviceHelper.getInstance(context);
        e = b.a(context);
        d = new HashMap();
        d.put("plat", Integer.valueOf(c.getPlatformCode()));
        d.put("sdkver", "2.1.3");
        try {
            h = c.checkPermission("android.permission.READ_PHONE_STATE");
            i = c.checkPermission("android.permission.READ_SMS");
            if (h) {
                simSerialNumber = c.getSimSerialNumber();
            } else {
                simSerialNumber = null;
            }
            try {
                if (h && i) {
                    str2 = c.getLine1Number();
                }
            } catch (Throwable th) {
            }
        } catch (Throwable th2) {
            simSerialNumber = null;
        }
        String carrier = c.getCarrier();
        if (!(carrier == null || carrier.equals("-1"))) {
            d.put("operator", carrier);
        }
        if (!(simSerialNumber == null || simSerialNumber.equals("-1"))) {
            d.put("simserial", simSerialNumber);
        }
        if (!(str2 == null || str2.equals("-1"))) {
            d.put("my_phone", str2);
        }
        b = true;
    }

    public HashMap<String, Object> a(String str, String str2, String str3, HashMap<String, Object> hashMap) throws Throwable {
        if (!b) {
            throw new Throwable("ParamsBuilder need prepare before use");
        } else if (str.equals("user_info_001")) {
            return a(str2, (HashMap) hashMap);
        } else {
            if (str.equals("device_001")) {
                return c();
            }
            if (str.equals("install_002")) {
                return b(str2, hashMap);
            }
            if (str.equals("collect_001")) {
                return a((HashMap) hashMap);
            }
            if (str.equals("contacts_001")) {
                return c(str2, hashMap);
            }
            if (str.equals("contacts_002")) {
                return d(str2, hashMap);
            }
            return null;
        }
    }

    public HashMap<String, Object> a(ArrayList<String> arrayList, String str, String str2, HashMap<String, Object> hashMap) throws Throwable {
        if (b) {
            HashMap<String, Object> hashMap2 = new HashMap();
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                String str3 = (String) it.next();
                if (str3.equals("appkey")) {
                    hashMap2.put("appkey", a);
                } else if (str3.equals(Constants.EXTRA_KEY_TOKEN)) {
                    hashMap2.put(Constants.EXTRA_KEY_TOKEN, str2);
                } else if (str3.equals("duid")) {
                    hashMap2.put("duid", str);
                } else if (str3.equals("contactphones")) {
                    hashMap2.put("contactphones", a((String[]) hashMap.get("contactphones")));
                } else {
                    Object obj = d.get(str3);
                    if (obj == null) {
                        obj = hashMap.get(str3);
                    }
                    hashMap2.put(str3, obj);
                }
            }
            return hashMap2;
        }
        throw new Throwable("ParamsBuilder need prepare before use");
    }

    public ArrayList<KVPair<String>> a(byte[] bArr, String str) throws Throwable {
        ArrayList<KVPair<String>> arrayList = new ArrayList();
        arrayList.add(new KVPair("appkey", a));
        String str2 = Constants.EXTRA_KEY_TOKEN;
        if (TextUtils.isEmpty(str)) {
            str = "";
        }
        arrayList.add(new KVPair(str2, str));
        arrayList.add(new KVPair("hash", Data.CRC32(bArr)));
        arrayList.add(new KVPair("User-Agent", b()));
        return arrayList;
    }

    private String b() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SMSSDK/").append("2.1.3").append(' ');
        stringBuilder.append("(Android; ").append(Data.urlEncode(c.getOSVersionName())).append(IOUtils.DIR_SEPARATOR_UNIX).append(c.getOSVersionInt()).append(") ");
        stringBuilder.append(Data.urlEncode(c.getManufacturer())).append(IOUtils.DIR_SEPARATOR_UNIX).append(Data.urlEncode(c.getModel())).append(' ');
        stringBuilder.append(Data.urlEncode(c.getAppName())).append(IOUtils.DIR_SEPARATOR_UNIX).append(c.getPackageName()).append(IOUtils.DIR_SEPARATOR_UNIX).append(Data.urlEncode(c.getAppVersionName()));
        return stringBuilder.toString();
    }

    private HashMap<String, Object> a(String str, HashMap<String, Object> hashMap) {
        String str2 = (String) hashMap.get("uid");
        String str3 = (String) hashMap.get(Preferences.NICKNAME);
        String str4 = (String) hashMap.get("avatar");
        String str5 = (String) hashMap.get("zone");
        String str6 = (String) hashMap.get("phone");
        HashMap<String, Object> hashMap2 = new HashMap();
        hashMap2.put("avatar", str4);
        hashMap2.put("uid", str2);
        hashMap2.put(Preferences.NICKNAME, str3);
        hashMap2.put("appkey", a);
        hashMap2.put("phone", str6);
        hashMap2.put("zone", str5);
        hashMap2.put("duid", str);
        return hashMap2;
    }

    private HashMap<String, Object> c() {
        HashMap<String, Object> hashMap = new HashMap();
        hashMap.put("appkey", a);
        hashMap.put("apppkg", c.getPackageName());
        hashMap.put("appver", c.getAppVersionName());
        hashMap.put("plat", Integer.valueOf(c.getPlatformCode()));
        hashMap.put("network", c.getNetworkTypeForStatic());
        hashMap.put("deviceinfo", d());
        return hashMap;
    }

    private HashMap<String, Object> b(String str, HashMap<String, Object> hashMap) {
        String str2 = (String) hashMap.get(MessageEncoder.ATTR_TYPE);
        Object obj = hashMap.get("list");
        HashMap<String, Object> hashMap2 = new HashMap();
        hashMap2.put(MessageEncoder.ATTR_TYPE, str2);
        hashMap2.put("plat", Integer.valueOf(c.getPlatformCode()));
        hashMap2.put("device", str);
        hashMap2.put("list", obj);
        return hashMap2;
    }

    private HashMap<String, Object> a(HashMap<String, Object> hashMap) {
        Object obj = hashMap.get("logs");
        HashMap<String, Object> hashMap2 = new HashMap();
        hashMap2.put("logs", obj);
        hashMap2.put("deviceinfo", d());
        return hashMap2;
    }

    private HashMap<String, Object> c(String str, HashMap<String, Object> hashMap) throws Throwable {
        Object simSerialNumber;
        if (h) {
            simSerialNumber = c.getSimSerialNumber();
        } else {
            simSerialNumber = null;
        }
        ArrayList arrayList = (ArrayList) hashMap.get("contacts");
        a(arrayList);
        HashMap<String, Object> hashMap2 = new HashMap();
        hashMap2.put("appkey", a);
        hashMap2.put("duid", str);
        hashMap2.put("simserial", simSerialNumber);
        hashMap2.put("contacts", b(arrayList));
        return hashMap2;
    }

    private HashMap<String, Object> d(String str, HashMap<String, Object> hashMap) throws Throwable {
        Object simSerialNumber;
        Object obj;
        Object obj2 = null;
        if (h) {
            simSerialNumber = c.getSimSerialNumber();
        } else {
            simSerialNumber = null;
        }
        String str2 = (String) hashMap.get("zone");
        String[] countryByMCC = SMSSDK.getCountryByMCC(c.getMCC());
        if (countryByMCC != null) {
            obj = countryByMCC[1];
        } else {
            String str3 = str2;
        }
        if (h && i) {
            obj2 = c.getLine1Number();
        }
        ArrayList arrayList = (ArrayList) hashMap.get("contacts");
        a(arrayList);
        HashMap<String, Object> hashMap2 = new HashMap();
        hashMap2.put("appkey", a);
        hashMap2.put("duid", str);
        hashMap2.put("my_phone", obj2);
        hashMap2.put("zone", obj);
        hashMap2.put("simserial", simSerialNumber);
        String carrier = c.getCarrier();
        if (!(carrier == null || carrier.equals("-1"))) {
            hashMap2.put("operator", carrier);
        }
        hashMap2.put("contacts", b(arrayList));
        return hashMap2;
    }

    private HashMap<String, Object> d() {
        HashMap<String, Object> hashMap = new HashMap();
        String imei = c.getIMEI();
        Object obj = null;
        try {
            obj = c.getAdvertisingID();
        } catch (Throwable th) {
            SMSLog.getInstance().d(th);
        }
        hashMap.put("adsid", obj);
        String str = "imei";
        if (imei == null) {
            obj = "";
        } else {
            String str2 = imei;
        }
        hashMap.put(str, obj);
        obj = c.getSerialno();
        imei = "serialno";
        if (obj == null) {
            obj = "";
        }
        hashMap.put(imei, obj);
        obj = c.getMacAddress();
        imei = "mac";
        if (obj == null) {
            obj = "";
        }
        hashMap.put(imei, obj);
        obj = c.getModel();
        imei = "model";
        if (obj == null) {
            obj = "";
        }
        hashMap.put(imei, obj);
        obj = c.getManufacturer();
        imei = "factory";
        if (obj == null) {
            obj = "";
        }
        hashMap.put(imei, obj);
        obj = c.getCarrier();
        imei = "carrier";
        if (obj == null) {
            obj = "";
        }
        hashMap.put(imei, obj);
        obj = c.getScreenSize();
        imei = "screensize";
        if (obj == null) {
            obj = "";
        }
        hashMap.put(imei, obj);
        obj = c.getOSVersionName();
        imei = "sysver";
        if (obj == null) {
            obj = "";
        }
        hashMap.put(imei, obj);
        hashMap.put("androidid", c.getAndroidID());
        return hashMap;
    }

    private void a(ArrayList<HashMap<String, Object>> arrayList) throws Throwable {
        if (arrayList != null && arrayList.size() > 0) {
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                HashMap hashMap = (HashMap) it.next();
                if (hashMap != null) {
                    Object obj = hashMap.get("phones");
                    if (obj != null) {
                        Iterator it2 = ((ArrayList) obj).iterator();
                        while (it2.hasNext()) {
                            hashMap = (HashMap) it2.next();
                            Object obj2 = hashMap.get("phone");
                            if (obj2 != null) {
                                hashMap.put("phone", e.b((String) obj2));
                            }
                        }
                    }
                }
            }
        }
    }

    private String b(ArrayList<HashMap<String, Object>> arrayList) throws Throwable {
        HashMap hashMap = new HashMap();
        hashMap.put("list", arrayList);
        String fromHashMap = this.f.fromHashMap(hashMap);
        return fromHashMap.substring(8, fromHashMap.length() - 1);
    }

    private String a(String[] strArr) {
        if (strArr == null || strArr.length == 0) {
            return null;
        }
        if (this.g == null) {
            this.g = new HashMap();
        }
        for (String str : strArr) {
            this.g.put(e.b(str), str);
        }
        if (this.g == null || this.g.size() <= 0) {
            return null;
        }
        return TextUtils.join(Constants.ACCEPT_TIME_SEPARATOR_SP, this.g.keySet());
    }

    public String a(String str) {
        if (this.g == null || this.g.size() == 0) {
            return null;
        }
        return (String) this.g.get(str);
    }
}
