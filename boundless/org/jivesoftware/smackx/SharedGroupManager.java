package org.jivesoftware.smackx;

import java.util.List;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;

public class SharedGroupManager {
    public static List<String> getSharedGroups(Connection connection) throws XMPPException {
        Packet sharedGroupsInfo = new SharedGroupsInfo();
        sharedGroupsInfo.setType(Type.GET);
        PacketCollector createPacketCollector = connection.createPacketCollector(new PacketIDFilter(sharedGroupsInfo.getPacketID()));
        connection.sendPacket(sharedGroupsInfo);
        IQ iq = (IQ) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if (iq == null) {
            throw new XMPPException("No response from the server.");
        } else if (iq.getType() != Type.ERROR) {
            return ((SharedGroupsInfo) iq).getGroups();
        } else {
            throw new XMPPException(iq.getError());
        }
    }
}
