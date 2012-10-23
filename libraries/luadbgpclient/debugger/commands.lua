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
-- Commands handlers for DBGp protocol.
-------------------------------------------------------------------------------
-- Debugger command functions. Each function handle a different command.
-- A command function is called with 3 arguments 
--   1. the debug session instance
--   2. the command arguments as table
--   3. the command data, if any
-- The result is either :
--   * true (or any value evaluated to true) : the debugger will resume the execution of the application (continuation command)
--   * false : only in async mode, the debugger WILL wait for further commands instead of continuing (typically, break command)
--   * nil/no return : in sync mode, the debugger will wait for another command. In async mode the debugger will continue the execution

local cowrap, coyield = coroutine.wrap, coroutine.yield
local debug = require "debug"

local core          = require "debugger.core"
local dbgp          = require "debugger.dbgp"
local util          = require "debugger.util"
local platform      = require "debugger.platform"
local introspection = require "debugger.introspection"
local context       = require "debugger.context"
local log = util.log

local M = { } -- command handlers table

--- Gets the coroutine behind an id
-- Throws errors on unknown identifiers
-- @param  coro_id  (string or nil) Coroutine identifier or nil (current coroutine)
-- @return Coroutine instance or nil (if coro_id was nil or if coroutine is the current coroutine)
local function get_coroutine(self, coro_id)
    if coro_id then
        local coro = dbgp.assert(399, core.active_coroutines.from_id[tonumber(coro_id)], "No such coroutine")
        dbgp.assert(399, coroutine.status(coro) ~= "dead", "Coroutine is dead")
        if coro ~= self.coro[1] then return util.ForeignThread(coro) end
    end
    return self.coro
end

M["break"] = function(self, args)
    self.state = "break"
    -- send response to previous command
    core.previous_context_response(self)
    -- and then response to break command itself
    dbgp.send_xml(self.skt, { tag = "response", attr = { command = "break", transaction_id = args.i, success = 1 } } )
    return false
end

function M.status(self, args)
    dbgp.send_xml(self.skt, { tag = "response", attr = {
        command = "status",
        reason = "ok",
        status = self.state,
        transaction_id = args.i } } )
end

function M.stop(self, args)
    dbgp.send_xml(self.skt, { tag = "response", attr = {
        command = "stop",
        reason = "ok",
        status = "stopped",
        transaction_id = args.i } } )
    self.skt:close()
    os.exit(1)
end

function M.feature_get(self, args)
    local name = args.n
    local response = util.features[name] or (not not M[name])
    dbgp.send_xml(self.skt, { tag = "response", attr = {
          command = "feature_get",
          feature_name = name,
          supported = response and "1" or "0",
          transaction_id = args.i },
        tostring(response) } )
end

function M.feature_set(self, args)
    local name, value = args.n, args.v
    local success = pcall(function() util.features[name] = value end)
    dbgp.send_xml(self.skt, { tag = "response", attr = {
        command = "feature_set",
        feature = name,
        success = success and 1 or 0,
        transaction_id = args.i
    } } )
end

function M.typemap_get(self, args)
    local function gentype(name, type, xsdtype)
        return { tag = "map", atts = { name = name, type = type, ["xsi:type"] = xsdtype } }
    end
    
    dbgp.send_xml(self.skt, { tag = "response", attr = {
            command = "typemap_get",
            transaction_id = args.i,
            ["xmlns:xsi"] = "http://www.w3.org/2001/XMLSchema-instance",
            ["xmlns:xsd"] = "http://www.w3.org/2001/XMLSchema",
        },
        gentype("nil", "null"),
        gentype("boolean", "bool", "xsd:boolean"),
        gentype("number", "float", "xsd:float"),
        gentype("string", "string", "xsd:string"),
        gentype("function", "resource"),
        gentype("userdata", "resource"),
        gentype("thread", "resource"),
        gentype("table", "hash"),
        gentype("sequence", "array"), -- artificial type to represent sequences (1-n continuous indexes)
        gentype("multival", "array"), -- used to represent return values
    } )
end

function M.run(self) return true end

function M.step_over(self)
    core.events.register("over")
    return true
end

function M.step_out(self)
    core.events.register("out")
    return true
end

function M.step_into(self)
    core.events.register("into")
    return true
end

