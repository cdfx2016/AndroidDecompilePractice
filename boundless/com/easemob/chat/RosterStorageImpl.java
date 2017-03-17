package com.easemob.chat;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import com.easemob.util.EMLog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jivesoftware.smack.RosterStorage;
import org.jivesoftware.smack.packet.RosterPacket.Item;

class RosterStorageImpl implements RosterStorage {
    private static final String PERF_KEY_ROSTERVER = "easemob.roster.ver.";
    private static final String TAG = "rosterstorage";
    private Context appContext;
    private EMContactManager contactManager;
    private ArrayList<Item> rosterItems = new ArrayList();
    private String version = null;

    public RosterStorageImpl(Context context, EMContactManager eMContactManager) {
        this.appContext = context;
        this.contactManager = eMContactManager;
        loadEntries();
    }

    private void loadEntries() {
        for (EMContact eMContact : this.contactManager.contactTable.values()) {
            this.rosterItems.add(new Item(eMContact.eid, eMContact.username));
        }
    }

    private void updateRosterVersion(String str) {
        this.version = str;
        Editor edit = PreferenceManager.getDefaultSharedPreferences(this.appContext).edit();
        edit.putString(new StringBuilder(PERF_KEY_ROSTERVER).append(EMSessionManager.getInstance(null).currentUser.eid).toString(), str);
        edit.commit();
        EMLog.d(TAG, "updated roster version to:" + str);
        System.err.println("!!! update roster version to:" + str);
    }

    public void addEntry(Item item, String str) {
        if (str != null && !str.equals("") && !str.equals(this.version)) {
            System.err.println("roster storage addEntry ver:" + str + " item");
            updateRosterVersion(str);
        }
    }

    public List<Item> getEntries() {
        return this.rosterItems;
    }

    public Item getEntry(String str) {
        Iterator it = this.rosterItems.iterator();
        while (it.hasNext()) {
            Item item = (Item) it.next();
            if (item.getName().equals(str)) {
                return item;
            }
        }
        EMLog.e(TAG, "cant find roster entry for jid:" + str);
        return null;
    }

    public int getEntryCount() {
        EMLog.d(TAG, "get entry count return:" + this.rosterItems.size());
        return this.rosterItems.size();
    }

    public String getRosterVersion() {
        if (this.version == null) {
            this.version = PreferenceManager.getDefaultSharedPreferences(this.appContext).getString(new StringBuilder(PERF_KEY_ROSTERVER).append(EMSessionManager.getInstance(null).currentUser.eid).toString(), "");
            EMLog.d(TAG, "load roster storage for jid" + EMSessionManager.getInstance(null).currentUser.eid + " version:" + this.version);
        }
        System.err.println("roster storage get roster version:" + this.version);
        return this.version;
    }

    public void removeEntry(String str) {
        System.err.println("[skip]roster storage removeEntry:" + str);
    }

    public void updateLocalEntry(Item item) {
        System.err.println("[skip]roster storage uplodateLocalEntry:" + item);
    }
}
