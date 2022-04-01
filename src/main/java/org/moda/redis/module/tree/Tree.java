package org.moda.redis.module.tree;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 树形结构
 * @author yangxuan
 */
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Tree<T, V> {
    private TreeNode<T, V> node;
    private List<Tree<T, V>> childNodes;
}
