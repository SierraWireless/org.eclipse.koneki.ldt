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
-- Apache Portable Runtime backend for DBGP debugger.
-------------------------------------------------------------------------------

local apr = require "apr"

-- base 64 wrapping
function b64_wrap(src)
  t = {}
  local b64_src = mime.b64(src)
  for i=1, #b64_src, 76 do t[#t+1] = b64_src:sub(i, i+75).."\r\n" end
  return table.concat(t)
end

-- implements a subset of LuaSocket API using APR
local SOCKET_MT = {
  connect = function(self, address, port) return self.skt:connect(address, port) end,
  receive = function(self, n)             return self.skt:read(n) end, -- only numeric read is used
  send    = function(self, data)          return self.skt:write(data) end,
  close   = function(self)                return self.skt:close() end,
  settimeout = function(self, sec)
    if     sec == nil then self.skt:timeout_set(true)
    elseif sec == 0   then self.skt:timeout_set(false)
    else                   self.skt:timeout_set(math.floor(sec * 1000000)) end
  end
}
SOCKET_MT.__index = SOCKET_MT

return {
    create = function()
      local skt, err = apr.socket_create('tcp')
      if not skt then return nil, err end
      return setmetatable({skt = skt}, SOCKET_MT)
    end,
    sleep      = apr.sleep, -- exact same API as LuaSocket
    
    -- Base64 related functions
    --- Encodes a string into Base64 with line wrapping
    -- @param data (string) data to encode
    -- @return base64 encoded string
    b64 = function(data)
        t = {}
        local b64_data = apr.base64_encode(data)
        for i=1, #b64_data, 76 do t[#t+1] = b64_data:sub(i, i+75).."\r\n" end
        return table.concat(t)
    end,

    --- Encodes a string into Base64, without any extra parsing (wrapping, ...)
    -- @param data (string) data to encode
    -- @return decoded string
    rawb64 = apr.base64_encode,

    --- Decodes base64 data
    -- @param data (string) base64 encoded data
    -- @return decoded string
    unb64 = apr.base64_decode,
}
