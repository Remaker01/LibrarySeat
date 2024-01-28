package com.libraryseat.utils.cache;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FIFOCache<K,V> implements Cache<K,V>{
    private ConcurrentMap<K,DLinkedNode<K,V>> map;
    private final int capacity;
    private DLinkedNode<K,V> head,tail;

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
    public V put(K k, V v) {
        DLinkedNode<K,V> node = map.get(k);
        if (node != null) {
            node.dirty = true;
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
                return (tail_ != head&&tail_.dirty) ? tail_.value : null;
            }
        }
        return null;
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
    public V invalidate(K k) {
        DLinkedNode<K,V> removed = map.remove(k);
        return (removed != null&&removed.dirty) ? removed.value : null;
    }

    private synchronized void addToHead(DLinkedNode<K,V> node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    private synchronized DLinkedNode<K,V> removeTail() {
        DLinkedNode<K,V> res = tail.prev;
        res.prev.next = tail.next;
        res.next.prev = tail.prev;
        return res;
    }
}