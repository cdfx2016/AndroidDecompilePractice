package com.mob.commons.logcollector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Base64;
import com.mob.tools.MobLog;
import com.xiaomi.mipush.sdk.Constants;
import java.util.ArrayList;
import java.util.HashMap;

/* compiled from: MessageUtils */
public class f {
    public static synchronized long a(Context context, long j, String str, int i, String str2) throws Throwable {
        long j2;
        synchronized (f.class) {
            if (TextUtils.isEmpty(str)) {
                j2 = -1;
            } else {
                b a = b.a(context);
                ContentValues contentValues = new ContentValues();
                contentValues.put("exception_time", Long.valueOf(j));
                contentValues.put("exception_msg", str.toString());
                contentValues.put("exception_level", Integer.valueOf(i));
                contentValues.put("exception_md5", str2);
                j2 = a.a("table_exception", contentValues);
            }
        }
        return j2;
    }

    public static synchronized long a(Context context, ArrayList<String> arrayList) throws Throwable {
        long j;
        synchronized (f.class) {
            if (arrayList == null) {
                j = 0;
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < arrayList.size(); i++) {
                    stringBuilder.append("'");
                    stringBuilder.append((String) arrayList.get(i));
                    stringBuilder.append("'");
                    stringBuilder.append(Constants.ACCEPT_TIME_SEPARATOR_SP);
                }
                MobLog.getInstance().i("delete COUNT == %s", Integer.valueOf(b.a(context).a("table_exception", "exception_md5 in ( " + stringBuilder.toString().substring(0, stringBuilder.length() - 1) + " )", null)));
                j = (long) b.a(context).a("table_exception", "exception_md5 in ( " + stringBuilder.toString().substring(0, stringBuilder.length() - 1) + " )", null);
            }
        }
        return j;
    }

    private static synchronized ArrayList<e> a(Context context, String str, String[] strArr) throws Throwable {
        ArrayList<e> arrayList;
        synchronized (f.class) {
            arrayList = new ArrayList();
            e eVar = new e();
            b a = b.a(context);
            String str2 = " select exception_md5, exception_level, exception_time, exception_msg, sum(exception_counts) from table_exception group by exception_md5 having max(_id)";
            if (!(TextUtils.isEmpty(str) || strArr == null || strArr.length <= 0)) {
                str2 = " select exception_md5, exception_level, exception_time, exception_msg, sum(exception_counts) from table_exception where " + str + " group by exception_md5 having max(_id)";
            }
            Cursor a2 = a.a(str2, strArr);
            while (a2 != null && a2.moveToNext()) {
                eVar.b.add(a2.getString(0));
                HashMap hashMap = new HashMap();
                hashMap.put(MessageEncoder.ATTR_TYPE, Integer.valueOf(a2.getInt(1)));
                hashMap.put("errat", Long.valueOf(a2.getLong(2)));
                hashMap.put("msg", Base64.encodeToString(a2.getString(3).getBytes(), 2));
                hashMap.put("times", Integer.valueOf(a2.getInt(4)));
                eVar.a.add(hashMap);
                if (eVar.b.size() == 50) {
                    arrayList.add(eVar);
                    eVar = new e();
                    break;
                }
            }
            a2.close();
            if (eVar.b.size() != 0) {
                arrayList.add(eVar);
            }
        }
        return arrayList;
    }

    public static synchronized ArrayList<e> a(Context context, String[] strArr) throws Throwable {
        ArrayList<e> a;
        synchronized (f.class) {
            String str = "exception_level = ?";
            if (strArr == null || strArr.length <= 0) {
                str = null;
                strArr = null;
            }
            if (b.a(context).a("table_exception") > 0) {
                a = a(context, str, strArr);
            } else {
                a = new ArrayList();
            }
        }
        return a;
    }
}
