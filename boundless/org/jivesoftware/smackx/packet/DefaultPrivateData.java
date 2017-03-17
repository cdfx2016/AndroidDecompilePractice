package org.jivesoftware.smackx.packet;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DefaultPrivateData implements PrivateData {
    private String elementName;
    private Map<String, String> map;
    private String namespace;

    public DefaultPrivateData(String str, String str2) {
        this.elementName = str;
        this.namespace = str2;
    }

    public String getElementName() {
        return this.elementName;
    }

    public synchronized Iterator<String> getNames() {
        return this.map == null ? Collections.emptyList().iterator() : Collections.unmodifiableSet(this.map.keySet()).iterator();
    }

    public String getNamespace() {
        return this.namespace;
    }

    public synchronized String getValue(String str) {
        return this.map == null ? null : (String) this.map.get(str);
    }

    public synchronized void setValue(String str, String str2) {
        if (this.map == null) {
            this.map = new HashMap();
        }
        this.map.put(str, str2);
    }

    public String toXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<").append(this.elementName).append(" xmlns=\"").append(this.namespace).append("\">");
        Iterator names = getNames();
        while (names.hasNext()) {
            String str = (String) names.next();
            String value = getValue(str);
            stringBuilder.append("<").append(str).append(">");
            stringBuilder.append(value);
            stringBuilder.append("</").append(str).append(">");
        }
        stringBuilder.append("</").append(this.elementName).append(">");
        return stringBuilder.toString();
    }
}
