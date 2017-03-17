package org.jivesoftware.smackx;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.IQTypeFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smackx.packet.LastActivity;

public class LastActivityManager {
    private Connection connection;
    private long lastMessageSent;

    static {
        Connection.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(Connection connection) {
                LastActivityManager lastActivityManager = new LastActivityManager(connection);
            }
        });
    }

    private LastActivityManager(Connection connection) {
        this.connection = connection;
        connection.addPacketSendingListener(new PacketListener() {
            public void processPacket(Packet packet) {
                Mode mode = ((Presence) packet).getMode();
                if (mode != null) {
                    switch (mode) {
                        case available:
                        case chat:
                            LastActivityManager.this.resetIdleTime();
                            return;
                        default:
                            return;
                    }
                }
            }
        }, new PacketTypeFilter(Presence.class));
        connection.addPacketListener(new PacketListener() {
            public void processPacket(Packet packet) {
                if (((Message) packet).getType() != Type.error) {
                    LastActivityManager.this.resetIdleTime();
                }
            }
        }, new PacketTypeFilter(Message.class));
        connection.addPacketListener(new PacketListener() {
            public void processPacket(Packet packet) {
                Packet lastActivity = new LastActivity();
                lastActivity.setType(IQ.Type.RESULT);
                lastActivity.setTo(packet.getFrom());
                lastActivity.setFrom(packet.getTo());
                lastActivity.setPacketID(packet.getPacketID());
                lastActivity.setLastActivity(LastActivityManager.this.getIdleTime());
                LastActivityManager.this.connection.sendPacket(lastActivity);
            }
        }, new AndFilter(new IQTypeFilter(IQ.Type.GET), new PacketTypeFilter(LastActivity.class)));
        ServiceDiscoveryManager.getInstanceFor(connection).addFeature(LastActivity.NAMESPACE);
        resetIdleTime();
    }

    private long getIdleTime() {
        long j;
        long currentTimeMillis = System.currentTimeMillis();
        synchronized (this) {
            j = this.lastMessageSent;
        }
        return (currentTimeMillis - j) / 1000;
    }

    public static LastActivity getLastActivity(Connection connection, String str) throws XMPPException {
        Packet lastActivity = new LastActivity();
        lastActivity.setTo(str);
        PacketCollector createPacketCollector = connection.createPacketCollector(new PacketIDFilter(lastActivity.getPacketID()));
        connection.sendPacket(lastActivity);
        LastActivity lastActivity2 = (LastActivity) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if (lastActivity2 == null) {
            throw new XMPPException("No response from server on status set.");
        } else if (lastActivity2.getError() == null) {
            return lastActivity2;
        } else {
            throw new XMPPException(lastActivity2.getError());
        }
    }

    public static boolean isLastActivitySupported(Connection connection, String str) {
        try {
            return ServiceDiscoveryManager.getInstanceFor(connection).discoverInfo(str).containsFeature(LastActivity.NAMESPACE);
        } catch (XMPPException e) {
            return false;
        }
    }

    private void resetIdleTime() {
        long currentTimeMillis = System.currentTimeMillis();
        synchronized (this) {
            this.lastMessageSent = currentTimeMillis;
        }
    }
}
