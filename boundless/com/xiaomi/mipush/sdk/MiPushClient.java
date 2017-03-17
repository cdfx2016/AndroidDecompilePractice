package com.xiaomi.mipush.sdk;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.ServiceInfo;
import android.text.TextUtils;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.xiaomi.channel.commonutils.android.b;
import com.xiaomi.channel.commonutils.android.e;
import com.xiaomi.channel.commonutils.android.g;
import com.xiaomi.channel.commonutils.android.h;
import com.xiaomi.channel.commonutils.android.j;
import com.xiaomi.channel.commonutils.misc.f;
import com.xiaomi.channel.commonutils.string.d;
import com.xiaomi.push.service.ah;
import com.xiaomi.push.service.aw;
import com.xiaomi.push.service.k;
import com.xiaomi.push.service.receivers.NetworkStatusReceiver;
import com.xiaomi.xmpush.thrift.ae;
import com.xiaomi.xmpush.thrift.af;
import com.xiaomi.xmpush.thrift.ak;
import com.xiaomi.xmpush.thrift.am;
import com.xiaomi.xmpush.thrift.ao;
import com.xiaomi.xmpush.thrift.r;
import com.xiaomi.xmpush.thrift.t;
import com.xiaomi.xmpush.thrift.z;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import org.apache.thrift.a;

public abstract class MiPushClient {
    public static final String COMMAND_REGISTER = "register";
    public static final String COMMAND_SET_ACCEPT_TIME = "accept-time";
    public static final String COMMAND_SET_ACCOUNT = "set-account";
    public static final String COMMAND_SET_ALIAS = "set-alias";
    public static final String COMMAND_SUBSCRIBE_TOPIC = "subscribe-topic";
    public static final String COMMAND_UNSET_ACCOUNT = "unset-account";
    public static final String COMMAND_UNSET_ALIAS = "unset-alias";
    public static final String COMMAND_UNSUBSCRIBE_TOPIC = "unsubscibe-topic";
    public static final String PREF_EXTRA = "mipush_extra";
    private static z mSyncMIIDHelper;
    private static Context sContext;
    private static long sCurMsgId = System.currentTimeMillis();

    @Deprecated
    public static abstract class MiPushClientCallback {
        private String category;

        protected String getCategory() {
            return this.category;
        }

        public void onCommandResult(String str, long j, String str2, List<String> list) {
        }

        public void onInitializeResult(long j, String str, String str2) {
        }

        public void onReceiveMessage(MiPushMessage miPushMessage) {
        }

        public void onReceiveMessage(String str, String str2, String str3, boolean z) {
        }

        public void onSubscribeResult(long j, String str, String str2) {
        }

        public void onUnsubscribeResult(long j, String str, String str2) {
        }

        protected void setCategory(String str) {
            this.category = str;
        }
    }

    private static boolean acceptTimeSet(Context context, String str, String str2) {
        return TextUtils.equals(getAcceptTime(context), str + Constants.ACCEPT_TIME_SEPARATOR_SP + str2);
    }

    public static long accountSetTime(Context context, String str) {
        return context.getSharedPreferences(PREF_EXTRA, 0).getLong("account_" + str, -1);
    }

    static synchronized void addAcceptTime(Context context, String str, String str2) {
        synchronized (MiPushClient.class) {
            context.getSharedPreferences(PREF_EXTRA, 0).edit().putString(Constants.EXTRA_KEY_ACCEPT_TIME, str + Constants.ACCEPT_TIME_SEPARATOR_SP + str2).commit();
        }
    }

    static synchronized void addAccount(Context context, String str) {
        synchronized (MiPushClient.class) {
            context.getSharedPreferences(PREF_EXTRA, 0).edit().putLong("account_" + str, System.currentTimeMillis()).commit();
        }
    }

