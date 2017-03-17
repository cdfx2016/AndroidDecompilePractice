package org.jivesoftware.smackx.pubsub;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.jivesoftware.smackx.packet.DiscoverInfo.Identity;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.pubsub.packet.PubSub;
import org.jivesoftware.smackx.pubsub.packet.PubSubNamespace;
import org.jivesoftware.smackx.pubsub.packet.SyncPacketSend;
import org.jivesoftware.smackx.pubsub.util.NodeUtils;

public final class PubSubManager {
    private Connection con;
    private Map<String, Node> nodeMap = new ConcurrentHashMap();
    private String to;

    public PubSubManager(Connection connection) {
        this.con = connection;
        this.to = "pubsub." + connection.getServiceName();
    }

    public PubSubManager(Connection connection, String str) {
        this.con = connection;
        this.to = str;
    }

    static PubSub createPubsubPacket(String str, Type type, PacketExtension packetExtension) {
        return createPubsubPacket(str, type, packetExtension, null);
    }

    static PubSub createPubsubPacket(String str, Type type, PacketExtension packetExtension, PubSubNamespace pubSubNamespace) {
        PubSub pubSub = new PubSub();
        pubSub.setTo(str);
        pubSub.setType(type);
        if (pubSubNamespace != null) {
            pubSub.setPubSubNamespace(pubSubNamespace);
        }
        pubSub.addExtension(packetExtension);
        return pubSub;
    }

    static Packet sendPubsubPacket(Connection connection, String str, Type type, PacketExtension packetExtension) throws XMPPException {
        return sendPubsubPacket(connection, str, type, packetExtension, null);
    }

    static Packet sendPubsubPacket(Connection connection, String str, Type type, PacketExtension packetExtension, PubSubNamespace pubSubNamespace) throws XMPPException {
        return SyncPacketSend.getReply(connection, createPubsubPacket(str, type, packetExtension, pubSubNamespace));
    }

    static Packet sendPubsubPacket(Connection connection, String str, Type type, PubSub pubSub) throws XMPPException {
        return sendPubsubPacket(connection, str, type, pubSub, null);
    }

    static Packet sendPubsubPacket(Connection connection, String str, Type type, PubSub pubSub, PubSubNamespace pubSubNamespace) throws XMPPException {
        return SyncPacketSend.getReply(connection, pubSub);
    }

    private Packet sendPubsubPacket(Type type, PacketExtension packetExtension) throws XMPPException {
        return sendPubsubPacket(type, packetExtension, null);
    }

    private Packet sendPubsubPacket(Type type, PacketExtension packetExtension, PubSubNamespace pubSubNamespace) throws XMPPException {
        return sendPubsubPacket(this.con, this.to, type, packetExtension, pubSubNamespace);
    }

    public LeafNode createNode() throws XMPPException {
        LeafNode leafNode = new LeafNode(this.con, ((NodeExtension) ((PubSub) sendPubsubPacket(Type.SET, new NodeExtension(PubSubElementType.CREATE))).getExtension("create", PubSubNamespace.BASIC.getXmlns())).getNode());
        leafNode.setTo(this.to);
        this.nodeMap.put(leafNode.getId(), leafNode);
        return leafNode;
    }

    public LeafNode createNode(String str) throws XMPPException {
        return (LeafNode) createNode(str, null);
    }

    public Node createNode(String str, Form form) throws XMPPException {
        PubSub createPubsubPacket = createPubsubPacket(this.to, Type.SET, new NodeExtension(PubSubElementType.CREATE, str));
        boolean z = true;
        if (form != null) {
            createPubsubPacket.addExtension(new FormNode(FormNodeType.CONFIGURE, form));
            FormField field = form.getField(ConfigureNodeFields.node_type.getFieldName());
            if (field != null) {
                z = ((String) field.getValues().next()).equals(NodeType.leaf.toString());
            }
        }
        sendPubsubPacket(this.con, this.to, Type.SET, createPubsubPacket);
        Node leafNode = z ? new LeafNode(this.con, str) : new CollectionNode(this.con, str);
        leafNode.setTo(this.to);
        this.nodeMap.put(leafNode.getId(), leafNode);
        return leafNode;
    }

    public void deleteNode(String str) throws XMPPException {
        sendPubsubPacket(Type.SET, new NodeExtension(PubSubElementType.DELETE, str), PubSubElementType.DELETE.getNamespace());
        this.nodeMap.remove(str);
    }

    public DiscoverItems discoverNodes(String str) throws XMPPException {
        Packet discoverItems = new DiscoverItems();
        if (str != null) {
            discoverItems.setNode(str);
        }
        discoverItems.setTo(this.to);
        return (DiscoverItems) SyncPacketSend.getReply(this.con, discoverItems);
    }

    public List<Affiliation> getAffiliations() throws XMPPException {
        return ((AffiliationsExtension) ((PubSub) sendPubsubPacket(Type.GET, new NodeExtension(PubSubElementType.AFFILIATIONS))).getExtension(PubSubElementType.AFFILIATIONS)).getAffiliations();
    }

    public ConfigureForm getDefaultConfiguration() throws XMPPException {
        return NodeUtils.getFormFromPacket((PubSub) sendPubsubPacket(Type.GET, new NodeExtension(PubSubElementType.DEFAULT), PubSubElementType.DEFAULT.getNamespace()), PubSubElementType.DEFAULT);
    }

    public <T extends Node> T getNode(String str) throws XMPPException {
        T t = (Node) this.nodeMap.get(str);
        if (t == null) {
            Packet discoverInfo = new DiscoverInfo();
            discoverInfo.setTo(this.to);
            discoverInfo.setNode(str);
            t = ((Identity) ((DiscoverInfo) SyncPacketSend.getReply(this.con, discoverInfo)).getIdentities().next()).getType().equals(NodeType.leaf.toString()) ? new LeafNode(this.con, str) : new CollectionNode(this.con, str);
            t.setTo(this.to);
            this.nodeMap.put(str, t);
        }
        return t;
    }

    public List<Subscription> getSubscriptions() throws XMPPException {
        return ((SubscriptionsExtension) sendPubsubPacket(Type.GET, new NodeExtension(PubSubElementType.SUBSCRIPTIONS)).getExtension(PubSubElementType.SUBSCRIPTIONS.getElementName(), PubSubElementType.SUBSCRIPTIONS.getNamespace().getXmlns())).getSubscriptions();
    }

    public DiscoverInfo getSupportedFeatures() throws XMPPException {
        return ServiceDiscoveryManager.getInstanceFor(this.con).discoverInfo(this.to);
    }
}
