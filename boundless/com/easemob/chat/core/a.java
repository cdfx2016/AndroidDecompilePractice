package com.easemob.chat.core;

import org.jivesoftware.smack.packet.DefaultPacketExtension;

public class a extends DefaultPacketExtension {
    public static final String a = "received";
    public static final String b = "acked";
    public static final String c = "request";
    public static final String d = "urn:xmpp:receipts";
    public static final String e = "id";

    public a(String str) {
        super(str, "urn:xmpp:receipts");
    }

    public String toXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\" ");
        stringBuilder.append("id=\"").append(getValue("id")).append("\"/>");
        return stringBuilder.toString();
    }
}
