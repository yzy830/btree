package com.gerald.btree.disk;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

public interface DataFile extends Closeable {    
    enum BlockSize {
        K_4(4 * 1024),
        K_8(8 * 1024),
        K_16(16 * 1024),
        K_32(32 * 1024),
        K_64(64 * 1024)
        ;
        
        private int size;
        
        private BlockSize(int size) {
            this.size = size;
        }

        public int getSize() {
            return size;
        }
        
        public long offset(int index) {
            return index * size;
        }
    }
    
    /**
     * 树存储文件
     */
    File file();
    
    /**
     * 块大小
     */
    BlockSize blockSize();
    
    /**
     * 分配一个block
     */
    Block alloc() throws IOException;
    
    /**
     * 释放一个block
     */
    void free(Block block);
    
    /**
     * 读取指定block
     */
    Block read(int blockIndex) throws IOException;
    
    /**
     * 写block
     */
    void write(Block block) throws IOException;
    
    /**
     * 文件占用的总块数
     */
    int totalBlockNum();
    
    /**
     * 文件中，最后一块block的index
     */
    int lastBlockIndex();
}
