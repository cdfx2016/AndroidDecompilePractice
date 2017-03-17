package org.jivesoftware.smack;

import com.fanyu.boundless.util.FileUtil;
import com.kenai.jbosh.BOSHClient;
import com.kenai.jbosh.BOSHClientConfig.Builder;
import com.kenai.jbosh.BOSHClientConnEvent;
import com.kenai.jbosh.BOSHClientConnListener;
import com.kenai.jbosh.BOSHClientRequestListener;
import com.kenai.jbosh.BOSHClientResponseListener;
import com.kenai.jbosh.BOSHException;
import com.kenai.jbosh.BOSHMessageEvent;
import com.kenai.jbosh.BodyQName;
import com.kenai.jbosh.ComposableBody;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Writer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.packet.XMPPError.Condition;
import org.jivesoftware.smack.util.StringUtils;

public class BOSHConnection extends Connection {
    public static final String BOSH_URI = "http://jabber.org/protocol/httpbind";
    public static final String XMPP_BOSH_NS = "urn:xmpp:xbosh";
    private boolean anonymous;
    protected String authID;
    private boolean authenticated;
    private BOSHClient client;
    private final BOSHConfiguration config;
    private boolean connected;
    private boolean done;
    private boolean isFirstInitialization;
    private ExecutorService listenerExecutor;
    private Thread readerConsumer;
    private PipedWriter readerPipe;
    private Roster roster;
    protected String sessionID;
    private String user;
    private boolean wasAuthenticated;

    private class BOSHConnectionListener implements BOSHClientConnListener {
        private final BOSHConnection connection;

        public BOSHConnectionListener(BOSHConnection bOSHConnection) {
            this.connection = bOSHConnection;
        }

        public void connectionEvent(BOSHClientConnEvent bOSHClientConnEvent) {
            try {
                if (bOSHClientConnEvent.isConnected()) {
                    BOSHConnection.this.connected = true;
                    if (BOSHConnection.this.isFirstInitialization) {
                        BOSHConnection.this.isFirstInitialization = false;
                        for (ConnectionCreationListener connectionCreated : Connection.getConnectionCreationListeners()) {
                            connectionCreated.connectionCreated(this.connection);
                        }
                    } else {
                        try {
                            if (BOSHConnection.this.wasAuthenticated) {
                                this.connection.login(BOSHConnection.this.config.getUsername(), BOSHConnection.this.config.getPassword(), BOSHConnection.this.config.getResource());
                            }
                            for (ConnectionListener reconnectionSuccessful : BOSHConnection.this.getConnectionListeners()) {
                                reconnectionSuccessful.reconnectionSuccessful();
                            }
                        } catch (Exception e) {
                            Exception exception = e;
                            for (ConnectionListener reconnectionSuccessful2 : BOSHConnection.this.getConnectionListeners()) {
                                reconnectionSuccessful2.reconnectionFailed(exception);
                            }
                        }
                    }
                } else {
                    if (bOSHClientConnEvent.isError()) {
                        try {
                            bOSHClientConnEvent.getCause();
                        } catch (Exception e2) {
                            BOSHConnection.this.notifyConnectionError(e2);
                        }
                    }
                    BOSHConnection.this.connected = false;
                }
                synchronized (this.connection) {
                    this.connection.notifyAll();
                }
            } catch (Throwable th) {
                synchronized (this.connection) {
                    this.connection.notifyAll();
                }
            }
        }
    }

    private class ListenerNotification implements Runnable {
        private Packet packet;

        public ListenerNotification(Packet packet) {
            this.packet = packet;
        }

        public void run() {
            for (ListenerWrapper notifyListener : BOSHConnection.this.recvListeners.values()) {
                try {
                    notifyListener.notifyListener(this.packet);
                } catch (Exception e) {
                    System.err.println("Exception in packet listener: " + e);
                    e.printStackTrace();
                }
            }
        }
    }

