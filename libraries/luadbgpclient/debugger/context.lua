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
--  Context handling: allows to evaluate code snippets in the context of a function
-------------------------------------------------------------------------------

local M = { }

local dbgp = require "debugger.dbgp"
local util = require "debugger.util"

-- make unique object to access contexts
local LOCAL, UPVAL, GLOBAL, STORE, HANDLE = {}, {}, {}, {}, {}

local getglobals
if _VERSION == "Lua 5.1" then
    getglobals = function(f) return getfenv(f) end
elseif _VERSION == "Lua 5.2" then
    getglobals = function(f, cxt)
        -- 'global' environment: this is either the local _ENV or upvalue _ENV. A special case happen when a
        -- function does not reference any global variable: the upvalue _ENV may not exist at all. In this case,
        -- global environment is not relevant so it is fixed to an empty table. Another solution would be to set it
        -- to the environment from above stack level but it would require some overhead (especially if multiple
        -- levels must be instantiated)
        if     cxt[LOCAL][STORE]["_ENV"] then return cxt[LOCAL]["_ENV"]
        elseif cxt[UPVAL][STORE]["_ENV"] then return cxt[UPVAL]["_ENV"]
        else return { } end
    end
end

--- Captures variables for given stack level. The capture contains local, upvalues and global variables.
-- The capture can be seen as a proxy table to the stack level: any value can be queried or set no matter
-- it is a local or an upvalue.
-- The individual local and upvalues context are also available and can be queried and modified with indexed notation too.
-- These objects are NOT persistant and must not be used outside the debugger loop which instanciated them !
M.Context = {
    -- Context identifiers can be accessed by their DBGp context ID
    [0] = LOCAL,
    [1] = GLOBAL, -- DLTK internal ID for globals is 1
    [2] = UPVAL,
    STORE = STORE,
    
    -- gets a variable by name with correct handling of Lua scope chain
    -- the or chain does not work here beacause __index metamethod would raise an error instead of returning nil
    __index = function(self, k)
        if     self[LOCAL][STORE][k] then return self[LOCAL][k]
        elseif self[UPVAL][STORE][k] then return self[UPVAL][k]
        else return self[GLOBAL][k] end
    end,
    __newindex = function(self, k, v)
        if     self[LOCAL][STORE][k] then self[LOCAL][k] = v
        elseif self[UPVAL][STORE][k] then self[UPVAL][k] = v
        else self[GLOBAL][k] = v end
    end,
    
    -- debug only !!
    __tostring = function(self)
        local buf = { "Locals: \n" }
        for k,v in pairs(self[LOCAL][STORE]) do
            buf[#buf+1] = "\t"..tostring(k).."("..tostring(v)..")="..tostring(self[LOCAL][k]).."\n"
        end
        buf[#buf+1] = "Upvalues: \n"
        for k,v in pairs(self[UPVAL][STORE]) do
            buf[#buf+1] = "\t"..tostring(k).."("..tostring(v)..")="..tostring(self[UPVAL][k]).."\n"
        end
        return table.concat(buf)
    end,
    
    LocalContext = {
        __index = function(self, k)
            local index = self[STORE][k]
            if not index then error("The local "..tostring(k).." does not exists.") end
            local handle = self[HANDLE]
            return select(2, handle.coro:getlocal(handle.level, index))
        end,
        __newindex = function(self, k, v)
            local index = self[STORE][k]
            if index then
                local handle = self[HANDLE]
                handle.coro:setlocal(handle.level, index, v)
            else error("Cannot set local " .. k) end
        end,
        -- Lua 5.2 ready :)
        --__pairs = function(self) return getmetatable(self).iterator, self, nil end,
        iterator = function(self, prev)
            local key, index = next(self[STORE], prev)
            if key then return key, self[key] else return nil end
        end,
    },
    
    UpvalContext = {
        __index = function(self, k)
            local index = self[STORE][k]
            if not index then error("The local "..tostring(k).." does not exitsts.") end
            return select(2, debug.getupvalue(self[HANDLE], index))
        end,
        __newindex = function(self, k, v)
            local index = self[STORE][k]
            if index then debug.setupvalue(self[HANDLE], index, v)
            else error("Cannot set upvalue " .. k) end
        end,
        -- Lua 5.2 ready :)
        -- __pairs = function(self) return getmetatable(self).iterator, self, nil end,
        iterator = function(self, prev)
            local key, index = next(self[STORE], prev)
            if key then return key, self[key] else return nil end
        end,
    },
    
    --- Context constructor
    -- @param coro  (util.*Thread instance) coroutine to map to
    -- @param level (number) stack level do dump (script stack level)
    new = function(cls, coro, level)
        local locals, upvalues = {}, {}
        if level < 0 then dbgp.error(301, "No such stack level: "..tostring(level)) end
        local func = (coro:getinfo(level, "f") or dbgp.error(301, "No such stack level: "..tostring(level))).func
        
        -- local variables
        for i=1, math.huge do
            local name, val = coro:getlocal(level, i)
            if not name then break
            elseif name:sub(1,1) ~= "(" then -- skip internal values
                locals[name] = i
            end
        end
        
        -- upvalues
        for i=1, math.huge do
            local name, val = debug.getupvalue(func, i)
            if not name then break end
            upvalues[name] = i
        end
        
        locals = setmetatable({ [STORE] = locals, [HANDLE] = { level = level, coro = coro } }, cls.LocalContext)
        upvalues = setmetatable({ [STORE] = upvalues, [HANDLE] = func }, cls.UpvalContext)
        
        local result = setmetatable({ [LOCAL] = locals, [UPVAL] = upvalues }, cls)
        rawset(result, GLOBAL, getglobals(func, result))
        return result
    end,
}

--- Handle caching of all instantiated context. 
-- Returns a function which takes 2 parameters: thread and stack level and returns the corresponding context. If this 
-- context has been already queried there is no new instantiation. A ContextManager is valid only during the debug loop 
-- on which it has been instantiated. References to a ContextManager must be lost after the end of debug loop (so 
-- threads can be collected).
-- If a context cannot be instantiated, an 301 DBGP error is thrown.
function M.ContextManager()
    local cache = { }
    return function(thread, level)
        -- the real coroutine is used as key (not the wrapped instance as its unicity is not guaranteed)
        -- otherwise, true is used to identify current thread (as nil is not a valid table key)
        local key = thread[1] or true
        local thread_contexts = cache[key]
        if not thread_contexts then
            thread_contexts = { }
            cache[key] = thread_contexts
        end
        
        local context = thread_contexts[level]
        if not context then
            context = M.Context:new(thread, level)
            thread_contexts[level] = context
        end
        
        return context
    end
end

return M
