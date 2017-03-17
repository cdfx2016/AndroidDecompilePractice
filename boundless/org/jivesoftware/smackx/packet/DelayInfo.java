package org.jivesoftware.smackx.packet;

import java.util.Date;
import org.jivesoftware.smack.util.StringUtils;

public class DelayInfo extends DelayInformation {
    DelayInformation wrappedInfo;

    public DelayInfo(DelayInformation delayInformation) {
        super(delayInformation.getStamp());
        this.wrappedInfo = delayInformation;
    }

    public String getElementName() {
        return "delay";
    }

    public String getFrom() {
        return this.wrappedInfo.getFrom();
    }

    public String getNamespace() {
        return "urn:xmpp:delay";
    }

    public String getReason() {
        return this.wrappedInfo.getReason();
    }

    public Date getStamp() {
        return this.wrappedInfo.getStamp();
    }

    public void setFrom(String str) {
        this.wrappedInfo.setFrom(str);
    }

    public void setReason(String str) {
        this.wrappedInfo.setReason(str);
    }

    public String toXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\"");
        stringBuilder.append(" stamp=\"");
        stringBuilder.append(StringUtils.formatXEP0082Date(getStamp()));
        stringBuilder.append("\"");
        if (getFrom() != null && getFrom().length() > 0) {
            stringBuilder.append(" from=\"").append(getFrom()).append("\"");
        }
        stringBuilder.append(">");
        if (getReason() != null && getReason().length() > 0) {
            stringBuilder.append(getReason());
        }
        stringBuilder.append("</").append(getElementName()).append(">");
        return stringBuilder.toString();
    }
}
