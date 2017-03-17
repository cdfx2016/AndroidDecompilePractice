package com.easemob.chat;

import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EMGroup extends EMContact {
    protected boolean allowInvites = false;
    protected String description = "";
    protected boolean isPublic = false;
    protected long lastModifiedTime = 0;
    protected ArrayList<String> members = new ArrayList();
    protected boolean membersOnly = false;
    protected String owner = "";

    public EMGroup(String str) {
        this.username = str;
        this.eid = EMContactManager.getEidFromGroupId(str);
    }

    public synchronized void addMember(String str) {
        this.members.add(str);
    }

    void copyGroup(EMGroup eMGroup) {
        this.eid = eMGroup.eid;
        this.description = eMGroup.description;
        this.isPublic = eMGroup.isPublic;
        this.allowInvites = eMGroup.allowInvites;
        this.membersOnly = eMGroup.membersOnly;
        this.lastModifiedTime = System.currentTimeMillis();
        this.members.clear();
        this.members.addAll(eMGroup.getMembers());
        this.nick = eMGroup.nick;
        this.owner = eMGroup.owner;
        this.username = eMGroup.username;
    }

    public String getDescription() {
        return this.description;
    }

    Bitmap getGroupAvator() {
        new Exception("group avator not supported yet").printStackTrace();
        return null;
    }

    public String getGroupId() {
        return this.username;
    }

    public String getGroupName() {
        return this.nick;
    }

    public boolean getIsPublic() {
        return this.isPublic;
    }

    public synchronized List<String> getMembers() {
        return Collections.unmodifiableList(this.members);
    }

    public String getOwner() {
        return this.owner;
    }

    public boolean isAllowInvites() {
        return this.allowInvites;
    }

    public boolean isMembersOnly() {
        return this.membersOnly;
    }

    public synchronized void removeMember(String str) {
        this.members.remove(str);
    }

    public void setDescription(String str) {
        this.description = str;
    }

    public void setGroupId(String str) {
        this.username = str;
    }

    public void setGroupName(String str) {
        this.nick = str;
    }

    public void setIsPublic(boolean z) {
        this.isPublic = z;
    }

    public synchronized void setMembers(List<String> list) {
        this.members.addAll(list);
    }

    public void setOwner(String str) {
        this.owner = str;
    }

    public String toString() {
        return this.nick;
    }
}
