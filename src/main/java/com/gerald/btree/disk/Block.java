package com.gerald.btree.disk;

public interface Block {
    /**
     * block偏移量
     */
    int index();
    
    /**
     * block的数据
     */
    byte[] data();
    
    /**
     * 数据文件
     */
    DataFile file();
    
    /**
     * 是否已经释放
     */
    boolean isFreed();
    
    /**
     * 释放
     */
    void free();
}