    public BOSHConnection(BOSHConfiguration bOSHConfiguration) {
        super(bOSHConfiguration);
        this.connected = false;
        this.authenticated = false;
        this.anonymous = false;
        this.isFirstInitialization = true;
        this.wasAuthenticated = false;
        this.done = false;
        this.authID = null;
        this.sessionID = null;
        this.user = null;
        this.roster = null;
        this.config = bOSHConfiguration;
    }

    public BOSHConnection(boolean z, String str, int i, String str2, String str3) {
        super(new BOSHConfiguration(z, str, i, str2, str3));
        this.connected = false;
        this.authenticated = false;
        this.anonymous = false;
        this.isFirstInitialization = true;
        this.wasAuthenticated = false;
        this.done = false;
        this.authID = null;
        this.sessionID = null;
        this.user = null;
        this.roster = null;
        this.config = (BOSHConfiguration) getConfiguration();
    }

    private void setWasAuthenticated(boolean z) {
        if (!this.wasAuthenticated) {
            this.wasAuthenticated = z;
        }
    }

    public void connect() throws XMPPException {
        if (this.connected) {
            throw new IllegalStateException("Already connected to a server.");
        }
        this.done = false;
        try {
            if (this.client != null) {
                this.client.close();
                this.client = null;
            }
            this.saslAuthentication.init();
            this.sessionID = null;
            this.authID = null;
            Builder create = Builder.create(this.config.getURI(), this.config.getServiceName());
            if (this.config.isProxyEnabled()) {
                create.setProxy(this.config.getProxyAddress(), this.config.getProxyPort());
            }
            this.client = BOSHClient.create(create.build());
            this.listenerExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
                public Thread newThread(Runnable runnable) {
                    Thread thread = new Thread(runnable, "Smack Listener Processor (" + BOSHConnection.this.connectionCounterValue + ")");
                    thread.setDaemon(true);
                    return thread;
                }
            });
            this.client.addBOSHClientConnListener(new BOSHConnectionListener(this));
            this.client.addBOSHClientResponseListener(new BOSHPacketReader(this));
            if (this.config.isDebuggerEnabled()) {
                initDebugger();
                if (this.isFirstInitialization) {
                    if (this.debugger.getReaderListener() != null) {
                        addPacketListener(this.debugger.getReaderListener(), null);
                    }
                    if (this.debugger.getWriterListener() != null) {
                        addPacketSendingListener(this.debugger.getWriterListener(), null);
                    }
                }
            }
            this.client.send(ComposableBody.builder().setNamespaceDefinition("xmpp", XMPP_BOSH_NS).setAttribute(BodyQName.createWithPrefix(XMPP_BOSH_NS, "version", "xmpp"), "1.0").build());
            synchronized (this) {
                long currentTimeMillis = System.currentTimeMillis() + ((long) (SmackConfiguration.getPacketReplyTimeout() * 6));
                while (!this.connected && System.currentTimeMillis() < currentTimeMillis) {
                    try {
                        wait(Math.abs(currentTimeMillis - System.currentTimeMillis()));
                    } catch (InterruptedException e) {
                    }
                }
            }
            if (!this.connected && !this.done) {
                this.done = true;
                String str = "Timeout reached for the connection to " + getHost() + ":" + getPort() + FileUtil.FILE_EXTENSION_SEPARATOR;
                throw new XMPPException(str, new XMPPError(Condition.remote_server_timeout, str));
            }
        } catch (Throwable e2) {
            throw new XMPPException("Can't connect to " + getServiceName(), e2);
        }
    }

    public void disconnect(Presence presence) {
        if (this.connected) {
            shutdown(presence);
            this.wasAuthenticated = false;
            this.isFirstInitialization = true;
            for (ConnectionListener connectionClosed : getConnectionListeners()) {
                try {
                    connectionClosed.connectionClosed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getConnectionID() {
        return !this.connected ? null : this.authID != null ? this.authID : this.sessionID;
    }

    public Roster getRoster() {
        if (this.roster == null) {
            return null;
        }
        if (!this.config.isRosterLoadedAtLogin()) {
            this.roster.reload();
        }
        if (!this.roster.rosterInitialized) {
            try {
                synchronized (this.roster) {
                    long packetReplyTimeout = (long) SmackConfiguration.getPacketReplyTimeout();
                    long currentTimeMillis = System.currentTimeMillis();
                    long j = packetReplyTimeout;
                    while (!this.roster.rosterInitialized && j > 0) {
                        this.roster.wait(j);
                        packetReplyTimeout = System.currentTimeMillis();
                        j -= packetReplyTimeout - currentTimeMillis;
                        currentTimeMillis = packetReplyTimeout;
                    }
                }
            } catch (InterruptedException e) {
            }
        }
        return this.roster;
    }

    public String getUser() {
        return this.user;
    }

    protected void initDebugger() {
        this.writer = new Writer() {
            public void close() {
            }

            public void flush() {
            }

            public void write(char[] cArr, int i, int i2) {
            }
        };
        try {
            this.readerPipe = new PipedWriter();
            this.reader = new PipedReader(this.readerPipe);
        } catch (IOException e) {
        }
        super.initDebugger();
        this.client.addBOSHClientResponseListener(new BOSHClientResponseListener() {
            public void responseReceived(BOSHMessageEvent bOSHMessageEvent) {
                if (bOSHMessageEvent.getBody() != null) {
                    try {
                        BOSHConnection.this.readerPipe.write(bOSHMessageEvent.getBody().toXML());
                        BOSHConnection.this.readerPipe.flush();
                    } catch (Exception e) {
                    }
                }
            }
        });
        this.client.addBOSHClientRequestListener(new BOSHClientRequestListener() {
            public void requestSent(BOSHMessageEvent bOSHMessageEvent) {
                if (bOSHMessageEvent.getBody() != null) {
                    try {
                        BOSHConnection.this.writer.write(bOSHMessageEvent.getBody().toXML());
                    } catch (Exception e) {
                    }
                }
            }
        });
        this.readerConsumer = new Thread() {
            private int bufferLength = 1024;
            private Thread thread = this;

            public void run() {
                try {
                    char[] cArr = new char[this.bufferLength];
                    while (BOSHConnection.this.readerConsumer == this.thread && !BOSHConnection.this.done) {
                        BOSHConnection.this.reader.read(cArr, 0, this.bufferLength);
                    }
                } catch (IOException e) {
                }
            }
        };
        this.readerConsumer.setDaemon(true);
        this.readerConsumer.start();
    }

    public boolean isAnonymous() {
        return this.anonymous;
    }

    public boolean isAuthenticated() {
        return this.authenticated;
    }

    public boolean isConnected() {
        return this.connected;
    }

    public boolean isSecureConnection() {
        return false;
    }

    public boolean isUsingCompression() {
        return false;
    }

    public void login(String str, String str2, String str3) throws XMPPException {
        if (!isConnected()) {
            throw new IllegalStateException("Not connected to server.");
        } else if (this.authenticated) {
            throw new IllegalStateException("Already logged in to server.");
        } else {
            String trim = str.toLowerCase().trim();
            String authenticate = (this.config.isSASLAuthenticationEnabled() && this.saslAuthentication.hasNonAnonymousAuthentication()) ? str2 != null ? this.saslAuthentication.authenticate(trim, str2, str3) : this.saslAuthentication.authenticate(trim, str3, this.config.getCallbackHandler()) : new NonSASLAuthentication(this).authenticate(trim, str2, str3);
            if (authenticate != null) {
                this.user = authenticate;
                this.config.setServiceName(StringUtils.parseServer(authenticate));
            } else {
                this.user = trim + "@" + getServiceName();
                if (str3 != null) {
                    this.user += "/" + str3;
                }
            }
            if (this.roster == null) {
                if (this.rosterStorage == null) {
                    this.roster = new Roster(this);
                } else {
                    this.roster = new Roster(this, this.rosterStorage);
                }
            }
            if (this.config.isSendPresence()) {
                sendPacket(new Presence(Type.available));
            }
            this.authenticated = true;
            this.anonymous = false;
            if (this.config.isRosterLoadedAtLogin()) {
                this.roster.reload();
            }
            this.config.setLoginInfo(trim, str2, str3);
            if (this.config.isDebuggerEnabled() && this.debugger != null) {
                this.debugger.userHasLogged(this.user);
            }
        }
    }

    public void loginAnonymously() throws XMPPException {
        if (!isConnected()) {
            throw new IllegalStateException("Not connected to server.");
        } else if (this.authenticated) {
            throw new IllegalStateException("Already logged in to server.");
        } else {
            String authenticateAnonymously = (this.config.isSASLAuthenticationEnabled() && this.saslAuthentication.hasAnonymousAuthentication()) ? this.saslAuthentication.authenticateAnonymously() : new NonSASLAuthentication(this).authenticateAnonymously();
            this.user = authenticateAnonymously;
            this.config.setServiceName(StringUtils.parseServer(authenticateAnonymously));
            this.roster = null;
            if (this.config.isSendPresence()) {
                sendPacket(new Presence(Type.available));
            }
            this.authenticated = true;
            this.anonymous = true;
            if (this.config.isDebuggerEnabled() && this.debugger != null) {
                this.debugger.userHasLogged(this.user);
            }
        }
    }

    protected void notifyConnectionError(Exception exception) {
        shutdown(new Presence(Type.unavailable));
        exception.printStackTrace();
        for (ConnectionListener connectionClosedOnError : getConnectionListeners()) {
            try {
                connectionClosedOnError.connectionClosedOnError(exception);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void processPacket(Packet packet) {
        if (packet != null) {
            for (PacketCollector processPacket : getPacketCollectors()) {
                processPacket.processPacket(packet);
            }
            this.listenerExecutor.submit(new ListenerNotification(packet));
        }
    }

    protected void send(ComposableBody composableBody) throws BOSHException {
        if (!this.connected) {
            throw new IllegalStateException("Not connected to a server!");
        } else if (composableBody == null) {
            throw new NullPointerException("Body mustn't be null!");
        } else {
            if (this.sessionID != null) {
                composableBody = composableBody.rebuild().setAttribute(BodyQName.create(BOSH_URI, "sid"), this.sessionID).build();
            }
            this.client.send(composableBody);
        }
    }

    public void sendPacket(Packet packet) {
        if (!isConnected()) {
            throw new IllegalStateException("Not connected to server.");
        } else if (packet == null) {
            throw new NullPointerException("Packet is null.");
        } else if (!this.done) {
            firePacketInterceptors(packet);
            try {
                send(ComposableBody.builder().setPayloadXML(packet.toXML()).build());
                firePacketSendingListeners(packet);
            } catch (BOSHException e) {
                e.printStackTrace();
            }
        }
    }

    public void setRosterStorage(RosterStorage rosterStorage) throws IllegalStateException {
        if (this.roster != null) {
            throw new IllegalStateException("Roster is already initialized");
        }
        this.rosterStorage = rosterStorage;
    }

    protected void shutdown(Presence presence) {
        setWasAuthenticated(this.authenticated);
        this.authID = null;
        this.sessionID = null;
        this.done = true;
        this.authenticated = false;
        this.connected = false;
        this.isFirstInitialization = false;
        try {
            this.client.disconnect(ComposableBody.builder().setNamespaceDefinition("xmpp", XMPP_BOSH_NS).setPayloadXML(presence.toXML()).build());
            Thread.sleep(150);
        } catch (Exception e) {
        }
        if (this.readerPipe != null) {
            try {
                this.readerPipe.close();
            } catch (Throwable th) {
            }
            this.reader = null;
        }
        if (this.reader != null) {
            try {
                this.reader.close();
            } catch (Throwable th2) {
            }
            this.reader = null;
        }
        if (this.writer != null) {
            try {
                this.writer.close();
            } catch (Throwable th3) {
            }
            this.writer = null;
        }
        if (this.listenerExecutor != null) {
            this.listenerExecutor.shutdown();
        }
        this.readerConsumer = null;
    }
}
