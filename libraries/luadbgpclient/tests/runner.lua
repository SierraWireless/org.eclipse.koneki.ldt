
-- get executable name (the lowest negative index in arg table)
for i=-1, -math.huge, -1 do
  if not arg[i] then break end
  LUA_EXECUTABLE = arg[i]
end
DBGP_TRANSPORT = arg[1]

require"lunatest"
--~ lunatest.suite("debugger_bench")
lunatest.suite("test_debugintrospection")
lunatest.suite("test_debugger_util")
lunatest.suite("test_debugger")
lunatest.run(true)
