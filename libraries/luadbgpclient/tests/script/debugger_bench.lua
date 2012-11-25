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
-- DBGp debugger benchmark tests
-- This test is not designed to be portable as it needs LuaSocket (relying on LuaRocks to find it).
-- Additionaly, it relies on PWD env var and needs /dev/null (nul on Windows).
-- The configuration lines may need to be adapted to a particular configuration.
-------------------------------------------------------------------------------

module(..., package.seeall)
require "socket"
require "mime"
require "lunatest"

local LUA_EXEC = string.format([[$(which time) -f "%%U" -a -o timestats lua -e 'io.output(io.open("/dev/null", "a"))' -e 'package.path=%q; package.cpath=%q' ]], package.path, package.cpath)
local DEBUGGER_INIT = [[ -e 'require"debugger"("127.0.0.1", 9000)' ]]
local NBODY_BENCH = os.getenv("PWD").."/3rd_party/bench/nbody.lua"
local NB_ITER = 1000
local server_skt

local function read_resp(skt)
    local size = {}
    while true do
        local byte = assert(skt:receive(1))
        if string.byte(byte) == 0 then break end
        size[#size+1] = byte
    end
    local cmd = assert(skt:receive(tonumber(table.concat(size))))
    assert(skt:receive(1) == "\000")
    return cmd
end

function run_command(skt, cmd)
    assert(skt:send(cmd))
    return read_resp(skt)
end

local function run_startup(init_script, expect_last_response)
    local client = server_skt:accept()
    for _, cmd in ipairs(init_script) do
        read_resp(client)
        client:send(cmd)
    end
    if expect_last_response then read_resp(client) end
    return client
end

local function run_script(args)
    local cmd = LUA_EXEC .. args
    return io.popen(cmd, "r")
end

function suite_setup()
    server_skt = assert(socket.bind("127.0.0.1", "9000"))
end

function suite_teardown()
    server_skt:close()
end

-- Raw test, without debugger
function test_raw()
    run_script(NBODY_BENCH.." "..tostring(NB_ITER)):read("*a")
end

-- Test with a dummy hook (to see the uncompressible overhead)
function test_dummy_hook()
    run_script([[ -e 'local getinfo = debug.getinfo; debug.sethook(function() getinfo(2, "S") end, "rlc")' ]] ..NBODY_BENCH.." "..tostring(NB_ITER)):read("*a")
end

-- Test with no breakpoint at all
function test_no_bp()
    local child = run_script(DEBUGGER_INIT..NBODY_BENCH.." "..tostring(NB_ITER))
    local client = run_startup{ "run -i 1\000" }
    child:read("*a")
    client:close()
end

-- Test with a breakpoint wich is never reached
function test_not_reached_bp()
    local child = run_script(DEBUGGER_INIT..NBODY_BENCH.." "..tostring(NB_ITER))
    local client = run_startup{
        "breakpoint_set -i 1 -t line -f file:///foo/bar.lua -n 100\000",
        "run -i 2\000",
    }
    child:read("*a")
    client:close()
end

-- Test with a disabled breakpoint (but reached)
function test_disabled_bp()
    local child = run_script(DEBUGGER_INIT..NBODY_BENCH.." "..tostring(NB_ITER))
    local client = run_startup{
        "breakpoint_set -i 1 -t line -f file://".. NBODY_BENCH .." -n 66 -s disabled\000", -- reached n*10 times
        "run -i 2\000",
    }
    child:read("*a")
    client:close()
end

-- Test with a reached breakpoint (only break & run)
function test_reached_bp()
    local child = run_script(DEBUGGER_INIT..NBODY_BENCH.." "..tostring(NB_ITER))
    local client = run_startup({
        "breakpoint_set -i 1 -t line -f file://".. NBODY_BENCH .." -n 66\000", -- reached n*10 times
        "run -i 2\000",
    }, true)
    
    reach_count = 0
    pcall(function() -- the function will fail at socket close
        while true do
            run_command(client, "run -i "..tostring(reach_count + 3).."\000")
            reach_count = reach_count + 1
        end
    end)
    
    assert_equal(NB_ITER*10, reach_count+1)
    child:read("*a")
    client:close()
end

function test_hit_condition()
    local child = run_script(DEBUGGER_INIT..NBODY_BENCH.." "..tostring(NB_ITER))
    local client = run_startup({
        "breakpoint_set -i 1 -t line -f file://".. NBODY_BENCH .." -n 66 -o % -h 100\000", -- reached n*10 times
        "run -i 2\000",
    }, true)
    
    reach_count = 0
    pcall(function() -- the function will fail at socket close
        while true do
            run_command(client, "run -i "..tostring(reach_count + 3).."\000")
            reach_count = reach_count + 1
        end
    end)
    
    assert_equal(NB_ITER/10, reach_count+1)
    child:read("*a")
    client:close()
end

function test_conditional_breakpoint()
    local child = run_script(DEBUGGER_INIT..NBODY_BENCH.." "..tostring(NB_ITER))
    local client = run_startup{
        "breakpoint_set -i 1 -t conditional -f file://".. NBODY_BENCH .." -n 66 -- ".. mime.b64("false") .."\000",
        "run -i 2\000",
    }

    child:read("*a")
    client:close()
end

-- Test with a reached breakpoint and then a step out
function test_out_event()
    local child = run_script(DEBUGGER_INIT..NBODY_BENCH.." "..tostring(NB_ITER))
    local client = run_startup({
        "breakpoint_set -i 1 -t line -f file://".. NBODY_BENCH .." -n 66\000",
        "run -i 2\000",
    }, true)
    
    reach_count = 0
    pcall(function() -- the function will fail at socket close
        while true do
            run_command(client, "breakpoint_update -i "..tostring(reach_count * 4 + 3).." -d 0 -s disabled\000")
            run_command(client, "step_out -i "..tostring(reach_count * 4 + 4).."\000")
            run_command(client, "breakpoint_update -i "..tostring(reach_count * 4 + 5).." -d 0 -s enabled\000")
            run_command(client, "run -i "..tostring(reach_count * 4 + 6).."\000")
            reach_count = reach_count + 1
        end
    end)
    
    assert_equal(NB_ITER, reach_count+1) -- the last increment has not been done
    child:read("*a")
    client:close()
end

