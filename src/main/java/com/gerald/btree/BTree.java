package com.gerald.btree;

import java.io.Closeable;

public interface BTree extends Closeable {
    int maxLeafRecordSize();
    
    int minLeafKeyNum();
    
    int insertReserveSpace();
}
