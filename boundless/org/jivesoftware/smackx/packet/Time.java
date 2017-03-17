package org.jivesoftware.smackx.packet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.jivesoftware.smack.packet.IQ;

public class Time extends IQ {
    private static DateFormat displayFormat = DateFormat.getDateTimeInstance();
    private static SimpleDateFormat utcFormat = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss");
    private String display = null;
    private String tz = null;
    private String utc = null;

    public Time(Calendar calendar) {
        TimeZone timeZone = calendar.getTimeZone();
        this.tz = calendar.getTimeZone().getID();
        this.display = displayFormat.format(calendar.getTime());
        this.utc = utcFormat.format(new Date(calendar.getTimeInMillis() - ((long) timeZone.getOffset(calendar.getTimeInMillis()))));
    }

    public String getChildElementXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<query xmlns=\"jabber:iq:time\">");
        if (this.utc != null) {
            stringBuilder.append("<utc>").append(this.utc).append("</utc>");
        }
        if (this.tz != null) {
            stringBuilder.append("<tz>").append(this.tz).append("</tz>");
        }
        if (this.display != null) {
            stringBuilder.append("<display>").append(this.display).append("</display>");
        }
        stringBuilder.append("</query>");
        return stringBuilder.toString();
    }

    public String getDisplay() {
        return this.display;
    }

    public Date getTime() {
        Date date = null;
        if (this.utc != null) {
            try {
                Calendar instance = Calendar.getInstance();
                instance.setTime(new Date(utcFormat.parse(this.utc).getTime() + ((long) instance.getTimeZone().getOffset(instance.getTimeInMillis()))));
                date = instance.getTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return date;
    }

    public String getTz() {
        return this.tz;
    }

    public String getUtc() {
        return this.utc;
    }

    public void setDisplay(String str) {
        this.display = str;
    }

    public void setTime(Date date) {
        this.utc = utcFormat.format(new Date(date.getTime() - ((long) TimeZone.getDefault().getOffset(date.getTime()))));
    }

    public void setTz(String str) {
        this.tz = str;
    }

    public void setUtc(String str) {
        this.utc = str;
    }
}
