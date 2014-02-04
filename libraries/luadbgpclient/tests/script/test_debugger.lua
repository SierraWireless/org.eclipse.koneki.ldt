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
-- DBGp debugger functional tests
-------------------------------------------------------------------------------
-- These tests runs a new Lua process with debugging enabled and use a minimal DBGp server
-- implemented with LuaSocket.
-- The child processes are controlled only with standard function so the control is limited:
-- if a test fail, it can be blocked, or else the child process can be lost. This is because 
-- the test has no way to kill child processes
-- This text expects that testing VM (this script) and tested VMs (tests scripts) are the same
-- version.

-- VM version specific stuff
local loadin
if _VERSION == "Lua 5.2" then
    loadin = function(src, env) return load(src, nil, nil, env) end
    newproxy = function() return setmetatable({ }, { }) end -- __gc on table work as expected on 5.2
    table.getn = function(t) return #t end -- for luaxml
else
    loadin = function(src, env) return setfenv(loadstring(src), env) end
end

module(..., package.seeall)

LUA_EXECUTABLE = LUA_EXECUTABLE or "lua"
LUA_OPTS = "" -- additional options for lua executable
OUTPUT_FILE = "/dev/null" -- used as stdout for debugged program, in particular the should be "nul" for windows
DBGP_HOST = "localhost"
DBGP_PORT = 16843
DBGP_TRANSPORT = DBGP_TRANSPORT or "luasocket"
SOCKET_TIMEOUT = 5 -- timeout for socket operations (seconds)
RM_EXEC = "rm -f '%s'" -- command to remove a file
DIR_SEP = package.config:sub(1,1) -- undocumented, but works also on 5.1

local function tmpdir()
    return io.popen("mktemp -d"):read("*a"):sub(1, -2) -- skip final newline
end

require "lunatest"
require "lunatest_xassert"
require "luaxml.xml"
require "luaxml.handler"

-- debugger stuff used to run tests (we do not actually start the debugger)
require "debugger"
local transport = require(DBGP_TRANSPORT)
local url = require "debugger.url"

local function XMLparse(xml)
    local handler = simpleTreeHandler()
    -- some tags are never reduced to ease length tests
    handler.options.noreduce.stack = true
    xmlParser(handler):parse(xml)
    return handler.root
end

local function unb64property(prop)
    if prop[1] then prop[1] = transport.unb64(prop[1]) end
    if prop.property then
        for _, sub in ipairs(type(prop.property[1]) == "table" and prop.property or { prop.property }) do
            unb64property(sub)
        end
    end
    return prop
end

local rawb64 = transport.rawb64

local function writefile(filename, data)
    local f = io.open(filename, "w")
    f:write(data)
    f:close()
end

-------------------------------------------------------------------------------
--  Implementation of a basic DBGp server
-------------------------------------------------------------------------------

local server -- DBGp server socket

-- Launches a subprocess
local debugger = { transaction_id = 0 }
debugger.__index = debugger

--- Builds a debugger instance from running given filename
-- Returns a debugger instance and the init packet
function debugger:from_file(file, bootstrap)
    bootstrap = bootstrap and (bootstrap .. ";") or ""
    bootstrap = bootstrap .. string.format('require("debugger")(%q, %d, nil, %q)', DBGP_HOST, DBGP_PORT, DBGP_TRANSPORT)
    local handle = io.popen(string.format([["%s" %s -e '%s' "%s" > "%s"]], LUA_EXECUTABLE, LUA_OPTS, bootstrap, file, OUTPUT_FILE))
    local skt = assert(server:accept())
    --skt:settimeout(SOCKET_TIMEOUT)
    
    local dbg = setmetatable({
        uri = url.build{ scheme="file", path=file, authority=""}, -- authority is to have uri of form scheme://...
        handle = handle,
        skt = skt,
    }, self)
    -- to be sure to delete the temp file at exit
    dbg.remover = newproxy(true)
    getmetatable(dbg.remover).__gc = function() assert(os.execute(RM_EXEC:format(file)) == 0) end 
    
    local init = dbg:receive()
    return dbg, init
end

-- Builds a debugger instance running given script (written to a temporary file)
function debugger:from_script(script)
    local filename = os.tmpname()
    writefile(filename, script)
    return self:from_file(filename)
end

