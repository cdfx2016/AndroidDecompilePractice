package org.jivesoftware.smackx.pubsub;

import org.jivesoftware.smackx.pubsub.util.XmlUtils;

public class UnsubscribeExtension extends NodeExtension {
    protected String id;
    protected String jid;

    public UnsubscribeExtension(String str) {
        this(str, null, null);
    }

    public UnsubscribeExtension(String str, String str2) {
        this(str, str2, null);
    }

    public UnsubscribeExtension(String str, String str2, String str3) {
        super(PubSubElementType.UNSUBSCRIBE, str2);
        this.jid = str;
        this.id = str3;
    }

    public String getId() {
        return this.id;
    }

    public String getJid() {
        return this.jid;
    }

    public String toXML() {
        StringBuilder stringBuilder = new StringBuilder("<");
        stringBuilder.append(getElementName());
        XmlUtils.appendAttribute(stringBuilder, "jid", this.jid);
        if (getNode() != null) {
            XmlUtils.appendAttribute(stringBuilder, "node", getNode());
        }
        if (this.id != null) {
            XmlUtils.appendAttribute(stringBuilder, "subid", this.id);
        }
        stringBuilder.append("/>");
        return stringBuilder.toString();
    }
}
