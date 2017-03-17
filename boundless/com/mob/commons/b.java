package com.mob.commons;

import android.content.Context;
import com.easemob.util.HanziToPinyin.Token;
import com.mob.tools.MobLog;
import com.mob.tools.network.NetworkHelper;
import com.mob.tools.utils.ReflectHelper;
import com.xiaomi.mipush.sdk.Constants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.TimeZone;

/* compiled from: CommonNetworkHelper */
public class b extends NetworkHelper {
    private static final String[] a = new String[]{"SHARESDK", "SMSSDK", "SHAREREC", "MOBAPI"};
    private static b b;
    private Context c;
    private HashMap<String, MobProduct> d = new HashMap();

    private b(Context context) {
        this.c = context.getApplicationContext();
    }

    public static b a(Context context) {
        if (b == null) {
            b = new b(context);
        }
        return b;
    }

    public ArrayList<MobProduct> a() {
        try {
            ReflectHelper.importClass("com.mob.commons.*");
            for (String newInstance : a) {
                try {
                    MobProduct mobProduct = (MobProduct) ReflectHelper.newInstance(newInstance, new Object[0]);
                    if (mobProduct != null) {
                        this.d.put(mobProduct.getProductTag(), mobProduct);
                    }
                } catch (Throwable th) {
                }
            }
        } catch (Throwable th2) {
            MobLog.getInstance().w(th2);
        }
        ArrayList<MobProduct> arrayList = new ArrayList();
        for (Entry value : this.d.entrySet()) {
            arrayList.add(value.getValue());
        }
        return arrayList;
    }

    public String a(ArrayList<MobProduct> arrayList) {
        try {
            Object invokeStaticMethod = ReflectHelper.invokeStaticMethod("DeviceHelper", "getInstance", this.c);
            String str = ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getPackageName", new Object[0]) + "/" + ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getAppVersionName", new Object[0]);
            String str2 = "";
            int size = arrayList.size();
            String str3 = str2;
            for (int i = 0; i < size; i++) {
                try {
                    if (str3.length() > 0) {
                        str2 = str3 + Token.SEPARATOR;
                    } else {
                        str2 = str3;
                    }
                    try {
                        MobProduct mobProduct = (MobProduct) arrayList.get(i);
                        str3 = str2 + mobProduct.getProductTag() + "/" + mobProduct.getSdkver();
                    } catch (Throwable th) {
                        str3 = str2;
                    }
                } catch (Throwable th2) {
                }
            }
            str2 = "Android/" + ReflectHelper.invokeInstanceMethod(invokeStaticMethod, "getOSVersionInt", new Object[0]);
            return str + Token.SEPARATOR + str3 + (str3.length() > 0 ? Token.SEPARATOR : "") + str2 + Token.SEPARATOR + TimeZone.getDefault().getID() + Token.SEPARATOR + ("Lang/" + Locale.getDefault().toString().replace("-r", Constants.ACCEPT_TIME_SEPARATOR_SERVER));
        } catch (Throwable th3) {
            MobLog.getInstance().w(th3);
            return "";
        }
    }

    public void a(MobProduct mobProduct) {
        if (mobProduct != null && !this.d.containsKey(mobProduct.getProductTag())) {
            this.d.put(mobProduct.getProductTag(), mobProduct);
        }
    }
}