function M.eval(self, args, data)
    log("DEBUG", "Going to eval "..data)
    local result, err, success
    local env = self.stack(self.coro, 0)
    -- first, try to load as expression
    -- DBGp does not support stack level here, see http://bugs.activestate.com/show_bug.cgi?id=81178
    local func, err = util.loadin("return "..data, env)
    
    -- if it is not an expression, try as statement (assignment, ...)
    if not func then
        func, err = util.loadin(data, env)
    end
    
    if func then
        success, result = pcall(function() return introspection.Multival(func()) end)
        if not success then err = result end
    end
    
    local response = { tag = "response", attr = { command = "eval", transaction_id = args.i } }
    if not err then
        response.attr.success = 1
        -- As of Lua 5.1, the maximum stack size (and result count) is 8000, this limit is used to fit all results in one page
        response[1] = introspection.make_property(0, result, data, "", 1, 8000, 0, nil)
    else
        response.attr.success = 0
        response[1] = dbgp.make_error(206, err)
    end
    dbgp.send_xml(self.skt, response)
end

function M.breakpoint_set(self, args, data)
    if args.o and not core.breakpoints.hit_conditions[args.o] then dbgp.error(200, "Invalid hit_condition operator: "..args.o) end
    
    local filename, lineno = args.f, tonumber(args.n)
    local bp = {
        type = args.t,
        state = args.s or "enabled",
        temporary = args.r == "1", -- "0" or nil makes this property false
        hit_count = 0,
        filename = filename,
        lineno = lineno,
        hit_value = tonumber(args.h or 0),
        hit_condition = args.o or ">=",
    }
    
    if args.t == "conditional" then
        bp.expression = data
        -- the expression is compiled only once
        bp.condition = dbgp.assert(207, loadstring("return (" .. data .. ")"))
    elseif args.t ~= "line" then dbgp.error(201, "BP type " .. args.t .. " not yet supported") end
    
    local bpid = core.breakpoints.insert(bp)
    dbgp.send_xml(self.skt, { tag = "response", attr = { command = "breakpoint_set", transaction_id = args.i, state = bp.state, id = bpid } } )
end

function M.breakpoint_get(self, args)
    dbgp.send_xml(self.skt, { tag = "response", 
                              attr = { command = "breakpoint_get", transaction_id = args.i }, 
                              dbgp.assert(205, core.breakpoints.get_xml(tonumber(args.d))) })
end

