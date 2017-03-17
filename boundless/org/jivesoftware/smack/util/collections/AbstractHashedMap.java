package org.jivesoftware.smack.util.collections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

public class AbstractHashedMap<K, V> extends AbstractMap<K, V> implements IterableMap<K, V> {
    protected static final int DEFAULT_CAPACITY = 16;
    protected static final float DEFAULT_LOAD_FACTOR = 0.75f;
    protected static final int DEFAULT_THRESHOLD = 12;
    protected static final String GETKEY_INVALID = "getKey() can only be called after next() and before remove()";
    protected static final String GETVALUE_INVALID = "getValue() can only be called after next() and before remove()";
    protected static final int MAXIMUM_CAPACITY = 1073741824;
    protected static final String NO_NEXT_ENTRY = "No next() entry in the iteration";
    protected static final String NO_PREVIOUS_ENTRY = "No previous() entry in the iteration";
    protected static final Object NULL = new Object();
    protected static final String REMOVE_INVALID = "remove() can only be called once after next()";
    protected static final String SETVALUE_INVALID = "setValue() can only be called after next() and before remove()";
    protected transient HashEntry<K, V>[] data;
    protected transient EntrySet<K, V> entrySet;
    protected transient KeySet<K, V> keySet;
    protected transient float loadFactor;
    protected transient int modCount;
    protected transient int size;
    protected transient int threshold;
    protected transient Values<K, V> values;

    protected static class EntrySet<K, V> extends AbstractSet<Entry<K, V>> {
        protected final AbstractHashedMap<K, V> parent;

        protected EntrySet(AbstractHashedMap<K, V> abstractHashedMap) {
            this.parent = abstractHashedMap;
        }

        public void clear() {
            this.parent.clear();
        }

        public boolean contains(Entry<K, V> entry) {
            HashEntry entry2 = this.parent.getEntry(entry.getKey());
            return entry2 != null && entry2.equals(entry);
        }

        public Iterator<Entry<K, V>> iterator() {
            return this.parent.createEntrySetIterator();
        }

        public boolean remove(Object obj) {
            if (!(obj instanceof Entry) || !contains(obj)) {
                return false;
            }
            this.parent.remove(((Entry) obj).getKey());
            return true;
        }

        public int size() {
            return this.parent.size();
        }
    }

    protected static abstract class HashIterator<K, V> {
        protected int expectedModCount;
        protected int hashIndex;
        protected HashEntry<K, V> last;
        protected HashEntry<K, V> next;
        protected final AbstractHashedMap parent;

        protected HashIterator(AbstractHashedMap<K, V> abstractHashedMap) {
            this.parent = abstractHashedMap;
            HashEntry[] hashEntryArr = abstractHashedMap.data;
            int length = hashEntryArr.length;
            HashEntry hashEntry = null;
            while (length > 0 && hashEntry == null) {
                length--;
                hashEntry = hashEntryArr[length];
            }
            this.next = hashEntry;
            this.hashIndex = length;
            this.expectedModCount = abstractHashedMap.modCount;
        }

        protected HashEntry<K, V> currentEntry() {
            return this.last;
        }

        public boolean hasNext() {
            return this.next != null;
        }

        protected HashEntry<K, V> nextEntry() {
            if (this.parent.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            HashEntry<K, V> hashEntry = this.next;
            if (hashEntry == null) {
                throw new NoSuchElementException(AbstractHashedMap.NO_NEXT_ENTRY);
            }
            HashEntry[] hashEntryArr = this.parent.data;
            int i = this.hashIndex;
            HashEntry hashEntry2 = hashEntry.next;
            while (hashEntry2 == null && i > 0) {
                i--;
                hashEntry2 = hashEntryArr[i];
            }
            this.next = hashEntry2;
            this.hashIndex = i;
            this.last = hashEntry;
            return hashEntry;
        }

        public void remove() {
            if (this.last == null) {
                throw new IllegalStateException(AbstractHashedMap.REMOVE_INVALID);
            } else if (this.parent.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            } else {
                this.parent.remove(this.last.getKey());
                this.last = null;
                this.expectedModCount = this.parent.modCount;
            }
        }

        public String toString() {
            return this.last != null ? "Iterator[" + this.last.getKey() + "=" + this.last.getValue() + "]" : "Iterator[]";
        }
    }

    protected static class EntrySetIterator<K, V> extends HashIterator<K, V> implements Iterator<Entry<K, V>> {
        protected EntrySetIterator(AbstractHashedMap<K, V> abstractHashedMap) {
            super(abstractHashedMap);
        }

        public HashEntry<K, V> next() {
            return super.nextEntry();
        }
    }

