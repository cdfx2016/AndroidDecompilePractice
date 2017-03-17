package org.jivesoftware.smackx.entitycaps.cache;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.Base32Encoder;
import org.jivesoftware.smack.util.StringEncoder;
import org.jivesoftware.smackx.entitycaps.EntityCapsManager;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class SimpleDirectoryPersistentCache implements EntityCapsPersistentCache {
    private File cacheDir;
    private StringEncoder filenameEncoder;

    public SimpleDirectoryPersistentCache(File file) {
        this(file, Base32Encoder.getInstance());
    }

    public SimpleDirectoryPersistentCache(File file, StringEncoder stringEncoder) {
        if (!file.exists()) {
            throw new IllegalStateException("Cache directory \"" + file + "\" does not exist");
        } else if (file.isDirectory()) {
            this.cacheDir = file;
            this.filenameEncoder = stringEncoder;
        } else {
            throw new IllegalStateException("Cache directory \"" + file + "\" is not a directory");
        }
    }

    private static DiscoverInfo restoreInfoFromFile(File file) throws IOException {
        String fileInputStream = new FileInputStream(file);
        DataInputStream dataInputStream = new DataInputStream(fileInputStream);
        try {
            fileInputStream = dataInputStream.readUTF();
            if (fileInputStream == null) {
                return null;
            }
            Reader stringReader = new StringReader(fileInputStream);
            try {
                XmlPullParser newPullParser = XmlPullParserFactory.newInstance().newPullParser();
                newPullParser.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", true);
                newPullParser.setInput(stringReader);
                IQProvider discoverInfoProvider = new DiscoverInfoProvider();
                try {
                    newPullParser.next();
                    String attributeValue = newPullParser.getAttributeValue("", "id");
                    String attributeValue2 = newPullParser.getAttributeValue("", "from");
                    String attributeValue3 = newPullParser.getAttributeValue("", "to");
                    newPullParser.next();
                    try {
                        DiscoverInfo discoverInfo = (DiscoverInfo) discoverInfoProvider.parseIQ(newPullParser);
                        discoverInfo.setPacketID(attributeValue);
                        discoverInfo.setFrom(attributeValue2);
                        discoverInfo.setTo(attributeValue3);
                        discoverInfo.setType(Type.RESULT);
                        return discoverInfo;
                    } catch (Exception e) {
                        return null;
                    }
                } catch (XmlPullParserException e2) {
                    return null;
                }
            } catch (XmlPullParserException e3) {
                e3.printStackTrace();
                return null;
            }
        } finally {
            dataInputStream.close();
        }
    }

    private static void writeInfoToFile(File file, DiscoverInfo discoverInfo) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));
        try {
            dataOutputStream.writeUTF(discoverInfo.toXML());
        } finally {
            dataOutputStream.close();
        }
    }

    public void addDiscoverInfoByNodePersistent(String str, DiscoverInfo discoverInfo) {
        File file = new File(this.cacheDir, this.filenameEncoder.encode(str));
        try {
            if (file.createNewFile()) {
                writeInfoToFile(file, discoverInfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void emptyCache() {
        for (File delete : this.cacheDir.listFiles()) {
            delete.delete();
        }
    }

    public void replay() throws IOException {
        for (File file : this.cacheDir.listFiles()) {
            String decode = this.filenameEncoder.decode(file.getName());
            DiscoverInfo restoreInfoFromFile = restoreInfoFromFile(file);
            if (restoreInfoFromFile != null) {
                EntityCapsManager.addDiscoverInfoByNode(decode, restoreInfoFromFile);
            }
        }
    }
}