    static synchronized void addAlias(Context context, String str) {
        synchronized (MiPushClient.class) {
            context.getSharedPreferences(PREF_EXTRA, 0).edit().putLong("alias_" + str, System.currentTimeMillis()).commit();
        }
    }

    private static void addPullNotificationTime(Context context) {
        context.getSharedPreferences(PREF_EXTRA, 0).edit().putLong("last_pull_notification", System.currentTimeMillis()).commit();
    }

    private static void addRegRequestTime(Context context) {
        context.getSharedPreferences(PREF_EXTRA, 0).edit().putLong("last_reg_request", System.currentTimeMillis()).commit();
    }

    static synchronized void addTopic(Context context, String str) {
        synchronized (MiPushClient.class) {
            context.getSharedPreferences(PREF_EXTRA, 0).edit().putLong("topic_" + str, System.currentTimeMillis()).commit();
        }
    }

    public static long aliasSetTime(Context context, String str) {
        return context.getSharedPreferences(PREF_EXTRA, 0).getLong("alias_" + str, -1);
    }

    public static void awakeApps(Context context, String[] strArr) {
        new Thread(new n(strArr, context)).start();
    }

    private static void awakePushServiceByPackageInfo(Context context, PackageInfo packageInfo) {
        ServiceInfo[] serviceInfoArr = packageInfo.services;
        if (serviceInfoArr != null) {
            for (ServiceInfo serviceInfo : serviceInfoArr) {
                if (serviceInfo.exported && serviceInfo.enabled && "com.xiaomi.mipush.sdk.PushMessageHandler".equals(serviceInfo.name) && !context.getPackageName().equals(serviceInfo.packageName)) {
                    try {
                        Thread.sleep(((long) ((Math.random() * 2.0d) + 1.0d)) * 1000);
                        Intent intent = new Intent();
                        intent.setClassName(serviceInfo.packageName, serviceInfo.name);
                        intent.setAction("com.xiaomi.mipush.sdk.WAKEUP");
                        intent.putExtra("waker_pkgname", context.getPackageName());
                        context.startService(intent);
                        return;
                    } catch (Throwable th) {
                        return;
                    }
                }
            }
        }
    }

    private static void checkNotNull(Object obj, String str) {
        if (obj == null) {
            throw new IllegalArgumentException("param " + str + " is not nullable");
        }
    }

    protected static void clearExtras(Context context) {
        Editor edit = context.getSharedPreferences(PREF_EXTRA, 0).edit();
        edit.clear();
        edit.commit();
    }

    public static void clearLocalNotificationType(Context context) {
        u.a(context).f();
    }

    public static void clearNotification(Context context) {
        u.a(context).a(-1);
    }

    public static void clearNotification(Context context, int i) {
        u.a(context).a(i);
    }

    public static void clearNotification(Context context, String str, String str2) {
        u.a(context).a(str, str2);
    }

    public static void disablePush(Context context) {
        u.a(context).a(true);
    }

    public static void enablePush(Context context) {
        u.a(context).a(false);
    }

    protected static synchronized String generatePacketID() {
        String str;
        synchronized (MiPushClient.class) {
            str = d.a(4) + sCurMsgId;
            sCurMsgId++;
        }
        return str;
    }

    protected static String getAcceptTime(Context context) {
        return context.getSharedPreferences(PREF_EXTRA, 0).getString(Constants.EXTRA_KEY_ACCEPT_TIME, "00:00-23:59");
    }

    public static List<String> getAllAlias(Context context) {
        List<String> arrayList = new ArrayList();
        for (String str : context.getSharedPreferences(PREF_EXTRA, 0).getAll().keySet()) {
            if (str.startsWith("alias_")) {
                arrayList.add(str.substring("alias_".length()));
            }
        }
        return arrayList;
    }

    public static List<String> getAllTopic(Context context) {
        List<String> arrayList = new ArrayList();
        for (String str : context.getSharedPreferences(PREF_EXTRA, 0).getAll().keySet()) {
            if (str.startsWith("topic_") && !str.contains("**ALL**")) {
                arrayList.add(str.substring("topic_".length()));
            }
        }
        return arrayList;
    }

