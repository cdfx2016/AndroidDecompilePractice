package org.jivesoftware.smackx.ping;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.ping.packet.Ping;
import org.jivesoftware.smackx.ping.packet.Pong;

public class PingManager {
    public static final String ELEMENT = "ping";
    public static final String NAMESPACE = "urn:xmpp:ping";
    private static final Map<Connection, PingManager> instances = Collections.synchronizedMap(new WeakHashMap());
    private static final ScheduledExecutorService periodicPingExecutorService = new ScheduledThreadPoolExecutor(1);
    private Connection connection;
    private long lastPingStamp = 0;
    private long lastSuccessfulManualPing = -1;
    protected volatile long lastSuccessfulPingByTask = -1;
    private ScheduledFuture<?> periodicPingTask;
    private Set<PingFailedListener> pingFailedListeners = Collections.synchronizedSet(new HashSet());
    private int pingInterval = SmackConfiguration.getDefaultPingInterval();
    private long pingMinDelta = 100;

    static {
        Connection.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(Connection connection) {
                PingManager.getInstanceFor(connection);
            }
        });
    }

    private PingManager(final Connection connection) {
        this.connection = connection;
        instances.put(connection, this);
        ServiceDiscoveryManager.getInstanceFor(connection).addFeature("urn:xmpp:ping");
        connection.addPacketListener(new PacketListener() {
            public void processPacket(Packet packet) {
                if (PingManager.this.pingMinDelta > 0) {
                    long currentTimeMillis = System.currentTimeMillis();
                    long access$100 = currentTimeMillis - PingManager.this.lastPingStamp;
                    PingManager.this.lastPingStamp = currentTimeMillis;
                    if (access$100 < PingManager.this.pingMinDelta) {
                        return;
                    }
                }
                connection.sendPacket(new Pong((Ping) packet));
            }
        }, new PacketTypeFilter(Ping.class));
        connection.addConnectionListener(new ConnectionListener() {
            public void connectionClosed() {
                PingManager.this.maybeStopPingServerTask();
            }

            public void connectionClosedOnError(Exception exception) {
                PingManager.this.maybeStopPingServerTask();
            }

            public void reconnectingIn(int i) {
            }

            public void reconnectionFailed(Exception exception) {
            }

            public void reconnectionSuccessful() {
                PingManager.this.maybeSchedulePingServerTask();
            }
        });
        maybeSchedulePingServerTask();
    }

    public static synchronized PingManager getInstanceFor(Connection connection) {
        PingManager pingManager;
        synchronized (PingManager.class) {
            pingManager = (PingManager) instances.get(connection);
            if (pingManager == null) {
                pingManager = new PingManager(connection);
            }
        }
        return pingManager;
    }

    private void maybeStopPingServerTask() {
        if (this.periodicPingTask != null) {
            this.periodicPingTask.cancel(true);
            this.periodicPingTask = null;
        }
    }

    private void pongReceived() {
        this.lastSuccessfulManualPing = System.currentTimeMillis();
    }

    public void disablePingFloodProtection() {
        setPingMinimumInterval(-1);
    }

    public long getLastSuccessfulPing() {
        return Math.max(this.lastSuccessfulPingByTask, this.lastSuccessfulManualPing);
    }

    protected Set<PingFailedListener> getPingFailedListeners() {
        return this.pingFailedListeners;
    }

    public int getPingIntervall() {
        return this.pingInterval;
    }

    public long getPingMinimumInterval() {
        return this.pingMinDelta;
    }

    public boolean isPingSupported(String str) {
        try {
            return ServiceDiscoveryManager.getInstanceFor(this.connection).discoverInfo(str).containsFeature("urn:xmpp:ping");
        } catch (XMPPException e) {
            return false;
        }
    }

    protected synchronized void maybeSchedulePingServerTask() {
        maybeStopPingServerTask();
        if (this.pingInterval > 0) {
            this.periodicPingTask = periodicPingExecutorService.schedule(new ServerPingTask(this.connection), (long) this.pingInterval, TimeUnit.SECONDS);
        }
    }

    public IQ ping(String str) {
        return ping(str, (long) SmackConfiguration.getPacketReplyTimeout());
    }

    public IQ ping(String str, long j) {
        if (!this.connection.isAuthenticated()) {
            return null;
        }
        Packet ping = new Ping(this.connection.getUser(), str);
        PacketCollector createPacketCollector = this.connection.createPacketCollector(new PacketIDFilter(ping.getPacketID()));
        this.connection.sendPacket(ping);
        IQ iq = (IQ) createPacketCollector.nextResult(j);
        createPacketCollector.cancel();
        return iq;
    }

    public boolean pingEntity(String str) {
        return pingEntity(str, (long) SmackConfiguration.getPacketReplyTimeout());
    }

    public boolean pingEntity(String str, long j) {
        IQ ping = ping(str, j);
        if (ping == null || ping.getType() == Type.ERROR) {
            return false;
        }
        pongReceived();
        return true;
    }

    public boolean pingMyServer() {
        return pingMyServer((long) SmackConfiguration.getPacketReplyTimeout());
    }

    public boolean pingMyServer(long j) {
        if (ping(this.connection.getServiceName(), j) == null) {
            for (PingFailedListener pingFailed : this.pingFailedListeners) {
                pingFailed.pingFailed();
            }
            return false;
        }
        pongReceived();
        return true;
    }

    public void registerPingFailedListener(PingFailedListener pingFailedListener) {
        this.pingFailedListeners.add(pingFailedListener);
    }

    public void setPingIntervall(int i) {
        this.pingInterval = i;
    }

    public void setPingMinimumInterval(long j) {
        this.pingMinDelta = j;
    }

    public void unregisterPingFailedListener(PingFailedListener pingFailedListener) {
        this.pingFailedListeners.remove(pingFailedListener);
    }
}
