package org.jivesoftware.smackx.packet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

public class OfflineMessageRequest extends IQ {
    private boolean fetch = false;
    private List<Item> items = new ArrayList();
    private boolean purge = false;

    public static class Item {
        private String action;
        private String jid;
        private String node;

        public Item(String str) {
            this.node = str;
        }

        public String getAction() {
            return this.action;
        }

        public String getJid() {
            return this.jid;
        }

        public String getNode() {
            return this.node;
        }

        public void setAction(String str) {
            this.action = str;
        }

        public void setJid(String str) {
            this.jid = str;
        }

        public String toXML() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<item");
            if (getAction() != null) {
                stringBuilder.append(" action=\"").append(getAction()).append("\"");
            }
            if (getJid() != null) {
                stringBuilder.append(" jid=\"").append(getJid()).append("\"");
            }
            if (getNode() != null) {
                stringBuilder.append(" node=\"").append(getNode()).append("\"");
            }
            stringBuilder.append("/>");
            return stringBuilder.toString();
        }
    }

    public static class Provider implements IQProvider {
        private Item parseItem(XmlPullParser xmlPullParser) throws Exception {
            Object obj = null;
            Item item = new Item(xmlPullParser.getAttributeValue("", "node"));
            item.setAction(xmlPullParser.getAttributeValue("", MessageEncoder.ATTR_ACTION));
            item.setJid(xmlPullParser.getAttributeValue("", "jid"));
            while (obj == null) {
                if (xmlPullParser.next() == 3 && xmlPullParser.getName().equals("item")) {
                    obj = 1;
                }
            }
            return item;
        }

        public IQ parseIQ(XmlPullParser xmlPullParser) throws Exception {
            IQ offlineMessageRequest = new OfflineMessageRequest();
            boolean z = false;
            while (!z) {
                int next = xmlPullParser.next();
                if (next == 2) {
                    if (xmlPullParser.getName().equals("item")) {
                        offlineMessageRequest.addItem(parseItem(xmlPullParser));
                    } else if (xmlPullParser.getName().equals("purge")) {
                        offlineMessageRequest.setPurge(true);
                    } else if (xmlPullParser.getName().equals("fetch")) {
                        offlineMessageRequest.setFetch(true);
                    }
                } else if (next == 3 && xmlPullParser.getName().equals(MessageEvent.OFFLINE)) {
                    z = true;
                }
            }
            return offlineMessageRequest;
        }
    }

    public void addItem(Item item) {
        synchronized (this.items) {
            this.items.add(item);
        }
    }

    public String getChildElementXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<offline xmlns=\"http://jabber.org/protocol/offline\">");
        synchronized (this.items) {
            for (int i = 0; i < this.items.size(); i++) {
                stringBuilder.append(((Item) this.items.get(i)).toXML());
            }
        }
        if (this.purge) {
            stringBuilder.append("<purge/>");
        }
        if (this.fetch) {
            stringBuilder.append("<fetch/>");
        }
        stringBuilder.append(getExtensionsXML());
        stringBuilder.append("</offline>");
        return stringBuilder.toString();
    }

    public Iterator<Item> getItems() {
        Iterator<Item> it;
        synchronized (this.items) {
            it = Collections.unmodifiableList(new ArrayList(this.items)).iterator();
        }
        return it;
    }

    public boolean isFetch() {
        return this.fetch;
    }

    public boolean isPurge() {
        return this.purge;
    }

    public void setFetch(boolean z) {
        this.fetch = z;
    }

    public void setPurge(boolean z) {
        this.purge = z;
    }
}
