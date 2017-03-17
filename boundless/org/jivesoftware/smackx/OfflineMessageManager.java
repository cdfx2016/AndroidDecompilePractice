package org.jivesoftware.smackx;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketExtensionFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.packet.MessageEvent;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.OfflineMessageRequest.Item;

public class OfflineMessageManager {
    private static final String namespace = "http://jabber.org/protocol/offline";
    private Connection connection;
    private PacketFilter packetFilter = new AndFilter(new PacketExtensionFilter(MessageEvent.OFFLINE, namespace), new PacketTypeFilter(Message.class));

    public OfflineMessageManager(Connection connection) {
        this.connection = connection;
    }

    public void deleteMessages() throws XMPPException {
        Packet offlineMessageRequest = new OfflineMessageRequest();
        offlineMessageRequest.setPurge(true);
        PacketCollector createPacketCollector = this.connection.createPacketCollector(new PacketIDFilter(offlineMessageRequest.getPacketID()));
        this.connection.sendPacket(offlineMessageRequest);
        IQ iq = (IQ) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if (iq == null) {
            throw new XMPPException("No response from server.");
        } else if (iq.getError() != null) {
            throw new XMPPException(iq.getError());
        }
    }

    public void deleteMessages(List<String> list) throws XMPPException {
        Packet offlineMessageRequest = new OfflineMessageRequest();
        for (String item : list) {
            Item item2 = new Item(item);
            item2.setAction(DiscoverItems.Item.REMOVE_ACTION);
            offlineMessageRequest.addItem(item2);
        }
        PacketCollector createPacketCollector = this.connection.createPacketCollector(new PacketIDFilter(offlineMessageRequest.getPacketID()));
        this.connection.sendPacket(offlineMessageRequest);
        IQ iq = (IQ) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if (iq == null) {
            throw new XMPPException("No response from server.");
        } else if (iq.getError() != null) {
            throw new XMPPException(iq.getError());
        }
    }

    public Iterator<OfflineMessageHeader> getHeaders() throws XMPPException {
        List arrayList = new ArrayList();
        Iterator items = ServiceDiscoveryManager.getInstanceFor(this.connection).discoverItems(null, namespace).getItems();
        while (items.hasNext()) {
            arrayList.add(new OfflineMessageHeader((DiscoverItems.Item) items.next()));
        }
        return arrayList.iterator();
    }

    public int getMessageCount() throws XMPPException {
        Form formFrom = Form.getFormFrom(ServiceDiscoveryManager.getInstanceFor(this.connection).discoverInfo(null, namespace));
        return formFrom != null ? Integer.parseInt((String) formFrom.getField("number_of_messages").getValues().next()) : 0;
    }

    public Iterator<Message> getMessages() throws XMPPException {
        List arrayList = new ArrayList();
        Packet offlineMessageRequest = new OfflineMessageRequest();
        offlineMessageRequest.setFetch(true);
        PacketCollector createPacketCollector = this.connection.createPacketCollector(new PacketIDFilter(offlineMessageRequest.getPacketID()));
        PacketCollector createPacketCollector2 = this.connection.createPacketCollector(this.packetFilter);
        this.connection.sendPacket(offlineMessageRequest);
        IQ iq = (IQ) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if (iq == null) {
            throw new XMPPException("No response from server.");
        } else if (iq.getError() != null) {
            throw new XMPPException(iq.getError());
        } else {
            for (Object obj = (Message) createPacketCollector2.nextResult((long) SmackConfiguration.getPacketReplyTimeout()); obj != null; Message message = (Message) createPacketCollector2.nextResult((long) SmackConfiguration.getPacketReplyTimeout())) {
                arrayList.add(obj);
            }
            createPacketCollector2.cancel();
            return arrayList.iterator();
        }
    }

    public Iterator<Message> getMessages(final List<String> list) throws XMPPException {
        List arrayList = new ArrayList();
        Packet offlineMessageRequest = new OfflineMessageRequest();
        for (String item : list) {
            Item item2 = new Item(item);
            item2.setAction("view");
            offlineMessageRequest.addItem(item2);
        }
        PacketCollector createPacketCollector = this.connection.createPacketCollector(new PacketIDFilter(offlineMessageRequest.getPacketID()));
        PacketCollector createPacketCollector2 = this.connection.createPacketCollector(new AndFilter(this.packetFilter, new PacketFilter() {
            public boolean accept(Packet packet) {
                return list.contains(((OfflineMessageInfo) packet.getExtension(MessageEvent.OFFLINE, OfflineMessageManager.namespace)).getNode());
            }
        }));
        this.connection.sendPacket(offlineMessageRequest);
        IQ iq = (IQ) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if (iq == null) {
            throw new XMPPException("No response from server.");
        } else if (iq.getError() != null) {
            throw new XMPPException(iq.getError());
        } else {
            for (Object obj = (Message) createPacketCollector2.nextResult((long) SmackConfiguration.getPacketReplyTimeout()); obj != null; Message message = (Message) createPacketCollector2.nextResult((long) SmackConfiguration.getPacketReplyTimeout())) {
                arrayList.add(obj);
            }
            createPacketCollector2.cancel();
            return arrayList.iterator();
        }
    }

    public boolean supportsFlexibleRetrieval() throws XMPPException {
        return ServiceDiscoveryManager.getInstanceFor(this.connection).discoverInfo(this.connection.getServiceName()).containsFeature(namespace);
    }
}
