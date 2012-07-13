---
-- Description of the module.
-- @module moduleName
local M = {
	-- single-line comment
	field = 1
}
-- TODO Task in the comment
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
