package org.jivesoftware.smack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import org.jivesoftware.smack.util.DNSUtil;
import org.jivesoftware.smack.util.dns.DNSJavaResolver;
import org.jivesoftware.smackx.ConfigureProviderManager;
import org.jivesoftware.smackx.InitStaticCode;
import org.xbill.DNS.ResolverConfig;

public class SmackAndroid {
    private static SmackAndroid sSmackAndroid = null;
    private BroadcastReceiver mConnectivityChangedReceiver;
    private Context mCtx;

    class ConnectivtyChangedReceiver extends BroadcastReceiver {
        ConnectivtyChangedReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            ResolverConfig.refresh();
        }
    }

    private SmackAndroid(Context context) {
        this.mCtx = context;
        DNSUtil.setDNSResolver(DNSJavaResolver.getInstance());
        InitStaticCode.initStaticCode(context);
        ConfigureProviderManager.configureProviderManager();
        maybeRegisterReceiver();
    }

    public static SmackAndroid init(Context context) {
        if (sSmackAndroid == null) {
            sSmackAndroid = new SmackAndroid(context);
        } else {
            sSmackAndroid.maybeRegisterReceiver();
        }
        return sSmackAndroid;
    }

    private void maybeRegisterReceiver() {
        if (this.mConnectivityChangedReceiver == null) {
            this.mConnectivityChangedReceiver = new ConnectivtyChangedReceiver();
            this.mCtx.registerReceiver(this.mConnectivityChangedReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        }
    }

    public void onDestroy() {
        if (this.mConnectivityChangedReceiver != null) {
            this.mCtx.unregisterReceiver(this.mConnectivityChangedReceiver);
            this.mConnectivityChangedReceiver = null;
        }
    }
}
