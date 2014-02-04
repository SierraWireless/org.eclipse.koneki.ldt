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
-- DBGp debugger tests for utility functions
-------------------------------------------------------------------------------
require "lunatest"
require "lunatest_xassert"
module(..., package.seeall)

-- Load debugger itself to access virtual "core" module
local debugger = require "debugger"
local dbgp = require "debugger.dbgp"
local core = require "debugger.core"
local util = require "debugger.util"
local platform = require "debugger.platform"
local introspection = require "debugger.introspection"

platform.init()

-- bypass base64 encoding (make tests more simple)
local identity = function(arg) return arg end
util.b64, util.rawb64, util.unb64 = identity, identity, identity

-------------------------------------------------------------------------------
--  Tests
-------------------------------------------------------------------------------

function test_arg_parse()
    local result = dbgp.arg_parse("-i 12 -n name -v a-value")
    assert_equal("12", result.i)
    assert_equal("name", result.n)
    assert_equal("a-value", result.v)
end

function test_cmd_parse()
    -- without data
    local cmd, args, data = dbgp.cmd_parse("command -a arg1 -b arg2")
    assert_equal("command", cmd)
    assert_equal("arg1", args.a)
    assert_equal("arg2", args.b)
    assert_nil(data)
    
    -- with data (base64 part is bypassed as mime fake use identity functions)
    cmd, args, data = dbgp.cmd_parse("command -a arg1 -b arg2 -- some data")
    assert_equal("command", cmd)
    assert_equal("arg1", args.a)
    assert_equal("arg2", args.b)
    assert_equal("some data", data)
end

function test_is_path_absolute()
    assert_true(platform.is_path_absolute("/foo/bar"))
    assert_false(platform.is_path_absolute("./foo/bar"))
end

function test_get_uri()
    assert_equal("file:///foo/bar", platform.get_uri("@/foo/bar"))
    assert_equal("file://"..platform.base_dir.."/foo/bar", platform.get_uri("@./foo/bar"))
    assert_false(platform.get_uri("foo"))
end

function test_get_path()
    assert_equal("/foo/bar", platform.get_path("file:///foo/bar"))
    assert_equal("/foo/bar baz", platform.get_path("file:///foo/bar%20baz"))
end

function test_generate_key() skip() -- not exposed by introspection
    assert_equal("12", util.generate_key(12))
    assert_equal('"foo"', util.generate_key("foo"))
    assert_equal([["foo\"bar"]], util.generate_key("foo\"bar"))
    assert_equal('"foo\\\nbar"', util.generate_key("foo\nbar"))
    assert_match("key_cache%[%d+%]", util.generate_key{})
end

