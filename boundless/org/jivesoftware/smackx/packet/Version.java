package org.jivesoftware.smackx.packet;

import org.jivesoftware.smack.packet.IQ;

public class Version extends IQ {
    private String name;
    private String os;
    private String version;

    public String getChildElementXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<query xmlns=\"jabber:iq:version\">");
        if (this.name != null) {
            stringBuilder.append("<name>").append(this.name).append("</name>");
        }
        if (this.version != null) {
            stringBuilder.append("<version>").append(this.version).append("</version>");
        }
        if (this.os != null) {
            stringBuilder.append("<os>").append(this.os).append("</os>");
        }
        stringBuilder.append("</query>");
        return stringBuilder.toString();
    }

    public String getName() {
        return this.name;
    }

    public String getOs() {
        return this.os;
    }

    public String getVersion() {
        return this.version;
    }

    public void setName(String str) {
        this.name = str;
    }

    public void setOs(String str) {
        this.os = str;
    }

    public void setVersion(String str) {
        this.version = str;
    }
}
