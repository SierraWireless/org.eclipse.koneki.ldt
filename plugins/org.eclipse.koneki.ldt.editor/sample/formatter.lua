--[[
 Long comment
]]
local M = {
field = 1
}
function M.sample(...)
-- Short comment
for index=1,select("#", ...) do
local var = select(index, ...)
end
end
return M