function test_make_error()
    local err = dbgp.make_error(100, "error message")
    assert_equal("error", err.tag)
    assert_equal(100, err.attr.code)
    assert_equal(1, #err)
    assert_equal("message", err[1].tag)
    assert_equal(1, #err[1])
    assert_equal("error message", err[1][1])
end

function test_make_property()
    -- basic property
    local prop = introspection.make_property(0, "a string", "str", "a.b.str", 0, 32, 0)
    assert_table_equal({tag="property", attr={
        name = '["str"]',
        fullname = "0|a.b.str",
        type = "string",
        children = 0,
        encoding = "base64",
        size = 10,
    }, '"a string"' }, prop)
    
    -- table with children (fully recursive)
    local f = coroutine.create(function() end)
    local subtable = setmetatable({ foo = "bar" }, {__tostring=function() return "hello!" end }) -- metatable will be used but not dumped
    local tbl = { "str", 56, f, subtable }
    prop = introspection.make_property(0, tbl, "tbl", nil, 2, 32, 0)
    assert_table_equal({tag="property", 
        attr={
            name = '["tbl"]',
            fullname = '0|(...)["tbl"]',
            type = "sequence",
            children = 1,
            numchildren = 4,
            page = 0,
            pagesize = 32,
            encoding = "base64",
            size = #tostring(tbl),
        },
        tostring(tbl), -- unpredictible
        { tag = "property", 
          attr = { name = '[1]', fullname = '0|(...)["tbl"][1]', type = "string", children = 0, encoding = "base64", size = 5, },
          '"str"' },
        { tag = "property", 
          attr = { name = '[2]', fullname = '0|(...)["tbl"][2]', type = "number", children = 0, encoding = "base64", size = 2, },
          "56" },
        { tag = "property", 
          attr = { name = '[3]', fullname = '0|(...)["tbl"][3]', type = "thread", children = 0, encoding = "base64", size = #tostring(f), },
          tostring(f) },
        { tag = "property", 
          attr = { name = '[4]', fullname = '0|(...)["tbl"][4]', type = "table", children = 1, numchildren = 2, page = 0, pagesize = 32, encoding = "base64", size = 6, },
          "hello!",
          { tag = "property", 
            attr = { name = '["foo"]', fullname = '0|(...)["tbl"][4]["foo"]', type = "string", children = 0, encoding = "base64", size = 5, },
            '"bar"' },
          { tag = "property",
            attr = { name = 'metatable', fullname = '0|metatable[(...)["tbl"][4]]', type = "special", children = 1, numchildren = 1, page = 0, pagesize = 32, encoding = "base64", size = #tostring(getmetatable(subtable)), },
            
              tostring(getmetatable(subtable)),
              -- child function is not encoded (too deep)
          },
        } ,
      }, prop )

    -- table with a recursion limit
    prop = introspection.make_property(0, { 1,2,{{{3}}} }, "tbl", nil, 2, 32, 0)
    assert_equal(4, #prop) -- string repr + 3 children
    assert_table(prop[4][2])
    assert_nil(prop[4][2][2]) -- recursion stopped
    
    -- partial table
    local t = {}
    for i = 1, 1000 do t[i] = i*2 end
    prop = introspection.make_property(0, t, "tbl", nil, 2, 32, 3)
    assert_equal(3, prop.attr.page)
    assert_equal(33, #prop)
    for i=1, 32 do assert_equal("[".. i+96 .."]", prop[i+1].attr.name) end
end

function test_breakpoint_manager()
    -- add 3 breakpoints
    local bp1 = { type = "line",  sess = { }, state = "enabled", temp = false, hit_count = 0, filename = "file:///script.lua", lineno = 100, hit_condition = ">=", hit_value = 0 }
    local bp1id = core.breakpoints.insert(bp1, "file:///script.lua", 100)
    local bp2 = { type = "line",  sess = { }, state = "enabled", temp = false, hit_count = 0, filename = "file:///script.lua", lineno = 100, hit_condition = ">=", hit_value = 0 }
    local bp2id = core.breakpoints.insert(bp2, "file:///script.lua", 100)
    local bp3 = { type = "line",  sess = { }, state = "enabled", temp = false, hit_count = 0, filename = "file:///script.lua", lineno = 70, hit_condition = ">=", hit_value = 0 }
    local bp3id = core.breakpoints.insert(bp3, "file:///script.lua", 70)
    assert_number(bp1id)
    assert_number(bp2id)
    assert_number(bp3id)
    assert_not_equal(bp1id, bp2id)
    assert_not_equal(bp1id, bp3id)
    assert_not_equal(bp2id, bp3id)
    
    assert_true(core.breakpoints.at("file:///script.lua", 100))
    assert_equal(bp1, core.breakpoints.get(bp1id))
    
    assert_true(core.breakpoints.remove(bp1id))
    assert_true(core.breakpoints.at("file:///script.lua", 100))
    assert_true(core.breakpoints.at("file:///script.lua", 70))
    assert_nil(core.breakpoints.get(bp1id))
    
    assert_true(core.breakpoints.remove(bp2id))
    assert_false(core.breakpoints.at("file:///script.lua", 100))
    assert_true(core.breakpoints.at("file:///script.lua", 70))
    assert_true(core.breakpoints.remove(bp3id))
    assert_false(core.breakpoints.remove(bp3id))
    assert_false(core.breakpoints.at("file:///script.lua", 70))
end

function test_xml_generator()
    -- this test relies on a space added a
    assert_equal('<tag/>', dbgp.lom2str({tag="tag"}))
    
    -- FIXME since the order of iteration for attributes is not defined, multiple attributes is not tested
    assert_equal('<tag attr="12"/>', dbgp.lom2str{tag="tag", attr={attr=12}})
    assert_equal('<tag attr="&lt;foo&amp;bar>"/>', dbgp.lom2str{tag="tag", attr={attr="<foo&bar>"}})
    
    assert_equal('<parent><child pos="1"/><child pos="2"/></parent>', dbgp.lom2str{ tag="parent",
        { tag="child", attr={pos=1} },
        { tag="child", attr={pos=2} },
    })
    assert_equal('<content><![CDATA[This is the &content>]]></content>',  dbgp.lom2str{ tag="content", "This is the &content>"})
    assert_equal('<content><before/><![CDATA[Text]]><after/></content>',  dbgp.lom2str{ tag="content", 
        { tag="before" },
        "Text",
        { tag="after" },
    })
end

function test_xml_generator_bench() skip()
    local properties = { tag = "properties", }
    for k,v in pairs(_G) do
        table.insert(properties, introspection.make_property(0, v, k, nil, 2, 128, 0))
    end
    for i=1, 100 do
        dbgp.lom2str(properties)
    end
end
