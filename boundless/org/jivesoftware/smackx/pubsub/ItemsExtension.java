package org.jivesoftware.smackx.pubsub;

import java.util.List;
import org.jivesoftware.smack.packet.PacketExtension;

public class ItemsExtension extends NodeExtension implements EmbeddedPacketExtension {
    protected List<? extends PacketExtension> items;
    protected Boolean notify;
    protected ItemsElementType type;

    public enum ItemsElementType {
        items(PubSubElementType.ITEMS, "max_items"),
        retract(PubSubElementType.RETRACT, "notify");
        
        private String att;
        private PubSubElementType elem;

        private ItemsElementType(PubSubElementType pubSubElementType, String str) {
            this.elem = pubSubElementType;
            this.att = str;
        }

        public String getElementAttribute() {
            return this.att;
        }

        public PubSubElementType getNodeElement() {
            return this.elem;
        }
    }

    public ItemsExtension(String str, List<? extends PacketExtension> list, boolean z) {
        super(ItemsElementType.retract.getNodeElement(), str);
        this.type = ItemsElementType.retract;
        this.items = list;
        this.notify = Boolean.valueOf(z);
    }

    public ItemsExtension(ItemsElementType itemsElementType, String str, List<? extends PacketExtension> list) {
        super(itemsElementType.getNodeElement(), str);
        this.type = itemsElementType;
        this.items = list;
    }

    public List<PacketExtension> getExtensions() {
        return getItems();
    }

    public List<? extends PacketExtension> getItems() {
        return this.items;
    }

    public ItemsElementType getItemsElementType() {
        return this.type;
    }

    public boolean getNotify() {
        return this.notify.booleanValue();
    }

    public String toString() {
        return getClass().getName() + "Content [" + toXML() + "]";
    }

    public String toXML() {
        if (this.items == null || this.items.size() == 0) {
            return super.toXML();
        }
        StringBuilder stringBuilder = new StringBuilder("<");
        stringBuilder.append(getElementName());
        stringBuilder.append(" node='");
        stringBuilder.append(getNode());
        if (this.notify != null) {
            stringBuilder.append("' ");
            stringBuilder.append(this.type.getElementAttribute());
            stringBuilder.append("='");
            stringBuilder.append(this.notify.equals(Boolean.TRUE) ? 1 : 0);
            stringBuilder.append("'>");
        } else {
            stringBuilder.append("'>");
            for (PacketExtension toXML : this.items) {
                stringBuilder.append(toXML.toXML());
            }
        }
        stringBuilder.append("</");
        stringBuilder.append(getElementName());
        stringBuilder.append(">");
        return stringBuilder.toString();
    }
}
