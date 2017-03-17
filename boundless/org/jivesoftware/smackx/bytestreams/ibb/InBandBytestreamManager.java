package org.jivesoftware.smackx.bytestreams.ibb;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.jivesoftware.smack.AbstractConnectionListener;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.packet.XMPPError.Condition;
import org.jivesoftware.smack.util.SyncPacketSend;
import org.jivesoftware.smackx.bytestreams.BytestreamListener;
import org.jivesoftware.smackx.bytestreams.BytestreamManager;
import org.jivesoftware.smackx.bytestreams.ibb.packet.Open;

public class InBandBytestreamManager implements BytestreamManager {
    public static final int MAXIMUM_BLOCK_SIZE = 65535;
    public static final String NAMESPACE = "http://jabber.org/protocol/ibb";
    private static final String SESSION_ID_PREFIX = "jibb_";
    private static final Map<Connection, InBandBytestreamManager> managers = new HashMap();
    private static final Random randomGenerator = new Random();
    private final List<BytestreamListener> allRequestListeners = Collections.synchronizedList(new LinkedList());
    private final CloseListener closeListener;
    private final Connection connection;
    private final DataListener dataListener;
    private int defaultBlockSize = 4096;
    private List<String> ignoredBytestreamRequests = Collections.synchronizedList(new LinkedList());
    private final InitiationListener initiationListener;
    private int maximumBlockSize = 65535;
    private final Map<String, InBandBytestreamSession> sessions = new ConcurrentHashMap();
    private StanzaType stanza = StanzaType.IQ;
    private final Map<String, BytestreamListener> userListeners = new ConcurrentHashMap();

    public enum StanzaType {
        IQ,
        MESSAGE
    }

    static {
        Connection.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(final Connection connection) {
                InBandBytestreamManager.getByteStreamManager(connection);
                connection.addConnectionListener(new AbstractConnectionListener() {
                    public void connectionClosed() {
                        InBandBytestreamManager.getByteStreamManager(connection).disableService();
                    }

                    public void connectionClosedOnError(Exception exception) {
                        InBandBytestreamManager.getByteStreamManager(connection).disableService();
                    }

                    public void reconnectionSuccessful() {
                        InBandBytestreamManager.getByteStreamManager(connection);
                    }
                });
            }
        });
    }

    private InBandBytestreamManager(Connection connection) {
        this.connection = connection;
        this.initiationListener = new InitiationListener(this);
        this.connection.addPacketListener(this.initiationListener, this.initiationListener.getFilter());
        this.dataListener = new DataListener(this);
        this.connection.addPacketListener(this.dataListener, this.dataListener.getFilter());
        this.closeListener = new CloseListener(this);
        this.connection.addPacketListener(this.closeListener, this.closeListener.getFilter());
    }

    private void disableService() {
        managers.remove(this.connection);
        this.connection.removePacketListener(this.initiationListener);
        this.connection.removePacketListener(this.dataListener);
        this.connection.removePacketListener(this.closeListener);
        this.initiationListener.shutdown();
        this.userListeners.clear();
        this.allRequestListeners.clear();
        this.sessions.clear();
        this.ignoredBytestreamRequests.clear();
    }

    public static synchronized InBandBytestreamManager getByteStreamManager(Connection connection) {
        InBandBytestreamManager inBandBytestreamManager;
        synchronized (InBandBytestreamManager.class) {
            if (connection == null) {
                inBandBytestreamManager = null;
            } else {
                inBandBytestreamManager = (InBandBytestreamManager) managers.get(connection);
                if (inBandBytestreamManager == null) {
                    inBandBytestreamManager = new InBandBytestreamManager(connection);
                    managers.put(connection, inBandBytestreamManager);
                }
            }
        }
        return inBandBytestreamManager;
    }

    private String getNextSessionID() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(SESSION_ID_PREFIX);
        stringBuilder.append(Math.abs(randomGenerator.nextLong()));
        return stringBuilder.toString();
    }

    public void addIncomingBytestreamListener(BytestreamListener bytestreamListener) {
        this.allRequestListeners.add(bytestreamListener);
    }

    public void addIncomingBytestreamListener(BytestreamListener bytestreamListener, String str) {
        this.userListeners.put(str, bytestreamListener);
    }

    public InBandBytestreamSession establishSession(String str) throws XMPPException {
        return establishSession(str, getNextSessionID());
    }

    public InBandBytestreamSession establishSession(String str, String str2) throws XMPPException {
        Packet open = new Open(str2, this.defaultBlockSize, this.stanza);
        open.setTo(str);
        SyncPacketSend.getReply(this.connection, open);
        InBandBytestreamSession inBandBytestreamSession = new InBandBytestreamSession(this.connection, open, str);
        this.sessions.put(str2, inBandBytestreamSession);
        return inBandBytestreamSession;
    }

    protected List<BytestreamListener> getAllRequestListeners() {
        return this.allRequestListeners;
    }

    protected Connection getConnection() {
        return this.connection;
    }

    public int getDefaultBlockSize() {
        return this.defaultBlockSize;
    }

    protected List<String> getIgnoredBytestreamRequests() {
        return this.ignoredBytestreamRequests;
    }

    public int getMaximumBlockSize() {
        return this.maximumBlockSize;
    }

    protected Map<String, InBandBytestreamSession> getSessions() {
        return this.sessions;
    }

    public StanzaType getStanza() {
        return this.stanza;
    }

    protected BytestreamListener getUserListener(String str) {
        return (BytestreamListener) this.userListeners.get(str);
    }

    public void ignoreBytestreamRequestOnce(String str) {
        this.ignoredBytestreamRequests.add(str);
    }

    public void removeIncomingBytestreamListener(String str) {
        this.userListeners.remove(str);
    }

    public void removeIncomingBytestreamListener(BytestreamListener bytestreamListener) {
        this.allRequestListeners.remove(bytestreamListener);
    }

    protected void replyItemNotFoundPacket(IQ iq) {
        this.connection.sendPacket(IQ.createErrorResponse(iq, new XMPPError(Condition.item_not_found)));
    }

    protected void replyRejectPacket(IQ iq) {
        this.connection.sendPacket(IQ.createErrorResponse(iq, new XMPPError(Condition.no_acceptable)));
    }

    protected void replyResourceConstraintPacket(IQ iq) {
        this.connection.sendPacket(IQ.createErrorResponse(iq, new XMPPError(Condition.resource_constraint)));
    }

    public void setDefaultBlockSize(int i) {
        if (i <= 0 || i > 65535) {
            throw new IllegalArgumentException("Default block size must be between 1 and 65535");
        }
        this.defaultBlockSize = i;
    }

    public void setMaximumBlockSize(int i) {
        if (i <= 0 || i > 65535) {
            throw new IllegalArgumentException("Maximum block size must be between 1 and 65535");
        }
        this.maximumBlockSize = i;
    }

    public void setStanza(StanzaType stanzaType) {
        this.stanza = stanzaType;
    }
}
