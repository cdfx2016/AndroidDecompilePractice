package com.easemob.chat.core;

import org.jivesoftware.smack.packet.PacketExtension;

public class b implements PacketExtension {
    public static final String a = "encrypt";
    public static final String b = "jabber:client";

    public String getElementName() {
        return a;
    }

    public String getNamespace() {
        return b;
    }

    public String toXML() {
        return "<encrypt/>";
    }
}