    public static List<String> getAllUserAccount(Context context) {
        List<String> arrayList = new ArrayList();
        for (String str : context.getSharedPreferences(PREF_EXTRA, 0).getAll().keySet()) {
            if (str.startsWith("account_")) {
                arrayList.add(str.substring("account_".length()));
            }
        }
        return arrayList;
    }

    private static boolean getDefaultSwitch() {
        return g.b();
    }

    public static String getRegId(Context context) {
        return a.a(context).i() ? a.a(context).e() : null;
    }

    @Deprecated
    public static void initialize(Context context, String str, String str2, MiPushClientCallback miPushClientCallback) {
        boolean z = false;
        checkNotNull(context, "context");
        checkNotNull(str, "appID");
        checkNotNull(str2, "appToken");
        try {
            if (aw.a().b()) {
                aw.a().a(context);
            }
            aw.a().a(new ab(context), "UPLOADER_FROM_MIPUSHCLIENT");
            sContext = context.getApplicationContext();
            if (sContext == null) {
                sContext = context;
            }
            if (miPushClientCallback != null) {
                PushMessageHandler.a(miPushClientCallback);
            }
            if (j.b(context)) {
                h.a(context);
            }
            if (a.a(sContext).m() != Constants.a()) {
                z = true;
            }
            if (z || shouldSendRegRequest(sContext)) {
                if (z || !a.a(sContext).a(str, str2) || a.a(sContext).n()) {
                    String a = d.a(6);
                    a.a(sContext).h();
                    a.a(sContext).a(Constants.a());
                    a.a(sContext).a(str, str2, a);
                    clearExtras(sContext);
                    af afVar = new af();
                    afVar.a(generatePacketID());
                    afVar.b(str);
                    afVar.e(str2);
                    afVar.d(context.getPackageName());
                    afVar.f(a);
                    afVar.c(b.a(context, context.getPackageName()));
                    afVar.b(b.b(context, context.getPackageName()));
                    afVar.g("3_2_1");
                    afVar.a(30201);
                    afVar.h(e.b(sContext));
                    afVar.a(t.Init);
                    a = e.d(sContext);
                    if (!TextUtils.isEmpty(a)) {
                        if (g.b()) {
                            afVar.i(a);
                        }
                        afVar.k(d.a(a));
                    }
                    afVar.j(e.a());
                    int b = e.b();
                    if (b >= 0) {
                        afVar.c(b);
                    }
                    u.a(sContext).a(afVar, z);
                } else {
                    if (1 == PushMessageHelper.getPushMode(context)) {
                        checkNotNull(miPushClientCallback, "callback");
                        miPushClientCallback.onInitializeResult(0, null, a.a(context).e());
                    } else {
                        List arrayList = new ArrayList();
                        arrayList.add(a.a(context).e());
                        PushMessageHelper.sendCommandMessageBroadcast(sContext, PushMessageHelper.generateCommandMessage(COMMAND_REGISTER, arrayList, 0, null, null));
                    }
                    u.a(context).a();
                    if (a.a(sContext).a()) {
                        a aeVar = new ae();
                        aeVar.b(a.a(context).c());
                        aeVar.c("client_info_update");
                        aeVar.a(generatePacketID());
                        aeVar.h = new HashMap();
                        aeVar.h.put(Constants.EXTRA_KEY_APP_VERSION, b.a(sContext, sContext.getPackageName()));
                        aeVar.h.put(Constants.EXTRA_KEY_APP_VERSION_CODE, Integer.toString(b.b(sContext, sContext.getPackageName())));
                        aeVar.h.put("push_sdk_vn", "3_2_1");
                        aeVar.h.put("push_sdk_vc", Integer.toString(30201));
                        CharSequence g = a.a(sContext).g();
                        if (!TextUtils.isEmpty(g)) {
                            aeVar.h.put("deviceid", g);
                        }
                        u.a(context).a(aeVar, com.xiaomi.xmpush.thrift.a.Notification, false, null);
                    }
                    if (!h.a(sContext, "update_devId", false)) {
                        updateIMEI();
                        h.b(sContext, "update_devId", true);
                    }
                    if (shouldUseMIUIPush(sContext) && shouldPullNotification(sContext)) {
                        a aeVar2 = new ae();
                        aeVar2.b(a.a(sContext).c());
                        aeVar2.c("pull");
                        aeVar2.a(generatePacketID());
                        aeVar2.a(false);
                        u.a(sContext).a(aeVar2, com.xiaomi.xmpush.thrift.a.Notification, false, null, false);
                        addPullNotificationTime(sContext);
                    }
                }
                addRegRequestTime(sContext);
                scheduleOcVersionCheckJob();
                scheduleGeoFenceRepeatJobs();
                loadPlugin();
                x.a(sContext);
                if (mSyncMIIDHelper == null) {
                    mSyncMIIDHelper = new z(sContext);
                }
                mSyncMIIDHelper.a(sContext);
                if ("disable_syncing".equals(p.a(sContext).a())) {
                    disablePush(sContext);
                }
                if ("enable_syncing".equals(p.a(sContext).a())) {
                    enablePush(sContext);
                    return;
                }
                return;
            }
            u.a(context).a();
            com.xiaomi.channel.commonutils.logger.b.a("Could not send  register message within 5s repeatly .");
        } catch (Exception e) {
            com.xiaomi.channel.commonutils.logger.b.d(e.toString());
        } catch (Throwable th) {
            com.xiaomi.channel.commonutils.logger.b.a(th);
        }
    }

