package org.jivesoftware.smackx.packet;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.jivesoftware.smack.packet.PacketExtension;

public class MUCInitialPresence implements PacketExtension {
    private History history;
    private String password;

    public static class History {
        private int maxChars = -1;
        private int maxStanzas = -1;
        private int seconds = -1;
        private Date since;

        public int getMaxChars() {
            return this.maxChars;
        }

        public int getMaxStanzas() {
            return this.maxStanzas;
        }

        public int getSeconds() {
            return this.seconds;
        }

        public Date getSince() {
            return this.since;
        }

        public void setMaxChars(int i) {
            this.maxChars = i;
        }

        public void setMaxStanzas(int i) {
            this.maxStanzas = i;
        }

        public void setSeconds(int i) {
            this.seconds = i;
        }

        public void setSince(Date date) {
            this.since = date;
        }

        public String toXML() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<history");
            if (getMaxChars() != -1) {
                stringBuilder.append(" maxchars=\"").append(getMaxChars()).append("\"");
            }
            if (getMaxStanzas() != -1) {
                stringBuilder.append(" maxstanzas=\"").append(getMaxStanzas()).append("\"");
            }
            if (getSeconds() != -1) {
                stringBuilder.append(" seconds=\"").append(getSeconds()).append("\"");
            }
            if (getSince() != null) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                stringBuilder.append(" since=\"").append(simpleDateFormat.format(getSince())).append("\"");
            }
            stringBuilder.append("/>");
            return stringBuilder.toString();
        }
    }

    public String getElementName() {
        return "x";
    }

    public History getHistory() {
        return this.history;
    }

    public String getNamespace() {
        return "http://jabber.org/protocol/muc";
    }

    public String getPassword() {
        return this.password;
    }

    public void setHistory(History history) {
        this.history = history;
    }

    public void setPassword(String str) {
        this.password = str;
    }

    public String toXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\">");
        if (getPassword() != null) {
            stringBuilder.append("<password>").append(getPassword()).append("</password>");
        }
        if (getHistory() != null) {
            stringBuilder.append(getHistory().toXML());
        }
        stringBuilder.append("</").append(getElementName()).append(">");
        return stringBuilder.toString();
    }
}
