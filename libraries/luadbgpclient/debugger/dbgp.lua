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
-- DBGp protocol utility function (parsing, error handling, XML generation).
-------------------------------------------------------------------------------

local util = require "debugger.util"

local error, setmetatable, type, pairs, ipairs, tostring, tconcat = 
      error, setmetatable, type, pairs, ipairs, tostring, table.concat

local M = { }

--- Parses the DBGp command arguments and returns it as a Lua table with key/value pairs.
-- For example, the sequence <code>-i 5 -j foo</code> will result in <code>{i=5, j=foo}</code>
-- @param cmd_args (string) sequence of arguments
-- @return table described above
function M.arg_parse(cmd_args)
    local args = {}
    for arg, val in cmd_args:gmatch("%-(%w) (%S+)") do
        args[arg] = val
    end
    return args
end

--- Parses a command line
-- @return commande name (string)
-- @retrun arguments (table)
-- @return data (string, optional)
function M.cmd_parse(cmd)
    local cmd_name, args, data
    if cmd:find("--", 1, true) then -- there is a data part
        cmd_name, args, data = cmd:match("^(%S+)%s+(.*)%s+%-%-%s*(.*)$")
        data = util.unb64(data)
    else
        cmd_name, args = cmd:match("^(%S+)%s+(.*)$")
    end
    return cmd_name, M.arg_parse(args), data
end

--- Returns the packet read from socket, or nil followed by an error message on errors.
function M.read_packet(skt)
    local size = {}
    while true do
        local byte, err = skt:receive(1)
        if not byte then return nil, err end
        if byte == "\000" then break end
        size[#size+1] = byte
    end
    return tconcat(size)
end

M.DBGP_ERR_METATABLE = {} -- unique object used to identify DBGp errors

--- Throws a correct DBGp error which result in a fine tuned error message to the server.
-- It is intended to be called into a command to make a useful error message, a standard Lua error
-- result in a code 998 error (internal debugger error).
-- @param code numerical error code
-- @param message message string (optional)
-- @param attr extra attributes to add to the response tag (optional)
function M.error(code, message, attr)
    error(setmetatable({ code = code, message = message, attr = attr or {} }, M.DBGP_ERR_METATABLE), 2)
end

--- Like core assert but throws a DBGp error if condition is not met.
-- @param code numerical error code thrown if condition is not met.
-- @param message condition to test
-- @param ... will be used as error message if test fails.
function M.assert(code, success, ...)
    if not success then M.error(code, (...)) end
    return success, ...
end

-- -----------------
-- Outgoing data
-- -----------------
local xmlattr_specialchars = { ['"'] = "&quot;", ["<"] = "&lt;", ["&"] = "&amp;" }
--- Very basic XML generator
-- Generates a XML string from a Lua Object Model (LOM) table.
-- See http://matthewwild.co.uk/projects/luaexpat/lom.html
function M.lom2str(xml)
    local pieces = { } -- string buffer

    local function generate(node)
        pieces[#pieces + 1] = "<"..node.tag
        pieces[#pieces + 1] = " "
        -- attribute ordering is not honored here
        for attr, val in pairs(node.attr or {}) do
            if type(attr) == "string" then
                pieces[#pieces + 1] = attr .. '="' .. tostring(val):gsub('["&<]', xmlattr_specialchars) .. '"'
                pieces[#pieces + 1] = " "
            end
        end
        pieces[#pieces] = nil -- remove the last separator (useless)
        
        if node[1] then
            pieces[#pieces + 1] = ">"
            for _, child in ipairs(node) do
                if type(child) == "table" then generate(child)
                else pieces[#pieces + 1] = "<![CDATA[" .. tostring(child) .. "]]>" end
            end
            pieces[#pieces + 1] = "</" .. node.tag .. ">"
        else
            pieces[#pieces + 1] = "/>"
        end
    end
    
    generate(xml)
    return tconcat(pieces)
end

function M.send_xml(skt, resp)
    if not resp.attr then resp.attr = {} end
    resp.attr.xmlns = "urn:debugger_protocol_v1"
    
    local data = '<?xml version="1.0" encoding="UTF-8" ?>\n'..M.lom2str(resp)
    util.log("DEBUG", "Send " .. data)
    skt:send(tostring(#data).."\000"..data.."\000")
end

--- Return an XML tag describing a debugger error, with an optional message
-- @param code (number) error code (see DBGp specification)
-- @param msg  (string, optional) textual description of error
-- @return table, suitable to be converted into XML
function M.make_error(code, msg)
    local elem = { tag = "error", attr = { code = code } }
    if msg then
        elem[1] = { tostring(msg), tag = "message" }
    end
    return elem
end

return M
