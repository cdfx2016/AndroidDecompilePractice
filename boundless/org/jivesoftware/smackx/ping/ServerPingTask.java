package org.jivesoftware.smackx.ping;

import java.lang.ref.WeakReference;
import org.jivesoftware.smack.Connection;

class ServerPingTask implements Runnable {
    private int delta = 1000;
    private int tries = 3;
    private WeakReference<Connection> weakConnection;

    protected ServerPingTask(Connection connection) {
        this.weakConnection = new WeakReference(connection);
    }

    public void run() {
        boolean z = false;
        Connection connection = (Connection) this.weakConnection.get();
        if (connection != null && connection.isAuthenticated()) {
            PingManager instanceFor = PingManager.getInstanceFor(connection);
            for (int i = 0; i < this.tries; i++) {
                if (i != 0) {
                    try {
                        Thread.sleep((long) this.delta);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
                z = instanceFor.pingMyServer();
                if (z) {
                    instanceFor.lastSuccessfulPingByTask = System.currentTimeMillis();
                    break;
                }
            }
            if (z) {
                instanceFor.maybeSchedulePingServerTask();
                return;
            }
            for (PingFailedListener pingFailed : instanceFor.getPingFailedListeners()) {
                pingFailed.pingFailed();
            }
        }
    }
}
