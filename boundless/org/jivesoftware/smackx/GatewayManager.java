package org.jivesoftware.smackx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.jivesoftware.smackx.packet.DiscoverInfo.Identity;
import org.jivesoftware.smackx.packet.DiscoverItems.Item;

public class GatewayManager {
    private static Map<Connection, GatewayManager> instances = new HashMap();
    private Connection connection;
    private Map<String, Gateway> gateways = new HashMap();
    private Map<String, Gateway> localGateways = new HashMap();
    private Map<String, Gateway> nonLocalGateways = new HashMap();
    private Roster roster;
    private ServiceDiscoveryManager sdManager;

    private GatewayManager() {
    }

    private GatewayManager(Connection connection) throws XMPPException {
        this.connection = connection;
        this.roster = connection.getRoster();
        this.sdManager = ServiceDiscoveryManager.getInstanceFor(connection);
    }

    private void discoverGateway(String str) throws XMPPException {
        DiscoverInfo discoverInfo = this.sdManager.discoverInfo(str);
        Iterator identities = discoverInfo.getIdentities();
        while (identities.hasNext()) {
            Identity identity = (Identity) identities.next();
            if (identity.getCategory().toLowerCase().equals("gateway")) {
                this.gateways.put(str, new Gateway(this.connection, str));
                if (str.contains(this.connection.getHost())) {
                    this.localGateways.put(str, new Gateway(this.connection, str, discoverInfo, identity));
                    return;
                } else {
                    this.nonLocalGateways.put(str, new Gateway(this.connection, str, discoverInfo, identity));
                    return;
                }
            }
        }
    }

    private void loadLocalGateways() throws XMPPException {
        Iterator items = this.sdManager.discoverItems(this.connection.getHost()).getItems();
        while (items.hasNext()) {
            discoverGateway(((Item) items.next()).getEntityID());
        }
    }

    private void loadNonLocalGateways() throws XMPPException {
        if (this.roster != null) {
            for (RosterEntry rosterEntry : this.roster.getEntries()) {
                if (rosterEntry.getUser().equalsIgnoreCase(StringUtils.parseServer(rosterEntry.getUser())) && !rosterEntry.getUser().contains(this.connection.getHost())) {
                    discoverGateway(rosterEntry.getUser());
                }
            }
        }
    }

    public Gateway getGateway(String str) {
        if (this.localGateways.containsKey(str)) {
            return (Gateway) this.localGateways.get(str);
        }
        if (this.nonLocalGateways.containsKey(str)) {
            return (Gateway) this.nonLocalGateways.get(str);
        }
        if (this.gateways.containsKey(str)) {
            return (Gateway) this.gateways.get(str);
        }
        Gateway gateway = new Gateway(this.connection, str);
        if (str.contains(this.connection.getHost())) {
            this.localGateways.put(str, gateway);
        } else {
            this.nonLocalGateways.put(str, gateway);
        }
        this.gateways.put(str, gateway);
        return gateway;
    }

    public GatewayManager getInstanceFor(Connection connection) throws XMPPException {
        GatewayManager gatewayManager;
        synchronized (instances) {
            if (instances.containsKey(connection)) {
                gatewayManager = (GatewayManager) instances.get(connection);
            } else {
                gatewayManager = new GatewayManager(connection);
                instances.put(connection, gatewayManager);
            }
        }
        return gatewayManager;
    }

    public List<Gateway> getLocalGateways() throws XMPPException {
        if (this.localGateways.size() == 0) {
            loadLocalGateways();
        }
        return new ArrayList(this.localGateways.values());
    }

    public List<Gateway> getNonLocalGateways() throws XMPPException {
        if (this.nonLocalGateways.size() == 0) {
            loadNonLocalGateways();
        }
        return new ArrayList(this.nonLocalGateways.values());
    }

    public void refreshNonLocalGateways() throws XMPPException {
        loadNonLocalGateways();
    }
}
