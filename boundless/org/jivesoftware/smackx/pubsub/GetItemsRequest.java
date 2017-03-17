package org.jivesoftware.smackx.pubsub;

public class GetItemsRequest extends NodeExtension {
    protected int maxItems;
    protected String subId;

    public GetItemsRequest(String str) {
        super(PubSubElementType.ITEMS, str);
    }

    public GetItemsRequest(String str, int i) {
        super(PubSubElementType.ITEMS, str);
        this.maxItems = i;
    }

    public GetItemsRequest(String str, String str2) {
        super(PubSubElementType.ITEMS, str);
        this.subId = str2;
    }

    public GetItemsRequest(String str, String str2, int i) {
        this(str, i);
        this.subId = str2;
    }

    public int getMaxItems() {
        return this.maxItems;
    }

    public String getSubscriptionId() {
        return this.subId;
    }

    public String toXML() {
        StringBuilder stringBuilder = new StringBuilder("<");
        stringBuilder.append(getElementName());
        stringBuilder.append(" node='");
        stringBuilder.append(getNode());
        stringBuilder.append("'");
        if (getSubscriptionId() != null) {
            stringBuilder.append(" subid='");
            stringBuilder.append(getSubscriptionId());
            stringBuilder.append("'");
        }
        if (getMaxItems() > 0) {
            stringBuilder.append(" max_items='");
            stringBuilder.append(getMaxItems());
            stringBuilder.append("'");
        }
        stringBuilder.append("/>");
        return stringBuilder.toString();
    }
}
