package org.jivesoftware.smack.proxy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.net.SocketFactory;

public class Socks4ProxySocketFactory extends SocketFactory {
    private ProxyInfo proxy;

    public Socks4ProxySocketFactory(ProxyInfo proxyInfo) {
        this.proxy = proxyInfo;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.net.Socket socks4ProxifiedSocket(java.lang.String r12, int r13) throws java.io.IOException {
        /*
        r11 = this;
        r3 = 4;
        r0 = 0;
        r2 = 0;
        r1 = r11.proxy;
        r4 = r1.getProxyAddress();
        r1 = r11.proxy;
        r5 = r1.getProxyPort();
        r1 = r11.proxy;
        r6 = r1.getProxyUsername();
        r1 = r11.proxy;
        r1.getProxyPassword();
        r1 = new java.net.Socket;	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x0108 }
        r1.<init>(r4, r5);	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x0108 }
        r5 = r1.getInputStream();	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r7 = r1.getOutputStream();	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r2 = 1;
        r1.setTcpNoDelay(r2);	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r2 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r8 = new byte[r2];	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r2 = 0;
        r4 = 1;
        r9 = 4;
        r8[r2] = r9;	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r2 = 2;
        r9 = 1;
        r8[r4] = r9;	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r4 = 3;
        r9 = r13 >>> 8;
        r9 = (byte) r9;	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r8[r2] = r9;	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r2 = r13 & 255;
        r2 = (byte) r2;	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r8[r4] = r2;	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r2 = java.net.InetAddress.getByName(r12);	 Catch:{ UnknownHostException -> 0x005b }
        r9 = r2.getAddress();	 Catch:{ UnknownHostException -> 0x005b }
        r2 = r3;
        r3 = r0;
    L_0x004d:
        r4 = r9.length;	 Catch:{ UnknownHostException -> 0x005b }
        if (r3 >= r4) goto L_0x006a;
    L_0x0050:
        r4 = r2 + 1;
        r10 = r9[r3];	 Catch:{ UnknownHostException -> 0x005b }
        r8[r2] = r10;	 Catch:{ UnknownHostException -> 0x005b }
        r2 = r3 + 1;
        r3 = r2;
        r2 = r4;
        goto L_0x004d;
    L_0x005b:
        r0 = move-exception;
        r2 = new org.jivesoftware.smack.proxy.ProxyException;	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r3 = org.jivesoftware.smack.proxy.ProxyInfo.ProxyType.SOCKS4;	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r4 = r0.toString();	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r2.<init>(r3, r4, r0);	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        throw r2;	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
    L_0x0068:
        r0 = move-exception;
        throw r0;
    L_0x006a:
        if (r6 == 0) goto L_0x007d;
    L_0x006c:
        r3 = r6.getBytes();	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r4 = 0;
        r9 = r6.length();	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        java.lang.System.arraycopy(r3, r4, r8, r2, r9);	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r3 = r6.length();	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r2 = r2 + r3;
    L_0x007d:
        r3 = r2 + 1;
        r4 = 0;
        r8[r2] = r4;	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r2 = 0;
        r7.write(r8, r2, r3);	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r2 = 6;
    L_0x0087:
        if (r0 >= r2) goto L_0x00b0;
    L_0x0089:
        r3 = r2 - r0;
        r3 = r5.read(r8, r0, r3);	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        if (r3 > 0) goto L_0x00ae;
    L_0x0091:
        r0 = new org.jivesoftware.smack.proxy.ProxyException;	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r2 = org.jivesoftware.smack.proxy.ProxyInfo.ProxyType.SOCKS4;	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r3 = "stream is closed";
        r0.<init>(r2, r3);	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        throw r0;	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
    L_0x009c:
        r0 = move-exception;
    L_0x009d:
        if (r1 == 0) goto L_0x00a2;
    L_0x009f:
        r1.close();	 Catch:{ Exception -> 0x0106 }
    L_0x00a2:
        r1 = new org.jivesoftware.smack.proxy.ProxyException;
        r2 = org.jivesoftware.smack.proxy.ProxyInfo.ProxyType.SOCKS4;
        r0 = r0.toString();
        r1.<init>(r2, r0);
        throw r1;
    L_0x00ae:
        r0 = r0 + r3;
        goto L_0x0087;
    L_0x00b0:
        r0 = 0;
        r0 = r8[r0];	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        if (r0 == 0) goto L_0x00d3;
    L_0x00b5:
        r0 = new org.jivesoftware.smack.proxy.ProxyException;	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r2 = org.jivesoftware.smack.proxy.ProxyInfo.ProxyType.SOCKS4;	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r3 = new java.lang.StringBuilder;	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r3.<init>();	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r4 = "server returns VN ";
        r3 = r3.append(r4);	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r4 = 0;
        r4 = r8[r4];	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r3 = r3.append(r4);	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r3 = r3.toString();	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r0.<init>(r2, r3);	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        throw r0;	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
    L_0x00d3:
        r0 = 1;
        r0 = r8[r0];	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r2 = 90;
        if (r0 == r2) goto L_0x00fb;
    L_0x00da:
        r1.close();	 Catch:{ Exception -> 0x0104, RuntimeException -> 0x0068 }
    L_0x00dd:
        r0 = new java.lang.StringBuilder;	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r0.<init>();	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r2 = "ProxySOCKS4: server returns CD ";
        r0 = r0.append(r2);	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r2 = 1;
        r2 = r8[r2];	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r0 = r0.append(r2);	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r0 = r0.toString();	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r2 = new org.jivesoftware.smack.proxy.ProxyException;	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r3 = org.jivesoftware.smack.proxy.ProxyInfo.ProxyType.SOCKS4;	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r2.<init>(r3, r0);	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        throw r2;	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
    L_0x00fb:
        r0 = 2;
        r0 = new byte[r0];	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        r2 = 0;
        r3 = 2;
        r5.read(r0, r2, r3);	 Catch:{ RuntimeException -> 0x0068, Exception -> 0x009c }
        return r1;
    L_0x0104:
        r0 = move-exception;
        goto L_0x00dd;
    L_0x0106:
        r1 = move-exception;
        goto L_0x00a2;
    L_0x0108:
        r0 = move-exception;
        r1 = r2;
        goto L_0x009d;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smack.proxy.Socks4ProxySocketFactory.socks4ProxifiedSocket(java.lang.String, int):java.net.Socket");
    }

    public Socket createSocket(String str, int i) throws IOException, UnknownHostException {
        return socks4ProxifiedSocket(str, i);
    }

    public Socket createSocket(String str, int i, InetAddress inetAddress, int i2) throws IOException, UnknownHostException {
        return socks4ProxifiedSocket(str, i);
    }

    public Socket createSocket(InetAddress inetAddress, int i) throws IOException {
        return socks4ProxifiedSocket(inetAddress.getHostAddress(), i);
    }

    public Socket createSocket(InetAddress inetAddress, int i, InetAddress inetAddress2, int i2) throws IOException {
        return socks4ProxifiedSocket(inetAddress.getHostAddress(), i);
    }
}
