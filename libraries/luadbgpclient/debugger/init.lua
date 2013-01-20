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

local debug = require "debug"

-- To avoid cyclic dependency, internal state of the debugger that must be accessed 
-- elsewhere (in commands most likely) will be stored in a fake module "debugger.core"
local core = { }
package.loaded["debugger.core"] = core

local util = require "debugger.util"
local platform = require "debugger.platform"
local dbgp = require "debugger.dbgp"
local commands = require "debugger.commands"
local context = require "debugger.context"
local url = require "debugger.url"

local log = util.log


-- TODO complete the stdlib access
local corunning, cocreate, cowrap, coyield, coresume, costatus = coroutine.running, coroutine.create, coroutine.wrap, coroutine.yield, coroutine.resume, coroutine.status


-- register the URI of the debugger, to not jump into with redefined function or coroutine bootstrap stuff
local debugger_uri = nil -- set in init function
local transportmodule_uri = nil -- set in init function

-- will contain the session object, and possibly a list of all sessions if a multi-threaded model is adopted
-- this is only used for async commands.
local active_session = nil

-- tracks all active coroutines and associate an id to them, the table from_id is the id=>coro mapping, the table from_coro is the reverse
core.active_coroutines = { n = 0, from_id = setmetatable({ }, { __mode = "v" }), from_coro = setmetatable({ }, { __mode = "k" }) }

-- "BEGIN VERSION DEPENDENT CODE"
local setbpenv     -- set environment of a breakpoint (compiled function)
if _VERSION == "Lua 5.1" then
    local setfenv = setfenv
    setbpenv = setfenv
elseif _VERSION == "Lua 5.2" then
    local setupvalue = debug.setupvalue
    -- _ENV is the first upvalue
    setbpenv = function(f, t) return setupvalue(f, 1, t) end
else error(_VERSION .. "is not supported.") end
-- "END VERSION DEPENDENT CODE"

-------------------------------------------------------------------------------
--  Output redirection handling
-------------------------------------------------------------------------------
-- Override standard output functions & constants to redirect data written to these files to IDE too. 
-- This works only for output done in Lua, output written by C extensions is still go to system output file.

-- references to native values
io.base = { output = io.output, stdin = io.stdin, stdout = io.stdout, stderr = io.stderr }

function print(...)
    local buf = {...}
    for i=1, select("#", ...) do
        buf[i] = tostring(buf[i])
    end
    io.stdout:write(table.concat(buf, "\t") .. "\n")
end

-- Actually change standard output file but still return the "fake" stdout
function io.output(output)
    io.base.output(output)
    return io.stdout
end

local dummy = function() end

-- metatable for redirecting output (not printed at all in actual output)
core.redirect_output = {
    write = function(self, ...)
        local buf = {...}
        for i=1, select("#", ...) do buf[i] = tostring(buf[i]) end
        buf = table.concat(buf):gsub("\n", "\r\n")
        dbgp.send_xml(self.skt, { tag = "stream", attr = { type=self.mode },  util.b64(buf) } )
    end,
    flush = dummy,
    close = dummy,
    setvbuf = dummy,
    seek = dummy
}
core.redirect_output.__index = core.redirect_output

-- metatable for cloning output (outputs to actual system and send to IDE)
core.copy_output = {
    write = function(self, ...)
        core.redirect_output.write(self, ...)
        io.base[self.mode]:write(...)
    end,
    flush   = function(self, ...) return self.out:flush(...) end,
    close   = function(self, ...) return self.out:close(...) end,
    setvbuf = function(self, ...) return self.out:setvbuf(...) end,
    seek    = function(self, ...) return self.out:seek(...) end,
}
core.copy_output.__index = core.copy_output

-------------------------------------------------------------------------------
--  Breakpoint registry
-------------------------------------------------------------------------------
-- Registry of current stack levels of all running threads
local stack_levels = setmetatable( { }, { __mode = "k" } )

