package org.jivesoftware.smackx.muc;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

class RoomListenerMultiplexor implements ConnectionListener {
    private static final Map<Connection, WeakReference<RoomListenerMultiplexor>> monitors = new WeakHashMap();
    private Connection connection;
    private RoomMultiplexFilter filter;
    private RoomMultiplexListener listener;

    private static class RoomMultiplexFilter implements PacketFilter {
        private Map<String, String> roomAddressTable;

        private RoomMultiplexFilter() {
            this.roomAddressTable = new ConcurrentHashMap();
        }

        public boolean accept(Packet packet) {
            String from = packet.getFrom();
            return from == null ? false : this.roomAddressTable.containsKey(StringUtils.parseBareAddress(from).toLowerCase());
        }

        public void addRoom(String str) {
            if (str != null) {
                this.roomAddressTable.put(str.toLowerCase(), str);
            }
        }

        public void removeRoom(String str) {
            if (str != null) {
                this.roomAddressTable.remove(str.toLowerCase());
            }
        }
    }

    private static class RoomMultiplexListener implements PacketListener {
        private Map<String, PacketMultiplexListener> roomListenersByAddress;

        private RoomMultiplexListener() {
            this.roomListenersByAddress = new ConcurrentHashMap();
        }

        public void addRoom(String str, PacketMultiplexListener packetMultiplexListener) {
            if (str != null) {
                this.roomListenersByAddress.put(str.toLowerCase(), packetMultiplexListener);
            }
        }

        public void processPacket(Packet packet) {
            String from = packet.getFrom();
            if (from != null) {
                PacketMultiplexListener packetMultiplexListener = (PacketMultiplexListener) this.roomListenersByAddress.get(StringUtils.parseBareAddress(from).toLowerCase());
                if (packetMultiplexListener != null) {
                    packetMultiplexListener.processPacket(packet);
                }
            }
        }

        public void removeRoom(String str) {
            if (str != null) {
                this.roomListenersByAddress.remove(str.toLowerCase());
            }
        }
    }

    private RoomListenerMultiplexor(Connection connection, RoomMultiplexFilter roomMultiplexFilter, RoomMultiplexListener roomMultiplexListener) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection is null");
        } else if (roomMultiplexFilter == null) {
            throw new IllegalArgumentException("Filter is null");
        } else if (roomMultiplexListener == null) {
            throw new IllegalArgumentException("Listener is null");
        } else {
            this.connection = connection;
            this.filter = roomMultiplexFilter;
            this.listener = roomMultiplexListener;
        }
    }

    private void cancel() {
        this.connection.removeConnectionListener(this);
        this.connection.removePacketListener(this.listener);
    }

    public static RoomListenerMultiplexor getRoomMultiplexor(Connection connection) {
        RoomListenerMultiplexor roomListenerMultiplexor;
        synchronized (monitors) {
            if (!monitors.containsKey(connection) || ((WeakReference) monitors.get(connection)).get() == null) {
                roomListenerMultiplexor = new RoomListenerMultiplexor(connection, new RoomMultiplexFilter(), new RoomMultiplexListener());
                roomListenerMultiplexor.init();
                monitors.put(connection, new WeakReference(roomListenerMultiplexor));
            }
            roomListenerMultiplexor = (RoomListenerMultiplexor) ((WeakReference) monitors.get(connection)).get();
        }
        return roomListenerMultiplexor;
    }

    public void addRoom(String str, PacketMultiplexListener packetMultiplexListener) {
        this.filter.addRoom(str);
        this.listener.addRoom(str, packetMultiplexListener);
    }

    public void connectionClosed() {
        cancel();
    }

    public void connectionClosedOnError(Exception exception) {
        cancel();
    }

    public void init() {
        this.connection.addConnectionListener(this);
        this.connection.addPacketListener(this.listener, this.filter);
    }

    public void reconnectingIn(int i) {
    }

    public void reconnectionFailed(Exception exception) {
    }

    public void reconnectionSuccessful() {
    }

    public void removeRoom(String str) {
        this.filter.removeRoom(str);
        this.listener.removeRoom(str);
    }
}
