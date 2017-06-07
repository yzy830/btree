package com.gerald.btree;

import java.util.List;

import com.gerald.btree.disk.Block;

public interface TreeNode<T extends Comparable<T>> {
	/**
	 * 关键字列表
	 */
    List<T> keys();
	
    /**
     * 是否是叶节点
     */
    boolean isLeaf();
    
    /**
     * 所属块
     */
    Block block();
    
    /**
     * 节点分裂
     * 
     * @return
     */
    SplitResult<T> split();
    
    interface SplitResult<T extends Comparable<T>> {
        /**
         * 分裂后提升的key
         */
        T promotedKey();
        
        /**
         * 分裂后的左子树，所有的key都小于或等于{{@link #promotedKey()}。只有叶节点分裂才可能等于，因为叶节点要保存所有的关键字
         */
        TreeNode<T> left();
        
        /**
         * 分裂后的右子树，所有的key都大于{{@link #promotedKey()}
         */
        TreeNode<T> right();
    }
}
