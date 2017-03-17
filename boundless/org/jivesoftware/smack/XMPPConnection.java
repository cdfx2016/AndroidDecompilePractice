package org.jivesoftware.smack;

import com.fanyu.boundless.util.FileUtil;
import com.google.android.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.net.Socket;
import java.security.KeyStore;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import org.apache.harmony.javax.security.auth.callback.Callback;
import org.apache.harmony.javax.security.auth.callback.CallbackHandler;
import org.apache.harmony.javax.security.auth.callback.PasswordCallback;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.compression.XMPPInputOutputStream;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.packet.XMPPError.Condition;
import org.jivesoftware.smack.parsing.ParsingExceptionCallback;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.dns.HostAddress;

public class XMPPConnection extends Connection {
    private boolean anonymous = false;
    private boolean authenticated = false;
    private Collection<String> compressionMethods;
    private boolean connected = false;
    String connectionID = null;
    PacketReader packetReader;
    PacketWriter packetWriter;
    private ParsingExceptionCallback parsingExceptionCallback = SmackConfiguration.getDefaultParsingExceptionCallback();
    Roster roster = null;
    private boolean serverAckdCompression = false;
    Socket socket;
    private volatile boolean socketClosed = false;
    private String user = null;
    private boolean usingTLS = false;
    private boolean wasAuthenticated = false;

    public XMPPConnection(String str) {
        super(new ConnectionConfiguration(str));
        this.config.setCompressionEnabled(false);
        this.config.setSASLAuthenticationEnabled(true);
        this.config.setDebuggerEnabled(DEBUG_ENABLED);
    }

    public XMPPConnection(String str, CallbackHandler callbackHandler) {
        super(new ConnectionConfiguration(str));
        this.config.setCompressionEnabled(false);
        this.config.setSASLAuthenticationEnabled(true);
        this.config.setDebuggerEnabled(DEBUG_ENABLED);
        this.config.setCallbackHandler(callbackHandler);
    }

    public XMPPConnection(ConnectionConfiguration connectionConfiguration) {
        super(connectionConfiguration);
    }

    public XMPPConnection(ConnectionConfiguration connectionConfiguration, CallbackHandler callbackHandler) {
        super(connectionConfiguration);
        connectionConfiguration.setCallbackHandler(callbackHandler);
    }

    private void connectUsingConfiguration(ConnectionConfiguration connectionConfiguration) throws XMPPException {
        Iterator it = connectionConfiguration.getHostAddresses().iterator();
        List<HostAddress> linkedList = new LinkedList();
        boolean z = false;
        while (it.hasNext()) {
            Throwable th = null;
            HostAddress hostAddress = (HostAddress) it.next();
            String fqdn = hostAddress.getFQDN();
            int port = hostAddress.getPort();
            try {
                if (connectionConfiguration.getSocketFactory() == null) {
                    this.socket = new Socket(fqdn, port);
                } else {
                    this.socket = connectionConfiguration.getSocketFactory().createSocket(fqdn, port);
                }
            } catch (Throwable th2) {
                String str = "Could not connect to " + fqdn + ":" + port + FileUtil.FILE_EXTENSION_SEPARATOR;
                th2 = new XMPPException(str, new XMPPError(Condition.remote_server_timeout, str), th2);
            } catch (Throwable e) {
                fqdn = "XMPPError connecting to " + fqdn + ":" + port + FileUtil.FILE_EXTENSION_SEPARATOR;
                th2 = new XMPPException(fqdn, new XMPPError(Condition.remote_server_error, fqdn), e);
                z = true;
            }
            if (th2 == null) {
                connectionConfiguration.setUsedHostAddress(hostAddress);
                break;
            }
            hostAddress.setException(th2);
            linkedList.add(hostAddress);
            if (!it.hasNext()) {
                StringBuilder stringBuilder = new StringBuilder();
                for (HostAddress hostAddress2 : linkedList) {
                    stringBuilder.append(hostAddress2.getErrorMessage());
                    stringBuilder.append("; ");
                }
                throw new XMPPException(stringBuilder.toString(), z ? new XMPPError(Condition.remote_server_error) : new XMPPError(Condition.remote_server_timeout), th2);
            }
        }
        this.socketClosed = false;
        initConnection();
    }

