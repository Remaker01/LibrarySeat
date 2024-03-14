package com.libraryseat.utils.cache;
/**通用的缓存接口*/
public interface Cache<K,V> {
    /**
     * 在缓存中获取一个元素。
     * @return k在缓存中对应的值。如找不到则返回null
     */
    V get(K k);
    /**
     * 在缓存中添加一个元素。
     */
    void put(K k,V v);
    int getSize();
    int getCapacity();
    /**
     * 让缓存中的某一项失效
     */
    void invalidate(K k);
}
class DLinkedNode<K,V> {
    K key;
    V value;
    DLinkedNode<K,V> prev,next;
    //脏位，用于写回操作
    DLinkedNode() {}
    DLinkedNode(K k, V v) {key = k;value = v;}
}