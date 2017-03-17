package org.jivesoftware.smack.filter;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;
import org.jivesoftware.smack.packet.Packet;

public class MessageTypeFilter implements PacketFilter {
    private final Type type;

    public MessageTypeFilter(Type type) {
        this.type = type;
    }

    public boolean accept(Packet packet) {
        return !(packet instanceof Message) ? false : ((Message) packet).getType().equals(this.type);
    }
}
