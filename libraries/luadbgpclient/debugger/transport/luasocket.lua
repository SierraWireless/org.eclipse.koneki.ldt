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
-- LuaSocket backend for DBGP debugger.
-------------------------------------------------------------------------------

-- in order to be as lightweight as possible with Luasocket, core API is used 
-- directly (to no add yet another layer)

--FIXME: remove this hack as soon as luasocket officially support 5.2
if _VERSION == "Lua 5.2" then
  table.getn = function(t) return t and #t end
end

local socket = require "socket"
local mime   = require "mime"
local ltn12  = require "ltn12"
local reg = debug.getregistry()


return {
    create = socket.tcp,
    sleep  = socket.sleep,
    
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