--- Reads a DBGp XML packet from the socket and returns the corresponding XML tree
function debugger:receive()
    -- read size and the null separator
    local size = { }
    while true do
        local byte = assert(self.skt:receive(1))
        if byte == "\000" then break end
        size[#size + 1] = byte
    end
    size = assert(tonumber(table.concat(size)))
    
    -- read the XML packet
    data = assert(self.skt:receive(size))
    -- and the terminator
    assert(assert(self.skt:receive(1)) == "\000")
    
    return XMLparse(data)
end

function debugger:send(command, args, data)
    args = args or {}
    args.i = self.transaction_id + 1
    self.transaction_id = args.i
    
    -- build command
    local cmd = { command }
    for k, v in pairs(args) do cmd[#cmd+1] = "-"..k.." "..tostring(v) end
    cmd = table.concat(cmd, " ")
    if data then cmd = cmd .. " --" .. rawb64(data) end
    
    self.skt:send(cmd.."\000")
end

function debugger:command(command, args, data, expect_error)
    self:send(command, args, data)
    local resp = self:receive(self.skt)
    assert_table(resp.response)     -- test that the response is actually a response
    local err = resp.response.error
    if err and not expect_error then -- test that the response is error-free
        fail(string.format("Got error %s (%s) for command %s", err._attr.code, tostring(err.message), command))
    end
    return resp.response
end

function debugger:stop()
    local resp = self:command("stop")
    self.skt:close()
    -- wait for child process to finish
    self.handle:read("*a")
    return resp
end

function debugger:wait()
    local ok, err = self.skt:receive(1)
    assert_nil(ok, "Debugger send something instead of dying")
    assert_equal("closed", err, "Wrong error when waiting for finish")
    self.skt:close()
end

-------------------------------------------------------------------------------
--  DBGp server startup and shutdown
-------------------------------------------------------------------------------
function suite_setup()
    server = assert(transport.create())
    assert(server:bind("*", DBGP_PORT))
    assert(server:listen(32))
    --server:settimeout(SOCKET_TIMEOUT)
    if server.setoption then
        server:setoption('reuseaddr', true) -- force reuse addr for LuaSocket
    end
end

function suite_teardown()
    server:close()
    server = nil
    collectgarbage "collect"
end

-------------------------------------------------------------------------------
--  Basic tests
-------------------------------------------------------------------------------
--- Just starts & stop a debugger, tests basic status handling
function test_basic() -- skip() --
    local dbg, init = debugger:from_script([[ print"Hello, world !" ]])
    assert_equal("luaidekey", init.init._attr.idekey)
    assert_equal("Lua", init.init._attr.language)
    assert_equal("1.0", init.init._attr.protocol_version)
    assert_equal("urn:debugger_protocol_v1", init.init._attr.xmlns)
    
    local status = dbg:command("status")
    assert_equal("starting", status._attr.status)
    assert_equal("ok", status._attr.reason)
    
    -- our implementation respond to stop
    local stop = dbg:stop()
    assert_equal("stopped", stop._attr.status)
    assert_equal("ok", stop._attr.reason)
end

function test_source() -- skip() --
    local script = {
        "-- This is a sample script to",
        "-- test correct handling of source command",
        "local a = 2",
        "local b = 3",
        "local c = 4",
        "local d = 6"
    }
    local function get_lines(b,e) return table.concat(script, "\n", b or 1, e or #script).."\n" end
    
    local dbg = debugger:from_script(get_lines())
    
    -- try to get the whole source
    local resp = dbg:command("source", {f=dbg.uri})
    assert_equal("1", resp._attr.success)
    assert_equal(get_lines(), transport.unb64(resp[1]), "Response is not the whole script")
    
    -- only the 2 first lines
    resp = dbg:command("source", {f=dbg.uri, e=2})
    assert_equal("1", resp._attr.success)
    assert_equal(get_lines(1,2), transport.unb64(resp[1]), "Response is not the 2 first lines")
    
    -- only the 2 last lines
    resp = dbg:command("source", {f=dbg.uri, b=#script-1})
    assert_equal("1", resp._attr.success)
    assert_equal(get_lines(#script-1), transport.unb64(resp[1]), "Response is not the 2 last lines")
    
    -- lines from 2 to 4
    resp = dbg:command("source", {f=dbg.uri, b=2, e=4})
    assert_equal("1", resp._attr.success)
    assert_equal(get_lines(2,4), transport.unb64(resp[1]), "Response is not the 2 to 4 lines")
    
    -- unknown uri
    resp = dbg:command("source", {f="file:///foo/bar/baz"}, nil, true)
    assert_equal("0", resp._attr.success)
    assert_equal("100", resp.error._attr.code)
    assert_nil(resp[1])
    
    -- test for current stack level
    dbg:command("breakpoint_set", {t="line", f=dbg.uri, n=3})
    dbg:command("run")
    resp = dbg:command("source")
    assert_equal("1", resp._attr.success)
    assert_equal(get_lines(), transport.unb64(resp[1]), "Response is not the current script")
    
    dbg:stop()
end

-- test that spaces are correctly handled in URIs (and later dynamic code, ...)
function test_uri_handling() -- skip() --
    local base = os.tmpname()
    os.execute(RM_EXEC:format(base)) -- the file is created
    local filename = base.." with spaces.lua"

    writefile(filename, [[ print"Hello, world !" ]])
    local dbg = debugger:from_file(filename)
    dbg:command("breakpoint_set", {t="line", f=dbg.uri, n=1})
    local resp = dbg:command("run")
    
    local resp = dbg:command("stack_get")
    assert_equal(dbg.uri, resp.stack[1]._attr.filename)
    dbg:stop()
end

--- Try to get debugger features
function test_feature_get() -- skip() --
    local dbg = debugger:from_script([[ print"Hello, world !" ]])
    
    local resp = dbg:command("feature_get", {n="language_name"})
    assert_equal(1, tonumber(resp._attr.supported), "language_name should be a supported feature.")
    assert_equal("Lua", resp[1])
    
    --TODO: make adaptative assert
    assert_equal(_VERSION, dbg:command("feature_get", {n="language_version"})[1])
    
    assert_equal(0, tonumber(dbg:command("feature_get", {n="language_supports_threads"})[1]))
    
    -- Test for a command
    assert_equal(1, tonumber(dbg:command("feature_get", {n="breakpoint_get"})._attr.supported), "Supported command not found")
    -- Test for an unknown feature/command
    assert_equal(0, tonumber(dbg:command("feature_get", {n="foobar"})._attr.supported), "Unexistant feature/command marked as supported")
    
    dbg:stop()
end

function test_feature_set() -- skip() --
    local dbg = debugger:from_script([[ print"Hello, world !" ]])
    -- Set a real feature
    assert_equal(1, tonumber(dbg:command("feature_set", {n="max_data", v=1024})._attr.success), "Cannot set a feature")
    assert_equal(1024, tonumber(dbg:command("feature_get", {n="max_data"})[1]))
    -- Set a non-existent feature
    assert_equal(0, tonumber(dbg:command("feature_set", {n="foobar", v=1024})._attr.success), "Can set a non-existent feature")
    dbg:stop()
end

function test_module_resolution() -- skip() --
    local root = tmpdir()
    local mainpath, submodulepath = root .. DIR_SEP .. "main.lua", root .. DIR_SEP .. "submodule.lua"

    writefile(submodulepath, [[
        return {
            func = function(a,b)
                return a*b
            end
        }
    ]])

    writefile(mainpath, [[
        local submodule = require "submodule"
        var = submodule.func(5, 6)
    ]])

    --FIXME: a limitation in debugger makes impossible to change package.path after is started
    local dbg = debugger:from_file(mainpath, string.format("package.path = %q .. \";\" .. package.path", root..DIR_SEP.."?.lua"))
    assert_equal(1, tonumber(dbg:command("feature_set", {n="uri", v="module"})._attr.success), "cannot enable module resolution")
    dbg:command("breakpoint_set", {t="line", f="module:///main", n=2})
    dbg:command("breakpoint_set", {t="line", f="module:///submodule", n=3})
    dbg:command("run")

    local resp = dbg:command("stack_get")
    assert_equal("module:///main", resp.stack[1]._attr.filename)
    dbg:command("run")

    local resp = dbg:command("stack_get")
    assert_equal("module:///submodule", resp.stack[1]._attr.filename)

    dbg:stop()
end

-------------------------------------------------------------------------------
--  Breakpoint tests
-------------------------------------------------------------------------------

-- Script used to test breakpoint handling
local FACT_SCRIPT = { code = [[
factorial = function(n)
    if n == 0 then
        return 1
    else
        return n * factorial(n - 1)
    end
end

local mem = { }
memoized_factorial = function(n)
    if mem[n] then
        return mem[n]
    else
        local result = factorial(n)
        mem[n] = result
        return result
    end
end

local a = factorial(5)
local b = memoized_factorial(3) + a
]] }
-- gather some informations about line numbers
do
    local env = { }
    local chunk = loadin(FACT_SCRIPT.code, env)
    chunk()
    
    local function set2list(set)
        local list = { }
        for k, _ in pairs(set) do list[#list + 1] = k end
        table.sort(list)
        return list
    end
    
    FACT_SCRIPT.main_lines = set2list(debug.getinfo(chunk, "L").activelines)
    FACT_SCRIPT.factorial_lines = set2list(debug.getinfo(env.factorial, "L").activelines)
    FACT_SCRIPT.memoized_factorial_lines = set2list(debug.getinfo(env.memoized_factorial, "L").activelines)
end


-- test for breakpoint get/list
function test_breakpoint_get_list() -- skip() --
    local dbg = debugger:from_script(FACT_SCRIPT.code)
    -- set some breakpoints
    local bp1 = dbg:command("breakpoint_set", {t="line", f=dbg.uri, s="enabled",  n=FACT_SCRIPT.factorial_lines[1]})._attr.id
    local bp2 = dbg:command("breakpoint_set", {t="line", f=dbg.uri, s="disabled",  n=FACT_SCRIPT.memoized_factorial_lines[1]})._attr.id
    local bp3 = dbg:command("breakpoint_set", {t="line", f=dbg.uri, h=2, o="%", n=FACT_SCRIPT.factorial_lines[2]})._attr.id
    local bp4 = dbg:command("breakpoint_set", {t="conditional", f=dbg.uri, n=FACT_SCRIPT.memoized_factorial_lines[3]}, "n == 3")._attr.id
    
    -- build expected data
    local bp_data = {
        [bp1] = { id=bp1, type="line", state="enabled", filename=dbg.uri, lineno=tostring(FACT_SCRIPT.factorial_lines[1]), hit_value="0", hit_condition=">=", hit_count="0", },
        [bp2] = { id=bp2, type="line", state="disabled", filename=dbg.uri, lineno=tostring(FACT_SCRIPT.memoized_factorial_lines[1]), hit_value="0", hit_condition=">=", hit_count="0", },
        [bp3] = { id=bp3, type="line", state="enabled", filename=dbg.uri, lineno=tostring(FACT_SCRIPT.factorial_lines[2]), hit_value="2", hit_condition="%", hit_count="0", },
        [bp4] = { id=bp4, type="conditional", state="enabled", filename=dbg.uri, lineno=tostring(FACT_SCRIPT.memoized_factorial_lines[3]), hit_value="0", hit_condition=">=", hit_count="0", },
    }
    
    -- try to get a breakpoint
    local resp = dbg:command("breakpoint_get", {d=bp1})
    assert_table_equal(bp_data[bp1], resp.breakpoint._attr, "Line breakpoint attributes (bp1) does not match")
    assert_nil(resp.breakpoint.expression, "The expression should not be provided for a line breakpoint")
    
    resp = dbg:command("breakpoint_get", {d=bp4})
    assert_table_equal(bp_data[bp4], resp.breakpoint._attr, "Conditional breakpoint attributes (bp4) does not match")
    assert_equal("n == 3", resp.breakpoint.expression, "The break condition does not match")
    
    -- non existent breakpoint
    resp = dbg:command("breakpoint_get", {d=1234}, nil, true)
    assert_equal("205", resp.error._attr.code, "Wrong error code for a non existent breakpoint")
    
    -- try to list breakpoints
    resp = dbg:command("breakpoint_list")
    assert_equal(4, #resp.breakpoint, "Wrong breakpoint count")
    for _, bp in ipairs(resp.breakpoint) do
        assert_table_equal(bp_data[bp._attr.id], bp._attr, "breakpoint attributes does not match for breakpoint "..bp._attr.id)
    end
    
    dbg:stop()
end

function test_line_breakpoints() -- skip() --
    local dbg = debugger:from_script(FACT_SCRIPT.code)
    local resp
    
    -- Set breakpoints at first lines of main code (next to last main line) and factorial function (first factorial line)
    resp = dbg:command("breakpoint_set", {t="line", f=dbg.uri, n=FACT_SCRIPT.main_lines[#FACT_SCRIPT.main_lines-1]})
    assert_equal("enabled", resp._attr.state, "Wrong default state for a line breakpoint")
    assert_string(resp._attr.id)
    local bp1 = resp._attr.id
    
    local bp2 = dbg:command("breakpoint_set", {t="line", f=dbg.uri, n=FACT_SCRIPT.factorial_lines[1]})._attr.id
    
    resp = dbg:command("run")
    assert_equal("break", resp._attr.status)
    assert_equal("ok", resp._attr.reason)
    
    -- debugger should have stopped at main level with a single stack level
    assert_equal("1", dbg:command("stack_depth")._attr.depth, "Wrong stack depth")
    resp = dbg:command("stack_get")
    assert_equal(1, #resp.stack, "Wrong stack size")
    assert_table_equal({ type = "file", level = "0", filename = dbg.uri, lineno = tostring(FACT_SCRIPT.main_lines[#FACT_SCRIPT.main_lines-1]) },
                       resp.stack[1]._attr, "Wrong stack level (level 0, main code)")
    
    -- now the debugger should run until second breakpoint, into factorial
    resp = dbg:command("run")
    assert_equal("break", resp._attr.status)
    assert_equal("2", dbg:command("stack_depth")._attr.depth, "Wrong stack depth")
    resp = dbg:command("stack_get")
    assert_equal(2, #resp.stack, "Wrong stack size")
    assert_table_equal({ type = "file", level = "0", filename = dbg.uri, where = "factorial", lineno = tostring(FACT_SCRIPT.factorial_lines[1]) },
                       resp.stack[1]._attr, "Wrong stack level (level 0, factorial)")
    assert_table_equal({ type = "file", level = "1", filename = dbg.uri, lineno = tostring(FACT_SCRIPT.main_lines[#FACT_SCRIPT.main_lines-1]) },
                       resp.stack[2]._attr, "Wrong stack level (level 1, main code)")
    
    assert_equal("1", dbg:command("breakpoint_get", {d=bp1}).breakpoint._attr.hit_count, "Wrong hit count")
    assert_equal("1", dbg:command("breakpoint_get", {d=bp2}).breakpoint._attr.hit_count, "Wrong hit count")
    dbg:stop()
end

function test_disable_breakpoint() -- skip() --
    local dbg = debugger:from_script(FACT_SCRIPT.code)
    local resp
    
    dbg:command("breakpoint_set", {t="line", f=dbg.uri, s="disabled", n=FACT_SCRIPT.main_lines[#FACT_SCRIPT.main_lines-1]})
    resp = dbg:command("breakpoint_set", {t="line", f=dbg.uri, s="enabled",  n=FACT_SCRIPT.factorial_lines[1]})
    local bpid = resp._attr.id
    
    assert_equal("break", dbg:command("run")._attr.status)
    -- the debugger should have passed the first breakpoint (at main level), and stopped to the one in factorial
    assert_equal("2", dbg:command("stack_depth")._attr.depth, "Wrong stack depth")
    resp = dbg:command("stack_get")
    assert_equal(2, #resp.stack, "Wrong stack size")
    assert_equal(tostring(FACT_SCRIPT.factorial_lines[1]), resp.stack[1]._attr.lineno)
    
    -- disable breakpoint so the program should not stop again
    dbg:command("breakpoint_update", {d=bpid, s="disabled"})
    dbg:send("run")
    dbg:wait()
end

function test_remove_breakpoint() -- skip() --
    local dbg = debugger:from_script(FACT_SCRIPT.code)
    resp = dbg:command("breakpoint_set", {t="line", f=dbg.uri, s="enabled",  n=FACT_SCRIPT.factorial_lines[1]})
    local bpid = resp._attr.id
    assert_equal("break", dbg:command("run")._attr.status)
    -- remove breakpoint so the program should not stop again
    dbg:command("breakpoint_remove", {d=bpid})
    dbg:send("run")
    dbg:wait() -- FIXME cannot test that program has not exited in error state
end

function test_conditional_breakpoint() -- skip() --
    local script = [[
    local a,b,c,d,e,f=34; for i = 1, 10 do
        var = i
    end
    ]]
    local dbg, resp
    
    -- ok breakpoint
    dbg = debugger:from_script(script)
    resp = dbg:command("breakpoint_set", {t="conditional", f=dbg.uri, n=2}, "i > 3 and i <= 5")
    dbg:command("run") -- stop at i = 4
    dbg:command("run") -- stop at i = 5
    dbg:send("run")    -- will not stop again
    dbg:wait()
    
    -- syntax error
    dbg = debugger:from_script(script)
    resp = dbg:command("breakpoint_set", {t="conditional", f=dbg.uri, n=2}, "i > 3 and ", true)
    assert_table(resp.error, "Syntax error doesn't cause exception")
    assert_equal("207", resp.error._attr.code, "Wrong error code")
    
    -- logic error
    resp = dbg:command("breakpoint_set", {t="conditional", f=dbg.uri, n=2}, "nil + 5 == 5")
    dbg:command("run")
    -- should have stopped because of error
    resp = dbg:command("stack_get")
    assert_equal("2", resp.stack[1]._attr.lineno)
    dbg:stop()
end

function test_breakpoint_hit_condition() -- skip() --
    local dbg = debugger:from_script[[
    for i = 1, 10 do
        var = i
    end
    ]]
    
    local resp = dbg:command("breakpoint_set", {t="line", f=dbg.uri, n=2, h=8, o=">="})
    dbg:command("run") -- stop at 8th hit
    dbg:command("run") -- stop at 9th hit
    -- test hit count
    assert_equal("9", dbg:command("breakpoint_get", {d=resp._attr.id}).breakpoint._attr.hit_count, "Wrong hit count")
    
    dbg:command("run") -- stop at 10th hit
    dbg:send("run")    -- will not stop again
    dbg:wait()
end

function test_temporary_breakpoint() -- skip() --
    local dbg = debugger:from_script[[
    for i = 1, 10 do
        var = i
    end
    ]]
    
    local bp = dbg:command("breakpoint_set", {t="line", f=dbg.uri, n=2, r=1})._attr.id
    dbg:command("run")
    
    assert_equal("205", dbg:command("breakpoint_get", {d=bp}, nil, true).error._attr.code, "Temporary breakpoint should not exist anymore")
    
    dbg:send("run")    -- will not stop again
    dbg:wait()
end

-------------------------------------------------------------------------------
--  Step tests
-------------------------------------------------------------------------------

-- test step over/into/out
function test_basic_steps() -- skip() --
    local dbg = debugger:from_script[[
        function func(val)
            local a = val
            local b = a + a
            return a + b
        end
        func(15)
        func(1)
        local dynfunc = loadstring("return 1")
        dynfunc()
        var = "finished"
    ]]
    
    resp = dbg:command("breakpoint_set", {t="line", f=dbg.uri, n=6})
    assert_equal("break", dbg:command("run")._attr.status)
    assert_equal("6", dbg:command("stack_get").stack[1]._attr.lineno, "Not stopped at right place")
    -- go into func
    assert_equal("break", dbg:command("step_into")._attr.status)
    assert_equal("2", dbg:command("stack_get").stack[1]._attr.lineno, "Not at first line of func")
    -- step out of func and get back in main
    assert_equal("break", dbg:command("step_out")._attr.status)
    assert_equal("7", dbg:command("stack_get").stack[1]._attr.lineno, "Not after the first call to func")
    -- step over the second call
    assert_equal("break", dbg:command("step_over")._attr.status)
    assert_equal("8", dbg:command("stack_get").stack[1]._attr.lineno, "Not after the second call to func")
    -- try to step into a C function (loadstring here)
    assert_equal("break", dbg:command("step_into")._attr.status)
    assert_equal("9", dbg:command("stack_get").stack[1]._attr.lineno, "Not after the call to a C function")
    -- try to step into a dynamic function (currently not handled : just step over, like C)
    assert_equal("break", dbg:command("step_into")._attr.status)
    assert_equal("10", dbg:command("stack_get").stack[1]._attr.lineno, "Not after the call to the dynamic function")
    dbg:stop()
end

function test_break_on_first_line() -- skip() --
    local dbg = debugger:from_script[[
    for i = 1, 10 do
        var = i
    end
    ]]

    dbg:command("step_into")
    assert_equal("1", dbg:command("stack_get").stack[1]._attr.lineno, "Not after the call to a C function")
end

-- Test step over/into resume/yield
function test_step_coro() -- skip() --
    local dbg = debugger:from_script[[
        local co = coroutine.create(function()  -- 01
            coroutine.yield(1)                  -- 02
            coroutine.yield(2)                  -- 03
        end)                                    -- 04
        local it = coroutine.wrap(function()    -- 05
            coroutine.yield("first")            -- 06
            return "second"                     -- 07
        end)                                    -- 08
                                                -- 09
        coroutine.resume(co)                    -- 10
        coroutine.resume(co)                    -- 11
        it()                                    -- 12
        it()                                    -- 13
        a = 2                                   -- 14
    ]]
    
    dbg:command("breakpoint_set", {t="line", f=dbg.uri, n=10}) -- run until first coroutine.resume(co)
    assert_equal("break", dbg:command("run")._attr.status)
    assert_equal("10", dbg:command("stack_get").stack[1]._attr.lineno, "Not stopped at right place")
    
    -- test for coroutine list, at this time no coroutine is reurned because they are not already started
    local coro_list = dbg:command("coroutine_list")
    assert_nil(coro_list.coroutine)
    
    -- step into the resume => go into the coroutine
    assert_equal("break", dbg:command("step_into")._attr.status)
    assert_equal("2", dbg:command("stack_get").stack[1]._attr.lineno, "Not into the coroutine")
    -- test that the running coro is co (i.e. not main in our situation)
    local coro_list = dbg:command("coroutine_list")
    assert_not_nil(coro_list.coroutine._attr, "there is multiple coroutine present")
    assert_equal("1", coro_list.coroutine._attr.running)
    -- get coroutine id for future interactions
    local co_id = coro_list.coroutine._attr.id
    assert_not_nil(co_id, "Cannot get coroutine id")
    -- get stack with coroutine identifier
    assert_equal("2", dbg:command("stack_get", {o=co_id}).stack[1]._attr.lineno, "Cannot query current coroutine with its ID")
    
    -- step into the yield => get back to main
    assert_equal("break", dbg:command("step_into")._attr.status)
    assert_equal("11", dbg:command("stack_get").stack[1]._attr.lineno, "Not back to main, after the first yield")
    -- query coroutine stack
    assert_equal("2", dbg:command("stack_depth", {o=co_id})._attr.depth, "Wrong stack depth for coroutine") -- yield call is the 1st level
    assert_equal("2", dbg:command("stack_get", {o=co_id}).stack[2]._attr.lineno, "Cannot query another coroutine")
    
    -- step over the resume => just jump over the coroutine
    assert_equal("break", dbg:command("step_over")._attr.status)
    assert_equal("12", dbg:command("stack_get").stack[1]._attr.lineno, "Not after the second resume")
    -- step into the iterator => go into it
    assert_equal("break", dbg:command("step_into")._attr.status)
    assert_equal("6", dbg:command("stack_get").stack[1]._attr.lineno, "Not into the wrapped coroutine")
    -- check that the iterator has been detected as coroutine
    local coro_list = dbg:command("coroutine_list")
    assert_len(2, coro_list.coroutine)
    -- exactly one of the 2 coroutines is running
    assert_true((coro_list.coroutine[1]._attr.running == "1") ~= (coro_list.coroutine[2]._attr.running == "1"), "Not one coroutine running (0 or 2)")
    
    -- step into the yield => get back to main
    assert_equal("break", dbg:command("step_into")._attr.status)
    assert_equal("13", dbg:command("stack_get").stack[1]._attr.lineno, "Not back to main, after the first yield (wrapped coroutine)")
    -- step over the call => just jump over the iterator
    assert_equal("break", dbg:command("step_over")._attr.status)
    assert_equal("14", dbg:command("stack_get").stack[1]._attr.lineno, "Not after the second call to wrapped coroutine")
    
    -- the wrapped coroutine is dead, it should not be listed anymore
    local coro_list = dbg:command("coroutine_list")
    assert_not_nil(coro_list.coroutine._attr, "Dead coroutine not removed from coroutine_list")
    assert_equal(co_id, coro_list.coroutine._attr.id, "The alive coroutine does not have the correct id")
    dbg:stop()
end

function test_unknown_coro() -- skip() --
    local dbg = debugger:from_script"local a = 0\n"
    local resp = dbg:command("stack_get", {o="some non existant id"}, nil, true)
    assert_table(resp.error, "The property_get command has not caused an error with local")
    assert_equal("399", resp.error._attr.code, "The error code does not match")
    dbg:stop()
end

function test_unreachable_stack_levels() -- skip() --
    -- test for a C stack level
    local dbg = debugger:from_script[[
    local func = function(...) return ... end
    pcall(func)
    ]]
    dbg:command("breakpoint_set", {t="line", f=dbg.uri, n=2})
    dbg:command("run")
    dbg:command("step_into") -- jump over foreach and go into func
    assert_equal("3", dbg:command("stack_depth")._attr.depth, "Wrong stack depth (C)")
    local resp = dbg:command("stack_get")
    assert_equal(3, #resp.stack, "Wrong stack size (C)")
    assert_equal("1", resp.stack[1]._attr.lineno, "Not into func (C)")
    assert_equal("ccode:/", resp.stack[2]._attr.filename, "Wrong C code special URI")
    -- variables should be query-able (but returns an empty list)
    resp = dbg:command("context_get", {c=0, d=1})
    assert_nil(resp.property, "A C function should not have properties")
    dbg:stop()
    
    --FIXME tail calls are not detected for LuaJIT
    if jit then return end
    
    -- test for a tail return
    local dbg = debugger:from_script[[
    local function do_stuff(a)
        return a * 2
    end
    local function do_tail(a) return do_stuff(a+1) end
    do_tail(15)
    ]]
    dbg:command("breakpoint_set", {t="line", f=dbg.uri, n=2})
    dbg:command("run")
    assert_equal("3", dbg:command("stack_depth")._attr.depth, "Wrong stack depth (tail)")
    local resp = dbg:command("stack_get")
    assert_equal(3, #resp.stack, "Wrong stack size (tail)")
    assert_equal("2", resp.stack[1]._attr.lineno, "Not into do_stuff (tail)")
    assert_equal("tailreturn:/", resp.stack[2]._attr.filename, "Wrong tail return special URI")
    if _VERSION == "Lua 5.1" then
        assert_equal("1", resp.stack[2]._attr.level, "Wrong level for tail call")
    else
        -- FIXME: tail levels handling is not ideal as they are signaled on the same level as above
        -- function. This could confuse server because two frames has the same level
        assert_equal("0", resp.stack[2]._attr.level, "Wrong level for tail call")
    end
    
    --resp = dbg:command("context_get", {c=0, d=-1})
    --assert_nil(resp.property, "A tail call should not have properties")
    dbg:stop()
end

-------------------------------------------------------------------------------
--  Property tests
-------------------------------------------------------------------------------

-- Test correct scope & context handling
function test_context_get() -- skip() --
    local dbg = debugger:from_script[[
        if setfenv then setfenv(1, { }) else _ENV = { } end
        my_global = 12                      -- global
        local upval = "This is an upvalue"
        local priv = true                   -- local to this scope
        local function func(a,b)
            local priv = {1,2,3}            -- different from above "priv"
            return a, b, upval, priv
        end
        func(my_global, "foo")
    ]]

    -- only bare minimal tests are done, more complete tests are done in test_property_get
    local func_local = {
        a = { _attr = { name="a", type="number" } },
        b = { _attr = { name="b", type="string" } },
        priv = { _attr = { name="priv", type="sequence" } },
    }
    
    local main_local = {
        upval = { _attr = { name="upval", type="string" } },
        priv = { _attr = { name="priv", type="boolean" } },
        func = { _attr = { name="func", type="function (Lua)" } },
    }

    dbg:command("breakpoint_set", {t="line", f=dbg.uri, n=7}) -- set a breakpoint at return
    dbg:command("run")

    -- top stack level, locals
    local resp = dbg:command("context_get", {c=0})
    assert_equal(3, #resp.property)
    for _, prop in ipairs(resp.property) do
        assert_table_subset(func_local[prop._attr.name], prop, "Wrong property for func local "..prop._attr.name)
    end
    
    -- upvalues
    resp = dbg:command("context_get", {c=2})
    assert_table_subset({ name="upval", type="string" }, resp.property._attr, "Wrong property for func upvalue")
    
    -- globals (only one, global table has been set to an empty one)
    resp = dbg:command("context_get", {c=1})
    -- global names are treated like table entries. As there is no global reference, there is no _ENV in 5.2
    if _VERSION == "Lua 5.2" then
        assert_nil(resp.property)
    else
        assert_table_subset({ name='["my_global"]', type="number" }, resp.property._attr, "Wrong property for global")
    end
    
    -- get locals from main level
    resp = dbg:command("context_get", {c=0, d=1})
    assert_equal(3, #resp.property)
    for _, prop in ipairs(resp.property) do
        assert_table_subset(main_local[prop._attr.name], prop, "Wrong property for main local "..prop._attr.name)
    end

    dbg:stop()
end

if _VERSION == "Lua 5.2" then
    function test_environment()
        local dbg = debugger:from_script[[
        _ENV = { }
        f = function(_ENV) -- function specific _ENV
            return a*2
        end
        f{a=2}
        do local _ENV = { foo = "bar" } -- set a new _ENV for the block
            foo = "baz"
        end
        a = 3 -- _ENV back to normal
        ]]
        dbg:command("breakpoint_set", {t="line", f=dbg.uri, n=5}) -- stop before calling f
        dbg:command("breakpoint_set", {t="line", f=dbg.uri, n=3}) -- stop inside f
        dbg:command("breakpoint_set", {t="line", f=dbg.uri, n=7}) -- stop inside nested block (different _ENV)
        dbg:command("breakpoint_set", {t="line", f=dbg.uri, n=9}) -- stop after nested block
        
        local resp
        dbg:command("run") -- run until f call
        resp = dbg:command("context_get", {c=1})
        assert_table_subset({ name='["f"]', type="function (Lua)" }, resp.property._attr)
        
        dbg:command("run") -- run inside f
        resp = dbg:command("context_get", {c=1})
        assert_table_subset({ name='["a"]', type="number" }, resp.property._attr)
        
        dbg:command("run") -- run inside nested block
        resp = dbg:command("context_get", {c=1})
        assert_table_subset({ name='["foo"]', type="string" }, resp.property._attr)
        
        dbg:command("run") -- run after nested block
        resp = dbg:command("context_get", {c=1})
        assert_table_subset({ name='["f"]', type="function (Lua)" }, resp.property._attr)
        dbg:stop()
    end
end

local property_get_cases = {
    -- name is not tested as it is not relevant in this situation
    { "var = 3",      { _attr = { type="number", children="0", size="1", encoding="base64" }, [1] = "3" } },
    { "var = 'abcd'", { _attr = { type="string", children="0", size="6", encoding="base64" }, [1] = '"abcd"' } },
    { "var = { }",    { _attr = { type="table", children="0", encoding="base64" } } },
    { "var = true",   { _attr = { type="boolean", children="0", encoding="base64", size="4" }, [1] = "true" } },
    { "var = false",  { _attr = { type="boolean", children="0", encoding="base64", size="5" }, [1] = "false" } },
    { "var = nil",    { _attr = { type="nil", children="0", encoding="base64", size="3" }, [1] = "nil" } },
    -- test some locals
    { "var = 12; local var = true", { _attr = { type="boolean", children="0", encoding="base64", size="4" }, [1] = "true" }, 0 },
    { "var = true; local var = nil", { _attr = { type="nil", children="0", encoding="base64", size="3" }, [1] = "nil" }, 0 },
    
    { "var = { 15, 'str' }", { 
        _attr = { type="sequence", children="1", numchildren="2", encoding="base64" },
        property = {
            { _attr = { name="[1]", type="number", children="0", size="2", encoding="base64" }, [1] = "15" }, 
            { _attr = { name="[2]", type="string", children="0", size="5", encoding="base64" }, [1] = '"str"' },
    } } },
    
    -- only one entry because otherwise iteration order is not specified
    { "var = { a=2 }", { 
        _attr = { type="table", children="1", numchildren="1", encoding="base64" }, 
        property = { _attr = { name='["a"]', type="number", children="0", size="1", encoding="base64" }, [1] = "2" }, 
    } },
    
    { "var = setmetatable({}, { key='val'})", { 
        _attr = { type="table", children="1", numchildren="1", encoding="base64" },
        property = { 
            _attr = { name="metatable", type="special", children="1", numchildren="1", encoding="base64" }, 
            property = { _attr = { name='["key"]', type="string", children="0", size="5", encoding="base64" }, [1] = '"val"' }, 
    } } },
    
    { "var = function() end", { _attr = { type="function (Lua)", children="0", encoding="base64" } } },
    { "var = next", { _attr = { type="function", children="0", encoding="base64" } } },
}

if _VERSION == "Lua 5.1" then
    table.insert(property_get_cases, { "var = setfenv(function() end, { a=2 })", { 
        _attr = { type="function (Lua)", children="1", numchildren="1", encoding="base64" },
        property = { 
            _attr = { name="environment", type="special", children="1", numchildren="1", encoding="base64", }, 
            property = { _attr = { name='["a"]', type="number", children="0", size="1", encoding="base64" }, [1] = "2"
    } } } })
end

-- Test global properties (with a single property_get query)
test_property_get = data_oriented_factory(property_get_cases,
function(script, data, cxt) -- skip() --
    local dbg = debugger:from_script(script.."\n print(var)")
    dbg:command("breakpoint_set", {t="line", f=dbg.uri, n=2})
    dbg:command("feature_set", {n="max_depth", v=100}) -- to have ALL children
    dbg:command("run")
    --TODO BUG ECLIPSE TOOLSLINUX-99 352316 
    resp = dbg:command("property_get", {d=0, n=rawb64(tostring(cxt or 1)..'|(...)["var"]')})
    unb64property(resp.property)
    assert_table_subset(data, resp.property)
    dbg:stop()
end)

function test_size_limit() -- skip() --
    local dbg = debugger:from_script("var = string.rep('a', 1000)\n print(var)")
    dbg:command("breakpoint_set", {t="line", f=dbg.uri, n=2})
    dbg:command("feature_set", {n="max_data", v=100})
    dbg:command("run")
    
    -- test for size limit
    resp = dbg:command("property_get", {d=0, n=rawb64('1|(...)["var"]')})
    unb64property(resp.property)
    assert_table_subset({ _attr = { type="string", children="0", size="1002", encoding="base64" }, [1] = '"'..string.rep("a", 99) }, resp.property)
    
    -- no size limit for property_value
    resp = dbg:command("property_value", {d=0, n=rawb64('1|(...)["var"]')})
    unb64property(resp.property)
    assert_table_subset({ _attr = { type="string", children="0", size="1002", encoding="base64" }, [1] = '"'..string.rep("a", 1000)..'"' }, resp.property)

    dbg:stop()
end

--- test getting a nested property with multiple queries
function test_nested_property() -- skip() --
    local dbg = debugger:from_script[[
        var = { key = "value" }
        var2 = { ["a key with spaces"] = 12 }
        print(var)
    ]]
    dbg:command("breakpoint_set", {t="line", f=dbg.uri, n=3})
    dbg:command("run")
    
    -- make a first query to get a fullname
    local resp = dbg:command("property_get", {d=0, n=rawb64('1|(...)["var"]')})
    assert_string(resp.property.property._attr.fullname)
    -- now make a second one with the fullname from the first
    resp = dbg:command("property_get", {c=1, d=0, n=resp.property.property._attr.fullname})
    unb64property(resp.property)
    assert_table_subset({ _attr = { type="string", size="7" }, [1] = '"value"' }, resp.property)
    
    -- test with a key with spaces
    resp = dbg:command("property_get", {d=0, n=rawb64('1|(...)["var2"]')})
    assert_string(resp.property.property._attr.fullname)
    -- now make a second one with the fullname from the first
    resp = dbg:command("property_get", {c=1, d=0, n=resp.property.property._attr.fullname})
    unb64property(resp.property)
    assert_table_subset({ _attr = { type="number", size="2" }, [1] = "12" }, resp.property)
    
    dbg:stop()
end

-- Test that querying non existent local/upvalue raises an error (globals are nil)
function test_unknown_properties() -- skip() --
    local dbg = debugger:from_script"print(1)"
    dbg:command("breakpoint_set", {t="line", f=dbg.uri, n=1})
    dbg:command("run")
    
    local resp = dbg:command("property_get", {d=0, n=rawb64('0|(...)["loc"]')}, nil, true)
    assert_table(resp.error, "The property_get command has not caused an error with local")
    assert_equal("300", resp.error._attr.code, resp.error.message, "The error code does not match")
    
    local resp = dbg:command("property_get", {d=0, n=rawb64('2|(...)["upval"]')}, nil, true)
    assert_table(resp.error, "The property_get command has not caused an error with upvalue")
    assert_equal("300", resp.error._attr.code, resp.error.message, "The error code does not match")
    
    dbg:stop()
end

-- try to change properties and check that they have been actually changed (minimal checks are done, this is not intended to test property_get)
local property_set_cases = {
    { "val = 12", '(...)["val"]', '"a string"', { _attr = { type="string" }, [1] = '"a string"' } },
    { "val = 12", '(...)["val"]', '{1,2}', { _attr = { type="sequence", numchildren="2" } } },
    { "val = 12; local newval = 'new'", '(...)["val"]', 'newval', { _attr = { type="string" }, [1] = '"new"' } },
    { "val = setmetatable({1,2}, {a=0})", 'metatable[(...)["val"]]["a"]', '"new"', { _attr = { type="string" }, [1] = '"new"' } },
    { "val = setmetatable({1,2}, {old=0})", 'metatable[(...)["val"]]', '{ b = "new" }', {
        _attr = { type="special", numchildren="1" }, 
        property = { _attr = { type="string", name='["b"]'}, [1] = '"new"' }
    } },
}

if _VERSION == "Lua 5.1" then
    table.insert(property_set_cases, 
      { "val = setfenv(function() end, {a=0})", 'environment[(...)["val"]]["a"]', '"new"', { _attr = { type="string" }, [1] = '"new"' } })
    table.insert(property_set_cases, 
      { "val = setfenv(function() end, {old=0})", 'environment[(...)["val"]]', '{ b = "new" }', {
          _attr = { type="special", numchildren="1" }, 
          property = { _attr = { type="string", name='["b"]'}, [1] = '"new"' }
      } })
end

test_property_set = data_oriented_factory(property_set_cases,
function(script, prop, value, data) -- skip() --
    local dbg = debugger:from_script(script.."\n print(var)")
    dbg:command("breakpoint_set", {t="line", f=dbg.uri, n=2})
    dbg:command("run")
    
    prop = rawb64("1|"..prop)
    assert_equal("1", dbg:command("property_set", {d=0, n=prop}, value)._attr.success)
    local resp = dbg:command("property_get", {d=0, n=prop})
    unb64property(resp.property)
    assert_table_subset(data, resp.property)
    
    dbg:stop()
end)

-- test script evaluation and multival results
function test_eval() -- skip() --
    local dbg = debugger:from_script[[
    local upval = true
    local unavailable = "This variable cannot be accessed from inside the function"
    masked = "This will be masked by a local var"
    (function(arg1, arg2)
        local masked = { }
        print(upval)
    end)(2, {1,2,3})
    ]]
    dbg:command("breakpoint_set", {t="line", f=dbg.uri, n=6})
    dbg:command("run")
    
    -- test expression evaluation (get environment)
    data_oriented_factory({
        { "arg1", { _attr = { type="number" }, [1] = "2" } },
        { "upval", { _attr = { type="boolean" }, [1] = "true" } },
        { "masked, _G.masked", 
            { _attr = { type="table", children="0" } }, 
            { _attr = { type="string" }, '"This will be masked by a local var"' } },
        { "unpack(arg2)", 
            { _attr = { type="number" }, [1] = "1" },  
            { _attr = { type="number" }, [1] = "2" }, 
            { _attr = { type="number" }, [1] = "3" }, },
    },
    function(expr, ...)
        local resp = dbg:command("eval", nil, expr)
        local results = select("#", ...)
        unb64property(resp.property)
        assert_equal("1", resp._attr.success, "Not successful")
        
        if results > 1 then
            assert_table_subset({ type="multival", numchildren=tostring(results) }, resp.property._attr, "Wrong multival root property")
            for i=1, results do
                assert_table_subset(select(i, ...), resp.property.property[i], "Result #"..tostring(i).." does not match")
            end
        else
            assert_table_subset(..., resp.property, "Result #"..tostring(i).." does not match")
        end
    end)()
    
    -- test environment modifications
    --TODO BUG ECLIPSE TOOLSLINUX-99 352316 
    data_oriented_factory({
        { "arg1 = 'new value'", { ['0|(...)["arg1"]'] = { _attr = { type="string" }, [1] = '"new value"' } } },
        { "masked = true", { 
            ['0|(...)["masked"]'] = { _attr = { type="boolean" }, [1] = "true" }, 
            ['1|(...)["masked"]'] = { _attr = { type="string" }, [1] = '"This will be masked by a local var"' } } },
        { "upval = false", { ['2|(...)["upval"]'] = { _attr = { type="boolean" }, [1] = "false" } } },
    },
    function(expr, properties)
        local resp = dbg:command("eval", nil, expr)
        assert_equal("1", resp._attr.success, "Not successful")
        for prop, expected in pairs(properties) do
            resp = dbg:command("property_get", {d=0, n=rawb64(prop)})
            unb64property(resp.property)
            assert_table_subset(expected, resp.property, "Property "..prop.." does ont match")
        end
    end)()
    
    dbg:stop()
end

-- test context of another coroutine
function test_coro_context_and_properties()
    local dbg = debugger:from_script[[
    co = coroutine.create(function()
        local var, var2 = 1, "foo"
        while true do coroutine.yield(var) end
    end)
    local var = true
    local _, yielded = coroutine.resume(co)
    _, yielded = coroutine.resume(co)
    _, yielded = coroutine.resume(co)
    ]]
    
    local co_local = {
        var  = { _attr = { name="var",  type="number" }, [1] = "1" },
        var2 = { _attr = { name="var2", type="string" }, [1] = '"foo"' },
    }
    dbg:command("breakpoint_set", {t="line", f=dbg.uri, n=7}) -- stop after the first yield
    dbg:command("run")
    
    local co_id = dbg:command("coroutine_list").coroutine._attr.id
    assert_len(3, dbg:command("context_names", {o=co_id, d=1}).context) -- level 0 is yield
    local resp = dbg:command("context_get", {c=0, o=co_id, d=1})
    assert_equal(2, #resp.property)
    for _, prop in ipairs(resp.property) do
        assert_table_subset(co_local[prop._attr.name], unb64property(prop), "Wrong property for local in coroutine "..prop._attr.name)
    end
    
    -- try getting some variables
    local resp = dbg:command("property_get", {d=1, o=co_id, n=rawb64("0"..'|(...)["var"]')})
    unb64property(resp.property)
    -- name cannot be tested for property_get (different from real variable name)
    assert_equal(co_local.var._attr.type, resp.property._attr.type)
    assert_equal(co_local.var[1], resp.property[1])
    
    -- set the variable
    assert_equal("1", dbg:command("property_set", {d=1, o=co_id, n=rawb64("0"..'|(...)["var"]')}, '"newvalue"')._attr.success)
    -- verify that this new value is yielded
    -- LuaJIT seem to fire multiple line events so multiple step_over could be necessary.
    repeat
      assert_equal("break", dbg:command("step_over")._attr.status)
    until tonumber(dbg:command("stack_get").stack[1]._attr.lineno) == 8
    resp = dbg:command("property_get", {n=rawb64("0"..'|(...)["yielded"]')})
    unb64property(resp.property)
    assert_equal("string", resp.property._attr.type)
    assert_equal('"newvalue"', resp.property[1])
    
    dbg:stop()
end

function test_coro_collecting()
    local dbg = debugger:from_script[[
    co = coroutine.create(function()
        while true do coroutine.yield(1) end
    end)
    coroutine.resume(co)
    co = nil
    collectgarbage("collect")
    a = 1
    ]]
    dbg:command("breakpoint_set", {t="line", f=dbg.uri, n=5}) -- stop after the resume
    dbg:command("breakpoint_set", {t="line", f=dbg.uri, n=7}) -- stop at the last line
    dbg:command("run")
    assert_not_nil(dbg:command("coroutine_list").coroutine, "Coroutine not present")
    dbg:command("run")
    assert_nil(dbg:command("coroutine_list").coroutine, "Coroutine still present (should have been collected)")
    dbg:stop()
end
