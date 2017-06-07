package com.gerald.btree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.gerald.btree.disk.DataFile;
import com.gerald.btree.disk.DataFiles;
import com.gerald.btree.disk.DataFile.BlockSize;

public class BTreeImpl implements BTree {
    /**
     * 叶节点最小关键字数量
     */
    private int minLeafKeyNum = 8;
    
    private int insertReserveSpace;
    
    private DataFile dataFile;
    
    private int maxLeafRecordSize;
    
    public BTreeImpl(File file, BlockSize blockSize, int minLeafKeyNum) throws FileNotFoundException, IOException {
        this.minLeafKeyNum = minLeafKeyNum;
        this.dataFile = DataFiles.newInstance(file, blockSize);
        this.insertReserveSpace = blockSize.getSize() / 16;
        this.maxLeafRecordSize = (blockSize.getSize() - insertReserveSpace) / minLeafKeyNum;
    }

    @Override
    public void close() throws IOException {
        dataFile.close();
    }

    @Override
    public int maxLeafRecordSize() {
        return maxLeafRecordSize;
    }

    @Override
    public int minLeafKeyNum() {
        return minLeafKeyNum;
    }

    @Override
    public int insertReserveSpace() {
        return insertReserveSpace;
    }
}
