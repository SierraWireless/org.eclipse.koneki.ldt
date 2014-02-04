-------------------------------------------------------------------------------
-- Copyright (c) 2011 Sierra Wireless and others.
-- All rights reserved. This program and the accompanying materials
-- are made available under the terms of the Eclipse Public License v1.0
-- which accompanies this distribution, and is available at
-- http://www.eclipse.org/legal/epl-v10.html
--
-- Contributors:
--     Sierra Wireless - initial API and implementation
-------------------------------------------------------------------------------
-- Runtime inspection unit tests

require "lunatest"
require "lunatest_xassert"
require "debugger" -- make debugger.introspection available in case of one-file build
local dump_pool = require"debugger.introspection".dump_pool

module(..., package.seeall)

local global_env
if setfenv then
  -- on Lua 5.1 sets the function environment to real global env (before module invocation)
  global_env = function(f) return setfenv(f, getfenv(0)) end
else
  global_env = function(f) return f end
end

local pool
function setup()
    pool = dump_pool:new(true, true, true, true, true)
end

-- Test basic type serialization
function test_basictype()
    assert_table_equal({ repr = "12", type = "number" }, pool:number(12))
end

-- Strings are escaped, but length is the raw (unsecaped) length
function test_string()
    local dumped = pool:string"a\nb\000c"
    assert_equal("string", dumped.type)
    assert_equal(5, dumped.length)
    -- %q behaves slightly differently depending on implementation
    assert_true(dumped.repr == '"a\\nb\\000c"' or dumped.repr == '"a\\nb\\0c"')
end

-- Simple table test
function test_table()
    local root = pool:table{ a = "foo", b = "bar" }
    local dumped = pool.dump[root]
    assert_table(dumped)
    assert_string(dumped.repr) -- result is unpredictible
    dumped.repr = ""
    
    assert_table_equal({ { { repr = '"a"', type = "string", length=1 }, { repr = '"foo"', type = "string", length=3 } },
                         { { repr = '"b"', type = "string", length=1 }, { repr = '"bar"', type = "string", length=3 } },
                           length = 0, repr = "", type = "table", array=false }, dumped)
end

-- Test table (array, actually) with metatable
function test_metatable()
    -- this metatable also define a string representation
    local mt = { __tostring = function(t) return string.format("(%d, %d)", unpack(t)) end }
    local root = pool:table(setmetatable({ 12, 23 }, mt))
    local dumped = pool.dump[root]
    assert_table(dumped)
    assert_number(dumped.metatable)
    assert_table_equal({ { { repr="1", type="number" }, { repr="12", type="number" } }, 
                         { { repr="2", type="number" }, { repr="23", type="number" } },
                         length = 2, repr = "(12, 23)", array=true, type="table", metatable=dumped.metatable }, dumped)
    -- most of function introspection is unpredictible and is tested by other test cases
    local dumped_mt = pool.dump[dumped.metatable]
    assert_table(dumped_mt)
    assert_table_equal({ repr='"__tostring"', type="string", length=10 }, dumped_mt[1][1])
end

-- Test nested and recusive tables
function test_nested_recursive_table()
    local t = { nested = {} }
    t.nested.parent = t
    local root = pool:table(t)
    local dumped = pool.dump[root]
    
    assert_table(dumped)
    assert_number(dumped[1][2], "id of nested table is not a number")
    assert_equal(root, pool.dump[dumped[1][2]][1][2], "recursion point verification failed")
end

-- Test a basic function with one upvalue
function test_function()
    local a = 2
    local f = global_env(function() return a end)
    local finfo = debug.getinfo(f)
    local dumped = pool["function"](pool, f)
    assert_number(dumped.upvalues)
    assert_table_subset( { type="function (Lua)", line_from=finfo.linedefined, line_to=finfo.lastlinedefined, 
                          upvalues=dumped.upvalues, file=finfo.source:sub(2), kind="Lua" }, dumped )
    assert_string(dumped.repr) -- unpredictible
    local ups = pool.dump[dumped.upvalues]
    assert_table(ups)
    ups.repr=""
    assert_table_equal( { { { repr='"a"', type="string", length=1 }, { repr="2", type="number" } },
                          type="special", repr="" }, ups)
end

