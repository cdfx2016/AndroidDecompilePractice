package de.measite.smack;

import android.util.Log;
import java.io.Reader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.debugger.SmackDebugger;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.ObservableReader;
import org.jivesoftware.smack.util.ObservableWriter;
import org.jivesoftware.smack.util.ReaderListener;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.WriterListener;

public class AndroidDebugger implements SmackDebugger {
    public static boolean printInterpreted = false;
    private ConnectionListener connListener = null;
    private Connection connection = null;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("hh:mm:ss aaa");
    private PacketListener listener = null;
    private Reader reader;
    private ReaderListener readerListener;
    private Writer writer;
    private WriterListener writerListener;

    public AndroidDebugger(Connection connection, Writer writer, Reader reader) {
        this.connection = connection;
        this.writer = writer;
        this.reader = reader;
        createDebug();
    }

    private void createDebug() {
        Reader observableReader = new ObservableReader(this.reader);
        this.readerListener = new ReaderListener() {
            public void read(String str) {
                Log.d("SMACK", AndroidDebugger.this.dateFormatter.format(new Date()) + " RCV  (" + AndroidDebugger.this.connection.hashCode() + "): " + str);
            }
        };
        observableReader.addReaderListener(this.readerListener);
        Writer observableWriter = new ObservableWriter(this.writer);
        this.writerListener = new WriterListener() {
            public void write(String str) {
                Log.d("SMACK", AndroidDebugger.this.dateFormatter.format(new Date()) + " SENT (" + AndroidDebugger.this.connection.hashCode() + "): " + str);
            }
        };
        observableWriter.addWriterListener(this.writerListener);
        this.reader = observableReader;
        this.writer = observableWriter;
        this.listener = new PacketListener() {
            public void processPacket(Packet packet) {
                if (AndroidDebugger.printInterpreted) {
                    Log.d("SMACK", AndroidDebugger.this.dateFormatter.format(new Date()) + " RCV PKT (" + AndroidDebugger.this.connection.hashCode() + "): " + packet.toXML());
                }
            }
        };
        this.connListener = new ConnectionListener() {
            public void connectionClosed() {
                Log.d("SMACK", AndroidDebugger.this.dateFormatter.format(new Date()) + " Connection closed (" + AndroidDebugger.this.connection.hashCode() + ")");
            }

            public void connectionClosedOnError(Exception exception) {
                Log.d("SMACK", AndroidDebugger.this.dateFormatter.format(new Date()) + " Connection closed due to an exception (" + AndroidDebugger.this.connection.hashCode() + ")");
                exception.printStackTrace();
            }

            public void reconnectingIn(int i) {
                Log.d("SMACK", AndroidDebugger.this.dateFormatter.format(new Date()) + " Connection (" + AndroidDebugger.this.connection.hashCode() + ") will reconnect in " + i);
            }

            public void reconnectionFailed(Exception exception) {
                Log.d("SMACK", AndroidDebugger.this.dateFormatter.format(new Date()) + " Reconnection failed due to an exception (" + AndroidDebugger.this.connection.hashCode() + ")");
                exception.printStackTrace();
            }

            public void reconnectionSuccessful() {
                Log.d("SMACK", AndroidDebugger.this.dateFormatter.format(new Date()) + " Connection reconnected (" + AndroidDebugger.this.connection.hashCode() + ")");
            }
        };
    }

    public Reader getReader() {
        return this.reader;
    }

    public PacketListener getReaderListener() {
        return this.listener;
    }

    public Writer getWriter() {
        return this.writer;
    }

    public PacketListener getWriterListener() {
        return null;
    }

    public Reader newConnectionReader(Reader reader) {
        ((ObservableReader) this.reader).removeReaderListener(this.readerListener);
        Reader observableReader = new ObservableReader(reader);
        observableReader.addReaderListener(this.readerListener);
        this.reader = observableReader;
        return this.reader;
    }

    public Writer newConnectionWriter(Writer writer) {
        ((ObservableWriter) this.writer).removeWriterListener(this.writerListener);
        Writer observableWriter = new ObservableWriter(writer);
        observableWriter.addWriterListener(this.writerListener);
        this.writer = observableWriter;
        return this.writer;
    }

    public void userHasLogged(String str) {
        Log.d("SMACK", ("User logged (" + this.connection.hashCode() + "): " + ("".equals(StringUtils.parseName(str)) ? "" : StringUtils.parseBareAddress(str)) + "@" + this.connection.getServiceName() + ":" + this.connection.getPort()) + "/" + StringUtils.parseResource(str));
        this.connection.addConnectionListener(this.connListener);
    }
}
