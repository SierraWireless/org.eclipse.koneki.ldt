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
-- Use minimal C implementation.
-------------------------------------------------------------------------------

local core = require "debuggertransport"

--- Encodes a string into Base64 with line wrapping
-- @param data (string) data to encode
-- @return base64 encoded string
core.b64 = function(data)
    local t = {}
    local b64_data = core.rawb64(data)
    for i=1, #b64_data, 76 do t[#t+1] = b64_data:sub(i, i+75).."\r\n" end
    return table.concat(t)
end

return core
