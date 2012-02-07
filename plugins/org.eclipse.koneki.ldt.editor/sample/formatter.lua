-- single-line comment
local M = {
field = 1
}
function M.sample(...)
--[[
 Long comment
]]
local table = {
foo = 'bar',
42
}
for index=1,select("#", ...) do
local var = select(index, ...)
end
end
return M
