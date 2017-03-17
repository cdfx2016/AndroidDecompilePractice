package org.jivesoftware.smackx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class RemoteRosterEntry {
    private final List<String> groupNames = new ArrayList();
    private String name;
    private String user;

    public RemoteRosterEntry(String str, String str2, String[] strArr) {
        this.user = str;
        this.name = str2;
        if (strArr != null) {
            this.groupNames.addAll(Arrays.asList(strArr));
        }
    }

    public String[] getGroupArrayNames() {
        String[] strArr;
        synchronized (this.groupNames) {
            strArr = (String[]) Collections.unmodifiableList(this.groupNames).toArray(new String[this.groupNames.size()]);
        }
        return strArr;
    }

    public Iterator<String> getGroupNames() {
        Iterator<String> it;
        synchronized (this.groupNames) {
            it = Collections.unmodifiableList(this.groupNames).iterator();
        }
        return it;
    }

    public String getName() {
        return this.name;
    }

    public String getUser() {
        return this.user;
    }

    public String toXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<item jid=\"").append(this.user).append("\"");
        if (this.name != null) {
            stringBuilder.append(" name=\"").append(this.name).append("\"");
        }
        stringBuilder.append(">");
        synchronized (this.groupNames) {
            for (String append : this.groupNames) {
                stringBuilder.append("<group>").append(append).append("</group>");
            }
        }
        stringBuilder.append("</item>");
        return stringBuilder.toString();
    }
}
