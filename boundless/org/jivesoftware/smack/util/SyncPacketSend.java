package org.jivesoftware.smack.util;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.Packet;

public final class SyncPacketSend {
    private SyncPacketSend() {
    }

    public static Packet getReply(Connection connection, Packet packet) throws XMPPException {
        return getReply(connection, packet, (long) SmackConfiguration.getPacketReplyTimeout());
    }

    public static Packet getReply(Connection connection, Packet packet, long j) throws XMPPException {
        PacketCollector createPacketCollector = connection.createPacketCollector(new PacketIDFilter(packet.getPacketID()));
        connection.sendPacket(packet);
        Packet nextResult = createPacketCollector.nextResult(j);
        createPacketCollector.cancel();
        if (nextResult == null) {
            throw new XMPPException("No response from server.");
        } else if (nextResult.getError() == null) {
            return nextResult;
        } else {
            throw new XMPPException(nextResult.getError());
        }
    }
}
