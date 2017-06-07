package com.gerald.btree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.gerald.btree.disk.Block;
import com.gerald.btree.disk.DataFile;
import com.gerald.btree.disk.DataFile.BlockSize;
import com.gerald.btree.disk.DataFiles;

public class DataFileTest {
    private File file = null;
    
    @Before
    public void setup() throws IOException {
        URL resource = DataFileTest.class.getResource("/datafile");
        file = new File(resource.getFile());
        
        if(file.exists()) {
            file.delete();
            file.createNewFile();
        }
    }
    
    @Test
    public void testAlloc() throws FileNotFoundException, IOException {
        try(DataFile dataFile = DataFiles.newInstance(file, BlockSize.K_16)) {
            // 分配block1~block4
            Block block1 = dataFile.alloc();
            Assert.assertEquals(block1.index(), 0);
            
            Block block2 = dataFile.alloc();
            Assert.assertEquals(block2.index(), 1);
            
            Block block3 = dataFile.alloc();
            Assert.assertEquals(block3.index(), 2);
            
            Block block4 = dataFile.alloc();
            Assert.assertEquals(block4.index(), 3);
            
            // 释放block3/4
            dataFile.free(block3);
            dataFile.free(block4);
            
            // 分配block5~block8
            Block block5 = dataFile.alloc();
            Assert.assertEquals(block5.index(), 2);
            
            Assert.assertEquals(dataFile.lastBlockIndex(), 3);
            
            Block block6 = dataFile.alloc();
            Assert.assertEquals(block6.index(), 3);
            
            Block block7 = dataFile.alloc();
            Assert.assertEquals(block7.index(), 4);
            
            Block block8 = dataFile.alloc();
            Assert.assertEquals(block8.index(), 5);
            
            // 释放block2/6/8，index分别是1,3,5；剩下block1/5/7，index分别为0,2,4
            dataFile.free(block2);
            dataFile.free(block6);
            dataFile.free(block8);
            
            // 给block7写入数据
            for(int i = 0; i < block1.data().length; ++i) {
                block1.data()[i] = 1;
                block7.data()[i] = 2;
            }
            
            dataFile.write(block1);
            dataFile.write(block7);
            
            Assert.assertEquals(dataFile.lastBlockIndex(), 5);
        }
        
        // 关闭后重新打开文件
        try(DataFile dataFile = DataFiles.newInstance(file, BlockSize.K_16)) {
            // 分配block
            Block block2 = dataFile.alloc();
            Assert.assertEquals(block2.index(), 1);
            
            Block block6 = dataFile.alloc();
            Assert.assertEquals(block6.index(), 3);
            
            Block block8 = dataFile.alloc();
            Assert.assertEquals(block8.index(), 5);
            
            Block block9 = dataFile.alloc();
            Assert.assertEquals(block9.index(), 6);
            
            Block block10 = dataFile.alloc();
            Assert.assertEquals(block10.index(), 7);
            
            Block block11 = dataFile.alloc();
            Assert.assertEquals(block11.index(), 8);
            
            Block block12 = dataFile.alloc();
            Assert.assertEquals(block12.index(), 9);
            
            Block block13 = dataFile.alloc();
            Assert.assertEquals(block13.index(), 10);
            
            Block block14 = dataFile.alloc();
            Assert.assertEquals(block14.index(), 11);
            
            Block block1 = dataFile.read(0);
            Block block7 = dataFile.read(4);
            
            for(int i = 0; i < block1.data().length; ++i) {
                Assert.assertEquals(block1.data()[i], 1);
                Assert.assertEquals(block7.data()[i], 2);
            }
        }
    }
}
