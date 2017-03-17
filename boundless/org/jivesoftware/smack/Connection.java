package org.jivesoftware.smack;

import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import org.jivesoftware.smack.compression.Java7ZlibInputOutputStream;
import org.jivesoftware.smack.compression.JzlibInputOutputStream;
import org.jivesoftware.smack.compression.XMPPInputOutputStream;
import org.jivesoftware.smack.debugger.SmackDebugger;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;

public abstract class Connection {
    public static boolean DEBUG_ENABLED;
    protected static final List<XMPPInputOutputStream> compressionHandlers = new ArrayList(2);
    private static final AtomicInteger connectionCounter = new AtomicInteger(0);
    private static final Set<ConnectionCreationListener> connectionEstablishedListeners = new CopyOnWriteArraySet();
    private AccountManager accountManager = null;
    protected ChatManager chatManager = null;
    protected final Collection<PacketCollector> collectors = new ConcurrentLinkedQueue();
    protected XMPPInputOutputStream compressionHandler;
    protected final ConnectionConfiguration config;
    protected final int connectionCounterValue = connectionCounter.getAndIncrement();
    protected final Collection<ConnectionListener> connectionListeners = new CopyOnWriteArrayList();
    protected SmackDebugger debugger = null;
    protected final Map<PacketInterceptor, InterceptorWrapper> interceptors = new ConcurrentHashMap();
    protected Reader reader;
    protected final Map<PacketListener, ListenerWrapper> recvListeners = new ConcurrentHashMap();
    protected RosterStorage rosterStorage;
    protected SASLAuthentication saslAuthentication = new SASLAuthentication(this);
    protected final Map<PacketListener, ListenerWrapper> sendListeners = new ConcurrentHashMap();
    private String serviceCapsNode;
    protected Writer writer;

    protected static class InterceptorWrapper {
        private PacketFilter packetFilter;
        private PacketInterceptor packetInterceptor;

        public InterceptorWrapper(PacketInterceptor packetInterceptor, PacketFilter packetFilter) {
            this.packetInterceptor = packetInterceptor;
            this.packetFilter = packetFilter;
        }

        public boolean equals(Object obj) {
            return obj == null ? false : obj instanceof InterceptorWrapper ? ((InterceptorWrapper) obj).packetInterceptor.equals(this.packetInterceptor) : obj instanceof PacketInterceptor ? obj.equals(this.packetInterceptor) : false;
        }

        public void notifyListener(Packet packet) {
            if (this.packetFilter == null || this.packetFilter.accept(packet)) {
                this.packetInterceptor.interceptPacket(packet);
            }
        }
    }

    protected static class ListenerWrapper {
        private PacketFilter packetFilter;
        private PacketListener packetListener;

        public ListenerWrapper(PacketListener packetListener, PacketFilter packetFilter) {
            this.packetListener = packetListener;
            this.packetFilter = packetFilter;
        }

        public void notifyListener(Packet packet) {
            if (this.packetFilter == null || this.packetFilter.accept(packet)) {
                this.packetListener.processPacket(packet);
            }
        }
    }

    static {
        DEBUG_ENABLED = false;
        try {
            DEBUG_ENABLED = Boolean.getBoolean("smack.debugEnabled");
        } catch (Exception e) {
        }
        SmackConfiguration.getVersion();
        compressionHandlers.add(new Java7ZlibInputOutputStream());
        compressionHandlers.add(new JzlibInputOutputStream());
    }

    protected Connection(ConnectionConfiguration connectionConfiguration) {
        this.config = connectionConfiguration;
    }

    public static void addConnectionCreationListener(ConnectionCreationListener connectionCreationListener) {
        connectionEstablishedListeners.add(connectionCreationListener);
    }

    protected static Collection<ConnectionCreationListener> getConnectionCreationListeners() {
        return Collections.unmodifiableCollection(connectionEstablishedListeners);
    }

    public static void removeConnectionCreationListener(ConnectionCreationListener connectionCreationListener) {
        connectionEstablishedListeners.remove(connectionCreationListener);
    }

    public void addConnectionListener(ConnectionListener connectionListener) {
        if (connectionListener != null && !this.connectionListeners.contains(connectionListener)) {
            this.connectionListeners.add(connectionListener);
        }
    }

    public void addPacketInterceptor(PacketInterceptor packetInterceptor, PacketFilter packetFilter) {
        if (packetInterceptor == null) {
            throw new NullPointerException("Packet interceptor is null.");
        }
        this.interceptors.put(packetInterceptor, new InterceptorWrapper(packetInterceptor, packetFilter));
    }

    public void addPacketListener(PacketListener packetListener, PacketFilter packetFilter) {
        if (packetListener == null) {
            throw new NullPointerException("Packet listener is null.");
        }
        this.recvListeners.put(packetListener, new ListenerWrapper(packetListener, packetFilter));
    }

    public void addPacketSendingListener(PacketListener packetListener, PacketFilter packetFilter) {
        if (packetListener == null) {
            throw new NullPointerException("Packet listener is null.");
        }
        this.sendListeners.put(packetListener, new ListenerWrapper(packetListener, packetFilter));
    }

    public abstract void connect() throws XMPPException;

