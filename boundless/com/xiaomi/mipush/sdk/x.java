package com.xiaomi.mipush.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.logger.b;
import com.xiaomi.channel.commonutils.misc.f;
import com.xiaomi.channel.commonutils.string.d;
import com.xiaomi.push.service.ah;
import com.xiaomi.xmpush.thrift.ae;
import com.xiaomi.xmpush.thrift.e;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class x {
    public static void a(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MiPushClient.PREF_EXTRA, 0);
        long j = sharedPreferences.getLong("last_sync_info", -1);
        long currentTimeMillis = System.currentTimeMillis() / 1000;
        long a = (long) ah.a(context).a(e.SyncInfoFrequency.a(), 1209600);
        if (j == -1) {
            sharedPreferences.edit().putLong("last_sync_info", currentTimeMillis).commit();
        } else if (Math.abs(currentTimeMillis - j) > a) {
            a(context, true);
            sharedPreferences.edit().putLong("last_sync_info", currentTimeMillis).commit();
        }
    }

    public static void a(Context context, ae aeVar) {
        b.a("need to update local info with: " + aeVar.i());
        String str = (String) aeVar.i().get(Constants.EXTRA_KEY_ACCEPT_TIME);
        if (str != null) {
            MiPushClient.removeAcceptTime(context);
            String[] split = str.split(Constants.ACCEPT_TIME_SEPARATOR_SERVER);
            if (split.length == 2) {
                MiPushClient.addAcceptTime(context, split[0], split[1]);
                if ("00:00".equals(split[0]) && "00:00".equals(split[1])) {
                    a.a(context).a(true);
                } else {
                    a.a(context).a(false);
                }
            }
        }
        str = (String) aeVar.i().get(Constants.EXTRA_KEY_ALIASES);
        if (str != null) {
            MiPushClient.removeAllAliases(context);
            if (!"".equals(str)) {
                for (String addAlias : str.split(Constants.ACCEPT_TIME_SEPARATOR_SP)) {
                    MiPushClient.addAlias(context, addAlias);
                }
            }
        }
        str = (String) aeVar.i().get(Constants.EXTRA_KEY_TOPICS);
        if (str != null) {
            MiPushClient.removeAllTopics(context);
            if (!"".equals(str)) {
                for (String addAlias2 : str.split(Constants.ACCEPT_TIME_SEPARATOR_SP)) {
                    MiPushClient.addTopic(context, addAlias2);
                }
            }
        }
        str = (String) aeVar.i().get(Constants.EXTRA_KEY_ACCOUNTS);
        if (str != null) {
            MiPushClient.removeAllAccounts(context);
            if (!"".equals(str)) {
                for (String addAccount : str.split(Constants.ACCEPT_TIME_SEPARATOR_SP)) {
                    MiPushClient.addAccount(context, addAccount);
                }
            }
        }
    }

    public static void a(Context context, boolean z) {
        f.a(context).a(new y(context, z));
    }

    private static String c(List<String> list) {
        String a = d.a(d(list));
        return (TextUtils.isEmpty(a) || a.length() <= 4) ? "" : a.substring(0, 4).toLowerCase();
    }

    private static String d(List<String> list) {
        if (com.xiaomi.channel.commonutils.misc.b.a(list)) {
            return "";
        }
        List<String> arrayList = new ArrayList(list);
        Collections.sort(arrayList, Collator.getInstance(Locale.CHINA));
        String str = "";
        for (String str2 : arrayList) {
            if (!TextUtils.isEmpty(str)) {
                str = str + Constants.ACCEPT_TIME_SEPARATOR_SP;
            }
            str = str + str2;
        }
        return str;
    }
}
