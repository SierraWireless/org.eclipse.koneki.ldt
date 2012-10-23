-------------------------------------------------------------------------------
-- Copyright (c) 2011-2012 Sierra Wireless and others.
-- All rights reserved. This program and the accompanying materials
-- are made available under the terms of the Eclipse Public License v1.0
-- which accompanies this distribution, and is available at
-- http://www.eclipse.org/legal/epl-v10.html
--
-- Contributors:
--     Sierra Wireless - initial API and implementation
-------------------------------------------------------------------------------
-- Properties generation. Generate a LOM table with data from introspection.
-------------------------------------------------------------------------------

-- This module defines a single "class" which allows to dump arbitrary values by gathering as munch data as
-- possible on it for debugging purposes.
-- 
-- Recursions are avoided by "flatten" all tables in the structure and referencing them by their ID.
-- The dump format is documented in Confluence Wiki:
--    https://confluence.anyware-tech.com/display/ECLIPSE/Lua+IDE+Internship#LuaIDEInternship-Datadumpformat
--
-- It has two major parts :
--   dump : Result data structure: keys are numbers which are IDs for all dumped tables.
--          Values are table structures.
--   tables : Table dictionary, used to register all dumped tables, keys are tables, 
--            values are their ID. Functions upvalues are also stored here in the same way.
--
-- The dump itself is done by type functions, one for each Lua type. For example a complete dump of the VM is done with:
--   local introspection = require"debugintrospection"
--   local dump = introspection:new()
--   dump.dump.root = dump:table(_G) -- so dump.root will be a number which point to the result of _G introspection
--
-- WARNING: do never keep any direct reference to internal fields (dump or tables): a dump object is not re-dumped to
-- avoid making huge data structures (and potentially overflow l2b)

local debug = require "debug"
local platform = require "debugger.platform"
local util = require "debugger.util"

local M = { }
local dump_pool = {}
dump_pool.__index = dump_pool
M.dump_pool = dump_pool

local all_dumps = setmetatable({ }, { __mode = "k" }) -- register all dumps to avoid to re-dump them (see above warning)

--- Creates a new dump pool with specified options
-- @param dump_locales (boolean) whether local values are dumped
-- @param dump_upvalues (boolean) whether function upvalues are dumped
-- @param dump_metatables (boolean) whether metatables (for tables and userdata) are dumped
-- @param dump_stacks (boolean) whether thread stacks are dumped
-- @param dump_fenv (boolean) whether function environments are dumped
function dump_pool:new(dump_locales, dump_upvalues, dump_metatables, dump_stacks, dump_fenv, keep_reference)
    local dump = setmetatable({
        current_id      = 1,
        tables          = { },
        dump            = { },
        -- set switches, force a boolean value because nil would mess with __index metamethod
        dump_locales    = dump_locales and true or false,
        dump_upvalues   = dump_upvalues and true or false,
        dump_metatables = dump_metatables and true or false,
        dump_stacks     = dump_stacks and true or false,
        dump_fenv       = dump_fenv and true or false,
        keep_reference  = keep_reference and true or false,
    }, self)
    all_dumps[dump] = true
    return dump
end

function dump_pool:_next_id()
    local id = self.current_id
    self.current_id = id + 1
    return id
end

function dump_pool:_register_new(value)
    local id = self.current_id
    self.current_id = id + 1
    self.tables[value] = id
    return id
end

--- Utility function to factorize all metatable handling
function dump_pool:_metatable(value, result, depth)
    --TODO: add support for __pairs and __ipairs ?
    if self.dump_metatables then
        local mt = getmetatable(value)
        if mt then
            result.metatable = self[type(mt)](self, mt, depth-1)
            if mt.__len then result.length = #value end
        end
    end
    return result
end

