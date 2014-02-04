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
-- Merges all debugger modules into a single file.
-------------------------------------------------------------------------------

local FILE_HEADER = [[
-- /!\ This file is auto-generated. Do not alter manually /!\

--------------------------------------------------------------------------------
--  Submodules body
--------------------------------------------------------------------------------

]]

local MODULE_TMPL = [[

--------------------------------------------------------------------------------
--  Module %s
package.preload[%q] = function(...)
%s
end
-- End of module %s
--------------------------------------------------------------------------------

]]

local MAIN_HEADER = [[

--------------------------------------------------------------------------------
--  Main content
--------------------------------------------------------------------------------

]]

local function get_file_content(path)
  local f = assert(io.open(path))
  local content = assert(f:read("*a"))
  f:close()
  return content
end

function merge(output, root, modules)
  output:write(FILE_HEADER)
  -- insert submodules
  for _, m in ipairs(modules) do
    local name, file = unpack(m)
    output:write(MODULE_TMPL:format(name, name, get_file_content(file), name))
  end
  
  -- insert root module
  output:write(MAIN_HEADER, get_file_content(root))
end


-- main
local output = ...
output = output and io.open(output, "w") or io.stdout
srcdir = debug.getinfo(1).source:match("@(.+)[/\\]%w+.lua") or "."
merge(output, srcdir .. "/debugger/init.lua", {
  { "debugger.transport.apr",       srcdir .. "/debugger/transport/apr.lua" },
  { "debugger.transport.luasocket", srcdir .. "/debugger/transport/luasocket.lua" },
  { "debugger.commands",            srcdir .. "/debugger/commands.lua" },
  { "debugger.context",             srcdir .. "/debugger/context.lua" },
  { "debugger.dbgp",                srcdir .. "/debugger/dbgp.lua" },
  { "debugger.introspection",       srcdir .. "/debugger/introspection.lua" },
  { "debugger.platform",            srcdir .. "/debugger/platform.lua" },
  { "debugger.util",                srcdir .. "/debugger/util.lua" },
  { "debugger.url",                 srcdir .. "/debugger/url.lua" },
})
if output ~= io.stdout then output:close() end
