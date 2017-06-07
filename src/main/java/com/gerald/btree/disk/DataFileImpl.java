package com.gerald.btree.disk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.List;

class DataFileImpl implements DataFile {
    /**
     * 文件头部分元数据空间，预留10M。
     * <ul>
     * <li>前8M空间，以bit形式描述每个block是否空闲，8M可以描述256G(4K block) ~ 4T(64K block)的空间。</li>
     * <li>current是最后一次分配block的位置，以小端存储</li>
     * <pre>
     * ---------------------------------------------
     * | free block mask(8M) | current(4byte) | 保留  |
     * ---------------------------------------------
     * </pre>
     */
    private static final int META_HEADER_SIZE = 10 * 1024 * 1024;
    
    private static final int FREE_BLOCK_MASK_SIZE = 8 * 1024 * 1024;
    
    /**
     * 每次分配10个block 
     */
    private static final int FILE_BATCH_INCREASE_SIZE = 10;
    
    private File file;
    
    private BlockSize blockSize;
    
    private RandomAccessFile dataFile;
    
    private FileChannel channel;
    
    private SpaceManager manager;
    
    public DataFileImpl(File file, BlockSize blockSize) throws FileNotFoundException, IOException {
        this.file = file;
        this.blockSize = blockSize;
        this.dataFile = new RandomAccessFile(file, "rw");
        this.channel = dataFile.getChannel();
        
        ByteBuffer buffer = ByteBuffer.allocate(META_HEADER_SIZE);
        
        int count = channel.read(buffer, 0);
        manager = new SpaceManager(buffer.array(), count);
    }

    public File file() {
        return file;
    }

    public BlockSize blockSize() {
        return blockSize;
    }

    public Block alloc() throws IOException {
        return manager.alloc();
    }
    
    @Override
    public void free(Block block) {
        manager.free(block);
    }

    public Block read(int blockIndex) throws IOException {
        BlockImpl block = new BlockImpl(this, blockIndex);
        
        ByteBuffer buffer = ByteBuffer.wrap(block.data());
        int count = channel.read(buffer, offset(blockIndex));
        if(count < blockSize.getSize()) {
            throw new IOException("read block fail. block index = " + block.index() 
                                + ", block size = " + blockSize.getSize() 
                                + ", read count = " + count);
        }
        
        return block;
    }

    public void write(Block block) throws IOException {
        if(block.isFreed()) {
            throw new DataFileWriteException(block);
        }
        
        ByteBuffer buffer = ByteBuffer.wrap(block.data());
        int count = channel.write(buffer, offset(block.index()));
        if(count < blockSize.getSize()) {
            throw new IOException("read block fail. block index = " + block.index() + ", block size = " + blockSize.getSize() + ", write count = " + count);
        }
    }

    @Override
    public void close() throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(manager.mask());
        channel.write(buffer);
        dataFile.close();
    }
    
    private long offset(int index) {
        return META_HEADER_SIZE + blockSize.offset(index);
    }
    
    @Override
    public int totalBlockNum() {
        return manager.getTotal();
    }

    @Override
    public int lastBlockIndex() {
        return manager.getCurrent();
    }
    
    //============================================
    
    public static class DataFileInitException extends RuntimeException {

        /**
         * 
         */
        private static final long serialVersionUID = -2481257821025349940L;
        
        public DataFileInitException(File file) {}
    }
    
    public static class DataFileWriteException extends RuntimeException {

        /**
         * 
         */
        private static final long serialVersionUID = 6092893026738734642L;
     
        public DataFileWriteException(Block block) {}
    }
    
    //============================================
    
    private class SpaceManager {      
        private List<Integer> freeIndexs = new LinkedList<Integer>();
        
        /**
         * 总块数
         */
        private int total;
        
        /**
         * 最后一次分配block的下标
         */
        private int current;
        
        public SpaceManager(byte[] meta, int count) throws IOException {
            if(count < 0) {
                // 新建文件，没有元数据
                total = 0;
                current = -1;
            } else {
                total = (int)((dataFile.length() - META_HEADER_SIZE) / blockSize.getSize());
                current = bytes2Int(meta, FREE_BLOCK_MASK_SIZE);
                int index = 0;                
                
                for(int i = 0; i < FREE_BLOCK_MASK_SIZE ; ++i) {
                    byte mask = meta[i];
                    
                    if(mask == 0) {
                        // mask为0，表示这个mask中，所有位置对应的block都已经被使用
                        index += 8;
                    } else {
                        for(int j = 0; j < 8; ++j) {
                            if(((mask>>j) & 0x01) > 0) {
                                freeIndexs.add(index);
                            }
                            ++index; 
                        }
                    }
                }
            }
        }
        
        public int getTotal() {
            return total;
        }

        public int getCurrent() {
            return current;
        }

        public Block alloc() throws IOException {
            if(freeIndexs.isEmpty()) {
                if(current + 1 == total) {
                    dataFile.setLength(dataFile.length() + FILE_BATCH_INCREASE_SIZE * blockSize.getSize());
                    total += FILE_BATCH_INCREASE_SIZE;
                }
                return new BlockImpl(DataFileImpl.this, ++current);
            } else {
                Integer index = freeIndexs.remove(0);
                return new BlockImpl(DataFileImpl.this, index);
            }
        }
        
        public void free(Block block) {
            block.free();
            freeIndexs.add(block.index());
        }
        
        public byte[] mask() {
            byte[] meta = new byte[META_HEADER_SIZE];
            
            //写入
            System.arraycopy(int2Bytes(current), 0, meta, FREE_BLOCK_MASK_SIZE, 4);
            
            for(Integer index : freeIndexs) {
                int byteIndex = index / 8;
                int biteIndex = index % 8;
                
                meta[byteIndex] |= (0x01 << biteIndex);
            }
            
            return meta;
        }
    }
    
    private static int bytes2Int(byte[] bytes, int offset) {
        return bytes[offset + 0] + (bytes[offset + 1] << 8) + (bytes[offset + 2] << 16) + (bytes[offset + 3] << 24);
    }
    
    private static byte[] int2Bytes(int value) {
        byte[] bytes = new byte[4];
        
        bytes[0] = (byte)(value & 0xff);
        bytes[1] = (byte)((value >> 8) & 0xff);
        bytes[2] = (byte)((value >> 16) & 0xff);
        bytes[3] = (byte)((value >> 24) & 0xff);
        
        return bytes;
    }
}
