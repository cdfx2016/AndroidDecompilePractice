package org.jivesoftware.smack;

import java.util.List;
import org.jivesoftware.smack.packet.PrivacyItem;

public class PrivacyList {
    private boolean isActiveList;
    private boolean isDefaultList;
    private List<PrivacyItem> items;
    private String listName;

    protected PrivacyList(boolean z, boolean z2, String str, List<PrivacyItem> list) {
        this.isActiveList = z;
        this.isDefaultList = z2;
        this.listName = str;
        this.items = list;
    }

    public List<PrivacyItem> getItems() {
        return this.items;
    }

    public boolean isActiveList() {
        return this.isActiveList;
    }

    public boolean isDefaultList() {
        return this.isDefaultList;
    }

    public String toString() {
        return this.listName;
    }
}