    private void initConnection() throws XMPPException {
        boolean z = true;
        if (!(this.packetReader == null || this.packetWriter == null)) {
            z = false;
        }
        this.compressionHandler = null;
        this.serverAckdCompression = false;
        initReaderAndWriter();
        if (z) {
            try {
                this.packetWriter = new PacketWriter(this);
                this.packetReader = new PacketReader(this);
                if (this.config.isDebuggerEnabled()) {
                    addPacketListener(this.debugger.getReaderListener(), null);
                    if (this.debugger.getWriterListener() != null) {
                        addPacketSendingListener(this.debugger.getWriterListener(), null);
                    }
                }
            } catch (XMPPException e) {
                if (this.packetWriter != null) {
                    this.packetWriter.shutdown();
                }
                if (this.packetReader != null) {
                    this.packetReader.shutdown();
                }
                if (this.reader != null) {
                    this.reader.close();
                }
                if (this.writer != null) {
                    this.writer.close();
                }
                if (this.socket != null) {
                    try {
                        this.socket.close();
                    } catch (Exception e2) {
                    }
                    this.socket = null;
                }
                setWasAuthenticated(this.authenticated);
                this.chatManager = null;
                this.authenticated = false;
                this.connected = false;
                throw e;
            } catch (Throwable th) {
            }
        } else {
            this.packetWriter.init();
            this.packetReader.init();
        }
        this.packetWriter.startup();
        this.packetReader.startup();
        this.connected = true;
        if (z) {
            for (ConnectionCreationListener connectionCreated : Connection.getConnectionCreationListeners()) {
                connectionCreated.connectionCreated(this);
            }
            return;
        }
        return;
        this.packetReader = null;
        if (this.reader != null) {
            this.reader.close();
            this.reader = null;
        }
        if (this.writer != null) {
            this.writer.close();
            this.writer = null;
        }
        if (this.socket != null) {
            this.socket.close();
            this.socket = null;
        }
        setWasAuthenticated(this.authenticated);
        this.chatManager = null;
        this.authenticated = false;
        this.connected = false;
        throw e;
        this.packetWriter = null;
        if (this.packetReader != null) {
            this.packetReader.shutdown();
            this.packetReader = null;
        }
        if (this.reader != null) {
            this.reader.close();
            this.reader = null;
        }
        if (this.writer != null) {
            this.writer.close();
            this.writer = null;
        }
        if (this.socket != null) {
            this.socket.close();
            this.socket = null;
        }
        setWasAuthenticated(this.authenticated);
        this.chatManager = null;
        this.authenticated = false;
        this.connected = false;
        throw e;
        this.writer = null;
        if (this.socket != null) {
            this.socket.close();
            this.socket = null;
        }
        setWasAuthenticated(this.authenticated);
        this.chatManager = null;
        this.authenticated = false;
        this.connected = false;
        throw e;
        this.reader = null;
        if (this.writer != null) {
            this.writer.close();
            this.writer = null;
        }
        if (this.socket != null) {
            this.socket.close();
            this.socket = null;
        }
        setWasAuthenticated(this.authenticated);
        this.chatManager = null;
        this.authenticated = false;
        this.connected = false;
        throw e;
    }

