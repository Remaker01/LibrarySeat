package com.libraryseat.utils.cache;
//可以再实现一个FIFOCache对比一下性能
public interface Cache<K,V> {
    V get(K k);
    /**
     * 在缓存中添加一个元素。
     * @return 若已从缓存中淘汰一个元素，则返回被淘汰的元素，否则返回null
     */
    V put(K k,V v);
    int getSize();
    int getCapacity();
    V invalidate(K k);
}
class DLinkedNode<K,V> {
    K key;
    V value;
    DLinkedNode<K,V> prev,next;
    //脏位，用于写回操作
    volatile boolean dirty;
    DLinkedNode() {dirty = false;}
    DLinkedNode(K k, V v) {key = k;value = v;dirty = false;}
}