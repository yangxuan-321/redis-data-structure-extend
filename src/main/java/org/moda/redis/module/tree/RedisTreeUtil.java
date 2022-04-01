package org.moda.redis.module.tree;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;
import org.moda.redis.common.fp.Either;
import org.moda.redis.common.util.RedisUtil;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Redis树形结构操作
 * @author yangxuan
 */
public class RedisTreeUtil {
    private static final String FICTITIOUS_NODE = "FICTITIOUS_NODE";

    private static final Supplier<DefaultRedisScript<List>> FIND_TREE_F = () -> {
        DefaultRedisScript<List> x = new DefaultRedisScript<List>();
        x.setResultType(List.class);
        x.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/tree/find_tree.lua")));
        return x;
    };
    private static final DefaultRedisScript<List> FIND_TREE_SCRIPT = FIND_TREE_F.get();

    public static <T, V> Either<String, List<TreeNode<T, V>>> init(List<TreeNode<T, V>> nodes, T rootPid, String treeName) {
        if (CollectionUtils.isEmpty(nodes)) {
            return Either.left("nodes不能为空");
        }

        // 通过pid 聚合
        Map<T, List<TreeNode<T, V>>> maps = nodes.stream().collect(Collectors.groupingBy(TreeNode::getPid));
        Function<T, List<T>> childF = pid -> Optional.ofNullable(maps.get(pid)).map(x ->
            x.stream().map(TreeNode::getId).collect(Collectors.toList())
        ).orElse(new ArrayList<T>());

        // 2、将 TreeNode<T, V> 转为 RedisTree<T, V>
        List<RedisTree<T, V>> treeNodes = nodes.stream().map(x ->
            RedisTree.<T, V>builder()
                .node(x)
                .childIds(childF.apply(x.getId()))
                .build()
        ).collect(Collectors.toList());

        String key = RedisUtil.TREE_BASE_KEY + treeName;
        Function<RedisTree<T, V>, String> hashFieldKey = x ->
            Optional.ofNullable(x.getNode()).map(TreeNode::getId).map(String::valueOf).orElse("");
        Map<String, String> hash = treeNodes.stream().collect(Collectors.toMap(hashFieldKey, JSON::toJSONString));

        // 1、为了能够快速构建一棵树，我们先找到 root 节点。并再生成一个虚拟节点作为root的根节点
        RedisTree<T, V> fictitiousNode = RedisTree.<T, V>builder()
            .childIds(Arrays.asList(rootPid))
            .build();
        hash.put("FICTITIOUS_NODE", JSON.toJSONString(fictitiousNode));
        boolean r = RedisUtil.hashPutAll(key, hash);

        return null;
    }

    public static <T, V> Either<String, Tree<T, V>> findTree(String id, String treeName) {
        String key = RedisUtil.TREE_BASE_KEY + treeName;
        List execute = RedisUtil.redisTemplate.execute(FIND_TREE_SCRIPT, Arrays.asList(key), id);
        if (CollectionUtils.isEmpty(execute)) {
            return Either.left("树形结构为空");
        }

        String res = (String) execute.get(0);
        if (StringUtils.isEmpty(res)) {
            return Either.left("树形结构为空");
        }

         Tree<T, V> tree = JSON.parseObject(res, new TypeReference<Tree<T, V>>() {
         });
//        Gson gson = new Gson();
//        Tree<T, V> tree = gson.<Tree<T, V>>fromJson(res, Tree.class);
        return Either.right(tree);
    }

    public static <T, V> Either<String, Tree<T, V>> findSubTreeById(T id, String treeName) {
        return RedisTreeUtil.<T, V>findTree(String.valueOf(id), treeName);
    }

    public static <T, V> Either<String, Tree<T, V>> findRootTree(String treeName) {
        return RedisTreeUtil.<T, V>findTree(FICTITIOUS_NODE, treeName);
    }
}