    private void initReaderAndWriter() throws XMPPException {
        try {
            if (this.compressionHandler == null) {
                this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), "UTF-8"));
                this.writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream(), "UTF-8"));
            } else {
                try {
                    this.writer = new BufferedWriter(new OutputStreamWriter(this.compressionHandler.getOutputStream(this.socket.getOutputStream()), "UTF-8"));
                    this.reader = new BufferedReader(new InputStreamReader(this.compressionHandler.getInputStream(this.socket.getInputStream()), "UTF-8"));
                } catch (Exception e) {
                    e.printStackTrace();
                    this.compressionHandler = null;
                    this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), "UTF-8"));
                    this.writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream(), "UTF-8"));
                }
            }
            initDebugger();
        } catch (Throwable e2) {
            throw new XMPPException("XMPPError establishing connection with server.", new XMPPError(Condition.remote_server_error, "XMPPError establishing connection with server."), e2);
        }
    }

    private XMPPInputOutputStream maybeGetCompressionHandler() {
        if (this.compressionMethods != null) {
            for (XMPPInputOutputStream xMPPInputOutputStream : compressionHandlers) {
                if (xMPPInputOutputStream.isSupported()) {
                    if (this.compressionMethods.contains(xMPPInputOutputStream.getCompressionMethod())) {
                        return xMPPInputOutputStream;
                    }
                }
            }
        }
        return null;
    }

    private void requestStreamCompression(String str) {
        try {
            this.writer.write("<compress xmlns='http://jabber.org/protocol/compress'>");
            this.writer.write("<method>" + str + "</method></compress>");
            this.writer.flush();
        } catch (Exception e) {
            notifyConnectionError(e);
        }
    }

    private void setWasAuthenticated(boolean z) {
        if (!this.wasAuthenticated) {
            this.wasAuthenticated = z;
        }
    }

    private boolean useCompression() {
        if (this.authenticated) {
            throw new IllegalStateException("Compression should be negotiated before authentication.");
        }
        XMPPInputOutputStream maybeGetCompressionHandler = maybeGetCompressionHandler();
        this.compressionHandler = maybeGetCompressionHandler;
        if (maybeGetCompressionHandler == null) {
            return false;
        }
        requestStreamCompression(this.compressionHandler.getCompressionMethod());
        synchronized (this) {
            try {
                wait((long) (SmackConfiguration.getPacketReplyTimeout() * 5));
            } catch (InterruptedException e) {
            }
        }
        return isUsingCompression();
    }

    public void addPacketWriterInterceptor(PacketInterceptor packetInterceptor, PacketFilter packetFilter) {
        addPacketInterceptor(packetInterceptor, packetFilter);
    }

    public void addPacketWriterListener(PacketListener packetListener, PacketFilter packetFilter) {
        addPacketSendingListener(packetListener, packetFilter);
    }

    public void connect() throws XMPPException {
        connectUsingConfiguration(this.config);
        if (this.connected && this.wasAuthenticated) {
            if (isAnonymous()) {
                loginAnonymously();
            } else {
                login(this.config.getUsername(), this.config.getPassword(), this.config.getResource());
            }
            notifyReconnection();
        }
    }

    public synchronized void disconnect(Presence presence) {
        PacketReader packetReader = this.packetReader;
        PacketWriter packetWriter = this.packetWriter;
        if (!(packetReader == null || packetWriter == null)) {
            if (isConnected()) {
                shutdown(presence);
                this.chatManager = null;
                this.wasAuthenticated = false;
            }
        }
    }

    public String getConnectionID() {
        return !isConnected() ? null : this.connectionID;
    }

    public ParsingExceptionCallback getParsingExceptionCallback() {
        return this.parsingExceptionCallback;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.jivesoftware.smack.Roster getRoster() {
        /*
        r7 = this;
        monitor-enter(r7);
        r0 = r7.isAuthenticated();	 Catch:{ all -> 0x004d }
        if (r0 == 0) goto L_0x000d;
    L_0x0007:
        r0 = r7.isAnonymous();	 Catch:{ all -> 0x004d }
        if (r0 == 0) goto L_0x001c;
    L_0x000d:
        r0 = r7.roster;	 Catch:{ all -> 0x004d }
        if (r0 != 0) goto L_0x0018;
    L_0x0011:
        r0 = new org.jivesoftware.smack.Roster;	 Catch:{ all -> 0x004d }
        r0.<init>(r7);	 Catch:{ all -> 0x004d }
        r7.roster = r0;	 Catch:{ all -> 0x004d }
    L_0x0018:
        r0 = r7.roster;	 Catch:{ all -> 0x004d }
        monitor-exit(r7);	 Catch:{ all -> 0x004d }
    L_0x001b:
        return r0;
    L_0x001c:
        monitor-exit(r7);	 Catch:{ all -> 0x004d }
        r0 = r7.config;
        r0 = r0.isRosterLoadedAtLogin();
        if (r0 != 0) goto L_0x002a;
    L_0x0025:
        r0 = r7.roster;
        r0.reload();
    L_0x002a:
        r0 = r7.roster;
        r0 = r0.rosterInitialized;
        if (r0 != 0) goto L_0x004a;
    L_0x0030:
        r6 = r7.roster;	 Catch:{ InterruptedException -> 0x0063 }
        monitor-enter(r6);	 Catch:{ InterruptedException -> 0x0063 }
        r0 = org.jivesoftware.smack.SmackConfiguration.getPacketReplyTimeout();	 Catch:{ all -> 0x0060 }
        r2 = (long) r0;	 Catch:{ all -> 0x0060 }
        r0 = java.lang.System.currentTimeMillis();	 Catch:{ all -> 0x0060 }
        r4 = r2;
    L_0x003d:
        r2 = r7.roster;	 Catch:{ all -> 0x0060 }
        r2 = r2.rosterInitialized;	 Catch:{ all -> 0x0060 }
        if (r2 != 0) goto L_0x0049;
    L_0x0043:
        r2 = 0;
        r2 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1));
        if (r2 > 0) goto L_0x0050;
    L_0x0049:
        monitor-exit(r6);	 Catch:{ all -> 0x0060 }
    L_0x004a:
        r0 = r7.roster;
        goto L_0x001b;
    L_0x004d:
        r0 = move-exception;
        monitor-exit(r7);	 Catch:{ all -> 0x004d }
        throw r0;
    L_0x0050:
        r2 = r7.roster;	 Catch:{ all -> 0x0060 }
        r2.wait(r4);	 Catch:{ all -> 0x0060 }
        r2 = java.lang.System.currentTimeMillis();	 Catch:{ all -> 0x0060 }
        r0 = r2 - r0;
        r0 = r4 - r0;
        r4 = r0;
        r0 = r2;
        goto L_0x003d;
    L_0x0060:
        r0 = move-exception;
        monitor-exit(r6);	 Catch:{ all -> 0x0060 }
        throw r0;	 Catch:{ InterruptedException -> 0x0063 }
    L_0x0063:
        r0 = move-exception;
        goto L_0x004a;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smack.XMPPConnection.getRoster():org.jivesoftware.smack.Roster");
    }

    public String getUser() {
        return !isAuthenticated() ? null : this.user;
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
        return isUsingTLS();
    }

    public boolean isSocketClosed() {
        return this.socketClosed;
    }

    public boolean isUsingCompression() {
        return this.compressionHandler != null && this.serverAckdCompression;
    }

    public boolean isUsingTLS() {
        return this.usingTLS;
    }

    public synchronized void login(String str, String str2, String str3) throws XMPPException {
        if (!isConnected()) {
            throw new XMPPException("Not connected to server.");
        } else if (this.authenticated) {
            throw new XMPPException("Already logged in to server.");
        } else {
            String trim = str.toLowerCase().trim();
            if (this.config.isCompressionEnabled()) {
                useCompression();
            }
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
            this.authenticated = true;
            this.anonymous = false;
            if (this.roster == null) {
                if (this.rosterStorage == null) {
                    this.roster = new Roster(this);
                } else {
                    this.roster = new Roster(this, this.rosterStorage);
                }
            }
            if (this.config.isRosterLoadedAtLogin()) {
                this.roster.reload();
            }
            if (this.config.isSendPresence()) {
                this.packetWriter.sendPacket(new Presence(Type.available));
            }
            this.config.setLoginInfo(trim, str2, str3);
            if (this.config.isDebuggerEnabled() && this.debugger != null) {
                this.debugger.userHasLogged(this.user);
            }
        }
    }

    public synchronized void loginAnonymously() throws XMPPException {
        if (!isConnected()) {
            throw new IllegalStateException("Not connected to server.");
        } else if (this.authenticated) {
            throw new IllegalStateException("Already logged in to server.");
        } else {
            String authenticateAnonymously = (this.config.isSASLAuthenticationEnabled() && this.saslAuthentication.hasAnonymousAuthentication()) ? this.saslAuthentication.authenticateAnonymously() : new NonSASLAuthentication(this).authenticateAnonymously();
            this.user = authenticateAnonymously;
            this.config.setServiceName(StringUtils.parseServer(authenticateAnonymously));
            if (this.config.isCompressionEnabled()) {
                useCompression();
            }
            this.packetWriter.sendPacket(new Presence(Type.available));
            this.authenticated = true;
            this.anonymous = true;
            if (this.config.isDebuggerEnabled() && this.debugger != null) {
                this.debugger.userHasLogged(this.user);
            }
        }
    }

    synchronized void notifyConnectionError(Exception exception) {
        if (!(this.packetReader == null || this.packetWriter == null)) {
            if (!(this.packetReader.done && this.packetWriter.done)) {
                this.packetReader.done = true;
                this.packetWriter.done = true;
                shutdown(new Presence(Type.unavailable));
                for (ConnectionListener connectionClosedOnError : getConnectionListeners()) {
                    try {
                        connectionClosedOnError.connectionClosedOnError(exception);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    protected void notifyReconnection() {
        for (ConnectionListener reconnectionSuccessful : getConnectionListeners()) {
            try {
                reconnectionSuccessful.reconnectionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void proceedTLSReceived() throws Exception {
        SSLContext instance;
        KeyManager[] keyManagerArr = null;
        SSLContext customSSLContext = this.config.getCustomSSLContext();
        if (this.config.getCallbackHandler() != null && customSSLContext == null) {
            PasswordCallback passwordCallback;
            KeyStore keyStore;
            if (this.config.getKeystoreType().equals(HlsMediaPlaylist.ENCRYPTION_METHOD_NONE)) {
                passwordCallback = null;
                keyStore = null;
            } else if (this.config.getKeystoreType().equals("PKCS11")) {
                try {
                    Constructor constructor = Class.forName("sun.security.pkcs11.SunPKCS11").getConstructor(new Class[]{InputStream.class});
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(("name = SmartCard\nlibrary = " + this.config.getPKCS11Library()).getBytes());
                    Provider provider = (Provider) constructor.newInstance(new Object[]{byteArrayInputStream});
                    Security.addProvider(provider);
                    keyStore = KeyStore.getInstance("PKCS11", provider);
                    passwordCallback = new PasswordCallback("PKCS11 Password: ", false);
                    this.config.getCallbackHandler().handle(new Callback[]{passwordCallback});
                    keyStore.load(null, passwordCallback.getPassword());
                } catch (Exception e) {
                    passwordCallback = null;
                    keyStore = null;
                }
            } else if (this.config.getKeystoreType().equals("Apple")) {
                KeyStore instance2 = KeyStore.getInstance("KeychainStore", "Apple");
                instance2.load(null, null);
                keyStore = instance2;
                passwordCallback = null;
            } else {
                keyStore = KeyStore.getInstance(this.config.getKeystoreType());
                try {
                    passwordCallback = new PasswordCallback("Keystore Password: ", false);
                    this.config.getCallbackHandler().handle(new Callback[]{passwordCallback});
                    keyStore.load(new FileInputStream(this.config.getKeystorePath()), passwordCallback.getPassword());
                } catch (Exception e2) {
                    passwordCallback = null;
                    keyStore = null;
                }
            }
            KeyManagerFactory instance3 = KeyManagerFactory.getInstance("SunX509");
            if (passwordCallback == null) {
                try {
                    instance3.init(keyStore, null);
                } catch (NullPointerException e3) {
                }
            } else {
                instance3.init(keyStore, passwordCallback.getPassword());
                passwordCallback.clearPassword();
            }
            keyManagerArr = instance3.getKeyManagers();
        }
        if (customSSLContext == null) {
            instance = SSLContext.getInstance("TLS");
            instance.init(keyManagerArr, new TrustManager[]{new ServerTrustManager(getServiceName(), this.config)}, new SecureRandom());
        } else {
            instance = customSSLContext;
        }
        Socket socket = this.socket;
        this.socket = instance.getSocketFactory().createSocket(socket, socket.getInetAddress().getHostAddress(), socket.getPort(), true);
        this.socket.setSoTimeout(0);
        this.socket.setKeepAlive(true);
        initReaderAndWriter();
        ((SSLSocket) this.socket).startHandshake();
        this.usingTLS = true;
        this.packetWriter.setWriter(this.writer);
        this.packetWriter.openStream();
    }

    public void removePacketWriterInterceptor(PacketInterceptor packetInterceptor) {
        removePacketInterceptor(packetInterceptor);
    }

    public void removePacketWriterListener(PacketListener packetListener) {
        removePacketSendingListener(packetListener);
    }

    public void sendPacket(Packet packet) {
        if (!isConnected()) {
            throw new IllegalStateException("Not connected to server.");
        } else if (packet == null) {
            throw new NullPointerException("Packet is null.");
        } else {
            this.packetWriter.sendPacket(packet);
        }
    }

    void setAvailableCompressionMethods(Collection<String> collection) {
        this.compressionMethods = collection;
    }

    public void setParsingExceptionCallback(ParsingExceptionCallback parsingExceptionCallback) {
        this.parsingExceptionCallback = parsingExceptionCallback;
    }

    public void setRosterStorage(RosterStorage rosterStorage) throws IllegalStateException {
        if (this.roster != null) {
            throw new IllegalStateException("Roster is already initialized");
        }
        this.rosterStorage = rosterStorage;
    }

    protected void shutdown(Presence presence) {
        if (this.packetWriter != null) {
            this.packetWriter.sendPacket(presence);
        }
        setWasAuthenticated(this.authenticated);
        this.authenticated = false;
        if (this.packetReader != null) {
            this.packetReader.shutdown();
        }
        if (this.packetWriter != null) {
            this.packetWriter.shutdown();
        }
        try {
            Thread.sleep(150);
        } catch (Exception e) {
        }
        this.socketClosed = true;
        try {
            this.socket.close();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        this.connected = false;
        this.reader = null;
        this.writer = null;
        this.saslAuthentication.init();
    }

    void startStreamCompression() throws Exception {
        this.serverAckdCompression = true;
        initReaderAndWriter();
        this.packetWriter.setWriter(this.writer);
        this.packetWriter.openStream();
        synchronized (this) {
            notify();
        }
    }

    void startTLSReceived(boolean z) {
        if (z && this.config.getSecurityMode() == SecurityMode.disabled) {
            notifyConnectionError(new IllegalStateException("TLS required by server but not allowed by connection configuration"));
        } else if (this.config.getSecurityMode() != SecurityMode.disabled) {
            try {
                this.writer.write("<starttls xmlns=\"urn:ietf:params:xml:ns:xmpp-tls\"/>");
                this.writer.flush();
            } catch (Exception e) {
                notifyConnectionError(e);
            }
        }
    }

    void streamCompressionDenied() {
        synchronized (this) {
            notify();
        }
    }
}
