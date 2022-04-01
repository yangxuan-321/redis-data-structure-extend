package org.moda.redis.module.tree;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
class RedisTree<T, V> {
    private TreeNode<T, V> node;
    private List<T> childIds;
}
