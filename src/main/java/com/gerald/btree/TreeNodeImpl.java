package com.gerald.btree;

import java.util.List;

import com.gerald.btree.disk.Block;

abstract class TreeNodeImpl<T extends Comparable<T>> implements TreeNode<T> {
//    public TreeNodeImpl()

    @Override
    public List<T> keys() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isLeaf() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Block block() {
        // TODO Auto-generated method stub
        return null;
    }

}
