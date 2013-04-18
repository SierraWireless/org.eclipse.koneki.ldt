-------------------------------------------------------------------------------
-- Copyright (c) 2012 Sierra Wireless and others.
-- All rights reserved. This program and the accompanying materials
-- are made available under the terms of the Eclipse Public License v1.0
-- which accompanies this distribution, and is available at
-- http://www.eclipse.org/legal/epl-v10.html
--
-- Contributors:
--     Sierra Wireless - initial API and implementation
-------------------------------------------------------------------------------
local apimodel = require "models.apimodel"

---
-- @module testmodelutil 
local M ={}

---
-- @function [parent=#testmodelutil] addfunctions
-- @param #table model without functions
-- @return #table model with functions
function M.addfunctions(model,visitednode)
	
	-- Avoid infinite self referenced table browsing 
	local visitednode = visitednode or {}
	visitednode[model] = true

	-- add function for known table
	if model.tag and apimodel["_"..model.tag] and type(apimodel["_"..model.tag]) == 'function' then
		local emptymodel = apimodel["_"..model.tag]()
		for k,v in pairs(emptymodel) do
			if (type(v) == 'function') then
				model[k] = v
			end
		end
	end
	
	-- do it recursively
	for k, v in pairs( model ) do
		if type(v) == 'table' and not visitednode[v] then
			M.addfunctions(v, visitednode)
		end
	end
	return model
end


return M