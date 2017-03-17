package org.jivesoftware.smackx.provider;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.packet.MUCOwner;
import org.jivesoftware.smackx.packet.MUCOwner.Destroy;
import org.jivesoftware.smackx.packet.MUCOwner.Item;
import org.jivesoftware.smackx.packet.Nick;
import org.xmlpull.v1.XmlPullParser;

public class MUCOwnerProvider implements IQProvider {
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

    private Item parseItem(XmlPullParser xmlPullParser) throws Exception {
        Object obj = null;
        Item item = new Item(xmlPullParser.getAttributeValue("", "affiliation"));
        item.setNick(xmlPullParser.getAttributeValue("", Nick.ELEMENT_NAME));
        item.setRole(xmlPullParser.getAttributeValue("", "role"));
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

    public IQ parseIQ(XmlPullParser xmlPullParser) throws Exception {
        IQ mUCOwner = new MUCOwner();
        Object obj = null;
        while (obj == null) {
            int next = xmlPullParser.next();
            if (next == 2) {
                if (xmlPullParser.getName().equals("item")) {
                    mUCOwner.addItem(parseItem(xmlPullParser));
                } else if (xmlPullParser.getName().equals("destroy")) {
                    mUCOwner.setDestroy(parseDestroy(xmlPullParser));
                } else {
                    mUCOwner.addExtension(PacketParserUtils.parsePacketExtension(xmlPullParser.getName(), xmlPullParser.getNamespace(), xmlPullParser));
                }
            } else if (next == 3 && xmlPullParser.getName().equals("query")) {
                obj = 1;
            }
        }
        return mUCOwner;
    }
}
