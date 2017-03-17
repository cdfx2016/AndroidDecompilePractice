package org.jivesoftware.smackx;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.packet.XMPPError.Condition;
import org.jivesoftware.smackx.entitycaps.EntityCapsManager;
import org.jivesoftware.smackx.entitycaps.EntityCapsManager.NodeVerHash;
import org.jivesoftware.smackx.packet.DataForm;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.jivesoftware.smackx.packet.DiscoverInfo.Identity;
import org.jivesoftware.smackx.packet.DiscoverItems;

public class ServiceDiscoveryManager {
    private static final String DEFAULT_IDENTITY_CATEGORY = "client";
    private static final String DEFAULT_IDENTITY_NAME = "Smack";
    private static final String DEFAULT_IDENTITY_TYPE = "pc";
    private static Identity defaultIdentity = new Identity(DEFAULT_IDENTITY_CATEGORY, DEFAULT_IDENTITY_NAME, DEFAULT_IDENTITY_TYPE);
    private static Map<Connection, ServiceDiscoveryManager> instances = Collections.synchronizedMap(new WeakHashMap());
    private EntityCapsManager capsManager;
    private WeakReference<Connection> connection;
    private DataForm extendedInfo = null;
    private final Set<String> features = new HashSet();
    private Set<Identity> identities = new HashSet();
    private Identity identity = defaultIdentity;
    private Map<String, NodeInformationProvider> nodeInformationProviders = new ConcurrentHashMap();

