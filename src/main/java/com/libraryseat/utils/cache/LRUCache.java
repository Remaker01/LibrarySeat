package com.libraryseat.utils.cache;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 实现了LRU淘汰算法的缓存。
 */
public class LRUCache<K,V> implements Cache<K,V>{
    public static final int DEFAULT_CAPACITY = 15;
    private ConcurrentMap<K, DLinkedNode<K,V>> map;
    private final int capacity;
    private DLinkedNode<K,V> head, tail;
    /**用默认容量构造*/
    public LRUCache() {this(DEFAULT_CAPACITY);}
    public LRUCache(int capacity) {
        this.capacity = capacity;
        map = new ConcurrentHashMap<>(capacity);
        // 使用伪头部和伪尾部节点（很重要）
        head = new DLinkedNode<>();
        tail = new DLinkedNode<>();
        head.next = tail;
        tail.prev = head;
    }
    @Override
    public V get(K key) {
        DLinkedNode<K,V> node = map.get(key);
        if (node == null)
            return null;
        // 如果 key 存在，先通过哈希表定位，再移到头部
        moveToHead(node);
        return node.value;
    }
    @Override
    public void put(K key, V value) {
        DLinkedNode<K,V> node = map.get(key);
        if (node == null) {
            // 如果 key 不存在，创建一个新的节点
            DLinkedNode<K,V> newNode = new DLinkedNode<>(key, value);
            // 添加进哈希表
            map.put(key, newNode);
            // 添加至双向链表的头部
            addToHead(newNode);
            if (map.size() > capacity) {
                // 如果超出容量，删除双向链表的尾部节点
                DLinkedNode<K,V> tail_ = removeTail();
                // 删除哈希表中对应的项
                map.remove(tail_.key);
                //返回
            } /*else {
                size.getAndIncrement();
            }*/
        }
        else {
            // 如果 key 存在，先通过哈希表定位，再修改 value，并移到头部
            node.value = value;
//            node.dirty = true;
            moveToHead(node);
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
        map.remove(k);
    }

    //添加到头部节点（放在伪头部后面）
    private synchronized void addToHead(DLinkedNode<K,V> node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }
    //移除节点
    private synchronized void removeNode(DLinkedNode<K,V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
    //移动到头部
    private synchronized void moveToHead(DLinkedNode<K,V> node) {
        removeNode(node);
        addToHead(node);
    }
    //删除尾部节点（LRU触发）
    private synchronized DLinkedNode<K,V> removeTail() {
        DLinkedNode<K,V> res = tail.prev;
        removeNode(res);
        return res;
    }
}