    protected static class HashEntry<K, V> implements Entry<K, V>, KeyValue<K, V> {
        protected int hashCode;
        private K key;
        protected HashEntry<K, V> next;
        private V value;

        protected HashEntry(HashEntry<K, V> hashEntry, int i, K k, V v) {
            this.next = hashEntry;
            this.hashCode = i;
            this.key = k;
            this.value = v;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean equals(java.lang.Object r5) {
            /*
            r4 = this;
            r0 = 1;
            r1 = 0;
            if (r5 != r4) goto L_0x0005;
        L_0x0004:
            return r0;
        L_0x0005:
            r2 = r5 instanceof java.util.Map.Entry;
            if (r2 != 0) goto L_0x000b;
        L_0x0009:
            r0 = r1;
            goto L_0x0004;
        L_0x000b:
            r5 = (java.util.Map.Entry) r5;
            r2 = r4.getKey();
            if (r2 != 0) goto L_0x0027;
        L_0x0013:
            r2 = r5.getKey();
            if (r2 != 0) goto L_0x0025;
        L_0x0019:
            r2 = r4.getValue();
            if (r2 != 0) goto L_0x0036;
        L_0x001f:
            r2 = r5.getValue();
            if (r2 == 0) goto L_0x0004;
        L_0x0025:
            r0 = r1;
            goto L_0x0004;
        L_0x0027:
            r2 = r4.getKey();
            r3 = r5.getKey();
            r2 = r2.equals(r3);
            if (r2 == 0) goto L_0x0025;
        L_0x0035:
            goto L_0x0019;
        L_0x0036:
            r2 = r4.getValue();
            r3 = r5.getValue();
            r2 = r2.equals(r3);
            if (r2 == 0) goto L_0x0025;
        L_0x0044:
            goto L_0x0004;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smack.util.collections.AbstractHashedMap.HashEntry.equals(java.lang.Object):boolean");
        }

        public K getKey() {
            return this.key;
        }

        public V getValue() {
            return this.value;
        }

        public int hashCode() {
            int i = 0;
            int hashCode = getKey() == null ? 0 : getKey().hashCode();
            if (getValue() != null) {
                i = getValue().hashCode();
            }
            return hashCode ^ i;
        }

        public void setKey(K k) {
            this.key = k;
        }

        public V setValue(V v) {
            V v2 = this.value;
            this.value = v;
            return v2;
        }

        public String toString() {
            return getKey() + '=' + getValue();
        }
    }

    protected static class HashMapIterator<K, V> extends HashIterator<K, V> implements MapIterator<K, V> {
        protected HashMapIterator(AbstractHashedMap<K, V> abstractHashedMap) {
            super(abstractHashedMap);
        }

        public K getKey() {
            HashEntry currentEntry = currentEntry();
            if (currentEntry != null) {
                return currentEntry.getKey();
            }
            throw new IllegalStateException(AbstractHashedMap.GETKEY_INVALID);
        }

        public V getValue() {
            HashEntry currentEntry = currentEntry();
            if (currentEntry != null) {
                return currentEntry.getValue();
            }
            throw new IllegalStateException(AbstractHashedMap.GETVALUE_INVALID);
        }

        public K next() {
            return super.nextEntry().getKey();
        }

        public V setValue(V v) {
            HashEntry currentEntry = currentEntry();
            if (currentEntry != null) {
                return currentEntry.setValue(v);
            }
            throw new IllegalStateException(AbstractHashedMap.SETVALUE_INVALID);
        }
    }

    protected static class KeySet<K, V> extends AbstractSet<K> {
        protected final AbstractHashedMap<K, V> parent;

        protected KeySet(AbstractHashedMap<K, V> abstractHashedMap) {
            this.parent = abstractHashedMap;
        }

        public void clear() {
            this.parent.clear();
        }

        public boolean contains(Object obj) {
            return this.parent.containsKey(obj);
        }

        public Iterator<K> iterator() {
            return this.parent.createKeySetIterator();
        }

        public boolean remove(Object obj) {
            boolean containsKey = this.parent.containsKey(obj);
            this.parent.remove(obj);
            return containsKey;
        }

        public int size() {
            return this.parent.size();
        }
    }

    protected static class KeySetIterator<K, V> extends HashIterator<K, V> implements Iterator<K> {
        protected KeySetIterator(AbstractHashedMap<K, V> abstractHashedMap) {
            super(abstractHashedMap);
        }

        public K next() {
            return super.nextEntry().getKey();
        }
    }

    protected static class Values<K, V> extends AbstractCollection<V> {
        protected final AbstractHashedMap<K, V> parent;

        protected Values(AbstractHashedMap<K, V> abstractHashedMap) {
            this.parent = abstractHashedMap;
        }

        public void clear() {
            this.parent.clear();
        }

        public boolean contains(Object obj) {
            return this.parent.containsValue(obj);
        }

        public Iterator<V> iterator() {
            return this.parent.createValuesIterator();
        }

        public int size() {
            return this.parent.size();
        }
    }

    protected static class ValuesIterator<K, V> extends HashIterator<K, V> implements Iterator<V> {
        protected ValuesIterator(AbstractHashedMap<K, V> abstractHashedMap) {
            super(abstractHashedMap);
        }

        public V next() {
            return super.nextEntry().getValue();
        }
    }

    protected AbstractHashedMap() {
    }

    protected AbstractHashedMap(int i) {
        this(i, 0.75f);
    }

    protected AbstractHashedMap(int i, float f) {
        if (i < 1) {
            throw new IllegalArgumentException("Initial capacity must be greater than 0");
        } else if (f <= 0.0f || Float.isNaN(f)) {
            throw new IllegalArgumentException("Load factor must be greater than 0");
        } else {
            this.loadFactor = f;
            this.threshold = calculateThreshold(i, f);
            this.data = new HashEntry[calculateNewCapacity(i)];
            init();
        }
    }

    protected AbstractHashedMap(int i, float f, int i2) {
        this.loadFactor = f;
        this.data = new HashEntry[i];
        this.threshold = i2;
        init();
    }

    protected AbstractHashedMap(Map<? extends K, ? extends V> map) {
        this(Math.max(map.size() * 2, 16), 0.75f);
        putAll(map);
    }

    protected void addEntry(HashEntry<K, V> hashEntry, int i) {
        this.data[i] = hashEntry;
    }

    protected void addMapping(int i, int i2, K k, V v) {
        this.modCount++;
        addEntry(createEntry(this.data[i], i2, k, v), i);
        this.size++;
        checkCapacity();
    }

    protected int calculateNewCapacity(int i) {
        int i2 = 1;
        if (i > 1073741824) {
            return 1073741824;
        }
        while (i2 < i) {
            i2 <<= 1;
        }
        return i2 <= 1073741824 ? i2 : 1073741824;
    }

    protected int calculateThreshold(int i, float f) {
        return (int) (((float) i) * f);
    }

    protected void checkCapacity() {
        if (this.size >= this.threshold) {
            int length = this.data.length * 2;
            if (length <= 1073741824) {
                ensureCapacity(length);
            }
        }
    }

    public void clear() {
        this.modCount++;
        HashEntry[] hashEntryArr = this.data;
        for (int length = hashEntryArr.length - 1; length >= 0; length--) {
            hashEntryArr[length] = null;
        }
        this.size = 0;
    }

    protected Object clone() {
        try {
            AbstractHashedMap abstractHashedMap = (AbstractHashedMap) super.clone();
            abstractHashedMap.data = new HashEntry[this.data.length];
            abstractHashedMap.entrySet = null;
            abstractHashedMap.keySet = null;
            abstractHashedMap.values = null;
            abstractHashedMap.modCount = 0;
            abstractHashedMap.size = 0;
            abstractHashedMap.init();
            abstractHashedMap.putAll(this);
            return abstractHashedMap;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public boolean containsKey(Object obj) {
        int hash = hash(obj == null ? NULL : obj);
        HashEntry hashEntry = this.data[hashIndex(hash, this.data.length)];
        while (hashEntry != null) {
            if (hashEntry.hashCode == hash && isEqualKey(obj, hashEntry.getKey())) {
                return true;
            }
            hashEntry = hashEntry.next;
        }
        return false;
    }

    public boolean containsValue(Object obj) {
        HashEntry hashEntry;
        if (obj == null) {
            for (HashEntry hashEntry2 : this.data) {
                for (hashEntry2 = this.data[r3]; hashEntry2 != null; hashEntry2 = hashEntry2.next) {
                    if (hashEntry2.getValue() == null) {
                        return true;
                    }
                }
            }
        } else {
            for (HashEntry hashEntry22 : this.data) {
                for (hashEntry22 = this.data[r3]; hashEntry22 != null; hashEntry22 = hashEntry22.next) {
                    if (isEqualValue(obj, hashEntry22.getValue())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected HashEntry<K, V> createEntry(HashEntry<K, V> hashEntry, int i, K k, V v) {
        return new HashEntry(hashEntry, i, k, v);
    }

    protected Iterator<Entry<K, V>> createEntrySetIterator() {
        return size() == 0 ? EmptyIterator.INSTANCE : new EntrySetIterator(this);
    }

    protected Iterator<K> createKeySetIterator() {
        return size() == 0 ? EmptyIterator.INSTANCE : new KeySetIterator(this);
    }

    protected Iterator<V> createValuesIterator() {
        return size() == 0 ? EmptyIterator.INSTANCE : new ValuesIterator(this);
    }

    protected void destroyEntry(HashEntry<K, V> hashEntry) {
        hashEntry.next = null;
        hashEntry.key = null;
        hashEntry.value = null;
    }

    protected void doReadObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        this.loadFactor = objectInputStream.readFloat();
        int readInt = objectInputStream.readInt();
        int readInt2 = objectInputStream.readInt();
        init();
        this.data = new HashEntry[readInt];
        for (readInt = 0; readInt < readInt2; readInt++) {
            put(objectInputStream.readObject(), objectInputStream.readObject());
        }
        this.threshold = calculateThreshold(this.data.length, this.loadFactor);
    }

    protected void doWriteObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeFloat(this.loadFactor);
        objectOutputStream.writeInt(this.data.length);
        objectOutputStream.writeInt(this.size);
        MapIterator mapIterator = mapIterator();
        while (mapIterator.hasNext()) {
            objectOutputStream.writeObject(mapIterator.next());
            objectOutputStream.writeObject(mapIterator.getValue());
        }
    }

    protected void ensureCapacity(int i) {
        int length = this.data.length;
        if (i > length) {
            if (this.size == 0) {
                this.threshold = calculateThreshold(i, this.loadFactor);
                this.data = new HashEntry[i];
                return;
            }
            HashEntry[] hashEntryArr = this.data;
            HashEntry[] hashEntryArr2 = new HashEntry[i];
            this.modCount++;
            for (int i2 = length - 1; i2 >= 0; i2--) {
                HashEntry hashEntry = hashEntryArr[i2];
                if (hashEntry != null) {
                    hashEntryArr[i2] = null;
                    while (true) {
                        HashEntry hashEntry2 = hashEntry.next;
                        int hashIndex = hashIndex(hashEntry.hashCode, i);
                        hashEntry.next = hashEntryArr2[hashIndex];
                        hashEntryArr2[hashIndex] = hashEntry;
                        if (hashEntry2 == null) {
                            break;
                        }
                        hashEntry = hashEntry2;
                    }
                }
            }
            this.threshold = calculateThreshold(i, this.loadFactor);
            this.data = hashEntryArr2;
        }
    }

    protected int entryHashCode(HashEntry<K, V> hashEntry) {
        return hashEntry.hashCode;
    }

    protected K entryKey(HashEntry<K, V> hashEntry) {
        return hashEntry.key;
    }

    protected HashEntry<K, V> entryNext(HashEntry<K, V> hashEntry) {
        return hashEntry.next;
    }

    public Set<Entry<K, V>> entrySet() {
        if (this.entrySet == null) {
            this.entrySet = new EntrySet(this);
        }
        return this.entrySet;
    }

    protected V entryValue(HashEntry<K, V> hashEntry) {
        return hashEntry.value;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Map)) {
            return false;
        }
        Map map = (Map) obj;
        if (map.size() != size()) {
            return false;
        }
        MapIterator mapIterator = mapIterator();
        while (mapIterator.hasNext()) {
            try {
                Object next = mapIterator.next();
                Object value = mapIterator.getValue();
                if (value == null) {
                    if (map.get(next) != null || !map.containsKey(next)) {
                        return false;
                    }
                } else if (!value.equals(map.get(next))) {
                    return false;
                }
            } catch (ClassCastException e) {
                return false;
            } catch (NullPointerException e2) {
                return false;
            }
        }
        return true;
    }

    public V get(Object obj) {
        int hash = hash(obj == null ? NULL : obj);
        HashEntry hashEntry = this.data[hashIndex(hash, this.data.length)];
        while (hashEntry != null) {
            if (hashEntry.hashCode == hash && isEqualKey(obj, hashEntry.key)) {
                return hashEntry.getValue();
            }
            hashEntry = hashEntry.next;
        }
        return null;
    }

    protected HashEntry<K, V> getEntry(Object obj) {
        int hash = hash(obj == null ? NULL : obj);
        HashEntry<K, V> hashEntry = this.data[hashIndex(hash, this.data.length)];
        while (hashEntry != null) {
            if (hashEntry.hashCode == hash && isEqualKey(obj, hashEntry.getKey())) {
                return hashEntry;
            }
            hashEntry = hashEntry.next;
        }
        return null;
    }

    protected int hash(Object obj) {
        int hashCode = obj.hashCode();
        hashCode += (hashCode << 9) ^ -1;
        hashCode ^= hashCode >>> 14;
        hashCode += hashCode << 4;
        return hashCode ^ (hashCode >>> 10);
    }

    public int hashCode() {
        int i = 0;
        Iterator createEntrySetIterator = createEntrySetIterator();
        while (createEntrySetIterator.hasNext()) {
            i += createEntrySetIterator.next().hashCode();
        }
        return i;
    }

    protected int hashIndex(int i, int i2) {
        return (i2 - 1) & i;
    }

    protected void init() {
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    protected boolean isEqualKey(Object obj, Object obj2) {
        return obj == obj2 || (obj != null && obj.equals(obj2));
    }

    protected boolean isEqualValue(Object obj, Object obj2) {
        return obj == obj2 || obj.equals(obj2);
    }

    public Set<K> keySet() {
        if (this.keySet == null) {
            this.keySet = new KeySet(this);
        }
        return this.keySet;
    }

    public MapIterator<K, V> mapIterator() {
        return this.size == 0 ? EmptyMapIterator.INSTANCE : new HashMapIterator(this);
    }

    public V put(K k, V v) {
        Object obj;
        if (k == null) {
            obj = NULL;
        } else {
            K k2 = k;
        }
        int hash = hash(obj);
        int hashIndex = hashIndex(hash, this.data.length);
        HashEntry hashEntry = this.data[hashIndex];
        while (hashEntry != null) {
            if (hashEntry.hashCode == hash && isEqualKey(k, hashEntry.getKey())) {
                V value = hashEntry.getValue();
                updateEntry(hashEntry, v);
                return value;
            }
            hashEntry = hashEntry.next;
        }
        addMapping(hashIndex, hash, k, v);
        return null;
    }

    public void putAll(Map<? extends K, ? extends V> map) {
        int size = map.size();
        if (size != 0) {
            ensureCapacity(calculateNewCapacity((int) ((((float) (size + this.size)) / this.loadFactor) + 1.0f)));
            for (Entry entry : map.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
        }
    }

    public V remove(Object obj) {
        int hash = hash(obj == null ? NULL : obj);
        int hashIndex = hashIndex(hash, this.data.length);
        HashEntry hashEntry = this.data[hashIndex];
        HashEntry hashEntry2 = null;
        while (hashEntry != null) {
            if (hashEntry.hashCode == hash && isEqualKey(obj, hashEntry.getKey())) {
                V value = hashEntry.getValue();
                removeMapping(hashEntry, hashIndex, hashEntry2);
                return value;
            }
            hashEntry2 = hashEntry;
            hashEntry = hashEntry.next;
        }
        return null;
    }

    protected void removeEntry(HashEntry<K, V> hashEntry, int i, HashEntry<K, V> hashEntry2) {
        if (hashEntry2 == null) {
            this.data[i] = hashEntry.next;
        } else {
            hashEntry2.next = hashEntry.next;
        }
    }

    protected void removeMapping(HashEntry<K, V> hashEntry, int i, HashEntry<K, V> hashEntry2) {
        this.modCount++;
        removeEntry(hashEntry, i, hashEntry2);
        this.size--;
        destroyEntry(hashEntry);
    }

    protected void reuseEntry(HashEntry<K, V> hashEntry, int i, int i2, K k, V v) {
        hashEntry.next = this.data[i];
        hashEntry.hashCode = i2;
        hashEntry.key = k;
        hashEntry.value = v;
    }

    public int size() {
        return this.size;
    }

    public String toString() {
        if (size() == 0) {
            return "{}";
        }
        StringBuilder stringBuilder = new StringBuilder(size() * 32);
        stringBuilder.append('{');
        MapIterator mapIterator = mapIterator();
        boolean hasNext = mapIterator.hasNext();
        while (hasNext) {
            Object next = mapIterator.next();
            AbstractHashedMap value = mapIterator.getValue();
            if (next == this) {
                next = "(this Map)";
            }
            StringBuilder append = stringBuilder.append(next).append('=');
            if (value == this) {
                next = "(this Map)";
            } else {
                AbstractHashedMap abstractHashedMap = value;
            }
            append.append(next);
            hasNext = mapIterator.hasNext();
            if (hasNext) {
                stringBuilder.append(',').append(' ');
            }
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    protected void updateEntry(HashEntry<K, V> hashEntry, V v) {
        hashEntry.setValue(v);
    }

    public Collection<V> values() {
        if (this.values == null) {
            this.values = new Values(this);
        }
        return this.values;
    }
}
