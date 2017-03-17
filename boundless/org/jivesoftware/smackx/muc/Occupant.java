package org.jivesoftware.smackx.muc;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.MUCAdmin;
import org.jivesoftware.smackx.packet.MUCUser;
import org.jivesoftware.smackx.packet.MUCUser.Item;

public class Occupant {
    private String affiliation;
    private String jid;
    private String nick;
    private String role;

    Occupant(Presence presence) {
        Item item = ((MUCUser) presence.getExtension("x", "http://jabber.org/protocol/muc#user")).getItem();
        this.jid = item.getJid();
        this.affiliation = item.getAffiliation();
        this.role = item.getRole();
        this.nick = StringUtils.parseResource(presence.getFrom());
    }

    Occupant(MUCAdmin.Item item) {
        this.jid = item.getJid();
        this.affiliation = item.getAffiliation();
        this.role = item.getRole();
        this.nick = item.getNick();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Occupant)) {
            return false;
        }
        return this.jid.equals(((Occupant) obj).jid);
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

    public String getRole() {
        return this.role;
    }

    public int hashCode() {
        return (this.nick != null ? this.nick.hashCode() : 0) + (((((this.affiliation.hashCode() * 17) + this.role.hashCode()) * 17) + this.jid.hashCode()) * 17);
    }
}
