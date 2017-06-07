package com.gerald.btree.record;

/**
 * 块空间管理。用于给记录分配空间
 *
 */
public interface BlockSpaceManager {
    /**
     * 分配记录空间
     * 
     * @param size
     *          空间大小
     *          
     * @return 记录空间
     */
    RecordSpace alloc(int size);
    
    /**
     * 释放记录空间
     */
    void free(RecordSpace record);
    
    /**
     * 剩余空间大小
     */
    int freeSize();
}