-- File/line mapping for breakpoints (BP). For a given file/line, a list of BP is associated (DBGp specification section 7.6.1
-- require that multiple BP at same place must be handled)
-- A BP is a table with all additional properties (type, condition, ...) the id is the string representation of the table.
core.breakpoints = {
    -- functions to call to match hit conditions
    hit_conditions = {
        [">="] = function(value, target) return value >= target end,
        ["=="] = function(value, target) return value == target end,
        ["%"]  = function(value, target) return (value % target) == 0 end,
    }
}

-- tracks events such as step_into or step_over
core.events = { }

do
    local file_mapping = { }
    local id_mapping = { }
    local waiting_sessions = { } -- sessions that wait for an event (over, into, out)
    local step_into = nil        -- session that registered a step_into event, if any
    local sequence = 0 -- used to generate breakpoint IDs

    --- Inserts a new breakpoint into registry
    -- @param bp (table) breakpoint data
    -- @param uri (string, optional) Absolute file URI, for line breakpoints
    -- @param line (number, optional) Line where breakpoint stops, for line breakpoints
    -- @return breakpoint identifier
    function core.breakpoints.insert(bp)
        local bpid = sequence
        sequence = bpid + 1
        bp.id = bpid
        -- re-encode the URI to avoid any mismatch (with authority for example)
        local uri = url.parse(bp.filename)
        bp.filename = url.build{ scheme=uri.scheme, authority="", path=platform.normalize(uri.path)}
        
        local filereg = file_mapping[bp.filename]
        if not filereg then
            filereg = { }
            file_mapping[bp.filename] = filereg
        end
        
        local linereg = filereg[bp.lineno]
        if not linereg then
            linereg = {}
            filereg[bp.lineno] = linereg
        end
    
        table.insert(linereg, bp)
        
        id_mapping[bpid] = bp
        return bpid
    end

    --- If breakpoint(s) exists for given file/line, uptates breakpoint counters
    -- and returns whether a breakpoint has matched (boolean)
    function core.breakpoints.at(file, line)
        local bps = file_mapping[file] and file_mapping[file][line]
        if not bps then return nil end
        
        local do_break = false
        for _, bp in pairs(bps) do
            if bp.state == "enabled" then
                local match = true
                if bp.condition then
                    -- TODO: this is not the optimal solution because Context can be instantiated twice if the breakpoint matches
                    local cxt = context.Context:new(active_session.coro, 0)
                    setbpenv(bp.condition, cxt)
                    local success, result = pcall(bp.condition)
                    if not success then log("ERROR", "Condition evaluation failed for breakpoint at %s:%d: %s", file, line, result) end
                    -- debugger always stops if an error occurs
                    match = (not success) or result
                end
                if match then
                    bp.hit_count = bp.hit_count + 1
                    if core.breakpoints.hit_conditions[bp.hit_condition](bp.hit_count, bp.hit_value) then
                        if bp.temporary then
                            core.breakpoints.remove(bp.id)
                        end
                        do_break = true
                        -- there is no break to handle multiple breakpoints: all hit counts must be updated
                    end
                end
            end
        end
        return do_break
    end

    function core.breakpoints.get(id)
        if id then return id_mapping[id] 
        else return id_mapping end
    end

    function core.breakpoints.remove(id)
        local bp = id_mapping[id]
        if bp then
            id_mapping[id] = nil
            local linereg = file_mapping[bp.filename][bp.lineno]
            for i=1, #linereg do
                if linereg[i] == bp then
                    table.remove(linereg, i)
                    break
                end
            end
                    
            -- cleanup file_mapping
            if not next(linereg) then file_mapping[bp.filename][bp.lineno] = nil end
            if not next(file_mapping[bp.filename]) then file_mapping[bp.filename] = nil end
            return true
        end
        return false
    end
    
    --- Returns an XML data structure that describes given breakpoint
    -- @param id (number) breakpoint ID
    -- @return Table describing a <breakpooint> tag or nil followed by an error message
    function core.breakpoints.get_xml(id)
        local bp = id_mapping[id]
        if not bp then return nil, "No such breakpoint: "..tostring(id) end
        
        local response = { tag = "breakpoint", attr = { } }
        for k,v in pairs(bp) do response.attr[k] = v end
        if bp.expression then
            response[1] = { tag = "expression",  bp.expression }
        end
        
        -- internal use only
        response.attr.expression = nil
        response.attr.condition = nil
        response.attr.temporary = nil -- TODO: the specification is not clear whether this should be provided, see other implementations
        return response
    end
    
    --- Register an event to be triggered.
    -- @param event event name to register (must be "over", "out" or "into")
    function core.events.register(event)
        local thread = active_session.coro[1]
        log("DEBUG", "Registered %s event for %s (%d)", event, tostring(thread), stack_levels[thread])
        if event == "into" then 
            step_into = true
        else
            waiting_sessions[thread] = { event, stack_levels[thread] }
        end
    end

    --- Returns if an event (step into, over, out) is triggered.
    -- Does *not* discard events (even if they match) as event must be discarded manually if a breakpoint match before anyway.
    -- @return true if an event has matched, false otherwise
    function core.events.does_match()
        if step_into then return true end
        
        local thread = active_session.coro[1]
        local event = waiting_sessions[thread]
        if event then
            local event_type, target_level = unpack(event)
            local current_level = stack_levels[thread]

            if (event_type == "over" and current_level <= target_level) or   -- step over
               (event_type == "out"  and current_level <  target_level) then -- step out
                log("DEBUG", "Event %s matched!", event_type)
                return true
            end
        end
        return false
    end
    
    --- Discards event for current thread (if any)
    function core.events.discard()
        waiting_sessions[active_session.coro[1]] = nil
        step_into = nil
    end
