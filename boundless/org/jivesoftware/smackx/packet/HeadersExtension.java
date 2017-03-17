package org.jivesoftware.smackx.packet;

import java.util.Collection;
import java.util.Collections;
import org.jivesoftware.smack.packet.PacketExtension;

public class HeadersExtension implements PacketExtension {
    public static final String NAMESPACE = "http://jabber.org/protocol/shim";
    private Collection<Header> headers = Collections.EMPTY_LIST;

    public HeadersExtension(Collection<Header> collection) {
        if (collection != null) {
            this.headers = collection;
        }
    }

    public String getElementName() {
        return "headers";
    }

    public Collection<Header> getHeaders() {
        return this.headers;
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public String toXML() {
        StringBuilder stringBuilder = new StringBuilder("<" + getElementName() + " xmlns='" + getNamespace() + "'>");
        for (Header toXML : this.headers) {
            stringBuilder.append(toXML.toXML());
        }
        stringBuilder.append("</" + getElementName() + '>');
        return stringBuilder.toString();
    }
}
