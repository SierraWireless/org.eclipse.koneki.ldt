local TESTS_ROOT = '/media/sf_Code/org.eclipse.koneki.ldt/libraries/luadbgpclient/tests/testroot'

local TESTS = { }            -- queue for installations to test
local FORCE_REBUILD = false  -- set to true to erase any existing install an rebuild it from scratch
local BUILD_DBG = false      -- build the all-in-one debugger or copy separated files

local config = require "dist.config"
config.verbose = true

local dist = require "dist"
local sys  = require "dist.sys"
local package = require "dist.package"

local PKG_TMPL = "./packages/%s"

local function run_tests()
  for _, case in ipairs(TESTS) do
    local path, transport = unpack(case)
    io.stdout:write("\n\n\n",
                    "--------------------------------------------------------------------------------\n",
                    "---- Running tests for ", path, " using transport ", transport, "\n",
                    "--------------------------------------------------------------------------------\n")
    os.execute(table.concat({ sys.quote(path.."/bin/lua"), sys.quote(path.."/runner.lua"), sys.quote(transport) }, " "))
  end
end

--- Adds a test environment (build Lua VM & libraries and run the tests)
-- @param name Name of the environment
-- @param contents List of packages to build (just package names)
-- @param transport Name of the transport backend used for tests
local function environment(name, contents, transport)
  local path = TESTS_ROOT .. "/" .. name

  if not sys.is_dir(path) or FORCE_REBUILD then
    assert(sys.delete(path))
    -- clear every luadist tempoary files (can cause compile error due to cmake cache files)
    if sys.is_dir(config.temp_dir) then assert(sys.delete(config.temp_dir)) end
    assert(sys.make_dir(config.temp_dir))

    for _, pkg in ipairs(contents) do
      assert(package.install_pkg(PKG_TMPL:format(pkg), path, nil, true))
    end
  end

  -- always overwrite debugger, tests and dependencies (could be done as dist packages...)
  local modpath = path .. "/lib/lua/"
  if BUILD_DBG then
      -- build debugger single file
      loadfile("../build.lua")(modpath .. "debugger.lua")
  else
      sys.copy("../debugger", modpath)
      os.rename(modpath .. "debugger/init.lua", modpath .. "debugger.lua")
  end
  -- copy test dependencies
  sys.copy("./3rd_party/luaxml", modpath)
  sys.copy("./3rd_party/lunatest.lua", modpath)
  --~ sys.copy("./3rd_party/dumper.lua", modpath .. "dumper.lua")
  sys.copy("./script/lunatest_xassert.lua", modpath)
  sys.copy("./script/test_debugger.lua", modpath)
  sys.copy("./script/test_debugger_util.lua", modpath)
  sys.copy("./script/test_debugintrospection.lua", modpath)
  sys.copy("./script/debugger_bench.lua", modpath)

  -- test runner
  sys.copy("runner.lua", path)

  -- add to tests queue
  TESTS[#TESTS+1] = { path, transport }
end

--environment("lua51-socket",   { "lua-5.1.5",           "luasocket-2.0.2" }, "luasocket")
-- LuaSocket is currently 5.1 only so all other tests are borken (cannot compile or does not pass tests)
--environment("lua52-socket",   { "lua-5.2",             "luasocket-unstable" })
--environment("luajit1-socket", { "luajit-1.1.8",        "luasocket-2.0.2" })
--environment("luajit2-socket", { "luajit-2.0.0-beta10", "luasocket-2.0.2" })

environment("lua51-core",   { "lua-5.1.5",    "debuggertransport" }, "core")
environment("lua52-core",   { "lua-5.2",      "debuggertransport" }, "core")
--environment("luajit1-core", { "luajit-1.1.8", "debuggertransport" }, "core")
environment("luajit2-core", { "luajit-2.0.0-beta10", "debuggertransport" }, "core")

run_tests()