end

-------------------------------------------------------------------------------
--  Debugger main loop
-------------------------------------------------------------------------------

--- Send the XML response to the previous continuation command and clear the previous context
function core.previous_context_response(self, reason)
    self.previous_context.status = self.state
    self.previous_context.reason = reason or "ok"
    dbgp.send_xml(self.skt, { tag = "response", attr = self.previous_context } )
    self.previous_context = nil
end

--- This function handles the debugger commands while the execution is paused. This does not use coroutines because there is no
-- way to get main coro in Lua 5.1 (only in 5.2)
local function debugger_loop(self, async_packet)
    self.skt:settimeout(nil) -- set socket blocking
    
    -- in async mode, the debugger does not wait for another command before continuing and does not modify previous_context
    local async_mode = async_packet ~= nil
    
    if self.previous_context and not async_mode then
        self.state = "break"
        core.previous_context_response(self)
    end
    self.stack = context.ContextManager(self.coro) -- will be used to mutualize context allocation for each loop
    
    while true do
        -- reads packet
        local packet = async_packet or assert(dbgp.read_packet(self.skt))
        async_packet = nil
        log("DEBUG", packet)
        local cmd, args, data = dbgp.cmd_parse(packet)
        
        -- FIXME: command such as continuations sent in async mode could lead both engine and IDE in inconsistent state :
        --        make a blacklist/whitelist of forbidden or allowed commands in async ?
        -- invoke function
        local func = commands[cmd]
        if func then
            local ok, cont = xpcall(function() return func(self, args, data) end, debug.traceback)
            if not ok then -- internal exception
                local code, msg, attr
                if type(cont) == "table" and getmetatable(cont) == dbgp.DBGP_ERR_METATABLE then
                    code, msg, attr = cont.code, cont.message, cont.attr
                else
                    code, msg, attr = 998, tostring(cont), { }
                end
                log("ERROR", "Command %s caused: (%d) %s", cmd, code, tostring(msg))
                attr.command, attr.transaction_id = cmd, args.i
                dbgp.send_xml(self.skt, { tag = "response", attr = attr, dbgp.make_error(code, msg) } )
            elseif cont then
                self.previous_context = { command = cmd, transaction_id = args.i }
                break
            elseif cont == nil and async_mode then
                break
            elseif cont == false then -- In case of commands that fully resumes debugger loop, the mode is sync
                async_mode = false
            end
        else
            log("Got unknown command: "..cmd)
            dbgp.send_xml(self.skt, { tag = "response", attr = { command = cmd, transaction_id = args.i, }, dbgp.make_error(4) } )
        end
    end
    
    self.stack = nil -- free allocated contexts
    self.state = "running"
    self.skt:settimeout(0) -- reset socket to async
