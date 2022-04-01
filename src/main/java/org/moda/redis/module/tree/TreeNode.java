package org.moda.redis.module.tree;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 树节点
 * @author yangxuan
 */
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TreeNode<T, V> {
    private T id;
    private T pid;
    private V v;
}
