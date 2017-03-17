package org.jivesoftware.smack;

import android.os.Build.VERSION;
import java.io.File;
import java.util.List;
import org.jivesoftware.smack.proxy.ProxyInfo;
import org.jivesoftware.smack.util.DNSUtil;
import org.jivesoftware.smack.util.dns.HostAddress;

public class AndroidConnectionConfiguration extends ConnectionConfiguration {
    private static final int DEFAULT_TIMEOUT = 10000;

    class AnonymousClass1DnsSrvLookupRunnable implements Runnable {
        List<HostAddress> addresses;
        String serviceName;

        public AnonymousClass1DnsSrvLookupRunnable(String str) {
            this.serviceName = str;
        }

        public List<HostAddress> getHostAddresses() {
            return this.addresses;
        }

        public void run() {
            this.addresses = DNSUtil.resolveXMPPDomain(this.serviceName);
        }
    }

    public AndroidConnectionConfiguration(String str) throws XMPPException {
        AndroidInit(str, 10000);
    }

    public AndroidConnectionConfiguration(String str, int i) throws XMPPException {
        AndroidInit(str, i);
    }

    public AndroidConnectionConfiguration(String str, int i, String str2) {
        super(str, i, str2);
        AndroidInit();
    }

    private void AndroidInit() {
        if (VERSION.SDK_INT >= 14) {
            setTruststoreType("AndroidCAStore");
            setTruststorePassword(null);
            setTruststorePath(null);
            return;
        }
        setTruststoreType("BKS");
        String property = System.getProperty("javax.net.ssl.trustStore");
        if (property == null) {
            property = System.getProperty("java.home") + File.separator + "etc" + File.separator + "security" + File.separator + "cacerts.bks";
        }
        setTruststorePath(property);
    }

    private void AndroidInit(String str, int i) throws XMPPException {
        AndroidInit();
        Object anonymousClass1DnsSrvLookupRunnable = new AnonymousClass1DnsSrvLookupRunnable(str);
        Thread thread = new Thread(anonymousClass1DnsSrvLookupRunnable, "dns-srv-lookup");
        thread.start();
        try {
            thread.join((long) i);
            this.hostAddresses = anonymousClass1DnsSrvLookupRunnable.getHostAddresses();
            if (this.hostAddresses == null) {
                throw new XMPPException("DNS lookup failure");
            }
            init(str, ProxyInfo.forDefaultProxy());
        } catch (Throwable e) {
            throw new XMPPException("DNS lookup timeout after " + i + "ms", e);
        }
    }
}