function M.breakpoint_list(self, args)
    local bps = { tag = "response", attr = { command = "breakpoint_list", transaction_id = args.i } }
    for id, bp in pairs(core.breakpoints.get()) do bps[#bps + 1] = core.breakpoints.get_xml(id) end
    dbgp.send_xml(self.skt, bps)
end

function M.breakpoint_update(self, args)
    local bp = core.breakpoints.get(tonumber(args.d))
    if not bp then dbgp.error(205, "No such breakpint "..args.d) end
    if args.o and not core.breakpoints.hit_conditions[args.o] then dbgp.error(200, "Invalid hit_condition operator: "..args.o) end
    
    local response = { tag = "response", attr = { command = "breakpoint_update", transaction_id = args.i } }
    bp.state = args.s or bp.state
    bp.lineno = tonumber(args.n or bp.lineno)
    bp.hit_value = tonumber(args.h or bp.hit_value)
    bp.hit_condition = args.o or bp.hit_condition
    dbgp.send_xml(self.skt, response)
end

function M.breakpoint_remove(self, args)
    local response = { tag = "response", attr = { command = "breakpoint_remove", transaction_id = args.i } }
    if not core.breakpoints.remove(tonumber(args.d)) then dbgp.error(205, "No such breakpint "..args.d) end
    dbgp.send_xml(self.skt, response)
end

function M.stack_depth(self, args)
    local depth = 0
    local coro = get_coroutine(self, args.o)
    for level = 0, math.huge do
        local info = coro:getinfo(level, "St")
        if not info then break end -- end of stack
        depth = depth + 1
        if info.istailcall then depth = depth + 1 end -- a 'fake' level is added in that case
        if info.what == "main" then break end -- levels below main chunk are not interesting
    end
    dbgp.send_xml(self.skt, { tag = "response", attr = { command = "stack_depth", transaction_id = args.i, depth = depth} } )
end

function M.stack_get(self, args) -- TODO: dynamic code
    -- special URIs to identify unreachable stack levels
    local what2uri = {
        tail = "tailreturn:/",
        C    = "ccode:/",
    }
    
    local function make_level(info, level)
        local attr = { level = level, where = info.name, type="file" }
        local uri = platform.get_uri(info.source)
        if uri and info.currentline then -- reachable level
            attr.filename = uri
            attr.lineno = info.currentline
        else
            attr.filename = what2uri[info.what] or "unknown:/"
            attr.lineno = -1
        end
        return { tag = "stack", attr = attr }
    end
    
    local node = { tag = "response", attr = { command = "stack_get", transaction_id = args.i} }
    local coro = get_coroutine(self, args.o)
    
    if args.d then
        local stack_level = tonumber(args.d)
        node[#node+1] = make_level(coro:getinfo(stack_level, "nSl"), stack_level)
    else
        for i=0, math.huge do
            local info = coro:getinfo(i, "nSlt")
            if not info then break end
            node[#node+1] = make_level(info, i)
            -- add a fake level of stack for tail calls (tells user that the function has not been called directly)
            if info.istailcall then
                node[#node+1] = { tag = "stack", attr = { level=i, type="file", filename="tailreturn:/", lineno=-1 } }
            end
            if info.what == "main" then break end -- levels below main chunk are not interesting
        end
    end
    
    dbgp.send_xml(self.skt, node)
end

--- Lists all active coroutines.
-- Returns a list of active coroutines with their id (an arbitrary string) to query stack and properties. The id is 
-- guaranteed to be unique and stable for all coroutine life (they can be reused as long as coroutine exists).
-- Others commands such as stack_get or property_* commands takes an additional -o switch to query a particular cOroutine.
-- If the switch is not given, running coroutine will be used.
-- In case of error on coroutines (most likely coroutine not found or dead), an error 399 is thrown.
-- Note there is an important limitation due to Lua 5.1 coroutine implementation: you cannot query main "coroutine" from
-- another one, so main coroutine is not in returned list (this will change with Lua 5.2).
-- 
-- This is a non-standard command. The returned XML has the following strucuture:
--     <response command="coroutine_list" transaction_id="0">
--       <coroutine name="<some printtable name>" id="<coroutine id>" running="0|1" />
--       ...
--     </response>
function M.coroutine_list(self, args)
    local running = self.coro[1]
    local coroutines = { tag = "response", attr = { command = "coroutine_list", transaction_id = args.i } }
    -- as any operation on main coroutine will fail, it is not yet listed
    -- coroutines[1] = { name = "coroutine", attr = { id = 0, name = "main", running = (running == nil) and "1" or "0" } }
    for id, coro in pairs(core.active_coroutines.from_id) do
        if id ~= "n" then
            coroutines[#coroutines + 1] = { tag = "coroutine", attr = { id = id, name = tostring(coro), running = (coro == running) and "1" or "0" } }
        end
    end
    dbgp.send_xml(self.skt, coroutines)
end

function M.context_names(self, args)
    local coro = get_coroutine(self, args.o)
    local level = tonumber(args.d or 0)
    local info = coro:getinfo(level, "f") or dbgp.error(301, "No such stack level "..tostring(level))
    
    -- All contexts are always passed, even if empty. This is how DLTK expect context, what about others ?
    local contexts = {
        tag = "response", attr = { command = "context_names", transaction_id = args.i },
        { tag = "context", attr = { name = "Local",   id = 0 } },
        { tag = "context", attr = { name = "Upvalue", id = 2 } },
        { tag = "context", attr = { name = "Global",  id = 1 } },
    }
    
    dbgp.send_xml(self.skt, contexts)
end

function M.context_get(self, args)
    local cxt_num = tonumber(args.c or 0)
    local cxt_id = context.Context[cxt_num] or dbgp.error(302, "No such context: "..tostring(cxt_num))
    local level = tonumber(args.d or 0)
    local coro = get_coroutine(self, args.o)
    local cxt = self.stack(coro, level)
    
    local properties = { tag = "response", attr = { command = "context_get", transaction_id = args.i, context = context} }
    -- iteration over global is different (this could be unified in Lua 5.2 thanks to __pairs metamethod)
    for name, val in (cxt_num == 1 and next or getmetatable(cxt[cxt_id]).iterator), cxt[cxt_id], nil do
        -- the DBGp specification is not clear about the depth of a context_get, but a recursive get could be *really* slow in Lua
        properties[#properties + 1] = introspection.make_property(cxt_num, val, name, nil, 0, util.features.max_children, 0,
                                                    util.features.max_data, cxt_num ~= 1)
    end
    
    dbgp.send_xml(self.skt, properties)
end

-------------------------------------------------------------------------------
--  Property_* commands
-------------------------------------------------------------------------------
-- This in the environment in which properties are get or set.
-- It notably contain a collection of proxy table which handle transparentely get/set operations on special fields
-- and the cache of complex keys.
local property_evaluation_environment = {
    key_cache = introspection.key_cache,
    metatable = setmetatable({ }, { 
        __index = function(self, tbl) return getmetatable(tbl) end,
        __newindex = function(self, tbl, mt) return setmetatable(tbl, mt) end,
    }),
    environment = util.eval_env,
}
-- to allows to be set as metatable
property_evaluation_environment.__index = property_evaluation_environment

function M.property_get(self, args)
    --TODO BUG ECLIPSE TOOLSLINUX-99 352316
    local cxt_num, name = assert(util.unb64(args.n):match("^(%d+)|(.*)$"))
    cxt_num = tonumber(args.c or cxt_num)
    local cxt_id = context.Context[cxt_num] or dbgp.error(302, "No such context: "..tostring(cxt_num))
    local level = tonumber(args.d or 0)
    local coro = get_coroutine(self, args.o)
    local size = tonumber(args.m or util.features.max_data)
    if size < 0 then size = nil end -- call from property_value
    local page = tonumber(args.p or 0)
    local cxt = self.stack(coro, level)
    local chunk = dbgp.assert(206, util.loadin("return "..name, property_evaluation_environment))
    local prop = select(2, dbgp.assert(300, pcall(chunk, cxt[cxt_id])))
    local response = introspection.make_property(cxt_num, prop, name, name, util.features.max_depth, util.features.max_children, page, size)
    -- make_property is not able to flag special variables as such when they are at root of property
    -- special variables queries are in the form "<proxy name>[(...)[a][b]<...>]"
    -- TODO: such parsing is far from perfect
    if name:match("^[%w_]+%[.-%b[]%]$") == name then response.attr.type = "special" end
    dbgp.send_xml(self.skt, { tag = "response", 
                              attr = { command = "property_get", transaction_id = args.i, context = context},
                              response } )
end

function M.property_value(self, args)
    args.m = -1
    M.property_get(self, args)
end

function M.property_set(self, args, data)
    local cxt_num, name = assert(util.unb64(args.n):match("^(%d+)|(.*)$"))
    cxt_num = tonumber(args.c or cxt_num)
    local cxt_id = context.Context[cxt_num] or dbgp.error(302, "No such context: "..tostring(cxt_num))
    local level = tonumber(args.d or 0)
    local coro = get_coroutine(self, args.o)
    local cxt = self.stack(coro, level)
    
    -- evaluate the new value in the local context
    local value = select(2, dbgp.assert(206, pcall(dbgp.assert(206, util.loadin("return "..data, cxt)))))
    
    local chunk = dbgp.assert(206, util.loadin(name .. " = value", setmetatable({ value = value }, property_evaluation_environment)))
    dbgp.assert(206, pcall(chunk, cxt[cxt_id]))
    dbgp.send_xml(self.skt, { tag = "response", attr = { success = 1, transaction_id = args.i } } )
end

--TODO dynamic code handling
-- The DBGp specification is not clear about the line number meaning, this implementation is 1-based and numbers are inclusive
function M.source(self, args)
    local path
    if args.f then
        path = platform.get_path(args.f)
    else
        path = self.coro:getinfo(0, "S").source
        assert(path:sub(1,1) == "@")
        path = path:sub(2)
    end
    local file, err = io.open(path)
    if not file then dbgp.error(100, err, { success = 0 }) end
    -- Try to identify compiled files
    if file:read(1) == "\033" then dbgp.error(100, args.f.." is bytecode", { success = 0 }) end
    file:seek("set", 0)
    
    
    local srclines = { }
    local beginline, endline, currentline = tonumber(args.b or 0), tonumber(args.e or math.huge), 0
    for line in file:lines() do
        currentline = currentline + 1
        if currentline >= beginline and currentline <= endline then
            srclines[#srclines + 1] = line
        elseif currentline >= endline then break end
    end
    file:close()
    srclines[#srclines + 1] = "" -- to add a trailing \n
    
    dbgp.send_xml(self.skt, { tag = "response", 
                              attr = { command = "source", transaction_id = args.i, success = 1}, 
                              util.b64(table.concat(srclines, "\n")) })
end

-- Factory for both stdout and stderr commands, change file descriptor in io
local function output_command_handler_factory(mode)
    return function(self, args)
        if args.c == "0" then -- disable
            io[mode] = io.base[mode]
        else
            io[mode] = setmetatable({ skt = self.skt, mode = mode }, args.c == "1" and core.copy_output or core.redirect_output)
        end
        dbgp.send_xml(self.skt, { tag = "response", attr = { command = mode, transaction_id = args.i, success = "1" } } )
    end
end

M.stdout = output_command_handler_factory("stdout")
M.stderr = output_command_handler_factory("stderr")


return M
