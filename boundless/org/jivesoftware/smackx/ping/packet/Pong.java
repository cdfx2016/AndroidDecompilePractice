package org.jivesoftware.smackx.ping.packet;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;

public class Pong extends IQ {
    public Pong(Ping ping) {
        setType(Type.RESULT);
        setFrom(ping.getTo());
        setTo(ping.getFrom());
        setPacketID(ping.getPacketID());
    }

    public String getChildElementXML() {
        return null;
    }
}
