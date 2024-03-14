/**
 * Cache，可用于缓存数据库对象等。目前提供FIFO与LRU两种实现。<br>
 * 只支持write-through，在写入操作后请手动保持与Cache的一致性。
 */
package com.libraryseat.utils.cache;