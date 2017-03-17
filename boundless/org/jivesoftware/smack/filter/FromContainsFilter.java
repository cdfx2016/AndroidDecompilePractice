package org.jivesoftware.smack.filter;

import org.jivesoftware.smack.packet.Packet;

public class FromContainsFilter implements PacketFilter {
    private String from;

    public FromContainsFilter(String str) {
        if (str == null) {
            throw new IllegalArgumentException("Parameter cannot be null.");
        }
        this.from = str.toLowerCase();
    }

    public boolean accept(Packet packet) {
        return (packet.getFrom() == null || packet.getFrom().toLowerCase().indexOf(this.from) == -1) ? false : true;
    }
}
