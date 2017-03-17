package org.jivesoftware.smackx.receipts;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketExtensionFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.ServiceDiscoveryManager;

public class DeliveryReceiptManager implements PacketListener {
    private static Map<Connection, DeliveryReceiptManager> instances = Collections.synchronizedMap(new WeakHashMap());
    private boolean auto_receipts_enabled = false;
    private Connection connection;
    private Set<ReceiptReceivedListener> receiptReceivedListeners = Collections.synchronizedSet(new HashSet());

    static {
        Connection.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(Connection connection) {
                DeliveryReceiptManager.getInstanceFor(connection);
            }
        });
    }

    private DeliveryReceiptManager(Connection connection) {
        ServiceDiscoveryManager.getInstanceFor(connection).addFeature("urn:xmpp:receipts");
        this.connection = connection;
        instances.put(connection, this);
        connection.addPacketListener(this, new PacketExtensionFilter("urn:xmpp:receipts"));
    }

    public static void addDeliveryReceiptRequest(Message message) {
        message.addExtension(new DeliveryReceiptRequest());
    }

    public static synchronized DeliveryReceiptManager getInstanceFor(Connection connection) {
        DeliveryReceiptManager deliveryReceiptManager;
        synchronized (DeliveryReceiptManager.class) {
            deliveryReceiptManager = (DeliveryReceiptManager) instances.get(connection);
            if (deliveryReceiptManager == null) {
                deliveryReceiptManager = new DeliveryReceiptManager(connection);
            }
        }
        return deliveryReceiptManager;
    }

    public static boolean hasDeliveryReceiptRequest(Packet packet) {
        return packet.getExtension("request", "urn:xmpp:receipts") != null;
    }

    public void addReceiptReceivedListener(ReceiptReceivedListener receiptReceivedListener) {
        this.receiptReceivedListeners.add(receiptReceivedListener);
    }

    public void disableAutoReceipts() {
        setAutoReceiptsEnabled(false);
    }

    public void enableAutoReceipts() {
        setAutoReceiptsEnabled(true);
    }

    public boolean getAutoReceiptsEnabled() {
        return this.auto_receipts_enabled;
    }

    public boolean isSupported(String str) {
        try {
            return ServiceDiscoveryManager.getInstanceFor(this.connection).discoverInfo(str).containsFeature("urn:xmpp:receipts");
        } catch (XMPPException e) {
            return false;
        }
    }

    public void processPacket(Packet packet) {
        DeliveryReceipt deliveryReceipt = (DeliveryReceipt) packet.getExtension("received", "urn:xmpp:receipts");
        if (deliveryReceipt != null) {
            for (ReceiptReceivedListener onReceiptReceived : this.receiptReceivedListeners) {
                onReceiptReceived.onReceiptReceived(packet.getFrom(), packet.getTo(), deliveryReceipt.getId());
            }
        }
        if (this.auto_receipts_enabled && ((DeliveryReceiptRequest) packet.getExtension("request", "urn:xmpp:receipts")) != null) {
            Packet message = new Message(packet.getFrom(), Type.normal);
            message.addExtension(new DeliveryReceipt(packet.getPacketID()));
            this.connection.sendPacket(message);
        }
    }

    public void removeReceiptReceivedListener(ReceiptReceivedListener receiptReceivedListener) {
        this.receiptReceivedListeners.remove(receiptReceivedListener);
    }

    public void setAutoReceiptsEnabled(boolean z) {
        this.auto_receipts_enabled = z;
    }
}
