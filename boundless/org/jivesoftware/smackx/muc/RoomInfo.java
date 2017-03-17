package org.jivesoftware.smackx.muc;

import java.util.Iterator;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.jivesoftware.smackx.packet.DiscoverInfo.Identity;

public class RoomInfo {
    private boolean allowInvites;
    private String description = "";
    private boolean membersOnly;
    private boolean moderated;
    private boolean nonanonymous;
    private int occupantsCount = -1;
    private boolean passwordProtected;
    private boolean persistent;
    private boolean publicRoom;
    private String room;
    private String roomName = null;
    private String subject = "";

    RoomInfo(DiscoverInfo discoverInfo) {
        this.room = discoverInfo.getFrom();
        this.membersOnly = discoverInfo.containsFeature("muc_membersonly");
        this.moderated = discoverInfo.containsFeature("muc_moderated");
        this.nonanonymous = discoverInfo.containsFeature("muc_nonanonymous");
        this.passwordProtected = discoverInfo.containsFeature("muc_passwordprotected");
        this.persistent = discoverInfo.containsFeature("muc_persistent");
        this.publicRoom = discoverInfo.containsFeature("muc_public");
        this.allowInvites = discoverInfo.containsFeature("muc_allowinvites");
        Form formFrom = Form.getFormFrom(discoverInfo);
        if (formFrom != null) {
            FormField field = formFrom.getField("muc#roominfo_description");
            String str = (field == null || !field.getValues().hasNext()) ? "" : (String) field.getValues().next();
            this.description = str;
            field = formFrom.getField("muc#roominfo_subject");
            str = (field == null || !field.getValues().hasNext()) ? "" : (String) field.getValues().next();
            this.subject = str;
            field = formFrom.getField("muc#roominfo_occupants");
            this.occupantsCount = field == null ? -1 : Integer.parseInt((String) field.getValues().next());
        }
        Iterator identities = discoverInfo.getIdentities();
        if (identities.hasNext()) {
            this.roomName = ((Identity) identities.next()).getName();
        }
    }

    public String getDescription() {
        return this.description;
    }

    public int getOccupantsCount() {
        return this.occupantsCount;
    }

    public String getRoom() {
        return this.room;
    }

    public String getRoomName() {
        return this.roomName;
    }

    public String getSubject() {
        return this.subject;
    }

    public boolean isAllowInvites() {
        return this.allowInvites;
    }

    public boolean isMembersOnly() {
        return this.membersOnly;
    }

    public boolean isModerated() {
        return this.moderated;
    }

    public boolean isNonanonymous() {
        return this.nonanonymous;
    }

    public boolean isPasswordProtected() {
        return this.passwordProtected;
    }

    public boolean isPersistent() {
        return this.persistent;
    }

    public boolean isPublic() {
        return this.publicRoom;
    }
}
