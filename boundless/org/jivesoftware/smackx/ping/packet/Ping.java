package org.jivesoftware.smackx.ping.packet;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;

public class Ping extends IQ {
    public Ping(String str, String str2) {
        setTo(str2);
        setFrom(str);
        setType(Type.GET);
        setPacketID(getPacketID());
    }

    public String getChildElementXML() {
        return "<ping xmlns='urn:xmpp:ping' />";
    }
}
