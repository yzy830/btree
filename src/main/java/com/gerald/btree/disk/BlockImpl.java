package com.gerald.btree.disk;

class BlockImpl implements Block {
    private DataFile file;
    
    private int index;
    
    private byte[] dataBuffer;
    
    private boolean isFreed;
    
    public BlockImpl(DataFile file, int index) {
        this.file = file;
        this.index = index;
        this.dataBuffer = new byte[file.blockSize().getSize()];
        isFreed = false;
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    public byte[] data() {
        return dataBuffer;
    }

    @Override
    public DataFile file() {
        return file;
    }

    @Override
    public boolean isFreed() {
        return isFreed;
    }

    @Override
    public void free() {
        isFreed = true;
    }
}
