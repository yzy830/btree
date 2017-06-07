package com.gerald.btree;

import java.util.List;

import com.gerald.btree.disk.Block;

public class LeafNodeImpl<T extends Comparable<T>> implements LeafNode<T> {

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

    @Override
    public com.gerald.btree.TreeNode.SplitResult<T> split() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int pre() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int next() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<com.gerald.btree.LeafNode.Record<T>> records() {
        // TODO Auto-generated method stub
        return null;
    }

}
