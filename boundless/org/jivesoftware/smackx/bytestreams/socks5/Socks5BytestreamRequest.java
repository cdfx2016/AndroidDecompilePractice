package org.jivesoftware.smackx.bytestreams.socks5;

import java.io.IOException;
import java.net.Socket;
import java.util.Collection;
import java.util.concurrent.TimeoutException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.packet.XMPPError.Condition;
import org.jivesoftware.smack.util.Cache;
import org.jivesoftware.smackx.bytestreams.BytestreamRequest;
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream;
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream.StreamHost;

public class Socks5BytestreamRequest implements BytestreamRequest {
    private static final Cache<String, Integer> ADDRESS_BLACKLIST = new Cache(100, BLACKLIST_LIFETIME);
    private static final long BLACKLIST_LIFETIME = 7200000;
    private static final int BLACKLIST_MAX_SIZE = 100;
    private static int CONNECTION_FAILURE_THRESHOLD = 2;
    private Bytestream bytestreamRequest;
    private Socks5BytestreamManager manager;
    private int minimumConnectTimeout = 2000;
    private int totalConnectTimeout = 10000;

    protected Socks5BytestreamRequest(Socks5BytestreamManager socks5BytestreamManager, Bytestream bytestream) {
        this.manager = socks5BytestreamManager;
        this.bytestreamRequest = bytestream;
    }

    private void cancelRequest() throws XMPPException {
        String str = "Could not establish socket with any provided host";
        XMPPError xMPPError = new XMPPError(Condition.item_not_found, str);
        this.manager.getConnection().sendPacket(IQ.createErrorResponse(this.bytestreamRequest, xMPPError));
        throw new XMPPException(str, xMPPError);
    }

    private Bytestream createUsedHostResponse(StreamHost streamHost) {
        Bytestream bytestream = new Bytestream(this.bytestreamRequest.getSessionID());
        bytestream.setTo(this.bytestreamRequest.getFrom());
        bytestream.setType(Type.RESULT);
        bytestream.setPacketID(this.bytestreamRequest.getPacketID());
        bytestream.setUsedHost(streamHost.getJID());
        return bytestream;
    }

    public static int getConnectFailureThreshold() {
        return CONNECTION_FAILURE_THRESHOLD;
    }

    private int getConnectionFailures(String str) {
        Integer num = (Integer) ADDRESS_BLACKLIST.get(str);
        return num != null ? num.intValue() : 0;
    }

    private void incrementConnectionFailures(String str) {
        Integer num = (Integer) ADDRESS_BLACKLIST.get(str);
        ADDRESS_BLACKLIST.put(str, Integer.valueOf(num == null ? 1 : num.intValue() + 1));
    }

    public static void setConnectFailureThreshold(int i) {
        CONNECTION_FAILURE_THRESHOLD = i;
    }

    public Socks5BytestreamSession accept() throws XMPPException, InterruptedException {
        Socket socket;
        StreamHost streamHost = null;
        Collection<StreamHost> streamHosts = this.bytestreamRequest.getStreamHosts();
        if (streamHosts.size() == 0) {
            cancelRequest();
        }
        String createDigest = Socks5Utils.createDigest(this.bytestreamRequest.getSessionID(), this.bytestreamRequest.getFrom(), this.manager.getConnection().getUser());
        int max = Math.max(getTotalConnectTimeout() / streamHosts.size(), getMinimumConnectTimeout());
        for (StreamHost streamHost2 : streamHosts) {
            String str = streamHost2.getAddress() + ":" + streamHost2.getPort();
            int connectionFailures = getConnectionFailures(str);
            if (CONNECTION_FAILURE_THRESHOLD <= 0 || connectionFailures < CONNECTION_FAILURE_THRESHOLD) {
                try {
                    streamHost = streamHost2;
                    socket = new Socks5Client(streamHost2, createDigest).getSocket(max);
                    break;
                } catch (TimeoutException e) {
                    incrementConnectionFailures(str);
                } catch (IOException e2) {
                    incrementConnectionFailures(str);
                } catch (XMPPException e3) {
                    incrementConnectionFailures(str);
                }
            }
        }
        Object obj = streamHost;
        if (streamHost == null || socket == null) {
            cancelRequest();
        }
        this.manager.getConnection().sendPacket(createUsedHostResponse(streamHost));
        return new Socks5BytestreamSession(socket, streamHost.getJID().equals(this.bytestreamRequest.getFrom()));
    }

    public String getFrom() {
        return this.bytestreamRequest.getFrom();
    }

    public int getMinimumConnectTimeout() {
        return this.minimumConnectTimeout <= 0 ? 2000 : this.minimumConnectTimeout;
    }

    public String getSessionID() {
        return this.bytestreamRequest.getSessionID();
    }

    public int getTotalConnectTimeout() {
        return this.totalConnectTimeout <= 0 ? 10000 : this.totalConnectTimeout;
    }

    public void reject() {
        this.manager.replyRejectPacket(this.bytestreamRequest);
    }

    public void setMinimumConnectTimeout(int i) {
        this.minimumConnectTimeout = i;
    }

    public void setTotalConnectTimeout(int i) {
        this.totalConnectTimeout = i;
    }
}
