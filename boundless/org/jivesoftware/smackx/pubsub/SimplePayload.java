package org.jivesoftware.smackx.pubsub;

import org.jivesoftware.smack.packet.PacketExtension;

public class SimplePayload implements PacketExtension {
    private String elemName;
    private String ns;
    private String payload;

    public SimplePayload(String str, String str2, String str3) {
        this.elemName = str;
        this.payload = str3;
        this.ns = str2;
    }

    public String getElementName() {
        return this.elemName;
    }

    public String getNamespace() {
        return this.ns;
    }

    public String toString() {
        return getClass().getName() + "payload [" + toXML() + "]";
    }

    public String toXML() {
        return this.payload;
    }
}
