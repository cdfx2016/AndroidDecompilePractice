package org.jivesoftware.smackx.packet;

import java.util.ArrayList;
import java.util.List;
import org.jivesoftware.smack.packet.PacketExtension;

public class MultipleAddresses implements PacketExtension {
    public static final String BCC = "bcc";
    public static final String CC = "cc";
    public static final String NO_REPLY = "noreply";
    public static final String REPLY_ROOM = "replyroom";
    public static final String REPLY_TO = "replyto";
    public static final String TO = "to";
    private List<Address> addresses = new ArrayList();

    public static class Address {
        private boolean delivered;
        private String description;
        private String jid;
        private String node;
        private String type;
        private String uri;

        private Address(String str) {
            this.type = str;
        }

        private void setDelivered(boolean z) {
            this.delivered = z;
        }

        private void setDescription(String str) {
            this.description = str;
        }

        private void setJid(String str) {
            this.jid = str;
        }

        private void setNode(String str) {
            this.node = str;
        }

        private void setUri(String str) {
            this.uri = str;
        }

        private String toXML() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<address type=\"");
            stringBuilder.append(this.type).append("\"");
            if (this.jid != null) {
                stringBuilder.append(" jid=\"");
                stringBuilder.append(this.jid).append("\"");
            }
            if (this.node != null) {
                stringBuilder.append(" node=\"");
                stringBuilder.append(this.node).append("\"");
            }
            if (this.description != null && this.description.trim().length() > 0) {
                stringBuilder.append(" desc=\"");
                stringBuilder.append(this.description).append("\"");
            }
            if (this.delivered) {
                stringBuilder.append(" delivered=\"true\"");
            }
            if (this.uri != null) {
                stringBuilder.append(" uri=\"");
                stringBuilder.append(this.uri).append("\"");
            }
            stringBuilder.append("/>");
            return stringBuilder.toString();
        }

        public String getDescription() {
            return this.description;
        }

        public String getJid() {
            return this.jid;
        }

        public String getNode() {
            return this.node;
        }

        public String getType() {
            return this.type;
        }

        public String getUri() {
            return this.uri;
        }

        public boolean isDelivered() {
            return this.delivered;
        }
    }

    public void addAddress(String str, String str2, String str3, String str4, boolean z, String str5) {
        Address address = new Address(str);
        address.setJid(str2);
        address.setNode(str3);
        address.setDescription(str4);
        address.setDelivered(z);
        address.setUri(str5);
        this.addresses.add(address);
    }

    public List<Address> getAddressesOfType(String str) {
        List<Address> arrayList = new ArrayList(this.addresses.size());
        for (Address address : this.addresses) {
            if (address.getType().equals(str)) {
                arrayList.add(address);
            }
        }
        return arrayList;
    }

    public String getElementName() {
        return "addresses";
    }

    public String getNamespace() {
        return "http://jabber.org/protocol/address";
    }

    public void setNoReply() {
        this.addresses.add(new Address(NO_REPLY));
    }

    public String toXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<").append(getElementName());
        stringBuilder.append(" xmlns=\"").append(getNamespace()).append("\">");
        for (Address access$600 : this.addresses) {
            stringBuilder.append(access$600.toXML());
        }
        stringBuilder.append("</").append(getElementName()).append(">");
        return stringBuilder.toString();
    }
}