end

-- Stack handling can be pretty complex sometimes, especially with LuaJIT (as tail-call optimization are
-- more aggressive as stock Lua). So all debugger stuff is done in another coroutine, which leave the program 
-- stack in a clean state and allow faster and clearer stack operations (no need to remove all debugger calls
-- from stack for each operation).
-- However, this does not always work with stock Lua 5.1 as the main coroutine cannot be referenced 
-- (coroutine.running() return nil). For this particular case, the debugger loop is started on the top of
-- program stack and every stack operation is relative the the hook level (see MainThread in util.lua).
local function line_hook(line)
    local do_break, packet = nil, nil
    local info = active_session.coro:getinfo(0, "S")
    local uri = platform.get_uri(info.source)
    if uri and uri ~= debugger_uri and uri ~= transportmodule_uri then -- the debugger does not break if the source is not known
        do_break = core.breakpoints.at(uri, line) or core.events.does_match()
        if do_break then
            core.events.discard()
        end

        -- check for async commands
        if not do_break then
            packet = dbgp.read_packet(active_session.skt)
            if packet then do_break = true end
        end
    end

    if do_break then
        local success, err = pcall(debugger_loop, active_session, packet)
        if not success then log("ERROR", "Error while debug loop: "..err) end
    end
end

local line_hook_coro = cocreate(function(line)
    while true do
        line_hook(line)
        line = coyield()
    end
end)

local function debugger_hook(event, line)
    local thread = corunning() or "main"
    if event == "call" then
        stack_levels[thread] = stack_levels[thread] + 1
    elseif event == "tail call" then
        -- tail calls has no effects on stack handling: it is only used only for step commands but a such even does not
        -- interfere with any of them
    elseif event == "return" or event == "tail return" then
        stack_levels[thread] = stack_levels[thread] - 1
    else -- line event: check for breakpoint
        active_session.coro = util.CurrentThread(corunning())
        if active_session.coro[1] == "main" then
            line_hook(line)
        else
            -- run the debugger loop in another thread on the other cases (simplifies stack handling)
            assert(coresume(line_hook_coro, line))
        end
        active_session.coro = nil
    end
end

if rawget(_G, "jit") then
    debugger_hook = function(event, line)
        local thread = corunning() or "main"
        if event == "call" then
            if debug.getinfo(2, "S").what == "C" then return end
            stack_levels[thread] = stack_levels[thread] + 1
        elseif event == "return" or event == "tail return" then
            -- Return hooks are not called for tail calls in JIT (but unlike 5.2 there is no way to know whether a call is tail or not).
            -- So the only reliable way to know stack depth is to walk it.
            local depth = 2
            -- TODO: find the fastest way to call getinfo ('what' parameter)
            while debug.getinfo(depth, "f") do depth = depth + 1 end
            stack_levels[thread] = depth - 2
        elseif event == "line" then
            active_session.coro = util.CurrentThread(corunning())
            if active_session.coro[1] == "main" then
                line_hook(line)
            else
                -- run the debugger loop in another thread on the other cases (simplifies stack handling)
                assert(coresume(line_hook_coro, line))
            end
            active_session.coro = nil
        end
    end
end