function test_function_env()
    if not setfenv then skip() end -- no environements in 5.2
    local f = setfenv(function() return a end, { a=12 })
    local fdump = pool["function"](pool, f)
    assert_number(fdump.environment)
    local dumped = pool.dump[fdump.environment]
    dumped.repr = ""
    assert_table_equal( { { { repr='"a"', type="string", length=1 }, { repr="12", type="number" } },
                          type="table", repr="", length=0, array=false }, dumped)
end

-- Test output for a function defined in C (using rawget, assuming that it has not been redefined)
function test_cfunction()
    assert_table_equal({ type="function", repr=tostring(rawget), kind="C" }, pool["function"](pool, rawget))
end

function test_thread()
    local body = global_env(function()
        for i=1, math.huge do coroutine.yield(i) end
    end)
    
    local bodyinfo = debug.getinfo(body)
    local thread = coroutine.create(body)
    assert(coroutine.resume(thread))
    
    local dumped = pool:thread(thread)
    assert_number(dumped.stack)
    assert_table_equal({ type="thread", status="suspended", stack=dumped.stack, repr=tostring(thread)}, dumped)
    
    local stack = pool.dump[dumped.stack]
    assert_number(stack[1][2].locales)
    assert_table_subset({ { { repr="1", type="number" }, 
                           { type="function (Lua)", locales=stack[1][2].locales,
                             line_from=bodyinfo.linedefined, line_to=bodyinfo.lastlinedefined, line_current=bodyinfo.linedefined+1,
                             file=bodyinfo.source:sub(2), kind="Lua",
                             upvalues = stack[1][2].upvalues } }, -- has _ENV on Lua 5.2
                          type="special", repr="1 levels"}, stack)
    
    local locales = pool.dump[stack[1][2].locales]
    locales.repr=""
    assert_table_equal({ { { repr='"i"', type="string", length=1}, { repr="1", type="number" } },
                         type="special", repr=""}, locales)
end

-- Test that a dump cannot be dumped
function test_dump_recursion()
    local subpool = dump_pool:new()
    subpool:table{1,2,3}
    local root = pool:table { ok = "abc", ko = subpool }
    local dumped = pool.dump[root]
    dumped.repr = ""
    -- the ko field is not present in result table
    assert_table_equal({ { { repr = '"ok"', type = "string", length=2 }, { repr = '"abc"', type = "string", length=3 } },
                           length = 0, repr = "", type = "table", array=false }, dumped)
end

function test_switches()
    do -- locales
        local pool = dump_pool:new(false, true, true, true, true)
        local dumped
        (function() local a; dumped = pool["function"](pool, 2) end)()
        assert_nil(dumped.locales)
        assert_nil(dumped.ref)
    end
    do -- upvalues
        local pool = dump_pool:new(true, false, true, true, true)
        local dumped = pool["function"](pool, function() return pool end)
        assert_nil(dumped.upvalues)
    end
    do -- metatble
        local pool = dump_pool:new(true, true, false, true, true)
        local root = pool:table(setmetatable({1,2,3}, { a = 12 }))
        assert_nil(pool.dump[root].metatable)
    end
    do -- stack
        local pool = dump_pool:new(true, true, true, false, true)
        local thread = coroutine.create(function() for i=1, math.huge do coroutine.yield(i) end end)
        coroutine.resume(thread)
        local dumped = pool:thread(thread)
        assert_nil(dumped.stack)
    end
    if setfenv then -- environment (5.1 only)
        local pool = dump_pool:new(true, true, true, true, false)
        local dumped = pool["function"](pool, setfenv(function() return a end, { a=12 }))
        assert_nil(dumped.environment)
    end
    do -- reference
        local pool = dump_pool:new(true, true, true, true, true, true)
        local func = function() return a end
        local dumped = pool["function"](pool, func)
        assert_equal(func, dumped.ref)
    end
end

function test_depth_limit()
    local root = pool:table({{{{1,2,3}}}}, 2)
    local level0 = pool.dump[root]
    assert_table(level0)
    assert_number(level0[1][2])
    local level1 = pool.dump[level0[1][2]]
    assert_table(level1)
    assert_number(level1[1][2])
    local level2 = pool.dump[level1[1][2]]
    assert_table(level2)
    assert_nil(level2[1]) -- no more recursion
end
