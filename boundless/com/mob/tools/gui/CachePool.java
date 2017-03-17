package com.mob.tools.gui;

import com.mob.tools.MobLog;

public class CachePool<K, V> {
    private int capacity;
    private Node<K, V> head;
    private int size;
    private Node<K, V> tail;

    private static class Node<K, V> {
        private long cacheTime;
        public K key;
        public Node<K, V> next;
        public Node<K, V> previous;
        public V value;

        private Node() {
        }
    }

    public CachePool(int capacity) {
        this.capacity = capacity;
    }

    public synchronized boolean put(K key, V value) {
        boolean z = true;
        synchronized (this) {
            if (key != null) {
                if (this.capacity > 0) {
                    Node<K, V> n = null;
                    while (this.size >= this.capacity) {
                        n = this.tail;
                        if (n == null) {
                            MobLog.getInstance().w("size != 0 but tail == null, this must meet any mistake! fix me!!", new Object[0]);
                            Node<K, V> n1 = this.head;
                            if (n1 == null) {
                                this.size = 0;
                                this.tail = null;
                            } else {
                                this.size = 1;
                                while (n1.next != null) {
                                    this.size++;
                                    n1 = n1.next;
                                }
                                this.tail = n1;
                            }
                        } else {
                            this.tail = this.tail.previous;
                            this.tail.next = null;
                            this.size--;
                        }
                    }
                    if (n == null) {
                        n = new Node();
                    }
                    n.cacheTime = System.currentTimeMillis();
                    n.key = key;
                    n.value = value;
                    n.previous = null;
                    n.next = this.head;
                    if (this.size == 0) {
                        this.tail = n;
                    } else if (this.head != null) {
                        this.head.previous = n;
                    } else {
                        MobLog.getInstance().w("size != 0 but head == null, this must meet any mistake! fix me!!", new Object[0]);
                        this.tail = n;
                        this.size = 0;
                    }
                    this.head = n;
                    this.size++;
                }
            }
            z = false;
        }
        return z;
    }

    public synchronized V get(K key) {
        V v = null;
        synchronized (this) {
            if (this.head == null) {
                this.size = 0;
                this.tail = null;
            } else if (this.head.key.equals(key)) {
                v = this.head.value;
            } else {
                Node<K, V> n = this.head;
                while (n.next != null) {
                    n = n.next;
                    if (n.key.equals(key)) {
                        if (n.next == null) {
                            n.previous.next = null;
                            this.tail = n.previous;
                        } else {
                            n.previous.next = n.next;
                            n.next.previous = n.previous;
                        }
                        n.previous = null;
                        n.next = this.head;
                        this.head.previous = n;
                        this.head = n;
                        v = n.value;
                    }
                }
            }
        }
        return v;
    }

    public synchronized void clear() {
        this.tail = null;
        this.head = null;
        this.size = 0;
    }

    public synchronized void trimBeforeTime(long time) {
        if (this.capacity > 0) {
            for (Node<K, V> n = this.head; n != null; n = n.next) {
                if (n.cacheTime < time) {
                    if (n.previous != null) {
                        n.previous.next = n.next;
                    }
                    if (n.next != null) {
                        n.next.previous = n.previous;
                    }
                    if (n.equals(this.head)) {
                        this.head = this.head.next;
                    }
                    this.size--;
                }
            }
        }
    }

    public int size() {
        return this.size;
    }
}