--- Adds a field into destination table, if both key and value has been successfully dumped
function dump_pool:_field(dest, key, value, depth)
    local dkey, dvalue = self[type(key)](self, key, depth-1), self[type(value)](self, value, depth-1)
    if dkey and dvalue then dest[#dest + 1] = { dkey, dvalue } end
end

--- Functions used to extract debug informations from different data types.
-- each function takes the value to debug as parameter and returns its 
-- debugging structure (or an id, for tables), modifying the pool if needed.

function dump_pool:table(value, depth)
    depth = depth or math.huge
    if depth < 0 then return nil end
    
    if all_dumps[value] then return nil end
    local id = self.tables[value]
    if not id then
        -- this is a new table: register it
        id = self:_register_new(value)
        local t = { type = "table", repr = tostring(value), ref = self.keep_reference and value or nil }
        
        -- iterate over table values and detect arrays at the same time
        -- next is used to circumvent __pairs metamethod in 5.2
        local isarray, i = true, 1
        for k,v in next, value, nil do
          self:_field(t, k, v, depth)
          -- array detection: keys should be accessible by 1..n keys
          isarray = isarray and rawget(value, i) ~= nil
          i = i + 1
        end
        t.array = i > 1 and isarray
        -- FIXME: sort fields for arrays ?
        
        -- The registered length refers to # result because if actual element count 
        -- can be known with dumped values
        t.length = #value
        self:_metatable(value, t, depth)
        self.dump[id] = t
    end
    return id
end

function dump_pool:userdata(value, depth)
    depth = depth or math.huge
    if depth < 0 then return nil end
    
    local result = { type = "userdata", repr = tostring(value), ref = self.keep_reference and value or nil }
    --TODO support uservalues
    
    return self:_metatable(value, result, depth)
end

function dump_pool:thread(value, depth)
    depth = depth or math.huge
    if depth < 0 then return nil end
    
    local result = { type = "thread", repr = tostring(value), status = coroutine.status(value), ref = self.keep_reference and value or nil }
    local stack = self.tables[value]
    if self.dump_stacks and not stack then
        stack = self:_register_new(value)
        local stack_table = { type="special" }
        
        for i=1, math.huge do
            if not debug.getinfo(value, i, "f") then break end
            -- _filed is not used here because i is not a function and there is no risk to get a nil from number or function
            stack_table[#stack_table+1] = { self:number(i, depth - 1), self["function"](self, i, depth - 1, value) }
        end
        
        stack_table.repr = tostring(#stack_table).." levels"
        self.dump[stack] = stack_table
    end
    result.stack = stack
    return result
end

dump_pool["function"] = function(self, value, depth, thread) -- function is a keyword...
    depth = depth or math.huge
    if depth < 0 then return nil end
    
    local info = thread and debug.getinfo(thread, value, "nSfl") or debug.getinfo(value, "nSfl")
    local func = info.func -- in case of value is a stack index
    local result = { type = "function", ref = self.keep_reference and func or nil }
    result.kind = info.what
    
    if info.name and #info.name > 0 then result.repr = "function: "..info.name -- put natural name, if available
    elseif func  then                    result.repr = tostring(func)          -- raw tostring otherwise
    else                                 result.repr = "<tail call>" end       -- nothing is available for tail calls
    
    if not func then return result end -- there is no more info to gather for tail calls
    
    if info.what ~= "C" then
        --TODO: do something if function is not defined in a file
        if info.source:sub(1,1) == "@" then
            result.file = info.source:sub(2)
            result.repr = result.repr .. "\n" .. platform.get_uri("@" .. result.file) .. "\n" .. tostring(result.line_from)
            result.type = "function (Lua)"
        end
        result.line_from = info.linedefined
        result.line_to = info.lastlinedefined
        if info.currentline >= 0 then
            result.line_current = info.currentline
        end
    end
    
    -- Dump function upvalues (if any), trated as a table (recursion is handled in the same way)
    local upvalues = self.tables[func]
    if self.dump_upvalues and not upvalues and func and debug.getupvalue(func, 1) then
        -- Register upvalues table into result
        local ups_table = { type="special" }
        upvalues = self:_register_new(func)
        
        for i=1, math.huge do
            local name, val = debug.getupvalue(func, i)
            if not name then break end
            self:_field(ups_table, name, val, depth)
        end
        
        ups_table.repr = tostring(#ups_table)
        self.dump[upvalues] = ups_table
    end
    result.upvalues = upvalues
    
    -- Dump function locales (only for running function, recursion not handled)
    if self.dump_locales and type(value) == "number" then
        local getlocal = thread and function(...) return debug.getlocal(thread, ...) end or debug.getlocal
        if getlocal(value, 1) then
            local locales = { type="special" }
            local locales_id = self:_next_id()
            
            for i=1, math.huge do
                local name, val = getlocal(value, i)
                if not name then break
                elseif name:sub(1,1) ~= "(" and val ~= self then -- internal values are ignored
                    self:_field(locales, name, val, depth)
                end
            end
            
            locales.repr = tostring(#locales)
            self.dump[locales_id] = locales
            result.locales = locales_id
        end
    end
    return result
end

function dump_pool:string(value, depth)
    depth = depth or math.huge
    if depth < 0 then return nil end
    
    -- make the string printable (%q pattern keeps real newlines and adds quotes)
    return { type = "string", repr = string.format("%q", value):gsub("\\\n", "\\n"), length = #value, 
             ref = self.keep_reference and value or nil }
end

if _VERSION == "Lua 5.1" then
    local oldfunc = dump_pool["function"]
    dump_pool["function"] = function(self, value, depth, thread)
        depth = depth or math.huge
        local result = oldfunc(self, value, depth, thread)
        if not result then return result end
        
        -- Dump function env (if different from _G)
        local env = getfenv(value)
        if self.dump_fenv and env ~= getfenv(0) then
            result.environment = self:table(env, depth - 1)
        end
        
        return result
    end
end

-- default debug function for other types
setmetatable(dump_pool, {
    __index = function(cls, vtype)
        return function(self, value, depth)
            return (depth == nil or depth >= 0) and { repr = tostring(value), type=vtype, ref = self.keep_reference and value or nil } or nil
        end
    end
})

-- ----------------------------------------------------------------------------
-- Public API.
-- ----------------------------------------------------------------------------

-- Used to store complex keys (other than string and number) as they cannot be passed in text
-- For these keys, the resulting expression will not be the key itself but "key_cache[...]"
-- where key_cache must be mapped to this table to resolve key correctly.
M.key_cache = setmetatable({ n=0 }, { __mode = "v" })

local MULTIVAL_MT = { __tostring = function() return "" end }

-- Used to inspect "multival" or "vararg" values. The typical use is to pack function result(s) in a single
-- value to inspect. The Multival instances can be passed to make_property as a single value, they will be
-- correctly reported to debugger
function M.Multival(...)
    return setmetatable({ n=select("#", ...), ... }, MULTIVAL_MT)
end

local function generate_key(name)
    if type(name) == "string" then return string.format("%q", name)
    elseif type(name) == "number" or type(name) == "boolean" then return tostring(name)
    else -- complex key, use key_cache for lookup
        local i = M.key_cache.n
        M.key_cache[i] = name
        M.key_cache.n = i+1
        return "key_cache["..tostring(i).."]"
    end
end

local function generate_printable_key(name)
    return "[" .. (type(name) == "string" and string.format("%q", name) or tostring(name)) .. "]"
end

--- Makes a property form a name/value pair (and fullname), see DBGp spec 7.11 for details
-- It has a pretty basic handling of complex types (function, table, userdata), relying to Lua Inspector for advanced stuff.
-- @param cxt_id (number) context ID in which this value resides (workaround bug 352316)
-- @param value (any) the value to debug
-- @param name (any) the name associated with value, passed through tostring
-- @param fullname (string) a Lua expression to eval to get that property again (if nil, computed automatically)
-- @param depth (number) the maximum property depth (recursive calls)
-- @param pagesize (number) maximum children to include
-- @param page (number) the page to generate (0 based)
-- @param size_limit (number, optional) if set, the maximum size of the string representation (in bytes)
-- @param safe_name (boolean) if true, does not encode the name as table key
--TODO BUG ECLIPSE TOOLSLINUX-99 352316 : as a workaround, context is encoded into the fullname property
function M.make_property(cxt_id, value, name, fullname, depth, pagesize, page, size_limit, safe_name)
    local dump = dump_pool:new(false, false, true, false, true, true)
    
    -- build XML
    local function build_xml(node, name, fullname, page, depth)
        local data = tostring(node.repr)
        
        local specials = { }
        if node.metatable then specials[#specials + 1] = "metatable" end
        if node.environment then specials[#specials + 1] = "environment" end
        
        local numchildren = #node + #specials
        local attr = { type = node.array and "sequence" or node.type, name=name, fullname=util.rawb64(tostring(cxt_id).."|"..fullname),
                        encoding="base64", children = 0, size=#data }
        if numchildren > 0 then
            attr.children = 1
            attr.numchildren = numchildren
            attr.pagesize = pagesize
            attr.page = page
        end
        local xmlnode = { tag = "property", attr = attr, util.b64(size_limit and data:sub(1, size_limit) or data) }
        
        if depth > 0 then
            local from, to = page * pagesize + 1, (page + 1) * (pagesize)
            for i = from, math.min(#node, to) do
                local key, value = unpack(node[i])
                key = type(key) == "number" and dump.dump[key] or key
                value = type(value) == "number" and dump.dump[value] or value
                xmlnode[#xmlnode + 1] = build_xml(value, "[" .. key.repr .. "]", fullname .. "[" .. generate_key(key.ref) .. "]", 0, depth - 1)
            end
            for i = #node + 1, math.min(to, numchildren) do
                local special = specials[i - #node]
                local prop = build_xml(dump.dump[node[special]], special, special .. "[" .. fullname .. "]", 0, depth - 1)
                prop.attr.type = "special"
                xmlnode[#xmlnode + 1] = prop
            end
        end
        
        return xmlnode
    end
    
    fullname = fullname or ("(...)[" .. generate_key(name) .. "]")
    if not safe_name then name = generate_printable_key(name) end
    
    if getmetatable(value) == MULTIVAL_MT then
        local node = { tag = "property" }
        for i=1, value.n do 
            local val = dump[type(value[i])](dump, value[i], depth)
            val = type(val) == "number" and dump.dump[val] or val
            -- Since fullname is impossible to build for multivals and they are read only, 
            -- generate_key is used to retireve reference to the object
            node[#node + 1] = build_xml(val, "["..i.."]", generate_key(val.ref), 0, depth - 1)
        end
        
        -- return just the value in case of single result
        if #node == 1 then
            return node[1]
        end
        
        -- when there are multiple results, they a wrapped into a multival
        node.attr = { type="multival", name=name, fullname=tostring(cxt_id).."|"..fullname, encoding="base64", 
                      numchildren=value.n, children=value.n > 0 and 1 or 0, size=0, pagesize=pagesize }
        return node
    else
        local root = dump[type(value)](dump, value, depth + 1)
        return build_xml(type(root) == "number" and dump.dump[root] or root, name, fullname, page, depth)
    end
end

return M
