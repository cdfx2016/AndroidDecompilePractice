package org.jivesoftware.smack;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;

public class PacketCollector {
    private boolean cancelled;
    private Connection connection;
    private PacketFilter packetFilter;
    private ArrayBlockingQueue<Packet> resultQueue;

    protected PacketCollector(Connection connection, PacketFilter packetFilter) {
        this(connection, packetFilter, SmackConfiguration.getPacketCollectorSize());
    }

    protected PacketCollector(Connection connection, PacketFilter packetFilter, int i) {
        this.cancelled = false;
        this.connection = connection;
        this.packetFilter = packetFilter;
        this.resultQueue = new ArrayBlockingQueue(i);
    }

    public void cancel() {
        if (!this.cancelled) {
            this.cancelled = true;
            this.connection.removePacketCollector(this);
        }
    }

    public PacketFilter getPacketFilter() {
        return this.packetFilter;
    }

    public Packet nextResult() {
        try {
            return (Packet) this.resultQueue.take();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public Packet nextResult(long j) {
        try {
            return (Packet) this.resultQueue.poll(j, TimeUnit.MILLISECONDS);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public Packet pollResult() {
        return (Packet) this.resultQueue.poll();
    }

    protected void processPacket(Packet packet) {
        if (packet != null) {
            if (this.packetFilter == null || this.packetFilter.accept(packet)) {
                while (!this.resultQueue.offer(packet)) {
                    this.resultQueue.poll();
                }
            }
        }
    }
}