local function init(host, port, idekey, transport, executionplatform, workingdirectory)
    -- get connection data
    local host = host or os.getenv "DBGP_IDEHOST" or "127.0.0.1"
    local port = port or os.getenv "DBGP_IDEPORT" or "10000"
    local idekey = idekey or os.getenv("DBGP_IDEKEY") or "luaidekey"

    -- init plaform module
    local executionplatform = executionplatform or os.getenv("DBGP_PLATFORM") or nil
    local workingdirectory = workingdirectory or os.getenv("DBGP_WORKINGDIR") or nil
    platform.init(executionplatform,workingdirectory)

    -- get transport layer
    local transportpath = transport or os.getenv("DBGP_TRANSPORT") or "debugger.transport.luasocket"
    local transport = require(transportpath)

    -- install base64 functions into util
    util.b64, util.rawb64, util.unb64 = transport.b64, transport.rawb64, transport.unb64

    local skt = assert(transport.create())
    skt:settimeout(nil)

    -- try to connect several times: if IDE launches both process and server at same time, first connect attempts may fail
    local ok, err
    for i=1, 5 do
        ok, err = skt:connect(host, port)
        if ok then break end
        transport.sleep(0.5)
    end
    if err then error(string.format("Cannot connect to %s:%d : %s", host, port, err)) end

    -- get the debugger and transport layer URI
    debugger_uri = platform.get_uri(debug.getinfo(1).source)
    transportmodule_uri = platform.get_uri(debug.getinfo(transport.create).source)

    -- get the root script path (the highest possible stack index)
    local source
    for i=2, math.huge do
        local info = debug.getinfo(i)
        if not info then break end
        source = platform.get_uri(info.source) or source
    end
    if not source then source = "unknown:/" end -- when loaded before actual script (with a command line switch)

    -- generate some kind of thread identifier
    local thread = corunning() or "main"
    stack_levels[thread] = 1 -- the return event will set the counter to 0
    local sessionid = tostring(os.time()) .. "_" .. tostring(thread)

    dbgp.send_xml(skt, { tag = "init", attr = {
        appid = "Lua DBGp",
        idekey = idekey,
        session = sessionid,
        thread = tostring(thread),
        parent = "",
        language = "Lua",
        protocol_version = "1.0",
        fileuri = source
    } })

    --FIXME util.CurrentThread(corunning) => util.CurrentThread(corunning()) WHAT DOES IT FIXES ??
    local sess = { skt = skt, state = "starting", id = sessionid, coro = util.CurrentThread(corunning) }
    active_session = sess
    debugger_loop(sess)

    -- set debug hooks
    debug.sethook(debugger_hook, "rlc")

    -- install coroutine collecting functions.
    -- TODO: maintain a list of *all* coroutines can be overkill (for example, the ones created by copcall), make a extension point to
    -- customize debugged coroutines
    -- coroutines are referenced during their first resume (so we are sure that they always have a stack frame)
    local function resume_handler(coro, ...)
        if costatus(coro) == "dead" then
            local coro_id = core.active_coroutines.from_coro[coro]
            core.active_coroutines.from_id[coro_id] = nil
            core.active_coroutines.from_coro[coro] = nil
            stack_levels[coro] = nil
        end
        return ...
    end
    
    function coroutine.resume(coro, ...)
        if not stack_levels[coro] then
            -- first time referenced
            stack_levels[coro] = 0
            core.active_coroutines.n = core.active_coroutines.n + 1
            core.active_coroutines.from_id[core.active_coroutines.n] = coro
            core.active_coroutines.from_coro[coro] = core.active_coroutines.n
            debug.sethook(coro, debugger_hook, "rlc")
        end
        return resume_handler(coro, coresume(coro, ...))
    end
    
    -- coroutine.wrap uses directly C API for coroutines and does not trigger our overridden coroutine.resume
    -- so this is an implementation of wrap in pure Lua
    local function wrap_handler(status, ...)
        if not status then error((...)) end
        return ...
    end

    function coroutine.wrap(f)
        local coro = coroutine.create(f)
        return function(...)
            return wrap_handler(coroutine.resume(coro, ...))
        end
    end

    return sess
end

return init
