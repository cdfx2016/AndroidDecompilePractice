package org.jivesoftware.smackx.entitycaps.packet;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smackx.entitycaps.EntityCapsManager;

public class CapsExtension implements PacketExtension {
    private String hash;
    private String node;
    private String ver;

    public CapsExtension(String str, String str2, String str3) {
        this.node = str;
        this.ver = str2;
        this.hash = str3;
    }

    public String getElementName() {
        return EntityCapsManager.ELEMENT;
    }

    public String getHash() {
        return this.hash;
    }

    public String getNamespace() {
        return EntityCapsManager.NAMESPACE;
    }

    public String getNode() {
        return this.node;
    }

    public String getVer() {
        return this.ver;
    }

    public void setHash(String str) {
        this.hash = str;
    }

    public void setNode(String str) {
        this.node = str;
    }

    public void setVer(String str) {
        this.ver = str;
    }

    public String toXML() {
        return "<c xmlns=\"http://jabber.org/protocol/caps\" hash=\"" + this.hash + "\" " + "node=\"" + this.node + "\" " + "ver=\"" + this.ver + "\"/>";
    }
}
