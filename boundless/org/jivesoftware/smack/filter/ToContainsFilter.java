package org.jivesoftware.smack.filter;

import org.jivesoftware.smack.packet.Packet;

public class ToContainsFilter implements PacketFilter {
    private String to;

    public ToContainsFilter(String str) {
        if (str == null) {
            throw new IllegalArgumentException("Parameter cannot be null.");
        }
        this.to = str.toLowerCase();
    }

    public boolean accept(Packet packet) {
        return (packet.getTo() == null || packet.getTo().toLowerCase().indexOf(this.to) == -1) ? false : true;
    }
}
