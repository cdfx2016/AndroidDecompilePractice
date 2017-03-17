package org.jivesoftware.smack.packet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Privacy extends IQ {
    private String activeName;
    private boolean declineActiveList = false;
    private boolean declineDefaultList = false;
    private String defaultName;
    private Map<String, List<PrivacyItem>> itemLists = new HashMap();

    public boolean changeDefaultList(String str) {
        if (!getItemLists().containsKey(str)) {
            return false;
        }
        setDefaultName(str);
        return true;
    }

    public void deleteList(String str) {
        getItemLists().remove(str);
    }

    public void deletePrivacyList(String str) {
        getItemLists().remove(str);
        if (getDefaultName() != null && str.equals(getDefaultName())) {
            setDefaultName(null);
        }
    }

    public String getActiveName() {
        return this.activeName;
    }

    public List<PrivacyItem> getActivePrivacyList() {
        return getActiveName() == null ? null : (List) getItemLists().get(getActiveName());
    }

    public String getChildElementXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<query xmlns=\"jabber:iq:privacy\">");
        if (isDeclineActiveList()) {
            stringBuilder.append("<active/>");
        } else if (getActiveName() != null) {
            stringBuilder.append("<active name=\"").append(getActiveName()).append("\"/>");
        }
        if (isDeclineDefaultList()) {
            stringBuilder.append("<default/>");
        } else if (getDefaultName() != null) {
            stringBuilder.append("<default name=\"").append(getDefaultName()).append("\"/>");
        }
        for (Entry entry : getItemLists().entrySet()) {
            String str = (String) entry.getKey();
            List<PrivacyItem> list = (List) entry.getValue();
            if (list.isEmpty()) {
                stringBuilder.append("<list name=\"").append(str).append("\"/>");
            } else {
                stringBuilder.append("<list name=\"").append(str).append("\">");
            }
            for (PrivacyItem toXML : list) {
                stringBuilder.append(toXML.toXML());
            }
            if (!list.isEmpty()) {
                stringBuilder.append("</list>");
            }
        }
        stringBuilder.append(getExtensionsXML());
        stringBuilder.append("</query>");
        return stringBuilder.toString();
    }

    public String getDefaultName() {
        return this.defaultName;
    }

    public List<PrivacyItem> getDefaultPrivacyList() {
        return getDefaultName() == null ? null : (List) getItemLists().get(getDefaultName());
    }

    public PrivacyItem getItem(String str, int i) {
        Iterator it = getPrivacyList(str).iterator();
        PrivacyItem privacyItem = null;
        while (privacyItem == null && it.hasNext()) {
            PrivacyItem privacyItem2 = (PrivacyItem) it.next();
            if (privacyItem2.getOrder() != i) {
                privacyItem2 = privacyItem;
            }
            privacyItem = privacyItem2;
        }
        return privacyItem;
    }

    public Map<String, List<PrivacyItem>> getItemLists() {
        return this.itemLists;
    }

    public List<PrivacyItem> getPrivacyList(String str) {
        return (List) getItemLists().get(str);
    }

    public Set<String> getPrivacyListNames() {
        return this.itemLists.keySet();
    }

    public boolean isDeclineActiveList() {
        return this.declineActiveList;
    }

    public boolean isDeclineDefaultList() {
        return this.declineDefaultList;
    }

    public void setActiveName(String str) {
        this.activeName = str;
    }

    public List<PrivacyItem> setActivePrivacyList() {
        setActiveName(getDefaultName());
        return (List) getItemLists().get(getActiveName());
    }

    public void setDeclineActiveList(boolean z) {
        this.declineActiveList = z;
    }

    public void setDeclineDefaultList(boolean z) {
        this.declineDefaultList = z;
    }

    public void setDefaultName(String str) {
        this.defaultName = str;
    }

    public List<PrivacyItem> setPrivacyList(String str, List<PrivacyItem> list) {
        getItemLists().put(str, list);
        return list;
    }
}
