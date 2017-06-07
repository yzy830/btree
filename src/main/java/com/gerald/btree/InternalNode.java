package com.gerald.btree;

import java.util.List;

public interface InternalNode<T extends Comparable<T>> extends TreeNode<T> {
	/**
	 * 子节点的Block编号
	 */
	List<Integer> children();
	
	
}
