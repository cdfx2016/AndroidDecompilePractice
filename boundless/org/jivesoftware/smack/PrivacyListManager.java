package org.jivesoftware.smack;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.IQTypeFilter;
import org.jivesoftware.smack.filter.PacketExtensionFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Privacy;
import org.jivesoftware.smack.packet.PrivacyItem;

public class PrivacyListManager {
    private static Map<Connection, PrivacyListManager> instances = Collections.synchronizedMap(new WeakHashMap());
    private WeakReference<Connection> connection;
    private final List<PrivacyListListener> listeners = new ArrayList();
    PacketFilter packetFilter = new AndFilter(new IQTypeFilter(Type.SET), new PacketExtensionFilter("query", "jabber:iq:privacy"));

    static {
        Connection.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(Connection connection) {
                PrivacyListManager.getInstanceFor(connection);
            }
        });
    }

    private PrivacyListManager(final Connection connection) {
        this.connection = new WeakReference(connection);
        instances.put(connection, this);
        connection.addPacketListener(new PacketListener() {
            public void processPacket(Packet packet) {
                if (packet != null && packet.getError() == null) {
                    Privacy privacy = (Privacy) packet;
                    synchronized (PrivacyListManager.this.listeners) {
                        for (PrivacyListListener privacyListListener : PrivacyListManager.this.listeners) {
                            for (Entry entry : privacy.getItemLists().entrySet()) {
                                String str = (String) entry.getKey();
                                List list = (List) entry.getValue();
                                if (list.isEmpty()) {
                                    privacyListListener.updatedPrivacyList(str);
                                } else {
                                    privacyListListener.setPrivacyList(str, list);
                                }
                            }
                        }
                    }
                    Packet anonymousClass1 = new IQ() {
                        public String getChildElementXML() {
                            return "";
                        }
                    };
                    anonymousClass1.setType(Type.RESULT);
                    anonymousClass1.setFrom(packet.getFrom());
                    anonymousClass1.setPacketID(packet.getPacketID());
                    connection.sendPacket(anonymousClass1);
                }
            }
        }, this.packetFilter);
    }

    public static synchronized PrivacyListManager getInstanceFor(Connection connection) {
        PrivacyListManager privacyListManager;
        synchronized (PrivacyListManager.class) {
            privacyListManager = (PrivacyListManager) instances.get(connection);
            if (privacyListManager == null) {
                privacyListManager = new PrivacyListManager(connection);
            }
        }
        return privacyListManager;
    }

    private List<PrivacyItem> getPrivacyListItems(String str) throws XMPPException {
        Privacy privacy = new Privacy();
        privacy.setPrivacyList(str, new ArrayList());
        return getRequest(privacy).getPrivacyList(str);
    }

    private Privacy getPrivacyWithListNames() throws XMPPException {
        return getRequest(new Privacy());
    }

    private Privacy getRequest(Privacy privacy) throws XMPPException {
        Connection connection = (Connection) this.connection.get();
        if (connection == null) {
            throw new XMPPException("Connection instance already gc'ed");
        }
        privacy.setType(Type.GET);
        privacy.setFrom(getUser());
        PacketCollector createPacketCollector = connection.createPacketCollector(new PacketIDFilter(privacy.getPacketID()));
        connection.sendPacket(privacy);
        Privacy privacy2 = (Privacy) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if (privacy2 == null) {
            throw new XMPPException("No response from server.");
        } else if (privacy2.getError() == null) {
            return privacy2;
        } else {
            throw new XMPPException(privacy2.getError());
        }
    }

    private String getUser() {
        return ((Connection) this.connection.get()).getUser();
    }

    private Packet setRequest(Privacy privacy) throws XMPPException {
        Connection connection = (Connection) this.connection.get();
        if (connection == null) {
            throw new XMPPException("Connection instance already gc'ed");
        }
        privacy.setType(Type.SET);
        privacy.setFrom(getUser());
        PacketCollector createPacketCollector = connection.createPacketCollector(new PacketIDFilter(privacy.getPacketID()));
        connection.sendPacket(privacy);
        Packet nextResult = createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if (nextResult == null) {
            throw new XMPPException("No response from server.");
        } else if (nextResult.getError() == null) {
            return nextResult;
        } else {
            throw new XMPPException(nextResult.getError());
        }
    }

    public void addListener(PrivacyListListener privacyListListener) {
        synchronized (this.listeners) {
            this.listeners.add(privacyListListener);
        }
    }

    public void createPrivacyList(String str, List<PrivacyItem> list) throws XMPPException {
        updatePrivacyList(str, list);
    }

    public void declineActiveList() throws XMPPException {
        Privacy privacy = new Privacy();
        privacy.setDeclineActiveList(true);
        setRequest(privacy);
    }

    public void declineDefaultList() throws XMPPException {
        Privacy privacy = new Privacy();
        privacy.setDeclineDefaultList(true);
        setRequest(privacy);
    }

    public void deletePrivacyList(String str) throws XMPPException {
        Privacy privacy = new Privacy();
        privacy.setPrivacyList(str, new ArrayList());
        setRequest(privacy);
    }

    public PrivacyList getActiveList() throws XMPPException {
        Privacy privacyWithListNames = getPrivacyWithListNames();
        String activeName = privacyWithListNames.getActiveName();
        boolean z = (privacyWithListNames.getActiveName() == null || privacyWithListNames.getDefaultName() == null || !privacyWithListNames.getActiveName().equals(privacyWithListNames.getDefaultName())) ? false : true;
        return new PrivacyList(true, z, activeName, getPrivacyListItems(activeName));
    }

    public PrivacyList getDefaultList() throws XMPPException {
        Privacy privacyWithListNames = getPrivacyWithListNames();
        String defaultName = privacyWithListNames.getDefaultName();
        boolean z = (privacyWithListNames.getActiveName() == null || privacyWithListNames.getDefaultName() == null || !privacyWithListNames.getActiveName().equals(privacyWithListNames.getDefaultName())) ? false : true;
        return new PrivacyList(z, true, defaultName, getPrivacyListItems(defaultName));
    }

    public PrivacyList getPrivacyList(String str) throws XMPPException {
        return new PrivacyList(false, false, str, getPrivacyListItems(str));
    }

    public PrivacyList[] getPrivacyLists() throws XMPPException {
        Privacy privacyWithListNames = getPrivacyWithListNames();
        Set<String> privacyListNames = privacyWithListNames.getPrivacyListNames();
        PrivacyList[] privacyListArr = new PrivacyList[privacyListNames.size()];
        int i = 0;
        for (String str : privacyListNames) {
            privacyListArr[i] = new PrivacyList(str.equals(privacyWithListNames.getActiveName()), str.equals(privacyWithListNames.getDefaultName()), str, getPrivacyListItems(str));
            i++;
        }
        return privacyListArr;
    }

    public void setActiveListName(String str) throws XMPPException {
        Privacy privacy = new Privacy();
        privacy.setActiveName(str);
        setRequest(privacy);
    }

    public void setDefaultListName(String str) throws XMPPException {
        Privacy privacy = new Privacy();
        privacy.setDefaultName(str);
        setRequest(privacy);
    }

    public void updatePrivacyList(String str, List<PrivacyItem> list) throws XMPPException {
        Privacy privacy = new Privacy();
        privacy.setPrivacyList(str, list);
        setRequest(privacy);
    }
}