    public PacketCollector createPacketCollector(PacketFilter packetFilter) {
        PacketCollector packetCollector = new PacketCollector(this, packetFilter);
        this.collectors.add(packetCollector);
        return packetCollector;
    }

    public void disconnect() {
        disconnect(new Presence(Type.unavailable));
    }

    public abstract void disconnect(Presence presence);

    protected void firePacketInterceptors(Packet packet) {
        if (packet != null) {
            for (InterceptorWrapper notifyListener : this.interceptors.values()) {
                notifyListener.notifyListener(packet);
            }
        }
    }

    protected void firePacketSendingListeners(Packet packet) {
        for (ListenerWrapper notifyListener : this.sendListeners.values()) {
            notifyListener.notifyListener(packet);
        }
    }

    public AccountManager getAccountManager() {
        if (this.accountManager == null) {
            this.accountManager = new AccountManager(this);
        }
        return this.accountManager;
    }

    public synchronized ChatManager getChatManager() {
        if (this.chatManager == null) {
            this.chatManager = new ChatManager(this);
        }
        return this.chatManager;
    }

    protected ConnectionConfiguration getConfiguration() {
        return this.config;
    }

    public abstract String getConnectionID();

    protected Collection<ConnectionListener> getConnectionListeners() {
        return this.connectionListeners;
    }

    public String getHost() {
        return this.config.getHost();
    }

    protected Collection<PacketCollector> getPacketCollectors() {
        return this.collectors;
    }

    protected Map<PacketInterceptor, InterceptorWrapper> getPacketInterceptors() {
        return this.interceptors;
    }

    protected Map<PacketListener, ListenerWrapper> getPacketListeners() {
        return this.recvListeners;
    }

    protected Map<PacketListener, ListenerWrapper> getPacketSendingListeners() {
        return this.sendListeners;
    }

    public int getPort() {
        return this.config.getPort();
    }

    public abstract Roster getRoster();

    public SASLAuthentication getSASLAuthentication() {
        return this.saslAuthentication;
    }

    public String getServiceCapsNode() {
        return this.serviceCapsNode;
    }

    public String getServiceName() {
        return this.config.getServiceName();
    }

    public abstract String getUser();

    protected void initDebugger() {
        Class cls = null;
        if (this.reader == null || this.writer == null) {
            throw new NullPointerException("Reader or writer isn't initialized.");
        } else if (!this.config.isDebuggerEnabled()) {
        } else {
            if (this.debugger == null) {
                String property;
                Class cls2;
                try {
                    property = System.getProperty("smack.debuggerClass");
                } catch (Throwable th) {
                    Object obj = cls;
                }
                if (property != null) {
                    try {
                        cls = Class.forName(property);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (cls == null) {
                    try {
                        cls2 = Class.forName("de.measite.smack.AndroidDebugger");
                    } catch (Exception e2) {
                        try {
                            cls2 = Class.forName("org.jivesoftware.smack.debugger.ConsoleDebugger");
                        } catch (Exception e3) {
                            e3.printStackTrace();
                        }
                    }
                    this.debugger = (SmackDebugger) cls2.getConstructor(new Class[]{Connection.class, Writer.class, Reader.class}).newInstance(new Object[]{this, this.writer, this.reader});
                    this.reader = this.debugger.getReader();
                    this.writer = this.debugger.getWriter();
                    return;
                }
                cls2 = cls;
                try {
                    this.debugger = (SmackDebugger) cls2.getConstructor(new Class[]{Connection.class, Writer.class, Reader.class}).newInstance(new Object[]{this, this.writer, this.reader});
                    this.reader = this.debugger.getReader();
                    this.writer = this.debugger.getWriter();
                    return;
                } catch (Throwable e4) {
                    throw new IllegalArgumentException("Can't initialize the configured debugger!", e4);
                }
            }
            this.reader = this.debugger.newConnectionReader(this.reader);
            this.writer = this.debugger.newConnectionWriter(this.writer);
        }
    }

    public abstract boolean isAnonymous();

    public abstract boolean isAuthenticated();

    public abstract boolean isConnected();

    protected boolean isReconnectionAllowed() {
        return this.config.isReconnectionAllowed();
    }

    public abstract boolean isSecureConnection();

    public boolean isSendPresence() {
        return this.config.isSendPresence();
    }

    public abstract boolean isUsingCompression();

    public void login(String str, String str2) throws XMPPException {
        login(str, str2, "Smack");
    }

    public abstract void login(String str, String str2, String str3) throws XMPPException;

    public abstract void loginAnonymously() throws XMPPException;

    public void removeConnectionListener(ConnectionListener connectionListener) {
        this.connectionListeners.remove(connectionListener);
    }

    protected void removePacketCollector(PacketCollector packetCollector) {
        this.collectors.remove(packetCollector);
    }

    public void removePacketInterceptor(PacketInterceptor packetInterceptor) {
        this.interceptors.remove(packetInterceptor);
    }

    public void removePacketListener(PacketListener packetListener) {
        this.recvListeners.remove(packetListener);
    }

    public void removePacketSendingListener(PacketListener packetListener) {
        this.sendListeners.remove(packetListener);
    }

    public abstract void sendPacket(Packet packet);

    public abstract void setRosterStorage(RosterStorage rosterStorage) throws IllegalStateException;

    protected void setServiceCapsNode(String str) {
        this.serviceCapsNode = str;
    }
}