    private static void loadPlugin() {
        if (ah.a(sContext).a(com.xiaomi.xmpush.thrift.e.DataCollectionSwitch.a(), getDefaultSwitch())) {
            f.a(sContext).a(new l(), 10);
        }
    }

    public static void pausePush(Context context, String str) {
        setAcceptTime(context, 0, 0, 0, 0, str);
    }

    static void reInitialize(Context context, t tVar) {
        if (a.a(context).i()) {
            String a = d.a(6);
            String c = a.a(context).c();
            String d = a.a(context).d();
            a.a(context).h();
            a.a(context).a(c, d, a);
            af afVar = new af();
            afVar.a(generatePacketID());
            afVar.b(c);
            afVar.e(d);
            afVar.f(a);
            afVar.d(context.getPackageName());
            afVar.c(b.a(context, context.getPackageName()));
            afVar.a(tVar);
            u.a(context).a(afVar, false);
        }
    }

    private static void registerNetworkReceiver(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addCategory("android.intent.category.DEFAULT");
        context.registerReceiver(new NetworkStatusReceiver(null), intentFilter);
    }

    public static void registerPush(Context context, String str, String str2) {
        if (!NetworkStatusReceiver.a()) {
            registerNetworkReceiver(context);
        }
        new Thread(new k(context, str, str2)).start();
    }

    static synchronized void removeAcceptTime(Context context) {
        synchronized (MiPushClient.class) {
            context.getSharedPreferences(PREF_EXTRA, 0).edit().remove(Constants.EXTRA_KEY_ACCEPT_TIME).commit();
        }
    }

    static synchronized void removeAccount(Context context, String str) {
        synchronized (MiPushClient.class) {
            context.getSharedPreferences(PREF_EXTRA, 0).edit().remove("account_" + str).commit();
        }
    }

    static synchronized void removeAlias(Context context, String str) {
        synchronized (MiPushClient.class) {
            context.getSharedPreferences(PREF_EXTRA, 0).edit().remove("alias_" + str).commit();
        }
    }

