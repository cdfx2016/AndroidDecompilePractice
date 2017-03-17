package org.jivesoftware.smackx.packet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.jivesoftware.smack.packet.PacketExtension;

public class DelayInformation implements PacketExtension {
    public static final DateFormat XEP_0091_UTC_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss");
    private String from;
    private String reason;
    private Date stamp;

    static {
        XEP_0091_UTC_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public DelayInformation(Date date) {
        this.stamp = date;
    }

    public String getElementName() {
        return "x";
    }

    public String getFrom() {
        return this.from;
    }

    public String getNamespace() {
        return "jabber:x:delay";
    }

    public String getReason() {
        return this.reason;
    }

    public Date getStamp() {
        return this.stamp;
    }

    public void setFrom(String str) {
        this.from = str;
    }

    public void setReason(String str) {
        this.reason = str;
    }

    public String toXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\"");
        stringBuilder.append(" stamp=\"");
        synchronized (XEP_0091_UTC_FORMAT) {
            stringBuilder.append(XEP_0091_UTC_FORMAT.format(this.stamp));
        }
        stringBuilder.append("\"");
        if (this.from != null && this.from.length() > 0) {
            stringBuilder.append(" from=\"").append(this.from).append("\"");
        }
        stringBuilder.append(">");
        if (this.reason != null && this.reason.length() > 0) {
            stringBuilder.append(this.reason);
        }
        stringBuilder.append("</").append(getElementName()).append(">");
        return stringBuilder.toString();
    }
}
