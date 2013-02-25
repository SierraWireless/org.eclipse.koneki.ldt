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

-------------------------------------------------------------------------------
-- JavaSocket backend for DBGP debugger.
-- @module debugger.transport.java

-- the main part of code is defined in the TransportLayerModule.java class
-- this file is just a wrapper to workaround some problems and be more compliant with defined API.

local javamodule = require ("debugger.transport.javasocket")
local M = {}


--------------------------------------------------------------------------------
-- Client socket to be connected to DBGP server.
-- @type socket
local socket = {}

--------------------------------------------------------------------------------
-- Connect socket to given server.
-- @function [parent=#socket] connect
-- @param self
-- @param #string host name or ip address.
-- @param #number port number.
-- @return true on success.
-- @return nil, error message on failure.
function socket:connect (host,port)
	local status, res = pcall(function() return self.wrapper:connect(host,port) end)
	if status then
		return true
	else
		return nil, res
	end
end

--------------------------------------------------------------------------------
-- Reads some data from socket.
-- @function [parent=#socket] receive
-- @param self
-- @param #number number of bytes to read.
-- @return #string read data on success.
-- @return nil, error message, partial buffer on failure.
function socket:receive (number)

	local function receive()
		local res = {}
		for i=1,number do
			local data = self.wrapper:receive()
			if (data =="") then
				res[#res+1] = "\000"
			else
				res[#res+1] = data
			end
		end
		return table.concat(res,"")
	end

	local status, res = pcall(receive)
	if status then
		return res
	else
		return nil, res
	end
end

--------------------------------------------------------------------------------
-- Send data to server.
-- @function [parent=#socket] send
-- @param self
-- @param #string buffer to send.
-- @return true on success.
-- @return nil, error message on failure.
function socket:send (buffer)
	return pcall(function () string.gsub(buffer,"([^%z]+)",
		function (data)
			self.wrapper:send(data)
			self.wrapper:send("")
		end)
	end)
end

--------------------------------------------------------------------------------
-- Set socket blocking or not.
--
-- The name is borrowed from LuaSocket, but the actual usage is just fully
-- blocking or non-blocking.
--
-- @function [parent=#socket] settimeout
-- @param self
-- @param nil to set non-blocking, any other value to set blocking.
-- @return true on success.
-- @return nil, error message on failure.

function socket:settimeout (nonblocking)
	return  pcall(function() return self.wrapper:settimeout(nonblocking) end)
end

--------------------------------------------------------------------------------
-- Closes the socket.
-- @function [parent=#socket] close
-- @param self
-- @return true.
function socket:close ()
	return  pcall(function() return self.wrapper:close() end)
end

--------------------------------------------------------------------------------
-- Create a new TCP socket, not yet connected to anything.
-- @function [parent=#debugger.transport.java] create
-- @return #socket the created socket
function M.create ()
	local status, res = pcall(function() return javamodule.create() end)
	if status then
		local t = setmetatable({wrapper = res},{__index = socket})
		return t
	else
		return nil, res
	end
end

--------------------------------------------------------------------------------
-- Wait for some time. Minimum precision is not defined strictly but should be
-- a millisecond resolution at least.
-- @function [parent=#debugger.transport.java] sleep
-- @param #number time amount of time to wait in seconds (decimal numbers
--  allowed).
function M.sleep (time)
	javamodule.sleep(time)
end

--------------------------------------------------------------------------------
-- Encode a string to its Base64 representation.
-- @function [parent=#debugger.transport.java] rawb64
-- @param #string input content to encode.
-- @return #string Base64 encoded string.
function M.rawb64 (input)
	return javamodule.rawb64(input)
end

--------------------------------------------------------------------------------
-- Encode a string to its Base64 representation with lines wrapped at 76
-- characters.
-- @function [parent=#debugger.transport.java] b64
-- @param #string input content to encode.
-- @return #string Base64 encoded string.
function M.b64 (input)
	return javamodule.b64(input)
end

--------------------------------------------------------------------------------
-- Decode a Base64 encoded string.
-- @function [parent=#debugger.transport.java] unb64
-- @param #string input Base64 encoded string.
-- @return #string decoded string.
function M.unb64 (input)
	return javamodule.unb64(input)
end

return M