    static synchronized void removeAllAccounts(Context context) {
        synchronized (MiPushClient.class) {
            for (String removeAccount : getAllUserAccount(context)) {
                removeAccount(context, removeAccount);
            }
        }
    }

    static synchronized void removeAllAliases(Context context) {
        synchronized (MiPushClient.class) {
            for (String removeAlias : getAllAlias(context)) {
                removeAlias(context, removeAlias);
            }
        }
    }

    static synchronized void removeAllTopics(Context context) {
        synchronized (MiPushClient.class) {
            for (String removeTopic : getAllTopic(context)) {
                removeTopic(context, removeTopic);
            }
        }
    }

    static synchronized void removeTopic(Context context, String str) {
        synchronized (MiPushClient.class) {
            context.getSharedPreferences(PREF_EXTRA, 0).edit().remove("topic_" + str).commit();
        }
    }

    static void reportIgnoreRegMessageClicked(Context context, String str, r rVar, String str2, String str3) {
        a aeVar = new ae();
        if (TextUtils.isEmpty(str3)) {
            com.xiaomi.channel.commonutils.logger.b.d("do not report clicked message");
            return;
        }
        aeVar.b(str3);
        aeVar.c("bar:click");
        aeVar.a(str);
        aeVar.a(false);
        u.a(context).a(aeVar, com.xiaomi.xmpush.thrift.a.Notification, false, true, rVar, true, str2, str3);
    }

    public static void reportMessageClicked(Context context, MiPushMessage miPushMessage) {
        r rVar = new r();
        rVar.a(miPushMessage.getMessageId());
        rVar.b(miPushMessage.getTopic());
        rVar.d(miPushMessage.getDescription());
        rVar.c(miPushMessage.getTitle());
        rVar.c(miPushMessage.getNotifyId());
        rVar.a(miPushMessage.getNotifyType());
        rVar.b(miPushMessage.getPassThrough());
        rVar.a(miPushMessage.getExtra());
        reportMessageClicked(context, miPushMessage.getMessageId(), rVar, null);
    }

    @Deprecated
    public static void reportMessageClicked(Context context, String str) {
        reportMessageClicked(context, str, null, null);
    }

    static void reportMessageClicked(Context context, String str, r rVar, String str2) {
        Object aeVar = new ae();
        if (!TextUtils.isEmpty(str2)) {
            aeVar.b(str2);
        } else if (a.a(context).b()) {
            aeVar.b(a.a(context).c());
        } else {
            com.xiaomi.channel.commonutils.logger.b.d("do not report clicked message");
            return;
        }
        aeVar.c("bar:click");
        aeVar.a(str);
        aeVar.a(false);
        u.a(context).a(aeVar, com.xiaomi.xmpush.thrift.a.Notification, false, rVar);
    }

    public static void resumePush(Context context, String str) {
        setAcceptTime(context, 0, 0, 23, 59, str);
    }

    private static void scheduleGeoFenceRepeatJobs() {
        if (com.xiaomi.push.service.h.b(sContext)) {
            f.a(sContext).a(new d(sContext), ah.a(sContext).a(com.xiaomi.xmpush.thrift.e.UploadWIFIGeoLocFrequency.a(), 900));
        }
    }

    private static void scheduleOcVersionCheckJob() {
        f.a(sContext).a(new o(sContext), ah.a(sContext).a(com.xiaomi.xmpush.thrift.e.OcVersionCheckFrequency.a(), 86400), 5);
    }

