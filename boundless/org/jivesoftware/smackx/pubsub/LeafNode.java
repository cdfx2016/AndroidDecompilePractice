package org.jivesoftware.smackx.pubsub;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.pubsub.ItemsExtension.ItemsElementType;
import org.jivesoftware.smackx.pubsub.packet.PubSub;
import org.jivesoftware.smackx.pubsub.packet.SyncPacketSend;

public class LeafNode extends Node {
    LeafNode(Connection connection, String str) {
        super(connection, str);
    }

    public void deleteAllItems() throws XMPPException {
        SyncPacketSend.getReply(this.con, createPubsubPacket(Type.SET, new NodeExtension(PubSubElementType.PURGE_OWNER, getId()), PubSubElementType.PURGE_OWNER.getNamespace()));
    }

    public void deleteItem(String str) throws XMPPException {
        Collection arrayList = new ArrayList(1);
        arrayList.add(str);
        deleteItem(arrayList);
    }

    public void deleteItem(Collection<String> collection) throws XMPPException {
        List arrayList = new ArrayList(collection.size());
        for (String item : collection) {
            arrayList.add(new Item(item));
        }
        SyncPacketSend.getReply(this.con, createPubsubPacket(Type.SET, new ItemsExtension(ItemsElementType.retract, getId(), arrayList)));
    }

    public DiscoverItems discoverItems() throws XMPPException {
        Packet discoverItems = new DiscoverItems();
        discoverItems.setTo(this.to);
        discoverItems.setNode(getId());
        return (DiscoverItems) SyncPacketSend.getReply(this.con, discoverItems);
    }

    public <T extends Item> List<T> getItems() throws XMPPException {
        return ((ItemsExtension) ((PubSub) SyncPacketSend.getReply(this.con, createPubsubPacket(Type.GET, new GetItemsRequest(getId())))).getExtension(PubSubElementType.ITEMS)).getItems();
    }

    public <T extends Item> List<T> getItems(int i) throws XMPPException {
        return ((ItemsExtension) ((PubSub) SyncPacketSend.getReply(this.con, createPubsubPacket(Type.GET, new GetItemsRequest(getId(), i)))).getExtension(PubSubElementType.ITEMS)).getItems();
    }

    public <T extends Item> List<T> getItems(int i, String str) throws XMPPException {
        return ((ItemsExtension) ((PubSub) SyncPacketSend.getReply(this.con, createPubsubPacket(Type.GET, new GetItemsRequest(getId(), str, i)))).getExtension(PubSubElementType.ITEMS)).getItems();
    }

    public <T extends Item> List<T> getItems(String str) throws XMPPException {
        return ((ItemsExtension) ((PubSub) SyncPacketSend.getReply(this.con, createPubsubPacket(Type.GET, new GetItemsRequest(getId(), str)))).getExtension(PubSubElementType.ITEMS)).getItems();
    }

    public <T extends Item> List<T> getItems(Collection<String> collection) throws XMPPException {
        List arrayList = new ArrayList(collection.size());
        for (String item : collection) {
            arrayList.add(new Item(item));
        }
        return ((ItemsExtension) ((PubSub) SyncPacketSend.getReply(this.con, createPubsubPacket(Type.GET, new ItemsExtension(ItemsElementType.items, getId(), arrayList)))).getExtension(PubSubElementType.ITEMS)).getItems();
    }

    public void publish() {
        this.con.sendPacket(createPubsubPacket(Type.SET, new NodeExtension(PubSubElementType.PUBLISH, getId())));
    }

    public <T extends Item> void publish(Collection<T> collection) {
        this.con.sendPacket(createPubsubPacket(Type.SET, new PublishItem(getId(), (Collection) collection)));
    }

    public <T extends Item> void publish(T t) {
        Object item;
        Collection arrayList = new ArrayList(1);
        if (t == null) {
            item = new Item();
        }
        arrayList.add(item);
        publish(arrayList);
    }

    public void send() throws XMPPException {
        SyncPacketSend.getReply(this.con, createPubsubPacket(Type.SET, new NodeExtension(PubSubElementType.PUBLISH, getId())));
    }

    public <T extends Item> void send(Collection<T> collection) throws XMPPException {
        SyncPacketSend.getReply(this.con, createPubsubPacket(Type.SET, new PublishItem(getId(), (Collection) collection)));
    }

    public <T extends Item> void send(T t) throws XMPPException {
        Object item;
        Collection arrayList = new ArrayList(1);
        if (t == null) {
            item = new Item();
        }
        arrayList.add(item);
        send(arrayList);
    }
}
