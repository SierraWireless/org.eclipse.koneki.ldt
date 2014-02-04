
-- get executable name (the lowest negative index in arg table)
for i=-1, -math.huge, -1 do
  if not arg[i] then break end
  LUA_EXECUTABLE = arg[i]
end

-- Configuration fields (set by bootstrapper while building the environment)
DBGP_TRANSPORT = "???"
local DEBUGGER_SOURCE = "???"

local cfg = require "luarocks.cfg"
local dir = require "luarocks.dir"
local fs = require "luarocks.fs"
local DEST_DIR = dir.path(cfg.rocks_trees[#cfg.rocks_trees], cfg.lua_modules_path)

-- build/copy debugger
if arg[1] == "build" then
    local builder = assert(loadfile(dir.path(DEBUGGER_SOURCE, "build.lua")))
    builder(dir.path(DEST_DIR, "debugger.lua"))
else
    fs.make_dir(dir.path(DEST_DIR, "debugger"))
    fs.copy_contents(dir.path(DEBUGGER_SOURCE, "debugger"), dir.path(DEST_DIR, "debugger"))
    -- /?/init.lua is not in default path
    fs.copy(dir.path(DEBUGGER_SOURCE, "debugger", "init.lua"), dir.path(DEST_DIR, "debugger.lua"))
end

fs.copy_contents(dir.path(DEBUGGER_SOURCE, "tests", "script"), DEST_DIR)

require"lunatest"
--~ lunatest.suite("debugger_bench")
lunatest.suite("test_debugintrospection")
lunatest.suite("test_debugger_util")
lunatest.suite("test_debugger")
lunatest.run(true)
