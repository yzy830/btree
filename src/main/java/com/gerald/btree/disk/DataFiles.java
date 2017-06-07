package com.gerald.btree.disk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.gerald.btree.disk.DataFile.BlockSize;

public abstract class DataFiles {
    private DataFiles() {
        throw new AssertionError();
    }
    
    public static DataFile newInstance(File file, BlockSize blockSize) throws FileNotFoundException, IOException {
        return new DataFileImpl(file, blockSize);
    }
}
