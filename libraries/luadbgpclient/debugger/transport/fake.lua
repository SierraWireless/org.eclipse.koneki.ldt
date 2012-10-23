-- This file contains only documentation.

--------------------------------------------------------------------------------
-- Network layer backend for Lua DBGP client.
-- 
-- As this debugger is a remote debugger, it needs a network communication
-- layer and some other misc functions. It allows to use your own by 
-- implementing some communication functions. Typical use case is when you run
-- the Lua VM in a embedded environment with specific network API.
--
-- API used by the debugger is a small subset of LuaSocket 2.0.2 API.
--
-- @module networklayer
--

--------------------------------------------------------------------------------
-- Create a new TCP socket, not yet connected to anything.
-- @return #socket the created socket
-- @function [parent=#networklayer] create
--

--------------------------------------------------------------------------------
-- Wait for some time. Minimum precision is not defined strictly but should be
-- a millisecond resolution at least.
-- @param #number time amount of time to wait in seconds (decimal numbers
--  allowed).
-- @function [parent=#networklayer] sleep
--

--------------------------------------------------------------------------------
-- Encode a string to its Base64 representation.
-- @param #string input content to encode.
-- @return #string Base64 encoded string.
-- @function [parent=#networklayer] rawb64
--

--------------------------------------------------------------------------------
-- Encode a string to its Base64 representation with lines wrapped at 76
-- characters.
-- @param #string input content to encode.
-- @return #string Base64 encoded string.
-- @function [parent=#networklayer] b64
--

--------------------------------------------------------------------------------
-- Decode a Base64 encoded string.
-- @param #string input Base64 encoded string.
-- @return #string decoded string.
-- @function [parent=#networklayer] unb64
--

--------------------------------------------------------------------------------
-- Decode a Base64 encoded string.
-- @param #string input Base64 encoded string.
-- @return #string decoded string.
-- @function [parent=#networklayer] unb64
--


--------------------------------------------------------------------------------
-- Client socket to be connected to DBGP server.
--
-- @type socket
--

--------------------------------------------------------------------------------
-- Connect socket to given server.
-- @param self
-- @param #string host name or ip address.
-- @param #number port number.
-- @return true on success.
-- @return nil, error message on failure.
-- @function [parent=#socket] connect
--

--------------------------------------------------------------------------------
-- Reads some data from socket.
-- @param self
-- @param #number number of bytes to read.
-- @return #string read data on success.
-- @return nil, error message, partial buffer on failure.
-- @function [parent=#socket] receive
--

--------------------------------------------------------------------------------
-- Send data to server.
-- @param self
-- @param #string buffer to send.
-- @return true on success.
-- @return nil, error message on failure.
-- @function [parent=#socket] send
--

--------------------------------------------------------------------------------
-- Set socket blocking or not.
--
-- The name is borrowed from LuaSocket, but the actual usage is just fully 
-- blocking or non-blocking.
--
-- @param self
-- @param nil to set non-blocking, any other value to set blocking.
-- @return true on success.
-- @return nil, error message on failure.
-- @function [parent=#socket] settimeout
--

--------------------------------------------------------------------------------
-- Closes the socket.
-- @param self
-- @return true.
-- @function [parent=#socket] close
--
return nil