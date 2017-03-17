package org.jivesoftware.smackx.packet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jivesoftware.smack.packet.PacketExtension;

public class XHTMLExtension implements PacketExtension {
    private List<String> bodies = new ArrayList();

    public void addBody(String str) {
        synchronized (this.bodies) {
            this.bodies.add(str);
        }
    }

    public Iterator<String> getBodies() {
        Iterator<String> it;
        synchronized (this.bodies) {
            it = Collections.unmodifiableList(new ArrayList(this.bodies)).iterator();
        }
        return it;
    }

    public int getBodiesCount() {
        return this.bodies.size();
    }

    public String getElementName() {
        return "html";
    }

    public String getNamespace() {
        return "http://jabber.org/protocol/xhtml-im";
    }

    public String toXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\">");
        Iterator bodies = getBodies();
        while (bodies.hasNext()) {
            stringBuilder.append((String) bodies.next());
        }
        stringBuilder.append("</").append(getElementName()).append(">");
        return stringBuilder.toString();
    }
}
