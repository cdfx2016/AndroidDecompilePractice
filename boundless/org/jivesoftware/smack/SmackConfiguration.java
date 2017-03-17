package org.jivesoftware.smack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import org.jivesoftware.smack.parsing.ExceptionThrowingCallback;
import org.jivesoftware.smack.parsing.ParsingExceptionCallback;
import org.xmlpull.v1.XmlPullParser;

public final class SmackConfiguration {
    private static final String SMACK_VERSION = "3.3.1";
    private static boolean autoEnableEntityCaps;
    private static ParsingExceptionCallback defaultCallback = new ExceptionThrowingCallback();
    private static Vector<String> defaultMechs = new Vector();
    private static int defaultPingInterval;
    private static boolean localSocks5ProxyEnabled;
    private static int localSocks5ProxyPort;
    private static int packetCollectorSize;
    private static int packetReplyTimeout;

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static {
        /*
        r1 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;
        r8 = 1;
        packetReplyTimeout = r1;
        r0 = new java.util.Vector;
        r0.<init>();
        defaultMechs = r0;
        localSocks5ProxyEnabled = r8;
        r0 = 7777; // 0x1e61 float:1.0898E-41 double:3.8423E-320;
        localSocks5ProxyPort = r0;
        packetCollectorSize = r1;
        r0 = 1800; // 0x708 float:2.522E-42 double:8.893E-321;
        defaultPingInterval = r0;
        r0 = new org.jivesoftware.smack.parsing.ExceptionThrowingCallback;
        r0.<init>();
        defaultCallback = r0;
        autoEnableEntityCaps = r8;
        r3 = getClassLoaders();	 Catch:{ Exception -> 0x00b2 }
        r4 = r3.length;	 Catch:{ Exception -> 0x00b2 }
        r0 = 0;
        r2 = r0;
    L_0x0028:
        if (r2 >= r4) goto L_0x00b6;
    L_0x002a:
        r0 = r3[r2];	 Catch:{ Exception -> 0x00b2 }
        r1 = "META-INF/smack-config.xml";
        r5 = r0.getResources(r1);	 Catch:{ Exception -> 0x00b2 }
    L_0x0032:
        r0 = r5.hasMoreElements();	 Catch:{ Exception -> 0x00b2 }
        if (r0 == 0) goto L_0x0127;
    L_0x0038:
        r0 = r5.nextElement();	 Catch:{ Exception -> 0x00b2 }
        r0 = (java.net.URL) r0;	 Catch:{ Exception -> 0x00b2 }
        r1 = 0;
        r1 = r0.openStream();	 Catch:{ Exception -> 0x008d }
        r0 = org.xmlpull.v1.XmlPullParserFactory.newInstance();	 Catch:{ Exception -> 0x008d }
        r6 = r0.newPullParser();	 Catch:{ Exception -> 0x008d }
        r0 = "http://xmlpull.org/v1/doc/features.html#process-namespaces";
        r7 = 1;
        r6.setFeature(r0, r7);	 Catch:{ Exception -> 0x008d }
        r0 = "UTF-8";
        r6.setInput(r1, r0);	 Catch:{ Exception -> 0x008d }
        r0 = r6.getEventType();	 Catch:{ Exception -> 0x008d }
    L_0x005a:
        r7 = 2;
        if (r0 != r7) goto L_0x006c;
    L_0x005d:
        r0 = r6.getName();	 Catch:{ Exception -> 0x008d }
        r7 = "className";
        r0 = r0.equals(r7);	 Catch:{ Exception -> 0x008d }
        if (r0 == 0) goto L_0x0078;
    L_0x0069:
        parseClassToLoad(r6);	 Catch:{ Exception -> 0x008d }
    L_0x006c:
        r0 = r6.next();	 Catch:{ Exception -> 0x008d }
        if (r0 != r8) goto L_0x005a;
    L_0x0072:
        r1.close();	 Catch:{ Exception -> 0x0076 }
        goto L_0x0032;
    L_0x0076:
        r0 = move-exception;
        goto L_0x0032;
    L_0x0078:
        r0 = r6.getName();	 Catch:{ Exception -> 0x008d }
        r7 = "packetReplyTimeout";
        r0 = r0.equals(r7);	 Catch:{ Exception -> 0x008d }
        if (r0 == 0) goto L_0x0097;
    L_0x0084:
        r0 = packetReplyTimeout;	 Catch:{ Exception -> 0x008d }
        r0 = parseIntProperty(r6, r0);	 Catch:{ Exception -> 0x008d }
        packetReplyTimeout = r0;	 Catch:{ Exception -> 0x008d }
        goto L_0x006c;
    L_0x008d:
        r0 = move-exception;
        r0.printStackTrace();	 Catch:{ all -> 0x00ad }
        r1.close();	 Catch:{ Exception -> 0x0095 }
        goto L_0x0032;
    L_0x0095:
        r0 = move-exception;
        goto L_0x0032;
    L_0x0097:
        r0 = r6.getName();	 Catch:{ Exception -> 0x008d }
        r7 = "mechName";
        r0 = r0.equals(r7);	 Catch:{ Exception -> 0x008d }
        if (r0 == 0) goto L_0x00b7;
    L_0x00a3:
        r0 = defaultMechs;	 Catch:{ Exception -> 0x008d }
        r7 = r6.nextText();	 Catch:{ Exception -> 0x008d }
        r0.add(r7);	 Catch:{ Exception -> 0x008d }
        goto L_0x006c;
    L_0x00ad:
        r0 = move-exception;
        r1.close();	 Catch:{ Exception -> 0x012c }
    L_0x00b1:
        throw r0;	 Catch:{ Exception -> 0x00b2 }
    L_0x00b2:
        r0 = move-exception;
        r0.printStackTrace();
    L_0x00b6:
        return;
    L_0x00b7:
        r0 = r6.getName();	 Catch:{ Exception -> 0x008d }
        r7 = "localSocks5ProxyEnabled";
        r0 = r0.equals(r7);	 Catch:{ Exception -> 0x008d }
        if (r0 == 0) goto L_0x00ce;
    L_0x00c3:
        r0 = r6.nextText();	 Catch:{ Exception -> 0x008d }
        r0 = java.lang.Boolean.parseBoolean(r0);	 Catch:{ Exception -> 0x008d }
        localSocks5ProxyEnabled = r0;	 Catch:{ Exception -> 0x008d }
        goto L_0x006c;
    L_0x00ce:
        r0 = r6.getName();	 Catch:{ Exception -> 0x008d }
        r7 = "localSocks5ProxyPort";
        r0 = r0.equals(r7);	 Catch:{ Exception -> 0x008d }
        if (r0 == 0) goto L_0x00e3;
    L_0x00da:
        r0 = localSocks5ProxyPort;	 Catch:{ Exception -> 0x008d }
        r0 = parseIntProperty(r6, r0);	 Catch:{ Exception -> 0x008d }
        localSocks5ProxyPort = r0;	 Catch:{ Exception -> 0x008d }
        goto L_0x006c;
    L_0x00e3:
        r0 = r6.getName();	 Catch:{ Exception -> 0x008d }
        r7 = "packetCollectorSize";
        r0 = r0.equals(r7);	 Catch:{ Exception -> 0x008d }
        if (r0 == 0) goto L_0x00f9;
    L_0x00ef:
        r0 = packetCollectorSize;	 Catch:{ Exception -> 0x008d }
        r0 = parseIntProperty(r6, r0);	 Catch:{ Exception -> 0x008d }
        packetCollectorSize = r0;	 Catch:{ Exception -> 0x008d }
        goto L_0x006c;
    L_0x00f9:
        r0 = r6.getName();	 Catch:{ Exception -> 0x008d }
        r7 = "defaultPingInterval";
        r0 = r0.equals(r7);	 Catch:{ Exception -> 0x008d }
        if (r0 == 0) goto L_0x010f;
    L_0x0105:
        r0 = defaultPingInterval;	 Catch:{ Exception -> 0x008d }
        r0 = parseIntProperty(r6, r0);	 Catch:{ Exception -> 0x008d }
        defaultPingInterval = r0;	 Catch:{ Exception -> 0x008d }
        goto L_0x006c;
    L_0x010f:
        r0 = r6.getName();	 Catch:{ Exception -> 0x008d }
        r7 = "autoEnableEntityCaps";
        r0 = r0.equals(r7);	 Catch:{ Exception -> 0x008d }
        if (r0 == 0) goto L_0x006c;
    L_0x011b:
        r0 = r6.nextText();	 Catch:{ Exception -> 0x008d }
        r0 = java.lang.Boolean.parseBoolean(r0);	 Catch:{ Exception -> 0x008d }
        autoEnableEntityCaps = r0;	 Catch:{ Exception -> 0x008d }
        goto L_0x006c;
    L_0x0127:
        r0 = r2 + 1;
        r2 = r0;
        goto L_0x0028;
    L_0x012c:
        r1 = move-exception;
        goto L_0x00b1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smack.SmackConfiguration.<clinit>():void");
    }

    private SmackConfiguration() {
    }

    public static void addSaslMech(String str) {
        if (!defaultMechs.contains(str)) {
            defaultMechs.add(str);
        }
    }

    public static void addSaslMechs(Collection<String> collection) {
        for (String addSaslMech : collection) {
            addSaslMech(addSaslMech);
        }
    }

    public static boolean autoEnableEntityCaps() {
        return autoEnableEntityCaps;
    }

    private static ClassLoader[] getClassLoaders() {
        int i = 0;
        ClassLoader[] classLoaderArr = new ClassLoader[]{SmackConfiguration.class.getClassLoader(), Thread.currentThread().getContextClassLoader()};
        List arrayList = new ArrayList();
        int length = classLoaderArr.length;
        while (i < length) {
            Object obj = classLoaderArr[i];
            if (obj != null) {
                arrayList.add(obj);
            }
            i++;
        }
        return (ClassLoader[]) arrayList.toArray(new ClassLoader[arrayList.size()]);
    }

    public static ParsingExceptionCallback getDefaultParsingExceptionCallback() {
        return defaultCallback;
    }

    public static int getDefaultPingInterval() {
        return defaultPingInterval;
    }

    public static int getLocalSocks5ProxyPort() {
        return localSocks5ProxyPort;
    }

    public static int getPacketCollectorSize() {
        return packetCollectorSize;
    }

    public static int getPacketReplyTimeout() {
        if (packetReplyTimeout <= 0) {
            packetReplyTimeout = 5000;
        }
        return packetReplyTimeout;
    }

    public static List<String> getSaslMechs() {
        return defaultMechs;
    }

    public static String getVersion() {
        return SMACK_VERSION;
    }

    public static boolean isLocalSocks5ProxyEnabled() {
        return localSocks5ProxyEnabled;
    }

    private static void parseClassToLoad(XmlPullParser xmlPullParser) throws Exception {
        String nextText = xmlPullParser.nextText();
        try {
            Class.forName(nextText);
        } catch (ClassNotFoundException e) {
            System.err.println("Error! A startup class specified in smack-config.xml could not be loaded: " + nextText);
        }
    }

    private static int parseIntProperty(XmlPullParser xmlPullParser, int i) throws Exception {
        try {
            i = Integer.parseInt(xmlPullParser.nextText());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return i;
    }

    public static void removeSaslMech(String str) {
        if (defaultMechs.contains(str)) {
            defaultMechs.remove(str);
        }
    }

    public static void removeSaslMechs(Collection<String> collection) {
        for (String removeSaslMech : collection) {
            removeSaslMech(removeSaslMech);
        }
    }

    public static void setAutoEnableEntityCaps(boolean z) {
        autoEnableEntityCaps = z;
    }

    public static void setDefaultParsingExceptionCallback(ParsingExceptionCallback parsingExceptionCallback) {
        defaultCallback = parsingExceptionCallback;
    }

    public static void setDefaultPingInterval(int i) {
        defaultPingInterval = i;
    }

    public static void setLocalSocks5ProxyEnabled(boolean z) {
        localSocks5ProxyEnabled = z;
    }

    public static void setLocalSocks5ProxyPort(int i) {
        localSocks5ProxyPort = i;
    }

    public static void setPacketCollectorSize(int i) {
        packetCollectorSize = i;
    }

    public static void setPacketReplyTimeout(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException();
        }
        packetReplyTimeout = i;
    }
}
