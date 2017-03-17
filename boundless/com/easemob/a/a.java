package com.easemob.a;

import android.annotation.SuppressLint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.Build.VERSION;
import android.os.Process;
import com.easemob.chat.EMChatConfig;
import com.easemob.chat.core.XmppConnectionManager;
import com.easemob.util.EMLog;
import org.jivesoftware.smack.XMPPConnection;

@SuppressLint({"NewApi"})
public class a {
    static final String a = "net";
    protected static a b = null;
    public a c = null;
    private final long d = 300000;

    class a extends Thread {
        int a;
        boolean b = false;
        final /* synthetic */ a c;

        a(a aVar) {
            this.c = aVar;
        }

        public void a(boolean z) {
            this.b = z;
        }

        public boolean a() {
            return this.b;
        }

        public void b() {
            long j;
            long uidTxPackets;
            long j2 = 0;
            long j3 = 0;
            long j4 = 0;
            long j5 = 0;
            long j6 = 0;
            long j7 = 0;
            long j8 = 0;
            long j9 = 0;
            this.b = true;
            this.a = Process.myUid();
            long uidRxBytes = TrafficStats.getUidRxBytes(this.a);
            long uidTxBytes = TrafficStats.getUidTxBytes(this.a);
            if (VERSION.SDK_INT >= 12) {
                j = uidRxBytes;
                uidRxBytes = uidTxBytes;
                uidTxBytes = TrafficStats.getUidRxPackets(this.a);
                uidTxPackets = TrafficStats.getUidTxPackets(this.a);
            } else {
                j = uidRxBytes;
                uidRxBytes = uidTxBytes;
                uidTxBytes = 0;
                uidTxPackets = 0;
            }
            while (this.b) {
                long uidRxBytes2 = TrafficStats.getUidRxBytes(this.a);
                long uidTxBytes2 = TrafficStats.getUidTxBytes(this.a);
                long j10 = uidRxBytes2 - j;
                long j11 = uidTxBytes2 - uidRxBytes;
                long j12 = j2 + j10;
                j2 = j3 + j11;
                if (VERSION.SDK_INT >= 12) {
                    j7 = TrafficStats.getUidRxPackets(this.a);
                    j8 = TrafficStats.getUidTxPackets(this.a);
                    j9 = j7 - uidTxBytes;
                    j6 = j8 - uidTxPackets;
                    j3 = j4 + j9;
                    j4 = j5 + j6;
                    j5 = j9;
                } else {
                    j3 = j4;
                    j4 = j5;
                    j5 = j6;
                    j6 = j7;
                    j7 = j8;
                    j8 = j9;
                }
                if (!(j10 == 0 && j11 == 0)) {
                    EMLog.d(a.a, new StringBuilder(String.valueOf(j11)).append(" bytes send; ").append(j10).append(" bytes received in ").append(300).append(" sec").toString());
                    if (VERSION.SDK_INT >= 12) {
                        EMLog.d(a.a, new StringBuilder(String.valueOf(j6)).append(" packets send; ").append(j5).append(" packets received in ").append(300).append(" sec").toString());
                    }
                    EMLog.d(a.a, "total:" + j2 + " bytes send; " + j12 + " bytes received");
                    if (VERSION.SDK_INT >= 12) {
                        EMLog.d(a.a, "total:" + j4 + " packets send; " + j3 + " packets received");
                    }
                    uidTxPackets = j8;
                    uidTxBytes = j7;
                    uidRxBytes = uidTxBytes2;
                    j = uidRxBytes2;
                }
                XmppConnectionManager instance = XmppConnectionManager.getInstance();
                if (instance != null) {
                    EMLog.d(a.a, "xmpp auto reconnect set to:" + instance.getAutoReconnect());
                    XMPPConnection connection = instance.getConnection();
                    if (connection == null) {
                        EMLog.d(a.a, "xmpp connection is null");
                    } else {
                        EMLog.d(a.a, "  xmpp connection is connected:" + connection.isConnected());
                    }
                }
                if (EMChatConfig.getInstance().getApplicationContext() != null) {
                    ConnectivityManager connectivityManager = (ConnectivityManager) EMChatConfig.getInstance().getApplicationContext().getSystemService("connectivity");
                    NetworkInfo networkInfo = connectivityManager.getNetworkInfo(0);
                    NetworkInfo networkInfo2 = connectivityManager.getNetworkInfo(1);
                    if (networkInfo != null) {
                        String str = a.a;
                        String str2 = str;
                        EMLog.d(str2, "mobile connection is available:" + networkInfo.isAvailable() + " isconnected:" + networkInfo.isConnected() + " isconnectedorconneting:" + networkInfo.isConnectedOrConnecting());
                    }
                    if (networkInfo2 != null) {
                        EMLog.d(a.a, "wifi connection is available:" + networkInfo2.isAvailable() + " isconnected:" + networkInfo2.isConnected() + " isconnectedorconneting:" + networkInfo2.isConnectedOrConnecting());
                    }
                }
                try {
                    Thread.sleep(300000);
                    j9 = j8;
                    j8 = j7;
                    j7 = j6;
                    j6 = j5;
                    j5 = j4;
                    j4 = j3;
                    j3 = j2;
                    j2 = j12;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    j9 = j8;
                    j8 = j7;
                    j7 = j6;
                    j6 = j5;
                    j5 = j4;
                    j4 = j3;
                    j3 = j2;
                    j2 = j12;
                }
            }
        }
    }

    protected a() {
    }

    public static void a() {
        if (b == null) {
            b = new a();
        }
        b.b();
    }

    public static void c() {
        if (b != null && b.c != null) {
            b.c.a(false);
            b.c = null;
        }
    }

    public void b() {
        if (this.c != null) {
            this.c.a(false);
            this.c = null;
        }
        this.c = new a(this);
        this.c.a(true);
        this.c.start();
    }
}
