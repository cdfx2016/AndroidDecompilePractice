package org.jivesoftware.smack.util.collections;

public class EmptyMapIterator extends AbstractEmptyIterator implements MapIterator, ResettableIterator {
    public static final MapIterator INSTANCE = new EmptyMapIterator();

    protected EmptyMapIterator() {
    }

    public /* bridge */ /* synthetic */ void add(Object obj) {
        super.add(obj);
    }

    public /* bridge */ /* synthetic */ Object getKey() {
        return super.getKey();
    }

    public /* bridge */ /* synthetic */ Object getValue() {
        return super.getValue();
    }

    public /* bridge */ /* synthetic */ boolean hasNext() {
        return super.hasNext();
    }

    public /* bridge */ /* synthetic */ boolean hasPrevious() {
        return super.hasPrevious();
    }

    public /* bridge */ /* synthetic */ Object next() {
        return super.next();
    }

    public /* bridge */ /* synthetic */ int nextIndex() {
        return super.nextIndex();
    }

    public /* bridge */ /* synthetic */ Object previous() {
        return super.previous();
    }

    public /* bridge */ /* synthetic */ int previousIndex() {
        return super.previousIndex();
    }

    public /* bridge */ /* synthetic */ void remove() {
        super.remove();
    }

    public /* bridge */ /* synthetic */ void reset() {
        super.reset();
    }

    public /* bridge */ /* synthetic */ void set(Object obj) {
        super.set(obj);
    }

    public /* bridge */ /* synthetic */ Object setValue(Object obj) {
        return super.setValue(obj);
    }
}
