--- 获取KEY
local tree_key = tostring(KEYS[1])
local tree_field = tostring(ARGV[1]);

local function parse_node(node_str)
    local node_json = cjson.decode(node_str)
    local node_child_ids = node_json['childIds']
    local node_data = node_json['node']

    return node_child_ids, node_data
end

local function tree_create(node_id)
    local node_str = redis.call('hget', tree_key, node_id)
    --  注意，这里返回的是字符串，所以需要用csjon库解码成table类型
    local node_child_ids, node_data = parse_node(node_str)
    local child_nodes = {}
    for i, v in ipairs(node_child_ids) do
        child_nodes[i] = tree_create(tostring(v))
    end

    local res = {}
    res.node = node_data
    if (next(child_nodes) ~= nil) then
        res.child_nodes = child_nodes
    end
    return res
end

local function main_func()
    --- 查看传递的参数
    --- redis.call('set', 'tree_param:tree_field', tostring(tree_field))
    --- redis.call('set', 'tree_param:tree_key', tostring(tree_key))
    local result = '{}'
    local first_field = tree_field

    if (first_field == 'FICTITIOUS_NODE') then
        local node_str = redis.call('hget', tree_key, 'FICTITIOUS_NODE')
        if (node_str == '') then
            return result
        end
        local node_child_ids, node_data = parse_node(node_str)
        if (node_child_ids == nil) then
            return result
        end
        if (node_child_ids[1] == nil) then
            return result
        end

        first_field = tostring(node_child_ids[1])
    end
    local ree = tree_create(first_field)
    result = cjson.encode(ree)
    return result
end

return main_func()