    static {
        Connection.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(Connection connection) {
                ServiceDiscoveryManager.getInstanceFor(connection);
            }
        });
    }

    @Deprecated
    public ServiceDiscoveryManager(Connection connection) {
        this.connection = new WeakReference(connection);
        instances.put(connection, this);
        addFeature(DiscoverInfo.NAMESPACE);
        addFeature(DiscoverItems.NAMESPACE);
        connection.addPacketListener(new PacketListener() {
            public void processPacket(Packet packet) {
                Connection connection = (Connection) ServiceDiscoveryManager.this.connection.get();
                if (connection != null) {
                    DiscoverItems discoverItems = (DiscoverItems) packet;
                    if (discoverItems != null && discoverItems.getType() == Type.GET) {
                        Packet discoverItems2 = new DiscoverItems();
                        discoverItems2.setType(Type.RESULT);
                        discoverItems2.setTo(discoverItems.getFrom());
                        discoverItems2.setPacketID(discoverItems.getPacketID());
                        discoverItems2.setNode(discoverItems.getNode());
                        NodeInformationProvider access$100 = ServiceDiscoveryManager.this.getNodeInformationProvider(discoverItems.getNode());
                        if (access$100 != null) {
                            discoverItems2.addItems(access$100.getNodeItems());
                            discoverItems2.addExtensions(access$100.getNodePacketExtensions());
                        } else if (discoverItems.getNode() != null) {
                            discoverItems2.setType(Type.ERROR);
                            discoverItems2.setError(new XMPPError(Condition.item_not_found));
                        }
                        connection.sendPacket(discoverItems2);
                    }
                }
            }
        }, new PacketTypeFilter(DiscoverItems.class));
        connection.addPacketListener(new PacketListener() {
            public void processPacket(Packet packet) {
                Connection connection = (Connection) ServiceDiscoveryManager.this.connection.get();
                if (connection != null) {
                    DiscoverInfo discoverInfo = (DiscoverInfo) packet;
                    if (discoverInfo != null && discoverInfo.getType() == Type.GET) {
                        Packet discoverInfo2 = new DiscoverInfo();
                        discoverInfo2.setType(Type.RESULT);
                        discoverInfo2.setTo(discoverInfo.getFrom());
                        discoverInfo2.setPacketID(discoverInfo.getPacketID());
                        discoverInfo2.setNode(discoverInfo.getNode());
                        if (discoverInfo.getNode() == null) {
                            ServiceDiscoveryManager.this.addDiscoverInfoTo(discoverInfo2);
                        } else {
                            NodeInformationProvider access$100 = ServiceDiscoveryManager.this.getNodeInformationProvider(discoverInfo.getNode());
                            if (access$100 != null) {
                                discoverInfo2.addFeatures(access$100.getNodeFeatures());
                                discoverInfo2.addIdentities(access$100.getNodeIdentities());
                                discoverInfo2.addExtensions(access$100.getNodePacketExtensions());
                            } else {
                                discoverInfo2.setType(Type.ERROR);
                                discoverInfo2.setError(new XMPPError(Condition.item_not_found));
                            }
                        }
                        connection.sendPacket(discoverInfo2);
                    }
                }
            }
        }, new PacketTypeFilter(DiscoverInfo.class));
    }

    public static boolean canPublishItems(DiscoverInfo discoverInfo) {
        return discoverInfo.containsFeature("http://jabber.org/protocol/disco#publish");
    }

    public static synchronized ServiceDiscoveryManager getInstanceFor(Connection connection) {
        ServiceDiscoveryManager serviceDiscoveryManager;
        synchronized (ServiceDiscoveryManager.class) {
            serviceDiscoveryManager = (ServiceDiscoveryManager) instances.get(connection);
            if (serviceDiscoveryManager == null) {
                serviceDiscoveryManager = new ServiceDiscoveryManager(connection);
            }
        }
        return serviceDiscoveryManager;
    }

    private NodeInformationProvider getNodeInformationProvider(String str) {
        return str == null ? null : (NodeInformationProvider) this.nodeInformationProviders.get(str);
    }

    private void renewEntityCapsVersion() {
        if (this.capsManager != null && this.capsManager.entityCapsEnabled()) {
            this.capsManager.updateLocalEntityCaps();
        }
    }

    public static void setDefaultIdentity(Identity identity) {
        defaultIdentity = identity;
    }

    public void addDiscoverInfoTo(DiscoverInfo discoverInfo) {
        discoverInfo.addIdentities(getIdentities());
        synchronized (this.features) {
            Iterator features = getFeatures();
            while (features.hasNext()) {
                discoverInfo.addFeature((String) features.next());
            }
            discoverInfo.addExtension(this.extendedInfo);
        }
    }

    public void addFeature(String str) {
        synchronized (this.features) {
            this.features.add(str);
            renewEntityCapsVersion();
        }
    }

    public void addIdentity(Identity identity) {
        this.identities.add(identity);
        renewEntityCapsVersion();
    }

    public boolean canPublishItems(String str) throws XMPPException {
        return canPublishItems(discoverInfo(str));
    }

    public DiscoverInfo discoverInfo(String str) throws XMPPException {
        String str2 = null;
        if (str == null) {
            return discoverInfo(null, null);
        }
        DiscoverInfo discoverInfoByUser = EntityCapsManager.getDiscoverInfoByUser(str);
        if (discoverInfoByUser != null) {
            return discoverInfoByUser;
        }
        NodeVerHash nodeVerHashByJid = EntityCapsManager.getNodeVerHashByJid(str);
        if (nodeVerHashByJid != null) {
            str2 = nodeVerHashByJid.getNodeVer();
        }
        DiscoverInfo discoverInfo = discoverInfo(str, str2);
        if (nodeVerHashByJid == null || !EntityCapsManager.verifyDiscoverInfoVersion(nodeVerHashByJid.getVer(), nodeVerHashByJid.getHash(), discoverInfo)) {
            return discoverInfo;
        }
        EntityCapsManager.addDiscoverInfoByNode(nodeVerHashByJid.getNodeVer(), discoverInfo);
        return discoverInfo;
    }

    public DiscoverInfo discoverInfo(String str, String str2) throws XMPPException {
        Connection connection = (Connection) this.connection.get();
        if (connection == null) {
            throw new XMPPException("Connection instance already gc'ed");
        }
        Packet discoverInfo = new DiscoverInfo();
        discoverInfo.setType(Type.GET);
        discoverInfo.setTo(str);
        discoverInfo.setNode(str2);
        PacketCollector createPacketCollector = connection.createPacketCollector(new PacketIDFilter(discoverInfo.getPacketID()));
        connection.sendPacket(discoverInfo);
        IQ iq = (IQ) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if (iq == null) {
            throw new XMPPException("No response from the server.");
        } else if (iq.getType() != Type.ERROR) {
            return (DiscoverInfo) iq;
        } else {
            throw new XMPPException(iq.getError());
        }
    }

    public DiscoverItems discoverItems(String str) throws XMPPException {
        return discoverItems(str, null);
    }

    public DiscoverItems discoverItems(String str, String str2) throws XMPPException {
        Connection connection = (Connection) this.connection.get();
        if (connection == null) {
            throw new XMPPException("Connection instance already gc'ed");
        }
        Packet discoverItems = new DiscoverItems();
        discoverItems.setType(Type.GET);
        discoverItems.setTo(str);
        discoverItems.setNode(str2);
        PacketCollector createPacketCollector = connection.createPacketCollector(new PacketIDFilter(discoverItems.getPacketID()));
        connection.sendPacket(discoverItems);
        IQ iq = (IQ) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if (iq == null) {
            throw new XMPPException("No response from the server.");
        } else if (iq.getType() != Type.ERROR) {
            return (DiscoverItems) iq;
        } else {
            throw new XMPPException(iq.getError());
        }
    }

    public DataForm getExtendedInfo() {
        return this.extendedInfo;
    }

    public List<PacketExtension> getExtendedInfoAsList() {
        if (this.extendedInfo == null) {
            return null;
        }
        List<PacketExtension> arrayList = new ArrayList(1);
        arrayList.add(this.extendedInfo);
        return arrayList;
    }

    public Iterator<String> getFeatures() {
        Iterator<String> it;
        synchronized (this.features) {
            it = Collections.unmodifiableList(new ArrayList(this.features)).iterator();
        }
        return it;
    }

    public List<String> getFeaturesList() {
        List linkedList;
        synchronized (this.features) {
            linkedList = new LinkedList(this.features);
        }
        return linkedList;
    }

    public Set<Identity> getIdentities() {
        Set hashSet = new HashSet(this.identities);
        hashSet.add(defaultIdentity);
        return Collections.unmodifiableSet(hashSet);
    }

    public String getIdentityName() {
        return this.identity.getName();
    }

    public String getIdentityType() {
        return this.identity.getType();
    }

    public boolean includesFeature(String str) {
        boolean contains;
        synchronized (this.features) {
            contains = this.features.contains(str);
        }
        return contains;
    }

    public void publishItems(String str, String str2, DiscoverItems discoverItems) throws XMPPException {
        Connection connection = (Connection) this.connection.get();
        if (connection == null) {
            throw new XMPPException("Connection instance already gc'ed");
        }
        discoverItems.setType(Type.SET);
        discoverItems.setTo(str);
        discoverItems.setNode(str2);
        PacketCollector createPacketCollector = connection.createPacketCollector(new PacketIDFilter(discoverItems.getPacketID()));
        connection.sendPacket(discoverItems);
        IQ iq = (IQ) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if (iq == null) {
            throw new XMPPException("No response from the server.");
        } else if (iq.getType() == Type.ERROR) {
            throw new XMPPException(iq.getError());
        }
    }

    public void publishItems(String str, DiscoverItems discoverItems) throws XMPPException {
        publishItems(str, null, discoverItems);
    }

    public void removeExtendedInfo() {
        this.extendedInfo = null;
        renewEntityCapsVersion();
    }

    public void removeFeature(String str) {
        synchronized (this.features) {
            this.features.remove(str);
            renewEntityCapsVersion();
        }
    }

    public boolean removeIdentity(Identity identity) {
        if (identity.equals(this.identity)) {
            return false;
        }
        this.identities.remove(identity);
        renewEntityCapsVersion();
        return true;
    }

    public void removeNodeInformationProvider(String str) {
        this.nodeInformationProviders.remove(str);
    }

    public void setEntityCapsManager(EntityCapsManager entityCapsManager) {
        this.capsManager = entityCapsManager;
    }

    public void setExtendedInfo(DataForm dataForm) {
        this.extendedInfo = dataForm;
        renewEntityCapsVersion();
    }

    public void setIdentityName(String str) {
        this.identity.setName(str);
        renewEntityCapsVersion();
    }

    public void setIdentityType(String str) {
        this.identity.setType(str);
        renewEntityCapsVersion();
    }

    public void setNodeInformationProvider(String str, NodeInformationProvider nodeInformationProvider) {
        this.nodeInformationProviders.put(str, nodeInformationProvider);
    }
}