    public static void setAcceptTime(Context context, int i, int i2, int i3, int i4, String str) {
        if (i < 0 || i >= 24 || i3 < 0 || i3 >= 24 || i2 < 0 || i2 >= 60 || i4 < 0 || i4 >= 60) {
            throw new IllegalArgumentException("the input parameter is not valid.");
        }
        long rawOffset = (long) (((TimeZone.getTimeZone("GMT+08").getRawOffset() - TimeZone.getDefault().getRawOffset()) / 1000) / 60);
        long j = ((((long) ((i * 60) + i2)) + rawOffset) + 1440) % 1440;
        rawOffset = ((rawOffset + ((long) ((i3 * 60) + i4))) + 1440) % 1440;
        ArrayList arrayList = new ArrayList();
        arrayList.add(String.format("%1$02d:%2$02d", new Object[]{Long.valueOf(j / 60), Long.valueOf(j % 60)}));
        arrayList.add(String.format("%1$02d:%2$02d", new Object[]{Long.valueOf(rawOffset / 60), Long.valueOf(rawOffset % 60)}));
        List arrayList2 = new ArrayList();
        arrayList2.add(String.format("%1$02d:%2$02d", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)}));
        arrayList2.add(String.format("%1$02d:%2$02d", new Object[]{Integer.valueOf(i3), Integer.valueOf(i4)}));
        if (!acceptTimeSet(context, (String) arrayList.get(0), (String) arrayList.get(1))) {
            setCommand(context, COMMAND_SET_ACCEPT_TIME, arrayList, str);
        } else if (1 == PushMessageHelper.getPushMode(context)) {
            PushMessageHandler.a(context, str, COMMAND_SET_ACCEPT_TIME, 0, null, arrayList2);
        } else {
            PushMessageHelper.sendCommandMessageBroadcast(context, PushMessageHelper.generateCommandMessage(COMMAND_SET_ACCEPT_TIME, arrayList2, 0, null, null));
        }
    }

    public static void setAlias(Context context, String str, String str2) {
        if (!TextUtils.isEmpty(str)) {
            setCommand(context, COMMAND_SET_ALIAS, str, str2);
        }
    }

    protected static void setCommand(Context context, String str, String str2, String str3) {
        ArrayList arrayList = new ArrayList();
        if (!TextUtils.isEmpty(str2)) {
            arrayList.add(str2);
        }
        if (!COMMAND_SET_ALIAS.equalsIgnoreCase(str) || Math.abs(System.currentTimeMillis() - aliasSetTime(context, str2)) >= 3600000) {
            if (COMMAND_UNSET_ALIAS.equalsIgnoreCase(str) && aliasSetTime(context, str2) < 0) {
                com.xiaomi.channel.commonutils.logger.b.a("Don't cancel alias for " + arrayList + " is unseted");
            } else if (!COMMAND_SET_ACCOUNT.equalsIgnoreCase(str) || Math.abs(System.currentTimeMillis() - accountSetTime(context, str2)) >= 3600000) {
                if (!COMMAND_UNSET_ACCOUNT.equalsIgnoreCase(str) || accountSetTime(context, str2) >= 0) {
                    setCommand(context, str, arrayList, str3);
                } else {
                    com.xiaomi.channel.commonutils.logger.b.a("Don't cancel account for " + arrayList + " is unseted");
                }
            } else if (1 == PushMessageHelper.getPushMode(context)) {
                PushMessageHandler.a(context, str3, str, 0, null, arrayList);
            } else {
                PushMessageHelper.sendCommandMessageBroadcast(context, PushMessageHelper.generateCommandMessage(COMMAND_SET_ACCOUNT, arrayList, 0, null, null));
            }
        } else if (1 == PushMessageHelper.getPushMode(context)) {
            PushMessageHandler.a(context, str3, str, 0, null, arrayList);
        } else {
            PushMessageHelper.sendCommandMessageBroadcast(context, PushMessageHelper.generateCommandMessage(COMMAND_SET_ALIAS, arrayList, 0, null, null));
        }
    }

    protected static void setCommand(Context context, String str, ArrayList<String> arrayList, String str2) {
        if (!TextUtils.isEmpty(a.a(context).c())) {
            a zVar = new z();
            zVar.a(generatePacketID());
            zVar.b(a.a(context).c());
            zVar.c(str);
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                zVar.d((String) it.next());
            }
            zVar.f(str2);
            zVar.e(context.getPackageName());
            u.a(context).a(zVar, com.xiaomi.xmpush.thrift.a.Command, null);
        }
    }

    public static void setLocalNotificationType(Context context, int i) {
        u.a(context).b(i & -1);
    }

    public static void setUserAccount(Context context, String str, String str2) {
        if (!TextUtils.isEmpty(str)) {
            setCommand(context, COMMAND_SET_ACCOUNT, str, str2);
        }
    }

    private static boolean shouldPullNotification(Context context) {
        return Math.abs(System.currentTimeMillis() - context.getSharedPreferences(PREF_EXTRA, 0).getLong("last_pull_notification", -1)) > 300000;
    }

    private static boolean shouldSendRegRequest(Context context) {
        return Math.abs(System.currentTimeMillis() - context.getSharedPreferences(PREF_EXTRA, 0).getLong("last_reg_request", -1)) > ExoPlayerFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
    }

    public static boolean shouldUseMIUIPush(Context context) {
        return u.a(context).c();
    }

    public static void subscribe(Context context, String str, String str2) {
        if (!TextUtils.isEmpty(a.a(context).c()) && !TextUtils.isEmpty(str)) {
            if (Math.abs(System.currentTimeMillis() - topicSubscribedTime(context, str)) > 86400000) {
                a akVar = new ak();
                akVar.a(generatePacketID());
                akVar.b(a.a(context).c());
                akVar.c(str);
                akVar.d(context.getPackageName());
                akVar.e(str2);
                u.a(context).a(akVar, com.xiaomi.xmpush.thrift.a.Subscription, null);
            } else if (1 == PushMessageHelper.getPushMode(context)) {
                PushMessageHandler.a(context, str2, 0, null, str);
            } else {
                List arrayList = new ArrayList();
                arrayList.add(str);
                PushMessageHelper.sendCommandMessageBroadcast(context, PushMessageHelper.generateCommandMessage(COMMAND_SUBSCRIBE_TOPIC, arrayList, 0, null, null));
            }
        }
    }

    public static long topicSubscribedTime(Context context, String str) {
        return context.getSharedPreferences(PREF_EXTRA, 0).getLong("topic_" + str, -1);
    }

    public static void unregisterPush(Context context) {
        if (a.a(context).b()) {
            am amVar = new am();
            amVar.a(generatePacketID());
            amVar.b(a.a(context).c());
            amVar.c(a.a(context).e());
            amVar.e(a.a(context).d());
            amVar.d(context.getPackageName());
            u.a(context).a(amVar);
            PushMessageHandler.a();
            a.a(context).k();
            clearExtras(context);
            clearLocalNotificationType(context);
            clearNotification(context);
            if (mSyncMIIDHelper != null) {
                k.a(context).b(mSyncMIIDHelper);
            }
        }
    }

    public static void unsetAlias(Context context, String str, String str2) {
        setCommand(context, COMMAND_UNSET_ALIAS, str, str2);
    }

    public static void unsetUserAccount(Context context, String str, String str2) {
        setCommand(context, COMMAND_UNSET_ACCOUNT, str, str2);
    }

    public static void unsubscribe(Context context, String str, String str2) {
        if (!a.a(context).b()) {
            return;
        }
        if (topicSubscribedTime(context, str) < 0) {
            com.xiaomi.channel.commonutils.logger.b.a("Don't cancel subscribe for " + str + " is unsubscribed");
            return;
        }
        a aoVar = new ao();
        aoVar.a(generatePacketID());
        aoVar.b(a.a(context).c());
        aoVar.c(str);
        aoVar.d(context.getPackageName());
        aoVar.e(str2);
        u.a(context).a(aoVar, com.xiaomi.xmpush.thrift.a.UnSubscription, null);
    }

    private static void updateIMEI() {
        new Thread(new m()).start();
    }
}
