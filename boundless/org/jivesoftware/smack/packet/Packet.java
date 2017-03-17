package org.jivesoftware.smack.packet;

import com.xiaomi.mipush.sdk.Constants;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jivesoftware.smack.util.StringUtils;

public abstract class Packet {
    protected static final String DEFAULT_LANGUAGE = Locale.getDefault().getLanguage().toLowerCase();
    private static String DEFAULT_XML_NS = null;
    public static final String ID_NOT_AVAILABLE = "ID_NOT_AVAILABLE";
    public static final DateFormat XEP_0082_UTC_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static long id = 0;
    private static String prefix = (StringUtils.randomString(5) + Constants.ACCEPT_TIME_SEPARATOR_SERVER);
    private XMPPError error = null;
    private String from = null;
    private final List<PacketExtension> packetExtensions = new CopyOnWriteArrayList();
    private String packetID = null;
    private final Map<String, Object> properties = new HashMap();
    private String to = null;
    private String xmlns = DEFAULT_XML_NS;

    static {
        XEP_0082_UTC_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public Packet(Packet packet) {
        this.packetID = packet.getPacketID();
        this.to = packet.getTo();
        this.from = packet.getFrom();
        this.xmlns = packet.xmlns;
        this.error = packet.error;
        for (PacketExtension addExtension : packet.getExtensions()) {
            addExtension(addExtension);
        }
    }

    public static String getDefaultLanguage() {
        return DEFAULT_LANGUAGE;
    }

    public static synchronized String nextID() {
        String stringBuilder;
        synchronized (Packet.class) {
            StringBuilder append = new StringBuilder().append(prefix);
            long j = id;
            id = 1 + j;
            stringBuilder = append.append(Long.toString(j)).toString();
        }
        return stringBuilder;
    }

    public static void setDefaultXmlns(String str) {
        DEFAULT_XML_NS = str;
    }

    public void addExtension(PacketExtension packetExtension) {
        if (packetExtension != null) {
            this.packetExtensions.add(packetExtension);
        }
    }

    public void addExtensions(Collection<PacketExtension> collection) {
        if (collection != null) {
            this.packetExtensions.addAll(collection);
        }
    }

    public synchronized void deleteProperty(String str) {
        if (this.properties != null) {
            this.properties.remove(str);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r5) {
        /*
        r4 = this;
        r0 = 1;
        r1 = 0;
        if (r4 != r5) goto L_0x0006;
    L_0x0004:
        r1 = r0;
    L_0x0005:
        return r1;
    L_0x0006:
        if (r5 == 0) goto L_0x0005;
    L_0x0008:
        r2 = r4.getClass();
        r3 = r5.getClass();
        if (r2 != r3) goto L_0x0005;
    L_0x0012:
        r5 = (org.jivesoftware.smack.packet.Packet) r5;
        r2 = r4.error;
        if (r2 == 0) goto L_0x0075;
    L_0x0018:
        r2 = r4.error;
        r3 = r5.error;
        r2 = r2.equals(r3);
        if (r2 == 0) goto L_0x0005;
    L_0x0022:
        r2 = r4.from;
        if (r2 == 0) goto L_0x007a;
    L_0x0026:
        r2 = r4.from;
        r3 = r5.from;
        r2 = r2.equals(r3);
        if (r2 == 0) goto L_0x0005;
    L_0x0030:
        r2 = r4.packetExtensions;
        r3 = r5.packetExtensions;
        r2 = r2.equals(r3);
        if (r2 == 0) goto L_0x0005;
    L_0x003a:
        r2 = r4.packetID;
        if (r2 == 0) goto L_0x007f;
    L_0x003e:
        r2 = r4.packetID;
        r3 = r5.packetID;
        r2 = r2.equals(r3);
        if (r2 == 0) goto L_0x0005;
    L_0x0048:
        r2 = r4.properties;
        if (r2 == 0) goto L_0x0084;
    L_0x004c:
        r2 = r4.properties;
        r3 = r5.properties;
        r2 = r2.equals(r3);
        if (r2 == 0) goto L_0x0005;
    L_0x0056:
        r2 = r4.to;
        if (r2 == 0) goto L_0x008a;
    L_0x005a:
        r2 = r4.to;
        r3 = r5.to;
        r2 = r2.equals(r3);
        if (r2 == 0) goto L_0x0005;
    L_0x0064:
        r2 = r4.xmlns;
        if (r2 == 0) goto L_0x0090;
    L_0x0068:
        r2 = r4.xmlns;
        r3 = r5.xmlns;
        r2 = r2.equals(r3);
        if (r2 != 0) goto L_0x0073;
    L_0x0072:
        r0 = r1;
    L_0x0073:
        r1 = r0;
        goto L_0x0005;
    L_0x0075:
        r2 = r5.error;
        if (r2 == 0) goto L_0x0022;
    L_0x0079:
        goto L_0x0005;
    L_0x007a:
        r2 = r5.from;
        if (r2 == 0) goto L_0x0030;
    L_0x007e:
        goto L_0x0005;
    L_0x007f:
        r2 = r5.packetID;
        if (r2 == 0) goto L_0x0048;
    L_0x0083:
        goto L_0x0005;
    L_0x0084:
        r2 = r5.properties;
        if (r2 == 0) goto L_0x0056;
    L_0x0088:
        goto L_0x0005;
    L_0x008a:
        r2 = r5.to;
        if (r2 == 0) goto L_0x0064;
    L_0x008e:
        goto L_0x0005;
    L_0x0090:
        r2 = r5.xmlns;
        if (r2 != 0) goto L_0x0072;
    L_0x0094:
        goto L_0x0073;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smack.packet.Packet.equals(java.lang.Object):boolean");
    }

    public XMPPError getError() {
        return this.error;
    }

    public PacketExtension getExtension(String str) {
        return getExtension(null, str);
    }

    public PacketExtension getExtension(String str, String str2) {
        if (str2 == null) {
            return null;
        }
        for (PacketExtension packetExtension : this.packetExtensions) {
            if ((str == null || str.equals(packetExtension.getElementName())) && str2.equals(packetExtension.getNamespace())) {
                return packetExtension;
            }
        }
        return null;
    }

    public synchronized Collection<PacketExtension> getExtensions() {
        return this.packetExtensions == null ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList(this.packetExtensions));
    }

    protected synchronized String getExtensionsXML() {
        StringBuilder stringBuilder;
        Exception e;
        ObjectOutputStream objectOutputStream;
        ByteArrayOutputStream byteArrayOutputStream;
        Throwable th;
        ByteArrayOutputStream byteArrayOutputStream2;
        ObjectOutputStream objectOutputStream2;
        stringBuilder = new StringBuilder();
        for (PacketExtension toXML : getExtensions()) {
            stringBuilder.append(toXML.toXML());
        }
        if (!(this.properties == null || this.properties.isEmpty())) {
            stringBuilder.append("<properties xmlns=\"http://www.jivesoftware.com/xmlns/xmpp/properties\">");
            for (String str : getPropertyNames()) {
                Object property = getProperty(str);
                stringBuilder.append("<property>");
                stringBuilder.append("<name>").append(StringUtils.escapeForXML(str)).append("</name>");
                stringBuilder.append("<value type=\"");
                if (property instanceof Integer) {
                    stringBuilder.append("integer\">").append(property).append("</value>");
                } else if (property instanceof Long) {
                    stringBuilder.append("long\">").append(property).append("</value>");
                } else if (property instanceof Float) {
                    stringBuilder.append("float\">").append(property).append("</value>");
                } else if (property instanceof Double) {
                    stringBuilder.append("double\">").append(property).append("</value>");
                } else if (property instanceof Boolean) {
                    stringBuilder.append("boolean\">").append(property).append("</value>");
                } else if (property instanceof String) {
                    stringBuilder.append("string\">");
                    stringBuilder.append(StringUtils.escapeForXML((String) property));
                    stringBuilder.append("</value>");
                } else {
                    try {
                        byteArrayOutputStream2 = new ByteArrayOutputStream();
                        try {
                            objectOutputStream2 = new ObjectOutputStream(byteArrayOutputStream2);
                            try {
                                objectOutputStream2.writeObject(property);
                                stringBuilder.append("java-object\">");
                                stringBuilder.append(StringUtils.encodeBase64(byteArrayOutputStream2.toByteArray())).append("</value>");
                                if (objectOutputStream2 != null) {
                                    try {
                                        objectOutputStream2.close();
                                    } catch (Exception e2) {
                                    }
                                }
                                if (byteArrayOutputStream2 != null) {
                                    try {
                                        byteArrayOutputStream2.close();
                                    } catch (Exception e3) {
                                    }
                                }
                            } catch (Exception e4) {
                                e = e4;
                                objectOutputStream = objectOutputStream2;
                                byteArrayOutputStream = byteArrayOutputStream2;
                                try {
                                    e.printStackTrace();
                                    if (objectOutputStream != null) {
                                        try {
                                            objectOutputStream.close();
                                        } catch (Exception e5) {
                                        }
                                    }
                                    if (byteArrayOutputStream == null) {
                                        try {
                                            byteArrayOutputStream.close();
                                        } catch (Exception e6) {
                                        }
                                    }
                                    stringBuilder.append("</property>");
                                } catch (Throwable th2) {
                                    th = th2;
                                    byteArrayOutputStream2 = byteArrayOutputStream;
                                    objectOutputStream2 = objectOutputStream;
                                }
                            } catch (Throwable th3) {
                                th = th3;
                            }
                        } catch (Exception e7) {
                            e = e7;
                            objectOutputStream = null;
                            byteArrayOutputStream = byteArrayOutputStream2;
                            e.printStackTrace();
                            if (objectOutputStream != null) {
                                objectOutputStream.close();
                            }
                            if (byteArrayOutputStream == null) {
                                byteArrayOutputStream.close();
                            }
                            stringBuilder.append("</property>");
                        } catch (Throwable th4) {
                            th = th4;
                            objectOutputStream2 = null;
                        }
                    } catch (Exception e8) {
                        e = e8;
                        objectOutputStream = null;
                        byteArrayOutputStream = null;
                        e.printStackTrace();
                        if (objectOutputStream != null) {
                            objectOutputStream.close();
                        }
                        if (byteArrayOutputStream == null) {
                            byteArrayOutputStream.close();
                        }
                        stringBuilder.append("</property>");
                    } catch (Throwable th5) {
                        th = th5;
                        objectOutputStream2 = null;
                        byteArrayOutputStream2 = null;
                    }
                }
                stringBuilder.append("</property>");
            }
            stringBuilder.append("</properties>");
        }
        return stringBuilder.toString();
        throw th;
        if (objectOutputStream2 != null) {
            try {
                objectOutputStream2.close();
            } catch (Exception e9) {
            }
        }
        if (byteArrayOutputStream2 != null) {
            try {
                byteArrayOutputStream2.close();
            } catch (Exception e10) {
            }
        }
        throw th;
        if (byteArrayOutputStream2 != null) {
            byteArrayOutputStream2.close();
        }
        throw th;
    }

    public String getFrom() {
        return this.from;
    }

    public String getPacketID() {
        if (ID_NOT_AVAILABLE.equals(this.packetID)) {
            return null;
        }
        if (this.packetID == null) {
            this.packetID = nextID();
        }
        return this.packetID;
    }

    public synchronized Object getProperty(String str) {
        return this.properties == null ? null : this.properties.get(str);
    }

    public synchronized Collection<String> getPropertyNames() {
        return this.properties == null ? Collections.emptySet() : Collections.unmodifiableSet(new HashSet(this.properties.keySet()));
    }

    public String getTo() {
        return this.to;
    }

    public String getXmlns() {
        return this.xmlns;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = ((((((this.from != null ? this.from.hashCode() : 0) + (((this.to != null ? this.to.hashCode() : 0) + (((this.packetID != null ? this.packetID.hashCode() : 0) + ((this.xmlns != null ? this.xmlns.hashCode() : 0) * 31)) * 31)) * 31)) * 31) + this.packetExtensions.hashCode()) * 31) + this.properties.hashCode()) * 31;
        if (this.error != null) {
            i = this.error.hashCode();
        }
        return hashCode + i;
    }

    public void removeExtension(PacketExtension packetExtension) {
        this.packetExtensions.remove(packetExtension);
    }

    public void setError(XMPPError xMPPError) {
        this.error = xMPPError;
    }

    public void setFrom(String str) {
        this.from = str;
    }

    public void setPacketID(String str) {
        this.packetID = str;
    }

    public synchronized void setProperty(String str, Object obj) {
        if (obj instanceof Serializable) {
            this.properties.put(str, obj);
        } else {
            throw new IllegalArgumentException("Value must be serialiazble");
        }
    }

    public void setTo(String str) {
        this.to = str;
    }

    public abstract String toXML();
}
