package rx.internal.util;

import java.util.Queue;
import rx.Observer;
import rx.Subscription;
import rx.exceptions.MissingBackpressureException;
import rx.internal.operators.NotificationLite;
import rx.internal.util.unsafe.SpmcArrayQueue;
import rx.internal.util.unsafe.SpscArrayQueue;
import rx.internal.util.unsafe.UnsafeAccess;

public class RxRingBuffer implements Subscription {
    public static final int SIZE;
    public static final ObjectPool<Queue<Object>> SPMC_POOL = new ObjectPool<Queue<Object>>() {
        protected SpmcArrayQueue<Object> createObject() {
            return new SpmcArrayQueue(RxRingBuffer.SIZE);
        }
    };
    public static final ObjectPool<Queue<Object>> SPSC_POOL = new ObjectPool<Queue<Object>>() {
        protected SpscArrayQueue<Object> createObject() {
            return new SpscArrayQueue(RxRingBuffer.SIZE);
        }
    };
    private final ObjectPool<Queue<Object>> pool;
    private Queue<Object> queue;
    private final int size;
    public volatile Object terminalState;

    static {
        int defaultSize = 128;
        if (PlatformDependent.isAndroid()) {
            defaultSize = 16;
        }
        String sizeFromProperty = System.getProperty("rx.ring-buffer.size");
        if (sizeFromProperty != null) {
            try {
                defaultSize = Integer.parseInt(sizeFromProperty);
            } catch (NumberFormatException e) {
                System.err.println("Failed to set 'rx.buffer.size' with value " + sizeFromProperty + " => " + e.getMessage());
            }
        }
        SIZE = defaultSize;
    }

    public static RxRingBuffer getSpscInstance() {
        if (UnsafeAccess.isUnsafeAvailable()) {
            return new RxRingBuffer(SPSC_POOL, SIZE);
        }
        return new RxRingBuffer();
    }

    public static RxRingBuffer getSpmcInstance() {
        if (UnsafeAccess.isUnsafeAvailable()) {
            return new RxRingBuffer(SPMC_POOL, SIZE);
        }
        return new RxRingBuffer();
    }

    private RxRingBuffer(Queue<Object> queue, int size) {
        this.queue = queue;
        this.pool = null;
        this.size = size;
    }

    private RxRingBuffer(ObjectPool<Queue<Object>> pool, int size) {
        this.pool = pool;
        this.queue = (Queue) pool.borrowObject();
        this.size = size;
    }

    public synchronized void release() {
        Queue<Object> q = this.queue;
        ObjectPool<Queue<Object>> p = this.pool;
        if (!(p == null || q == null)) {
            q.clear();
            this.queue = null;
            p.returnObject(q);
        }
    }

    public void unsubscribe() {
        release();
    }

    RxRingBuffer() {
        this(new SynchronizedQueue(SIZE), SIZE);
    }

    public void onNext(Object o) throws MissingBackpressureException {
        boolean iae = false;
        boolean mbe = false;
        synchronized (this) {
            Queue<Object> q = this.queue;
            if (q != null) {
                mbe = !q.offer(NotificationLite.next(o));
            } else {
                iae = true;
            }
        }
        if (iae) {
            throw new IllegalStateException("This instance has been unsubscribed and the queue is no longer usable.");
        } else if (mbe) {
            throw new MissingBackpressureException();
        }
    }

    public void onCompleted() {
        if (this.terminalState == null) {
            this.terminalState = NotificationLite.completed();
        }
    }

    public void onError(Throwable t) {
        if (this.terminalState == null) {
            this.terminalState = NotificationLite.error(t);
        }
    }

    public int available() {
        return this.size - count();
    }

    public int capacity() {
        return this.size;
    }

    public int count() {
        Queue<Object> q = this.queue;
        if (q == null) {
            return 0;
        }
        return q.size();
    }

    public boolean isEmpty() {
        Queue<Object> q = this.queue;
        return q == null || q.isEmpty();
    }

    public Object poll() {
        Object obj = null;
        synchronized (this) {
            Queue<Object> q = this.queue;
            if (q == null) {
            } else {
                obj = q.poll();
                Object ts = this.terminalState;
                if (obj == null && ts != null && q.peek() == null) {
                    obj = ts;
                    this.terminalState = null;
                }
            }
        }
        return obj;
    }

    public Object peek() {
        Object obj;
        synchronized (this) {
            Queue<Object> q = this.queue;
            if (q == null) {
                obj = null;
            } else {
                obj = q.peek();
                Object ts = this.terminalState;
                if (obj == null && ts != null && q.peek() == null) {
                    obj = ts;
                }
            }
        }
        return obj;
    }

    public boolean isCompleted(Object o) {
        return NotificationLite.isCompleted(o);
    }

    public boolean isError(Object o) {
        return NotificationLite.isError(o);
    }

    public Object getValue(Object o) {
        return NotificationLite.getValue(o);
    }

    public boolean accept(Object o, Observer child) {
        return NotificationLite.accept(child, o);
    }

    public Throwable asError(Object o) {
        return NotificationLite.getError(o);
    }

    public boolean isUnsubscribed() {
        return this.queue == null;
    }
}
