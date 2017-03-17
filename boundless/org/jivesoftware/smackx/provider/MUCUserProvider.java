package org.jivesoftware.smackx.provider;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.jivesoftware.smackx.packet.MUCUser;
import org.jivesoftware.smackx.packet.MUCUser.Decline;
import org.jivesoftware.smackx.packet.MUCUser.Destroy;
import org.jivesoftware.smackx.packet.MUCUser.Invite;
import org.jivesoftware.smackx.packet.MUCUser.Item;
import org.jivesoftware.smackx.packet.MUCUser.Status;
import org.jivesoftware.smackx.packet.Nick;
import org.xmlpull.v1.XmlPullParser;

public class MUCUserProvider implements PacketExtensionProvider {
    private Decline parseDecline(XmlPullParser xmlPullParser) throws Exception {
        Object obj = null;
        Decline decline = new Decline();
        decline.setFrom(xmlPullParser.getAttributeValue("", "from"));
        decline.setTo(xmlPullParser.getAttributeValue("", "to"));
        while (obj == null) {
            int next = xmlPullParser.next();
            if (next == 2) {
                if (xmlPullParser.getName().equals("reason")) {
                    decline.setReason(xmlPullParser.nextText());
                }
            } else if (next == 3 && xmlPullParser.getName().equals("decline")) {
                obj = 1;
            }
        }
        return decline;
    }

    private Destroy parseDestroy(XmlPullParser xmlPullParser) throws Exception {
        Object obj = null;
        Destroy destroy = new Destroy();
        destroy.setJid(xmlPullParser.getAttributeValue("", "jid"));
        while (obj == null) {
            int next = xmlPullParser.next();
            if (next == 2) {
                if (xmlPullParser.getName().equals("reason")) {
                    destroy.setReason(xmlPullParser.nextText());
                }
            } else if (next == 3 && xmlPullParser.getName().equals("destroy")) {
                obj = 1;
            }
        }
        return destroy;
    }

    private Invite parseInvite(XmlPullParser xmlPullParser) throws Exception {
        Object obj = null;
        Invite invite = new Invite();
        invite.setFrom(xmlPullParser.getAttributeValue("", "from"));
        invite.setTo(xmlPullParser.getAttributeValue("", "to"));
        while (obj == null) {
            int next = xmlPullParser.next();
            if (next == 2) {
                if (xmlPullParser.getName().equals("reason")) {
                    invite.setReason(xmlPullParser.nextText());
                }
            } else if (next == 3 && xmlPullParser.getName().equals("invite")) {
                obj = 1;
            }
        }
        return invite;
    }

    private Item parseItem(XmlPullParser xmlPullParser) throws Exception {
        Object obj = null;
        Item item = new Item(xmlPullParser.getAttributeValue("", "affiliation"), xmlPullParser.getAttributeValue("", "role"));
        item.setNick(xmlPullParser.getAttributeValue("", Nick.ELEMENT_NAME));
        item.setJid(xmlPullParser.getAttributeValue("", "jid"));
        while (obj == null) {
            int next = xmlPullParser.next();
            if (next == 2) {
                if (xmlPullParser.getName().equals("actor")) {
                    item.setActor(xmlPullParser.getAttributeValue("", "jid"));
                }
                if (xmlPullParser.getName().equals("reason")) {
                    item.setReason(xmlPullParser.nextText());
                }
            } else if (next == 3 && xmlPullParser.getName().equals("item")) {
                obj = 1;
            }
        }
        return item;
    }

    public PacketExtension parseExtension(XmlPullParser xmlPullParser) throws Exception {
        PacketExtension mUCUser = new MUCUser();
        Object obj = null;
        while (obj == null) {
            int next = xmlPullParser.next();
            if (next == 2) {
                if (xmlPullParser.getName().equals("invite")) {
                    mUCUser.setInvite(parseInvite(xmlPullParser));
                }
                if (xmlPullParser.getName().equals("item")) {
                    mUCUser.setItem(parseItem(xmlPullParser));
                }
                if (xmlPullParser.getName().equals("password")) {
                    mUCUser.setPassword(xmlPullParser.nextText());
                }
                if (xmlPullParser.getName().equals("status")) {
                    mUCUser.setStatus(new Status(xmlPullParser.getAttributeValue("", "code")));
                }
                if (xmlPullParser.getName().equals("decline")) {
                    mUCUser.setDecline(parseDecline(xmlPullParser));
                }
                if (xmlPullParser.getName().equals("destroy")) {
                    mUCUser.setDestroy(parseDestroy(xmlPullParser));
                }
            } else if (next == 3 && xmlPullParser.getName().equals("x")) {
                obj = 1;
            }
        }
        return mUCUser;
    }
}
