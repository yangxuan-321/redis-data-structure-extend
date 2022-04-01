package org.moda.redis.module;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.moda.redis.common.fp.Either;
import org.moda.redis.module.tree.RedisTreeUtil;
import org.moda.redis.module.tree.Tree;
import org.moda.redis.module.tree.TreeNode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * @author yangxuan
 */
@RestController
@RequestMapping("/test/")
public class TestController {

    @GetMapping("/tree/init")
    public String putTree() {
        Dept a = new Dept("A", "A_");
        Dept ab = new Dept("AB", "AB_");
        Dept ac = new Dept("AC", "AC_");
        Dept acd = new Dept("ACD", "ACD_");
        TreeNode<Long, Dept> ax = new TreeNode<Long, Dept>(1L , -1L, a);
        TreeNode<Long, Dept> abx = new TreeNode<Long, Dept>(2L , 1L, ab);
        TreeNode<Long, Dept> acx = new TreeNode<Long, Dept>(3L , 1L, ac);
        TreeNode<Long, Dept> acdx = new TreeNode<Long, Dept>(4L , 3L, acd);
        List<TreeNode<Long, Dept>> treeNodes = Arrays.asList(ax, abx, acx, acdx);
        RedisTreeUtil.init(treeNodes, 1L, "dept");

        return "yes";
    }

    @GetMapping("/tree/all")
    public Tree<Long, Dept> treeAll() {
        long start = System.currentTimeMillis();
        Either<String, Tree<Long, Dept>> dept = RedisTreeUtil.<Long, Dept>findRootTree("dept");
        long stop = System.currentTimeMillis();
        System.out.println(stop - start);
        return dept.fold(l -> null, r -> r);
    }
}

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
class Dept {
    private String name;
    private String fullName;
}
