package com.libraryseat.utils.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FIFOCache<K,V> implements Cache<K,V>{
    private ConcurrentMap<K,DLinkedNode<K,V>> map;
    private final int capacity;
    private final DLinkedNode<K,V> head,tail;

    public FIFOCache() {this(LRUCache.DEFAULT_CAPACITY);}
    public FIFOCache(int capacity) {
        this.capacity = capacity;
        map = new ConcurrentHashMap<>(capacity);
        head = new DLinkedNode<>();
        tail = new DLinkedNode<>();
        head.next = tail;
        tail.prev = head;
    }

    @Override
    public V get(K k) {
        DLinkedNode<K,V> node = map.get(k);
        return node == null ? null : node.value;
    }

    @Override
    public void put(K k, V v) {
        DLinkedNode<K,V> node = map.get(k);
        if (node != null) {
//            node.dirty = true;
            node.value = v;
        }
        else {
            DLinkedNode<K,V> newNode = new DLinkedNode<>(k,v);
            map.put(k,newNode);
            addToHead(newNode);
            if (map.size() > capacity) {
                // 如果超出容量，删除双向链表的尾部节点
                DLinkedNode<K,V> tail_ = removeTail();
                // 删除哈希表中对应的项
                map.remove(tail_.key);
                //返回
            }
        }
    }

    @Override
    public int getSize() {
        return map.size();
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public void invalidate(K k) {
        removeNode(map.remove(k));
    }

    private synchronized void addToHead(DLinkedNode<K,V> node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    private synchronized void removeNode(DLinkedNode<K,V> node) {
        if (node == null)
            return;
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private synchronized DLinkedNode<K,V> removeTail() {
        DLinkedNode<K,V> res = tail.prev;
        res.prev.next = tail.next;
        res.next.prev = tail.prev;
        return res;
    }
}