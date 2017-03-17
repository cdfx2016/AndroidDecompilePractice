package com.easemob.chat.a.a;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;

public class a extends IQ {
    public static final String a = "urn:xmpp:ping";
    public static final String b = "ping";

    public String getChildElementXML() {
        return getType() == Type.RESULT ? null : "<ping xmlns=\"urn:xmpp:ping\" />";
    }
}
