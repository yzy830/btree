package com.gerald.btree;

import java.util.List;

public interface LeafNode<T extends Comparable<T>> extends TreeNode<T> {
	/**
	 * 上一个节点的block序号
	 */
	int pre();
	
	/**
     * 下一个节点的block序号
     */
	int next();
	
	/**
	 * 叶节点记录
	 */
	List<Record<T>> records();
	
	/**
	 * 叶节点记录
	 */
	interface Record<T extends Comparable<T>> {	    
	    /**
	     * 数据
	     */
	    byte[] data();
	    
	    /**
	     * 关键字
	     */
	    T key();
	}
}
