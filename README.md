# 工程简介
### 实现redis的各种数据结构
- tree
```
1、使用hash结构
    为什么选择hash结构呢？
        1.1、hash结构的时间复杂度为O(1)
        1.2、曾经想过使用sort set结构(id作为score，节点信息作为value)，但是sort set可以重复。所以有点尴尬
        1.3、Redis 中每个 hash 可以存储 232 - 1 键值对（40多亿）
```
- graph
# 延伸阅读

