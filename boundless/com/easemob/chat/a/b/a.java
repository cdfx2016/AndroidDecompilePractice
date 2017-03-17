package com.easemob.chat.a.b;

import org.jivesoftware.smackx.pubsub.NodeExtension;
import org.jivesoftware.smackx.pubsub.PubSubElementType;

public class a extends NodeExtension {
    private String a;

    public a() {
        super(PubSubElementType.ITEM);
    }

    public a(String str) {
        super(PubSubElementType.ITEM);
        this.a = str;
    }

    public String a() {
        return this.a;
    }

    public String getNamespace() {
        return null;
    }

    public String toString() {
        return new StringBuilder(String.valueOf(getClass().getName())).append(" | Content [").append(toXML()).append("]").toString();
    }

    public String toXML() {
        return "<entry xmlns='easemob:pubsub'>" + this.a + "</entry>";
    }
}
