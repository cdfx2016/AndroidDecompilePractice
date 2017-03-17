package org.jivesoftware.smackx.packet;

import org.jivesoftware.smack.packet.PacketExtension;

public class MUCUser implements PacketExtension {
    private Decline decline;
    private Destroy destroy;
    private Invite invite;
    private Item item;
    private String password;
    private Status status;

    public static class Decline {
        private String from;
        private String reason;
        private String to;

        public String getFrom() {
            return this.from;
        }

        public String getReason() {
            return this.reason;
        }

        public String getTo() {
            return this.to;
        }

        public void setFrom(String str) {
            this.from = str;
        }

        public void setReason(String str) {
            this.reason = str;
        }

        public void setTo(String str) {
            this.to = str;
        }

        public String toXML() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<decline ");
            if (getTo() != null) {
                stringBuilder.append(" to=\"").append(getTo()).append("\"");
            }
            if (getFrom() != null) {
                stringBuilder.append(" from=\"").append(getFrom()).append("\"");
            }
            stringBuilder.append(">");
            if (getReason() != null) {
                stringBuilder.append("<reason>").append(getReason()).append("</reason>");
            }
            stringBuilder.append("</decline>");
            return stringBuilder.toString();
        }
    }

    public static class Destroy {
        private String jid;
        private String reason;

        public String getJid() {
            return this.jid;
        }

        public String getReason() {
            return this.reason;
        }

        public void setJid(String str) {
            this.jid = str;
        }

        public void setReason(String str) {
            this.reason = str;
        }

        public String toXML() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<destroy");
            if (getJid() != null) {
                stringBuilder.append(" jid=\"").append(getJid()).append("\"");
            }
            if (getReason() == null) {
                stringBuilder.append("/>");
            } else {
                stringBuilder.append(">");
                if (getReason() != null) {
                    stringBuilder.append("<reason>").append(getReason()).append("</reason>");
                }
                stringBuilder.append("</destroy>");
            }
            return stringBuilder.toString();
        }
    }

    public static class Invite {
        private String from;
        private String reason;
        private String to;

        public String getFrom() {
            return this.from;
        }

        public String getReason() {
            return this.reason;
        }

        public String getTo() {
            return this.to;
        }

        public void setFrom(String str) {
            this.from = str;
        }

        public void setReason(String str) {
            this.reason = str;
        }

        public void setTo(String str) {
            this.to = str;
        }

        public String toXML() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<invite ");
            if (getTo() != null) {
                stringBuilder.append(" to=\"").append(getTo()).append("\"");
            }
            if (getFrom() != null) {
                stringBuilder.append(" from=\"").append(getFrom()).append("\"");
            }
            stringBuilder.append(">");
            if (getReason() != null) {
                stringBuilder.append("<reason>").append(getReason()).append("</reason>");
            }
            stringBuilder.append("</invite>");
            return stringBuilder.toString();
        }
    }

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
            return this.actor == null ? "" : this.actor;
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
            return this.reason == null ? "" : this.reason;
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

    public static class Status {
        private String code;

        public Status(String str) {
            this.code = str;
        }

        public String getCode() {
            return this.code;
        }

        public String toXML() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<status code=\"").append(getCode()).append("\"/>");
            return stringBuilder.toString();
        }
    }

    public Decline getDecline() {
        return this.decline;
    }

    public Destroy getDestroy() {
        return this.destroy;
    }

    public String getElementName() {
        return "x";
    }

    public Invite getInvite() {
        return this.invite;
    }

    public Item getItem() {
        return this.item;
    }

    public String getNamespace() {
        return "http://jabber.org/protocol/muc#user";
    }

    public String getPassword() {
        return this.password;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setDecline(Decline decline) {
        this.decline = decline;
    }

    public void setDestroy(Destroy destroy) {
        this.destroy = destroy;
    }

    public void setInvite(Invite invite) {
        this.invite = invite;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setPassword(String str) {
        this.password = str;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String toXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\">");
        if (getInvite() != null) {
            stringBuilder.append(getInvite().toXML());
        }
        if (getDecline() != null) {
            stringBuilder.append(getDecline().toXML());
        }
        if (getItem() != null) {
            stringBuilder.append(getItem().toXML());
        }
        if (getPassword() != null) {
            stringBuilder.append("<password>").append(getPassword()).append("</password>");
        }
        if (getStatus() != null) {
            stringBuilder.append(getStatus().toXML());
        }
        if (getDestroy() != null) {
            stringBuilder.append(getDestroy().toXML());
        }
        stringBuilder.append("</").append(getElementName()).append(">");
        return stringBuilder.toString();
    }
}
