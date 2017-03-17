package org.jivesoftware.smack.filter;

import org.jivesoftware.smack.packet.Packet;

public class PacketTypeFilter implements PacketFilter {
    Class<? extends Packet> packetType;

    public PacketTypeFilter(Class<? extends Packet> cls) {
        if (Packet.class.isAssignableFrom(cls)) {
            this.packetType = cls;
            return;
        }
        throw new IllegalArgumentException("Packet type must be a sub-class of Packet.");
    }

    public boolean accept(Packet packet) {
        return this.packetType.isInstance(packet);
    }

    public String toString() {
        return "PacketTypeFilter: " + this.packetType.getName();
    }
}
