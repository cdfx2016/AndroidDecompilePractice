package org.jivesoftware.smack.provider;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.PacketExtension;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class ProviderManager {
    private static ProviderManager instance;
    private Map<String, Object> extensionProviders = new ConcurrentHashMap();
    private Map<String, Object> iqProviders = new ConcurrentHashMap();

    private ProviderManager() {
        initialize();
    }

    private ClassLoader[] getClassLoaders() {
        int i = 0;
        ClassLoader[] classLoaderArr = new ClassLoader[]{ProviderManager.class.getClassLoader(), Thread.currentThread().getContextClassLoader()};
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

    public static synchronized ProviderManager getInstance() {
        ProviderManager providerManager;
        synchronized (ProviderManager.class) {
            if (instance == null) {
                instance = new ProviderManager();
            }
            providerManager = instance;
        }
        return providerManager;
    }

    private String getProviderKey(String str, String str2) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<").append(str).append("/><").append(str2).append("/>");
        return stringBuilder.toString();
    }

    public static synchronized void setInstance(ProviderManager providerManager) {
        synchronized (ProviderManager.class) {
            if (instance != null) {
                throw new IllegalStateException("ProviderManager singleton already set");
            }
            instance = providerManager;
        }
    }

    public void addExtensionProvider(String str, String str2, Object obj) {
        if ((obj instanceof PacketExtensionProvider) || (obj instanceof Class)) {
            this.extensionProviders.put(getProviderKey(str, str2), obj);
            return;
        }
        throw new IllegalArgumentException("Provider must be a PacketExtensionProvider or a Class instance.");
    }

    public void addIQProvider(String str, String str2, Object obj) {
        if ((obj instanceof IQProvider) || ((obj instanceof Class) && IQ.class.isAssignableFrom((Class) obj))) {
            this.iqProviders.put(getProviderKey(str, str2), obj);
            return;
        }
        throw new IllegalArgumentException("Provider must be an IQProvider or a Class instance.");
    }

    public Object getExtensionProvider(String str, String str2) {
        return this.extensionProviders.get(getProviderKey(str, str2));
    }

    public Collection<Object> getExtensionProviders() {
        return Collections.unmodifiableCollection(this.extensionProviders.values());
    }

    public Object getIQProvider(String str, String str2) {
        return this.iqProviders.get(getProviderKey(str, str2));
    }

    public Collection<Object> getIQProviders() {
        return Collections.unmodifiableCollection(this.iqProviders.values());
    }

    protected void initialize() {
        try {
            for (ClassLoader resources : getClassLoaders()) {
                Enumeration resources2 = resources.getResources("META-INF/smack.providers");
                while (resources2.hasMoreElements()) {
                    InputStream inputStream = null;
                    inputStream = ((URL) resources2.nextElement()).openStream();
                    XmlPullParser newPullParser = XmlPullParserFactory.newInstance().newPullParser();
                    newPullParser.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", true);
                    newPullParser.setInput(inputStream, "UTF-8");
                    int eventType = newPullParser.getEventType();
                    do {
                        if (eventType == 2) {
                            String nextText;
                            String nextText2;
                            String nextText3;
                            Class cls;
                            if (newPullParser.getName().equals("iqProvider")) {
                                newPullParser.next();
                                newPullParser.next();
                                nextText = newPullParser.nextText();
                                newPullParser.next();
                                newPullParser.next();
                                nextText2 = newPullParser.nextText();
                                newPullParser.next();
                                newPullParser.next();
                                nextText3 = newPullParser.nextText();
                                nextText = getProviderKey(nextText, nextText2);
                                if (!this.iqProviders.containsKey(nextText)) {
                                    try {
                                        cls = Class.forName(nextText3);
                                        if (IQProvider.class.isAssignableFrom(cls)) {
                                            this.iqProviders.put(nextText, cls.newInstance());
                                        } else if (IQ.class.isAssignableFrom(cls)) {
                                            this.iqProviders.put(nextText, cls);
                                        }
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    } catch (Throwable th) {
                                        try {
                                            inputStream.close();
                                        } catch (Exception e2) {
                                        }
                                    }
                                }
                            } else if (newPullParser.getName().equals("extensionProvider")) {
                                newPullParser.next();
                                newPullParser.next();
                                nextText = newPullParser.nextText();
                                newPullParser.next();
                                newPullParser.next();
                                nextText2 = newPullParser.nextText();
                                newPullParser.next();
                                newPullParser.next();
                                nextText3 = newPullParser.nextText();
                                nextText = getProviderKey(nextText, nextText2);
                                if (!this.extensionProviders.containsKey(nextText)) {
                                    try {
                                        cls = Class.forName(nextText3);
                                        if (PacketExtensionProvider.class.isAssignableFrom(cls)) {
                                            this.extensionProviders.put(nextText, cls.newInstance());
                                        } else if (PacketExtension.class.isAssignableFrom(cls)) {
                                            this.extensionProviders.put(nextText, cls);
                                        }
                                    } catch (ClassNotFoundException e3) {
                                        e3.printStackTrace();
                                    }
                                }
                            }
                        }
                        eventType = newPullParser.next();
                    } while (eventType != 1);
                    try {
                        inputStream.close();
                    } catch (Exception e4) {
                    }
                }
            }
        } catch (Exception e5) {
            e5.printStackTrace();
        }
    }

    public void removeExtensionProvider(String str, String str2) {
        this.extensionProviders.remove(getProviderKey(str, str2));
    }

    public void removeIQProvider(String str, String str2) {
        this.iqProviders.remove(getProviderKey(str, str2));
    }
}
