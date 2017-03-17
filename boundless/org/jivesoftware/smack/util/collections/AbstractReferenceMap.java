package org.jivesoftware.smack.util.collections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

public abstract class AbstractReferenceMap<K, V> extends AbstractHashedMap<K, V> {
    public static final int HARD = 0;
    public static final int SOFT = 1;
    public static final int WEAK = 2;
    protected int keyType;
    protected boolean purgeValues;
    private transient ReferenceQueue queue;
    protected int valueType;

    protected static class ReferenceEntry<K, V> extends HashEntry<K, V> {
        protected final AbstractReferenceMap<K, V> parent;
        protected Reference<K> refKey;
        protected Reference<V> refValue;

        public ReferenceEntry(AbstractReferenceMap<K, V> abstractReferenceMap, ReferenceEntry<K, V> referenceEntry, int i, K k, V v) {
            super(referenceEntry, i, null, null);
            this.parent = abstractReferenceMap;
            if (abstractReferenceMap.keyType != 0) {
                this.refKey = toReference(abstractReferenceMap.keyType, k, i);
            } else {
                setKey(k);
            }
            if (abstractReferenceMap.valueType != 0) {
                this.refValue = toReference(abstractReferenceMap.valueType, v, i);
            } else {
                setValue(v);
            }
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Entry)) {
                return false;
            }
            Entry entry = (Entry) obj;
            Object key = entry.getKey();
            Object value = entry.getValue();
            return (key == null || value == null) ? false : this.parent.isEqualKey(key, getKey()) && this.parent.isEqualValue(value, getValue());
        }

        public K getKey() {
            return this.parent.keyType > 0 ? this.refKey.get() : super.getKey();
        }

        public V getValue() {
            return this.parent.valueType > 0 ? this.refValue.get() : super.getValue();
        }

        public int hashCode() {
            return this.parent.hashEntry(getKey(), getValue());
        }

        protected ReferenceEntry<K, V> next() {
            return (ReferenceEntry) this.next;
        }

        boolean purge(Reference reference) {
            boolean z = false;
            boolean z2 = this.parent.keyType > 0 && this.refKey == reference;
            if (z2 || (this.parent.valueType > 0 && this.refValue == reference)) {
                z = true;
            }
            if (z) {
                if (this.parent.keyType > 0) {
                    this.refKey.clear();
                }
                if (this.parent.valueType > 0) {
                    this.refValue.clear();
                } else if (this.parent.purgeValues) {
                    setValue(null);
                }
            }
            return z;
        }

        public V setValue(V v) {
            V value = getValue();
            if (this.parent.valueType > 0) {
                this.refValue.clear();
                this.refValue = toReference(this.parent.valueType, v, this.hashCode);
            } else {
                super.setValue(v);
            }
            return value;
        }

        protected <T> Reference<T> toReference(int i, T t, int i2) {
            switch (i) {
                case 1:
                    return new SoftRef(i2, t, this.parent.queue);
                case 2:
                    return new WeakRef(i2, t, this.parent.queue);
                default:
                    throw new Error("Attempt to create hard reference in ReferenceMap!");
            }
        }
    }

    static class ReferenceEntrySet<K, V> extends EntrySet<K, V> {
        protected ReferenceEntrySet(AbstractHashedMap<K, V> abstractHashedMap) {
            super(abstractHashedMap);
        }

        public Object[] toArray() {
            return toArray(new Object[0]);
        }

        public <T> T[] toArray(T[] tArr) {
            ArrayList arrayList = new ArrayList();
            Iterator it = iterator();
            while (it.hasNext()) {
                Entry entry = (Entry) it.next();
                arrayList.add(new DefaultMapEntry(entry.getKey(), entry.getValue()));
            }
            return arrayList.toArray(tArr);
        }
    }

    static class ReferenceIteratorBase<K, V> {
        K currentKey;
        V currentValue;
        ReferenceEntry<K, V> entry;
        int expectedModCount;
        int index;
        K nextKey;
        V nextValue;
        final AbstractReferenceMap<K, V> parent;
        ReferenceEntry<K, V> previous;

        public ReferenceIteratorBase(AbstractReferenceMap<K, V> abstractReferenceMap) {
            this.parent = abstractReferenceMap;
            this.index = abstractReferenceMap.size() != 0 ? abstractReferenceMap.data.length : 0;
            this.expectedModCount = abstractReferenceMap.modCount;
        }

        private void checkMod() {
            if (this.parent.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }

        private boolean nextNull() {
            return this.nextKey == null || this.nextValue == null;
        }

        protected ReferenceEntry<K, V> currentEntry() {
            checkMod();
            return this.previous;
        }

        public boolean hasNext() {
            checkMod();
            while (nextNull()) {
                ReferenceEntry referenceEntry = this.entry;
                int i = this.index;
                while (referenceEntry == null && i > 0) {
                    int i2 = i - 1;
                    int i3 = i2;
                    referenceEntry = (ReferenceEntry) this.parent.data[i2];
                    i = i3;
                }
                this.entry = referenceEntry;
                this.index = i;
                if (referenceEntry == null) {
                    this.currentKey = null;
                    this.currentValue = null;
                    return false;
                }
                this.nextKey = referenceEntry.getKey();
                this.nextValue = referenceEntry.getValue();
                if (nextNull()) {
                    this.entry = this.entry.next();
                }
            }
            return true;
        }

        protected ReferenceEntry<K, V> nextEntry() {
            checkMod();
            if (!nextNull() || hasNext()) {
                this.previous = this.entry;
                this.entry = this.entry.next();
                this.currentKey = this.nextKey;
                this.currentValue = this.nextValue;
                this.nextKey = null;
                this.nextValue = null;
                return this.previous;
            }
            throw new NoSuchElementException();
        }

        public void remove() {
            checkMod();
            if (this.previous == null) {
                throw new IllegalStateException();
            }
            this.parent.remove(this.currentKey);
            this.previous = null;
            this.currentKey = null;
            this.currentValue = null;
            this.expectedModCount = this.parent.modCount;
        }

        public ReferenceEntry<K, V> superNext() {
            return nextEntry();
        }
    }

    static class ReferenceEntrySetIterator<K, V> extends ReferenceIteratorBase<K, V> implements Iterator<Entry<K, V>> {
        public ReferenceEntrySetIterator(AbstractReferenceMap<K, V> abstractReferenceMap) {
            super(abstractReferenceMap);
        }

        public ReferenceEntry<K, V> next() {
            return superNext();
        }
    }

    static class ReferenceKeySet<K, V> extends KeySet<K, V> {
        protected ReferenceKeySet(AbstractHashedMap<K, V> abstractHashedMap) {
            super(abstractHashedMap);
        }

        public Object[] toArray() {
            return toArray(new Object[0]);
        }

        public <T> T[] toArray(T[] tArr) {
            List arrayList = new ArrayList(this.parent.size());
            Iterator it = iterator();
            while (it.hasNext()) {
                arrayList.add(it.next());
            }
            return arrayList.toArray(tArr);
        }
    }

    static class ReferenceKeySetIterator<K, V> extends ReferenceIteratorBase<K, V> implements Iterator<K> {
        ReferenceKeySetIterator(AbstractReferenceMap<K, V> abstractReferenceMap) {
            super(abstractReferenceMap);
        }

        public K next() {
            return nextEntry().getKey();
        }
    }

    static class ReferenceMapIterator<K, V> extends ReferenceIteratorBase<K, V> implements MapIterator<K, V> {
        protected ReferenceMapIterator(AbstractReferenceMap<K, V> abstractReferenceMap) {
            super(abstractReferenceMap);
        }

        public K getKey() {
            HashEntry currentEntry = currentEntry();
            if (currentEntry != null) {
                return currentEntry.getKey();
            }
            throw new IllegalStateException("getKey() can only be called after next() and before remove()");
        }

        public V getValue() {
            HashEntry currentEntry = currentEntry();
            if (currentEntry != null) {
                return currentEntry.getValue();
            }
            throw new IllegalStateException("getValue() can only be called after next() and before remove()");
        }

        public K next() {
            return nextEntry().getKey();
        }

        public V setValue(V v) {
            HashEntry currentEntry = currentEntry();
            if (currentEntry != null) {
                return currentEntry.setValue(v);
            }
            throw new IllegalStateException("setValue() can only be called after next() and before remove()");
        }
    }

    static class ReferenceValues<K, V> extends Values<K, V> {
        protected ReferenceValues(AbstractHashedMap<K, V> abstractHashedMap) {
            super(abstractHashedMap);
        }

        public Object[] toArray() {
            return toArray(new Object[0]);
        }

        public <T> T[] toArray(T[] tArr) {
            List arrayList = new ArrayList(this.parent.size());
            Iterator it = iterator();
            while (it.hasNext()) {
                arrayList.add(it.next());
            }
            return arrayList.toArray(tArr);
        }
    }

    static class ReferenceValuesIterator<K, V> extends ReferenceIteratorBase<K, V> implements Iterator<V> {
        ReferenceValuesIterator(AbstractReferenceMap<K, V> abstractReferenceMap) {
            super(abstractReferenceMap);
        }

        public V next() {
            return nextEntry().getValue();
        }
    }

    static class SoftRef<T> extends SoftReference<T> {
        private int hash;

        public SoftRef(int i, T t, ReferenceQueue referenceQueue) {
            super(t, referenceQueue);
            this.hash = i;
        }

        public int hashCode() {
            return this.hash;
        }
    }

    static class WeakRef<T> extends WeakReference<T> {
        private int hash;

        public WeakRef(int i, T t, ReferenceQueue referenceQueue) {
            super(t, referenceQueue);
            this.hash = i;
        }

        public int hashCode() {
            return this.hash;
        }
    }

    protected AbstractReferenceMap() {
    }

    protected AbstractReferenceMap(int i, int i2, int i3, float f, boolean z) {
        super(i3, f);
        verify("keyType", i);
        verify("valueType", i2);
        this.keyType = i;
        this.valueType = i2;
        this.purgeValues = z;
    }

    private static void verify(String str, int i) {
        if (i < 0 || i > 2) {
            throw new IllegalArgumentException(str + " must be HARD, SOFT, WEAK.");
        }
    }

    public void clear() {
        super.clear();
        do {
        } while (this.queue.poll() != null);
    }

    public boolean containsKey(Object obj) {
        purgeBeforeRead();
        Entry entry = getEntry(obj);
        return (entry == null || entry.getValue() == null) ? false : true;
    }

    public boolean containsValue(Object obj) {
        purgeBeforeRead();
        return obj == null ? false : super.containsValue(obj);
    }

    public HashEntry<K, V> createEntry(HashEntry<K, V> hashEntry, int i, K k, V v) {
        return new ReferenceEntry(this, (ReferenceEntry) hashEntry, i, k, v);
    }

    protected Iterator<Entry<K, V>> createEntrySetIterator() {
        return new ReferenceEntrySetIterator(this);
    }

    protected Iterator<K> createKeySetIterator() {
        return new ReferenceKeySetIterator(this);
    }

    protected Iterator<V> createValuesIterator() {
        return new ReferenceValuesIterator(this);
    }

    protected void doReadObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        this.keyType = objectInputStream.readInt();
        this.valueType = objectInputStream.readInt();
        this.purgeValues = objectInputStream.readBoolean();
        this.loadFactor = objectInputStream.readFloat();
        int readInt = objectInputStream.readInt();
        init();
        this.data = new HashEntry[readInt];
        while (true) {
            Object readObject = objectInputStream.readObject();
            if (readObject == null) {
                this.threshold = calculateThreshold(this.data.length, this.loadFactor);
                return;
            }
            put(readObject, objectInputStream.readObject());
        }
    }

    protected void doWriteObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeInt(this.keyType);
        objectOutputStream.writeInt(this.valueType);
        objectOutputStream.writeBoolean(this.purgeValues);
        objectOutputStream.writeFloat(this.loadFactor);
        objectOutputStream.writeInt(this.data.length);
        MapIterator mapIterator = mapIterator();
        while (mapIterator.hasNext()) {
            objectOutputStream.writeObject(mapIterator.next());
            objectOutputStream.writeObject(mapIterator.getValue());
        }
        objectOutputStream.writeObject(null);
    }

    public Set<Entry<K, V>> entrySet() {
        if (this.entrySet == null) {
            this.entrySet = new ReferenceEntrySet(this);
        }
        return this.entrySet;
    }

    public V get(Object obj) {
        purgeBeforeRead();
        Entry entry = getEntry(obj);
        return entry == null ? null : entry.getValue();
    }

    protected HashEntry<K, V> getEntry(Object obj) {
        return obj == null ? null : super.getEntry(obj);
    }

    protected int hashEntry(Object obj, Object obj2) {
        int i = 0;
        int hashCode = obj == null ? 0 : obj.hashCode();
        if (obj2 != null) {
            i = obj2.hashCode();
        }
        return i ^ hashCode;
    }

    protected void init() {
        this.queue = new ReferenceQueue();
    }

    public boolean isEmpty() {
        purgeBeforeRead();
        return super.isEmpty();
    }

    protected boolean isEqualKey(Object obj, Object obj2) {
        return obj == obj2 || obj.equals(obj2);
    }

    public Set<K> keySet() {
        if (this.keySet == null) {
            this.keySet = new ReferenceKeySet(this);
        }
        return this.keySet;
    }

    public MapIterator<K, V> mapIterator() {
        return new ReferenceMapIterator(this);
    }

    protected void purge() {
        Reference poll = this.queue.poll();
        while (poll != null) {
            purge(poll);
            poll = this.queue.poll();
        }
    }

    protected void purge(Reference reference) {
        int hashIndex = hashIndex(reference.hashCode(), this.data.length);
        HashEntry hashEntry = null;
        for (HashEntry hashEntry2 = this.data[hashIndex]; hashEntry2 != null; hashEntry2 = hashEntry2.next) {
            if (((ReferenceEntry) hashEntry2).purge(reference)) {
                if (hashEntry == null) {
                    this.data[hashIndex] = hashEntry2.next;
                } else {
                    hashEntry.next = hashEntry2.next;
                }
                this.size--;
                return;
            }
            hashEntry = hashEntry2;
        }
    }

    protected void purgeBeforeRead() {
        purge();
    }

    protected void purgeBeforeWrite() {
        purge();
    }

    public V put(K k, V v) {
        if (k == null) {
            throw new NullPointerException("null keys not allowed");
        } else if (v == null) {
            throw new NullPointerException("null values not allowed");
        } else {
            purgeBeforeWrite();
            return super.put(k, v);
        }
    }

    public V remove(Object obj) {
        if (obj == null) {
            return null;
        }
        purgeBeforeWrite();
        return super.remove(obj);
    }

    public int size() {
        purgeBeforeRead();
        return super.size();
    }

    public Collection<V> values() {
        if (this.values == null) {
            this.values = new ReferenceValues(this);
        }
        return this.values;
    }
}
