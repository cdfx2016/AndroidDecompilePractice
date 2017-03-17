package org.jivesoftware.smackx.packet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jivesoftware.smack.packet.IQ;

public class MUCAdmin extends IQ {
    private List<Item> items = new ArrayList();

    public static class Item {
        private String actor;
        private String affiliation;
        private String jid;
        private String nick;
        private String reason;
        private String role;

        public Item(String str, String str2) {
            this.affiliation = str;
            this.role = str2;
        }

        public String getActor() {
            return this.actor;
        }

        public String getAffiliation() {
            return this.affiliation;
        }

        public String getJid() {
            return this.jid;
        }

        public String getNick() {
            return this.nick;
        }

        public String getReason() {
            return this.reason;
        }

        public String getRole() {
            return this.role;
        }

        public void setActor(String str) {
            this.actor = str;
        }

        public void setJid(String str) {
            this.jid = str;
        }

        public void setNick(String str) {
            this.nick = str;
        }

        public void setReason(String str) {
            this.reason = str;
        }

        public String toXML() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<item");
            if (getAffiliation() != null) {
                stringBuilder.append(" affiliation=\"").append(getAffiliation()).append("\"");
            }
            if (getJid() != null) {
                stringBuilder.append(" jid=\"").append(getJid()).append("\"");
            }
            if (getNick() != null) {
                stringBuilder.append(" nick=\"").append(getNick()).append("\"");
            }
            if (getRole() != null) {
                stringBuilder.append(" role=\"").append(getRole()).append("\"");
            }
            if (getReason() == null && getActor() == null) {
                stringBuilder.append("/>");
            } else {
                stringBuilder.append(">");
                if (getReason() != null) {
                    stringBuilder.append("<reason>").append(getReason()).append("</reason>");
                }
                if (getActor() != null) {
                    stringBuilder.append("<actor jid=\"").append(getActor()).append("\"/>");
                }
                stringBuilder.append("</item>");
            }
            return stringBuilder.toString();
        }
    }

    public void addItem(Item item) {
        synchronized (this.items) {
            this.items.add(item);
        }
    }

    public String getChildElementXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<query xmlns=\"http://jabber.org/protocol/muc#admin\">");
        synchronized (this.items) {
            for (int i = 0; i < this.items.size(); i++) {
                stringBuilder.append(((Item) this.items.get(i)).toXML());
            }
        }
        stringBuilder.append(getExtensionsXML());
        stringBuilder.append("</query>");
        return stringBuilder.toString();
    }

    public Iterator<Item> getItems() {
        Iterator<Item> it;
        synchronized (this.items) {
            it = Collections.unmodifiableList(new ArrayList(this.items)).iterator();
        }
        return it;
    }
}
