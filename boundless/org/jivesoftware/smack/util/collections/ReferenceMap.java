package org.jivesoftware.smack.util.collections;

import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ReferenceMap<K, V> extends AbstractReferenceMap<K, V> implements Serializable {
    private static final long serialVersionUID = 1555089888138299607L;

    public ReferenceMap() {
        super(0, 1, 16, AdaptiveVideoTrackSelection.DEFAULT_BANDWIDTH_FRACTION, false);
    }

    public ReferenceMap(int i, int i2) {
        super(i, i2, 16, AdaptiveVideoTrackSelection.DEFAULT_BANDWIDTH_FRACTION, false);
    }

    public ReferenceMap(int i, int i2, int i3, float f) {
        super(i, i2, i3, f, false);
    }

    public ReferenceMap(int i, int i2, int i3, float f, boolean z) {
        super(i, i2, i3, f, z);
    }

    public ReferenceMap(int i, int i2, boolean z) {
        super(i, i2, 16, AdaptiveVideoTrackSelection.DEFAULT_BANDWIDTH_FRACTION, z);
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        doReadObject(objectInputStream);
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        doWriteObject(objectOutputStream);
    }
}
