package com.easemob.chat.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build.VERSION;
import com.easemob.chat.EMChatConfig;
import com.easemob.chat.a.a.a;
import com.easemob.exceptions.EMAuthenticationException;
import com.easemob.exceptions.EMNetworkUnconnectedException;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.NetUtils;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import java.io.File;
import org.apache.qpid.management.common.sasl.Constants;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.entitycaps.EntityCapsManager;
import org.jivesoftware.smackx.packet.ChatStateExtension.Provider;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.packet.MessageEvent;
import org.jivesoftware.smackx.packet.Nick;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.VCardProvider;

public class XmppConnectionManager {
    private static final String RESOURCE = "mobile";
    private static final String TAG = XmppConnectionManager.class.getSimpleName();
    private static XmppConnectionManager instance = null;
    private static String xmppResource = null;
    int XMPP_PORT = 5222;
    boolean autoreconnect = false;
    private final String bareJid;
    private ConnectionListener chatConnectionListener = null;
    private XMPPConnection connection;
    private ConnectionConfiguration connectionConfig;
    private final XmppConnectionListener connectionListener = new XmppConnectionListener();
    private BroadcastReceiver connectivityBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE") && !intent.getBooleanExtra("noConnectivity", false) && context != null) {
                if (!NetUtils.hasDataConnection(context)) {
                    EMLog.d(XmppConnectionManager.TAG, "in connectivity broadcast, skip since no data connection");
                } else if (context != null) {
                    context.unregisterReceiver(XmppConnectionManager.this.connectivityBroadcastReceiver);
                    EMLog.i(XmppConnectionManager.TAG, " [x] Re-enable auto-reconnecting as the network is restored.  '");
                    XmppConnectionManager.this.setAutoReconnect(true);
                    XmppConnectionManager.this.autoreconnect = true;
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                XmppConnectionManager.this.reconnectSync();
                            } catch (EaseMobException e) {
                                EMLog.i(XmppConnectionManager.TAG, "EaseMobService reconnecting failed: " + e);
                            }
                        }
                    }).start();
                }
            }
        }
    };
    private Context context;
    private final String host;
    private boolean isForcedDisconnect = false;
    private final String password;
    private final PingListener pingListener = new PingListener();

    private class PingListener implements PacketListener {
        private PingListener() {
        }

        public void processPacket(Packet packet) {
            EMLog.d(XmppConnectionManager.TAG, "received ping packet from :" + packet.getFrom());
            if (packet instanceof a) {
                a aVar = (a) packet;
                if (aVar.getType() == Type.GET) {
                    Packet aVar2 = new a();
                    aVar2.setType(Type.RESULT);
                    aVar2.setTo(aVar.getFrom());
                    aVar2.setPacketID(aVar.getPacketID());
                    XmppConnectionManager.this.connection.sendPacket(aVar2);
                }
            }
        }
    }

    private class XmppConnectionListener implements ConnectionListener {
        private XmppConnectionListener() {
        }

        public void connectionClosed() {
            EMLog.e(XmppConnectionManager.TAG, "connectionClosed");
            XmppConnectionManager.this.onDisconnected();
        }

        public void connectionClosedOnError(Exception exception) {
            EMLog.e(XmppConnectionManager.TAG, "connectionClosedOnError in " + exception);
            if (!(exception == null || exception.getMessage() == null || !exception.getMessage().contains("conflict"))) {
                XmppConnectionManager.this.setAutoReconnect(false);
                EMLog.e(XmppConnectionManager.TAG, "connection closed caused by conflict. set autoreconnect to false");
            }
            XmppConnectionManager.this.onDisconnected();
        }

        public void reconnectingIn(int i) {
            EMLog.d(XmppConnectionManager.TAG, "reconnectingIn in " + i);
        }

        public void reconnectionFailed(Exception exception) {
            EMLog.e(XmppConnectionManager.TAG, "xmpp con mgr reconnectionFailed:" + exception);
            XmppConnectionManager.this.onDisconnected();
        }

        public void reconnectionSuccessful() {
            EMLog.d(XmppConnectionManager.TAG, "reconnectionSuccessful");
            EMLog.d(XmppConnectionManager.TAG, "send available presence after reconnected");
            XmppConnectionManager.this.connection.sendPacket(new Presence(Presence.Type.available));
        }
    }

    public XmppConnectionManager(String str, String str2, String str3, Context context) {
        this.bareJid = str;
        this.password = str2;
        this.host = str3;
        this.context = context;
        initConnectionConfig();
        this.connection = new XMPPConnection(this.connectionConfig);
        EntityCapsManager.getInstanceFor(this.connection).disableEntityCaps();
        instance = this;
    }

    private void configure(ProviderManager providerManager) {
        EMLog.d(TAG, "configure");
        ReconnectionManager.class.getConstructors();
        providerManager.addIQProvider("query", DiscoverItems.NAMESPACE, new DiscoverItemsProvider());
        providerManager.addIQProvider("query", DiscoverInfo.NAMESPACE, new DiscoverInfoProvider());
        providerManager.addExtensionProvider("delay", "urn:xmpp:delay", new DelayInfoProvider());
        providerManager.addIQProvider("query", DiscoverItems.NAMESPACE, new DiscoverItemsProvider());
        providerManager.addIQProvider("query", DiscoverInfo.NAMESPACE, new DiscoverInfoProvider());
        Provider provider = new Provider();
        providerManager.addExtensionProvider("active", "http://jabber.org/protocol/chatstates", provider);
        providerManager.addExtensionProvider(MessageEvent.COMPOSING, "http://jabber.org/protocol/chatstates", provider);
        providerManager.addExtensionProvider("paused", "http://jabber.org/protocol/chatstates", provider);
        providerManager.addExtensionProvider("inactive", "http://jabber.org/protocol/chatstates", provider);
        providerManager.addExtensionProvider("gone", "http://jabber.org/protocol/chatstates", provider);
        providerManager.addIQProvider("ping", "urn:xmpp:ping", a.class);
        ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new VCardProvider());
        providerManager.addExtensionProvider("x", "http://jabber.org/protocol/muc#user", new MUCUserProvider());
        providerManager.addIQProvider("query", "http://jabber.org/protocol/muc#admin", new MUCAdminProvider());
        providerManager.addIQProvider("query", "http://jabber.org/protocol/muc#owner", new MUCOwnerProvider());
        providerManager.addExtensionProvider("x", GroupChatInvitation.NAMESPACE, new GroupChatInvitation.Provider());
        providerManager.addIQProvider(MessageEvent.OFFLINE, "http://jabber.org/protocol/offline", new OfflineMessageRequest.Provider());
        providerManager.addExtensionProvider(MessageEvent.OFFLINE, "http://jabber.org/protocol/offline", new OfflineMessageInfo.Provider());
        providerManager.addExtensionProvider("x", Form.NAMESPACE, new DataFormProvider());
    }

    private void discoverServerFeatures() {
        try {
            ServiceDiscoveryManager.getInstanceFor(this.connection).discoverInfo(this.connection.getServiceName());
        } catch (Throwable e) {
            EMLog.w(TAG, "Unable to discover server features", e);
        }
    }

    public static XmppConnectionManager getInstance() {
        return instance;
    }

    public static String getXmppResource(Context context) {
        if (xmppResource == null) {
            xmppResource = RESOURCE;
        }
        return xmppResource;
    }

    private void initConnection() throws EMNetworkUnconnectedException {
        if (this.connection.isConnected()) {
            this.connection.addConnectionListener(this.connectionListener);
            initFeatures();
            this.connection.addPacketListener(this.pingListener, new PacketTypeFilter(a.class));
            return;
        }
        EMLog.e(TAG, "Connection is not connected as expected");
        throw new EMNetworkUnconnectedException("Connection is not connected as expected");
    }

    private void initConnectionConfig() {
        configure(ProviderManager.getInstance());
        if (this.connectionConfig == null) {
            Roster.setDefaultSubscriptionMode(SubscriptionMode.manual);
            SASLAuthentication.supportSASLMechanism(Constants.MECH_PLAIN);
            XMPPConnection.DEBUG_ENABLED = EMChatConfig.debugMode;
            SmackConfiguration.setPacketReplyTimeout(BaseImageDownloader.DEFAULT_HTTP_READ_TIMEOUT);
            this.connectionConfig = new ConnectionConfiguration(this.host, this.XMPP_PORT, EMChatConfig.getInstance().getDomain());
            this.connectionConfig.setRosterLoadedAtLogin(false);
            this.connectionConfig.setSendPresence(false);
            this.connectionConfig.setReconnectionAllowed(true);
            this.autoreconnect = true;
            this.connectionConfig.setCompressionEnabled(true);
            if (VERSION.SDK_INT >= 14) {
                this.connectionConfig.setTruststoreType("AndroidCAStore");
                this.connectionConfig.setTruststorePassword(null);
                this.connectionConfig.setTruststorePath(null);
                return;
            }
            this.connectionConfig.setTruststoreType("BKS");
            String property = System.getProperty("javax.net.ssl.trustStore");
            if (property == null) {
                property = new StringBuilder(String.valueOf(System.getProperty("java.home"))).append(File.separator).append("etc").append(File.separator).append("security").append(File.separator).append("cacerts.bks").toString();
            }
            this.connectionConfig.setTruststorePath(property);
        }
    }

    private void initFeatures() {
        ServiceDiscoveryManager instanceFor = ServiceDiscoveryManager.getInstanceFor(this.connection);
        if (instanceFor == null) {
            instanceFor = new ServiceDiscoveryManager(this.connection);
        }
        instanceFor.setIdentityName("EaseMob");
        instanceFor.setIdentityType("phone");
        instanceFor.addFeature(DiscoverInfo.NAMESPACE);
        instanceFor.addFeature("urn:xmpp:avatar:metadata");
        instanceFor.addFeature("urn:xmpp:avatar:metadata+notify");
        instanceFor.addFeature("urn:xmpp:avatar:data");
        instanceFor.addFeature(Nick.NAMESPACE);
        instanceFor.addFeature("http://jabber.org/protocol/nick+notify");
        instanceFor.addFeature("http://jabber.org/protocol/muc");
        instanceFor.addFeature("http://jabber.org/protocol/muc#rooms");
        instanceFor.addFeature("urn:xmpp:ping");
    }

    private void login() throws EaseMobException {
        if (this.connection == null) {
            setupConnection();
        }
        try {
            if (!this.connection.isAuthenticated()) {
                if (this.connection.isConnected()) {
                    EMLog.d(TAG, "try to login with barejid" + this.bareJid);
                    this.connection.login(this.bareJid, this.password, getXmppResource(this.context));
                    EMLog.d(TAG, "send available presence");
                    this.connection.sendPacket(new Presence(Presence.Type.available));
                    return;
                }
                EMLog.e(TAG, "Connection is not connected as expected");
                throw new EMNetworkUnconnectedException("Connection is not connected as expected");
            }
        } catch (IllegalStateException e) {
            EMLog.d(TAG, "illegalState in connection.login:" + e.toString());
            if (e.toString().indexOf(" Already logged in to server") < 0) {
                this.connection = null;
                setupConnection();
                throw new EaseMobException(e.toString());
            }
        } catch (Exception e2) {
            EMLog.e(TAG, "Failed to login to xmpp server. Caused by: " + e2.getMessage());
            Object obj = "401";
            Object obj2 = "not-authorized";
            Object obj3 = "SASL authentication failed using mechanism PLAIN";
            String message = e2.getMessage();
            if (message != null && message.contains(obj)) {
                throw new EMAuthenticationException(obj);
            } else if (message != null && message.contains(obj2)) {
                throw new EMAuthenticationException(obj2);
            } else if (message == null || !message.contains(obj3)) {
                try {
                    this.connection.removeConnectionListener(this.connectionListener);
                    this.connection.removeConnectionListener(this.chatConnectionListener);
                    this.connection.disconnect();
                } catch (Exception e22) {
                    e22.printStackTrace();
                }
                this.connection = null;
                setupConnection();
                throw new EaseMobException(message);
            } else {
                throw new EMAuthenticationException(obj3);
            }
        }
    }

    private void onDisconnected() {
        EMLog.d(TAG, "on disconnected");
        if (this.context != null && !this.isForcedDisconnect) {
            registerConnectivityReceiver();
        }
    }

    public static void resetInstance() {
        instance = null;
    }

    private void setupConnection() {
        try {
            this.connection = new XMPPConnection(this.connectionConfig);
            EntityCapsManager.getInstanceFor(this.connection).disableEntityCaps();
            this.connection.addConnectionListener(this.chatConnectionListener);
            this.connection.addConnectionListener(this.connectionListener);
            this.connection.addPacketListener(this.pingListener, new PacketTypeFilter(a.class));
        } catch (Throwable th) {
            this.connection = null;
        }
    }

    public void connect() throws EMNetworkUnconnectedException {
        if (this.connection == null) {
            setupConnection();
        }
        if (this.connection == null) {
            EMLog.e(TAG, "fail to setup connection");
            throw new EMNetworkUnconnectedException("fail to setup connection");
        }
        synchronized (this.connection) {
            if (this.connection.isConnected()) {
                return;
            }
            try {
                synchronized (this.connection) {
                    this.connection.connect();
                }
            } catch (Exception e) {
                e.printStackTrace();
                String message = !"".equals(e.getMessage()) ? e.getMessage() : e.toString();
                EMLog.e(TAG, "connection.connect() failed: " + message);
                throw new EMNetworkUnconnectedException(message);
            }
        }
    }

    public void connectSync() throws EaseMobException {
        this.isForcedDisconnect = false;
        if (!this.connection.isConnected() || !this.connection.isAuthenticated()) {
            connect();
            initConnection();
            login();
        }
    }

    public boolean disconnect() {
        if (this.connection != null && this.connection.isConnected()) {
            EMLog.d(TAG, "disconnect, disable autoreconnect");
            setAutoReconnect(false);
            this.autoreconnect = false;
            this.isForcedDisconnect = true;
            this.connection.disconnect();
            this.connection = null;
        }
        return true;
    }

    public boolean getAutoReconnect() {
        return this.autoreconnect;
    }

    public XMPPConnection getConnection() {
        return this.connection;
    }

    public boolean isAuthentificated() {
        return this.connection == null ? false : this.connection.isAuthenticated();
    }

    public boolean isConnected() {
        return instance.connection == null ? false : this.connection.isConnected();
    }

    public void reconnectASync() {
        new Thread() {
            public void run() {
                try {
                    XmppConnectionManager.this.reconnectSync();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void reconnectSync() throws com.easemob.exceptions.EaseMobException {
        /*
        r3 = this;
        r0 = 0;
        r3.isForcedDisconnect = r0;
        r1 = r3.connection;
        monitor-enter(r1);
        r0 = r3.connection;	 Catch:{ all -> 0x0027 }
        r0 = r0.isConnected();	 Catch:{ all -> 0x0027 }
        if (r0 == 0) goto L_0x001f;
    L_0x000e:
        r0 = r3.connection;	 Catch:{ all -> 0x0027 }
        r0 = r0.isAuthenticated();	 Catch:{ all -> 0x0027 }
        if (r0 == 0) goto L_0x001f;
    L_0x0016:
        r0 = TAG;	 Catch:{ all -> 0x0027 }
        r2 = "connection is connected,no need to reconnect";
        com.easemob.util.EMLog.d(r0, r2);	 Catch:{ all -> 0x0027 }
        monitor-exit(r1);	 Catch:{ all -> 0x0027 }
    L_0x001e:
        return;
    L_0x001f:
        monitor-exit(r1);	 Catch:{ all -> 0x0027 }
        r3.connect();
        r3.login();
        goto L_0x001e;
    L_0x0027:
        r0 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x0027 }
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.easemob.chat.core.XmppConnectionManager.reconnectSync():void");
    }

    public void registerConnectivityReceiver() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) this.context.getSystemService("connectivity");
            if (!NetUtils.hasDataConnection(EMChatConfig.getInstance().getApplicationContext())) {
                this.context.registerReceiver(this.connectivityBroadcastReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
                setAutoReconnect(false);
                EMLog.d(TAG, " [x] Stopped reconnecting as there is no network.  '");
            }
        } catch (Exception e) {
        }
    }

    public void setAutoReconnect(boolean z) {
        this.autoreconnect = z;
        EMLog.d(TAG, "set autoreconnect to:" + z);
        this.connectionConfig.setReconnectionAllowed(z);
    }

    public void setChatConnectionListener(ConnectionListener connectionListener) {
        this.chatConnectionListener = connectionListener;
        this.connection.addConnectionListener(this.chatConnectionListener);
    }
}
