package org.jivesoftware.smackx;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.Cache;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.DiscoverItems.Item;
import org.jivesoftware.smackx.packet.MultipleAddresses;
import org.jivesoftware.smackx.packet.MultipleAddresses.Address;

public class MultipleRecipientManager {
    private static Cache<String, String> services = new Cache(100, 86400000);

    private static class PacketCopy extends Packet {
        private String text;

        public PacketCopy(String str) {
            this.text = str;
        }

        public String toXML() {
            return this.text;
        }
    }

    private static String getMultipleRecipienServiceAddress(Connection connection) {
        XMPPException e;
        String serviceName = connection.getServiceName();
        String str = (String) services.get(serviceName);
        if (str == null) {
            synchronized (services) {
                str = (String) services.get(serviceName);
                if (str == null) {
                    String str2;
                    try {
                        if (ServiceDiscoveryManager.getInstanceFor(connection).discoverInfo(serviceName).containsFeature("http://jabber.org/protocol/address")) {
                            str2 = serviceName;
                        } else {
                            Iterator items = ServiceDiscoveryManager.getInstanceFor(connection).discoverItems(serviceName).getItems();
                            while (items.hasNext()) {
                                Item item = (Item) items.next();
                                if (ServiceDiscoveryManager.getInstanceFor(connection).discoverInfo(item.getEntityID(), item.getNode()).containsFeature("http://jabber.org/protocol/address")) {
                                    str2 = serviceName;
                                    break;
                                }
                            }
                            str2 = str;
                        }
                        try {
                            Object obj;
                            Cache cache = services;
                            if (str2 == null) {
                                obj = "";
                            } else {
                                str = str2;
                            }
                            cache.put(serviceName, obj);
                            str = str2;
                        } catch (XMPPException e2) {
                            e = e2;
                            e.printStackTrace();
                            str = str2;
                            return "".equals(str) ? str : null;
                        }
                    } catch (XMPPException e3) {
                        XMPPException xMPPException = e3;
                        str2 = str;
                        e = xMPPException;
                        e.printStackTrace();
                        str = str2;
                        if ("".equals(str)) {
                        }
                    }
                }
            }
        }
        if ("".equals(str)) {
        }
    }

    public static MultipleRecipientInfo getMultipleRecipientInfo(Packet packet) {
        MultipleAddresses multipleAddresses = (MultipleAddresses) packet.getExtension("addresses", "http://jabber.org/protocol/address");
        return multipleAddresses == null ? null : new MultipleRecipientInfo(multipleAddresses);
    }

    public static void reply(Connection connection, Message message, Message message2) throws XMPPException {
        MultipleRecipientInfo multipleRecipientInfo = getMultipleRecipientInfo(message);
        if (multipleRecipientInfo == null) {
            throw new XMPPException("Original message does not contain multiple recipient info");
        } else if (multipleRecipientInfo.shouldNotReply()) {
            throw new XMPPException("Original message should not be replied");
        } else if (multipleRecipientInfo.getReplyRoom() != null) {
            throw new XMPPException("Reply should be sent through a room");
        } else {
            if (message.getThread() != null) {
                message2.setThread(message.getThread());
            }
            Address replyAddress = multipleRecipientInfo.getReplyAddress();
            if (replyAddress == null || replyAddress.getJid() == null) {
                List arrayList = new ArrayList();
                List arrayList2 = new ArrayList();
                for (Address replyAddress2 : multipleRecipientInfo.getTOAddresses()) {
                    arrayList.add(replyAddress2.getJid());
                }
                for (Address replyAddress22 : multipleRecipientInfo.getCCAddresses()) {
                    arrayList2.add(replyAddress22.getJid());
                }
                if (!(arrayList.contains(message.getFrom()) || arrayList2.contains(message.getFrom()))) {
                    arrayList.add(message.getFrom());
                }
                String user = connection.getUser();
                if (!(arrayList.remove(user) || arrayList2.remove(user))) {
                    user = StringUtils.parseBareAddress(user);
                    arrayList.remove(user);
                    arrayList2.remove(user);
                }
                String multipleRecipienServiceAddress = getMultipleRecipienServiceAddress(connection);
                if (multipleRecipienServiceAddress != null) {
                    sendThroughService(connection, message2, arrayList, arrayList2, null, null, null, false, multipleRecipienServiceAddress);
                    return;
                } else {
                    sendToIndividualRecipients(connection, message2, arrayList, arrayList2, null);
                    return;
                }
            }
            message2.setTo(replyAddress22.getJid());
            connection.sendPacket(message2);
        }
    }

    public static void send(Connection connection, Packet packet, List<String> list, List<String> list2, List<String> list3) throws XMPPException {
        send(connection, packet, list, list2, list3, null, null, false);
    }

    public static void send(Connection connection, Packet packet, List<String> list, List<String> list2, List<String> list3, String str, String str2, boolean z) throws XMPPException {
        String multipleRecipienServiceAddress = getMultipleRecipienServiceAddress(connection);
        if (multipleRecipienServiceAddress != null) {
            sendThroughService(connection, packet, list, list2, list3, str, str2, z, multipleRecipienServiceAddress);
        } else if (z || ((str != null && str.trim().length() > 0) || (str2 != null && str2.trim().length() > 0))) {
            throw new XMPPException("Extended Stanza Addressing not supported by server");
        } else {
            sendToIndividualRecipients(connection, packet, list, list2, list3);
        }
    }

    private static void sendThroughService(Connection connection, Packet packet, List<String> list, List<String> list2, List<String> list3, String str, String str2, boolean z, String str3) {
        MultipleAddresses multipleAddresses = new MultipleAddresses();
        if (list != null) {
            for (String addAddress : list) {
                multipleAddresses.addAddress("to", addAddress, null, null, false, null);
            }
        }
        if (list2 != null) {
            for (String addAddress2 : list2) {
                multipleAddresses.addAddress(MultipleAddresses.CC, addAddress2, null, null, false, null);
            }
        }
        if (list3 != null) {
            for (String addAddress22 : list3) {
                multipleAddresses.addAddress(MultipleAddresses.BCC, addAddress22, null, null, false, null);
            }
        }
        if (z) {
            multipleAddresses.setNoReply();
        } else {
            if (str != null && str.trim().length() > 0) {
                multipleAddresses.addAddress(MultipleAddresses.REPLY_TO, str, null, null, false, null);
            }
            if (str2 != null && str2.trim().length() > 0) {
                multipleAddresses.addAddress(MultipleAddresses.REPLY_ROOM, str2, null, null, false, null);
            }
        }
        packet.setTo(str3);
        packet.addExtension(multipleAddresses);
        connection.sendPacket(packet);
    }

    private static void sendToIndividualRecipients(Connection connection, Packet packet, List<String> list, List<String> list2, List<String> list3) {
        if (list != null) {
            for (String to : list) {
                packet.setTo(to);
                connection.sendPacket(new PacketCopy(packet.toXML()));
            }
        }
        if (list2 != null) {
            for (String to2 : list2) {
                packet.setTo(to2);
                connection.sendPacket(new PacketCopy(packet.toXML()));
            }
        }
        if (list3 != null) {
            for (String to22 : list3) {
                packet.setTo(to22);
                connection.sendPacket(new PacketCopy(packet.toXML()));
            }
        }
    }
}
