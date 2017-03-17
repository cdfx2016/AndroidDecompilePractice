package com.easemob.chat;

import java.io.Serializable;

public class EMGroupInfo implements Serializable {
    private static final long serialVersionUID = -2004486389398310700L;
    private String groupId;
    private String groupName;

    public EMGroupInfo(String str, String str2) {
        this.groupId = str;
        this.groupName = str2;
    }

    public String getGroupId() {
        return this.groupId;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public void setGroupId(String str) {
        this.groupId = str;
    }

    public void setGroupName(String str) {
        this.groupName = str;
    }

    public String toString() {
        return this.groupName;
    }
}
