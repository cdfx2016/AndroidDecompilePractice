package org.jivesoftware.smackx.bytestreams.socks5;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPException;

public class Socks5Proxy {
    private static Socks5Proxy socks5Server;
    private final List<String> allowedConnections = Collections.synchronizedList(new LinkedList());
    private final Map<String, Socket> connectionMap = new ConcurrentHashMap();
    private final Set<String> localAddresses = Collections.synchronizedSet(new LinkedHashSet());
    private Socks5ServerProcess serverProcess = new Socks5ServerProcess();
    private ServerSocket serverSocket;
    private Thread serverThread;

    private class Socks5ServerProcess implements Runnable {
        private Socks5ServerProcess() {
        }

        private void establishConnection(Socket socket) throws XMPPException, IOException {
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            if (dataInputStream.read() != 5) {
                throw new XMPPException("Only SOCKS5 supported");
            }
            int i;
            byte[] bArr = new byte[dataInputStream.read()];
            dataInputStream.readFully(bArr);
            byte[] bArr2 = new byte[2];
            bArr2[0] = (byte) 5;
            for (byte b : bArr) {
                if (b == (byte) 0) {
                    i = 1;
                    break;
                }
            }
            byte b2 = (byte) 0;
            if (i == 0) {
                bArr2[1] = (byte) -1;
                dataOutputStream.write(bArr2);
                dataOutputStream.flush();
                throw new XMPPException("Authentication method not supported");
            }
            bArr2[1] = (byte) 0;
            dataOutputStream.write(bArr2);
            dataOutputStream.flush();
            byte[] receiveSocks5Message = Socks5Utils.receiveSocks5Message(dataInputStream);
            String str = new String(receiveSocks5Message, 5, receiveSocks5Message[4]);
            if (Socks5Proxy.this.allowedConnections.contains(str)) {
                receiveSocks5Message[1] = (byte) 0;
                dataOutputStream.write(receiveSocks5Message);
                dataOutputStream.flush();
                Socks5Proxy.this.connectionMap.put(str, socket);
                return;
            }
            receiveSocks5Message[1] = (byte) 5;
            dataOutputStream.write(receiveSocks5Message);
            dataOutputStream.flush();
            throw new XMPPException("Connection is not allowed");
        }

        public void run() {
            while (true) {
                Socket socket = null;
                try {
                    if (!Socks5Proxy.this.serverSocket.isClosed() && !Thread.currentThread().isInterrupted()) {
                        establishConnection(Socks5Proxy.this.serverSocket.accept());
                    } else {
                        return;
                    }
                } catch (SocketException e) {
                } catch (Exception e2) {
                    if (socket != null) {
                        try {
                            socket.close();
                        } catch (IOException e3) {
                        }
                    }
                }
            }
        }
    }

    private Socks5Proxy() {
        try {
            this.localAddresses.add(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
        }
    }

    public static synchronized Socks5Proxy getSocks5Proxy() {
        Socks5Proxy socks5Proxy;
        synchronized (Socks5Proxy.class) {
            if (socks5Server == null) {
                socks5Server = new Socks5Proxy();
            }
            if (SmackConfiguration.isLocalSocks5ProxyEnabled()) {
                socks5Server.start();
            }
            socks5Proxy = socks5Server;
        }
        return socks5Proxy;
    }

    public void addLocalAddress(String str) {
        if (str == null) {
            throw new IllegalArgumentException("address may not be null");
        }
        this.localAddresses.add(str);
    }

    protected void addTransfer(String str) {
        this.allowedConnections.add(str);
    }

    public List<String> getLocalAddresses() {
        return Collections.unmodifiableList(new ArrayList(this.localAddresses));
    }

    public int getPort() {
        return !isRunning() ? -1 : this.serverSocket.getLocalPort();
    }

    protected Socket getSocket(String str) {
        return (Socket) this.connectionMap.get(str);
    }

    public boolean isRunning() {
        return this.serverSocket != null;
    }

    public void removeLocalAddress(String str) {
        this.localAddresses.remove(str);
    }

    protected void removeTransfer(String str) {
        this.allowedConnections.remove(str);
        this.connectionMap.remove(str);
    }

    public void replaceLocalAddresses(List<String> list) {
        if (list == null) {
            throw new IllegalArgumentException("list must not be null");
        }
        this.localAddresses.clear();
        this.localAddresses.addAll(list);
    }

    public synchronized void start() {
        if (!isRunning()) {
            try {
                if (SmackConfiguration.getLocalSocks5ProxyPort() < 0) {
                    int abs = Math.abs(SmackConfiguration.getLocalSocks5ProxyPort());
                    int i = 0;
                    while (i < 65535 - abs) {
                        try {
                            this.serverSocket = new ServerSocket(abs + i);
                            break;
                        } catch (IOException e) {
                            i++;
                        }
                    }
                } else {
                    this.serverSocket = new ServerSocket(SmackConfiguration.getLocalSocks5ProxyPort());
                }
                if (this.serverSocket != null) {
                    this.serverThread = new Thread(this.serverProcess);
                    this.serverThread.start();
                }
            } catch (IOException e2) {
                System.err.println("couldn't setup local SOCKS5 proxy on port " + SmackConfiguration.getLocalSocks5ProxyPort() + ": " + e2.getMessage());
            }
        }
    }

    public synchronized void stop() {
        if (isRunning()) {
            try {
                this.serverSocket.close();
            } catch (IOException e) {
            }
            if (this.serverThread != null && this.serverThread.isAlive()) {
                try {
                    this.serverThread.interrupt();
                    this.serverThread.join();
                } catch (InterruptedException e2) {
                }
            }
            this.serverThread = null;
            this.serverSocket = null;
        }
    }
}
