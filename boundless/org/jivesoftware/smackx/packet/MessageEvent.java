package org.jivesoftware.smackx.packet;

import java.util.ArrayList;
import java.util.Iterator;
import org.jivesoftware.smack.packet.PacketExtension;

public class MessageEvent implements PacketExtension {
    public static final String CANCELLED = "cancelled";
    public static final String COMPOSING = "composing";
    public static final String DELIVERED = "delivered";
    public static final String DISPLAYED = "displayed";
    public static final String OFFLINE = "offline";
    private boolean cancelled = true;
    private boolean composing = false;
    private boolean delivered = false;
    private boolean displayed = false;
    private boolean offline = false;
    private String packetID = null;

    public String getElementName() {
        return "x";
    }

    public Iterator<String> getEventTypes() {
        ArrayList arrayList = new ArrayList();
        if (isDelivered()) {
            arrayList.add(DELIVERED);
        }
        if (!isMessageEventRequest() && isCancelled()) {
            arrayList.add(CANCELLED);
        }
        if (isComposing()) {
            arrayList.add(COMPOSING);
        }
        if (isDisplayed()) {
            arrayList.add(DISPLAYED);
        }
        if (isOffline()) {
            arrayList.add(OFFLINE);
        }
        return arrayList.iterator();
    }

    public String getNamespace() {
        return "jabber:x:event";
    }

    public String getPacketID() {
        return this.packetID;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public boolean isComposing() {
        return this.composing;
    }

    public boolean isDelivered() {
        return this.delivered;
    }

    public boolean isDisplayed() {
        return this.displayed;
    }

    public boolean isMessageEventRequest() {
        return this.packetID == null;
    }

    public boolean isOffline() {
        return this.offline;
    }

    public void setCancelled(boolean z) {
        this.cancelled = z;
    }

    public void setComposing(boolean z) {
        this.composing = z;
        setCancelled(false);
    }

    public void setDelivered(boolean z) {
        this.delivered = z;
        setCancelled(false);
    }

    public void setDisplayed(boolean z) {
        this.displayed = z;
        setCancelled(false);
    }

    public void setOffline(boolean z) {
        this.offline = z;
        setCancelled(false);
    }

    public void setPacketID(String str) {
        this.packetID = str;
    }

    public String toXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\">");
        if (isOffline()) {
            stringBuilder.append("<").append(OFFLINE).append("/>");
        }
        if (isDelivered()) {
            stringBuilder.append("<").append(DELIVERED).append("/>");
        }
        if (isDisplayed()) {
            stringBuilder.append("<").append(DISPLAYED).append("/>");
        }
        if (isComposing()) {
            stringBuilder.append("<").append(COMPOSING).append("/>");
        }
        if (getPacketID() != null) {
            stringBuilder.append("<id>").append(getPacketID()).append("</id>");
        }
        stringBuilder.append("</").append(getElementName()).append(">");
        return stringBuilder.toString();
    }
}
