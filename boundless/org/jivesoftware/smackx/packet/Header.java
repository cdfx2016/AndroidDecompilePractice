package org.jivesoftware.smackx.packet;

import org.jivesoftware.smack.packet.PacketExtension;

public class Header implements PacketExtension {
    private String name;
    private String value;

    public Header(String str, String str2) {
        this.name = str;
        this.value = str2;
    }

    public String getElementName() {
        return "header";
    }

    public String getName() {
        return this.name;
    }

    public String getNamespace() {
        return HeadersExtension.NAMESPACE;
    }

    public String getValue() {
        return this.value;
    }

    public String toXML() {
        return "<header name='" + this.name + "'>" + this.value + "</header>";
    }
}
