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
-- LuaSocket with LuaSched backend for DBGP debugger.
-------------------------------------------------------------------------------

-- As LuaShed totally hides blocking functions, this module MUST be loaded on the very start of the program 
-- (before loading sched) to catch references to blocking functions.

local socketcore = require"socket.core"
local debug      = require "debug"
local reg = debug.getregistry()

local blockingcreate  = socketcore.tcp
local blockingsleep   = socketcore.sleep

local blockingconnect    = reg["tcp{master}"].__index.connect
local blockingreceive    = reg["tcp{client}"].__index.receive
local blockingsend       = reg["tcp{client}"].__index.send
local blockingsettimeout = reg["tcp{master}"].__index.settimeout
local blockingclose      = reg["tcp{master}"].__index.close

-- we cannot set a new metatable directly on socket object, so wrap it into a new table
-- and forward all calls.
local blockingtcp = {
  connect    = function(self, address, port) return blockingconnect(self.skt, address, port) end,
  receive    = function(self, n)             return blockingreceive(self.skt, n) end,
  send       = function(self, data)          return blockingsend(self.skt, data) end,
  settimeout = function(self, sec)           return blockingsettimeout(self.skt, sec) end,
  close      = function(self)                return blockingclose(self.skt) end,
}

blockingtcp.__index = blockingtcp

local mime  = require "mime"
local ltn12 = require "ltn12"

-- verify that the socket function are the real ones and not sched not blocking versions
assert(debug.getinfo(blockingcreate, "S").what == "C", "The debugger needs the real socket functions !")
-- cleanup the package.loaded table (socket.core adds socket field into it)
package.loaded.socket = nil

return {
    create = function() return setmetatable({ skt = blockingcreate() }, blockingtcp) end,
    sleep  = blockingsleep,
    
    -- Base64 related functions
    --- Encodes a string into Base64 with line wrapping
    -- @param data (string) data to encode
    -- @return base64 encoded string
    b64 = function(data)
        local filter = ltn12.filter.chain(mime.encode("base64"), mime.wrap("base64"))
        local sink, output = ltn12.sink.table()
        ltn12.pump.all(ltn12.source.string(data), ltn12.sink.chain(filter, sink))
        return table.concat(output)
    end,

    --- Encodes a string into Base64, without any extra parsing (wrapping, ...)
    -- @param data (string) data to encode
    -- @return decoded string
    rawb64 = function(data)
        return (mime.b64(data)) -- first result of the low-level function is fine here
    end,

    --- Decodes base64 data
    -- @param data (string) base64 encoded data
    -- @return decoded string
    unb64 = function(data)
        return (mime.unb64(data)) -- first result of the low-level function is fine here
    end,
}
