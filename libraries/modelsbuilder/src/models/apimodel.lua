--------------------------------------------------------------------------------
--  Copyright (c) 2011-2012 Sierra Wireless.
--  All rights reserved. This program and the accompanying materials
--  are made available under the terms of the Eclipse Public License v1.0
--  which accompanies this distribution, and is available at
--  http://www.eclipse.org/legal/epl-v10.html
--
--  Contributors:
--       Simon BERNARD <sbernard@sierrawireless.com>
--           - initial API and implementation and initial documentation
--------------------------------------------------------------------------------
local M = {}

--------------------------------------------------------------------------------
-- API MODEL
--------------------------------------------------------------------------------

function M._file()
	local file = {
		-- FIELDS
		tag = "file",
		name,							-- string
		shortdescription,	-- string
		description,			-- string
		types = {},				-- map from typename to type
		globalvars ={},		-- map from varname to item
		returns={},				-- list of return

		-- FUNCTIONS
		addtype =  function (self,type)
			self.types[type.name] = type
			type.parent = self
		end,
		addglobalvar =  function (self,item)
			self.globalvars[item.name] = item
			item.parent = self
		end
	}
	return file
end

function M._recordtypedef(name)
	local recordtype = {
		-- FIELDS
		tag = "recordtypedef",
		name=name,				-- string (mandatory)
		shortdescription,	-- string
		description,			-- string
		fields = {},			-- map from fieldname to field
		sourcerange = {min=0,max=0},

		-- FUNCTIONS
		addfield = function (self,field)
			self.fields[field.name] = field
			field.parent = self
		end
	}
	return recordtype
end


function M._functiontypedef(name)
	return {
		tag = "functiontypedef",
		name=name,				-- string (mandatory)
		shortdescription,	-- string
		description ,			-- string
		params = {},			-- list of parameter
		returns ={}				-- list of return
	}
end


function M._parameter(name)
	return {
		tag = "parameter",
		name = name, 				-- string (mandatory)
		description =  "",	-- string
		type = nil					-- typeref (external or internal or primitive typeref)
	}
end


function M._item(name)
	return {
		-- FIELDS
		tag = "item",
		name = name,						-- string (mandatory)
		shortdescription = "",	-- string
		description =  "",			-- string
		type = nil,							-- typeref (external or internal or primitive typeref)
		occurrences={},					-- list of identifier (see internalmodel)
		sourcerange = {min=0,max=0},

		-- FUNCTIONS
		addoccurence = function (self,occ)
			table.insert(self.occurrences,occ)
			occ.definition = self
		end
	}
end

function M._externaltypref(modulename, typename)
	return {
		tag = "externaltyperef",
		modulename = modulename,	-- string
		typename =  typename			-- string
	}
end
function M._internaltyperef(typename)
	return {
		tag = "internaltyperef",
		typename =  typename			-- string
	}
end

function M._primitivetyperef(typename)
	return {
		tag = "primitivetyperef",
		typename =  typename			-- string
	}
end

function M._moduletyperef(modulename,returnposition)
	return {
		tag = "moduletyperef",
		modulename =  modulename,					-- string
		returnposition = returnposition		-- number
	}
end

function M._exprtyperef(expression,returnposition)
	return {
		tag = "exprtyperef",
		expression =  expression,					-- expression (see internal model)
		returnposition = returnposition		-- number
	}
end


function M._return(description)
	return {
		tag = "return",
		description =  description or "", -- string
		types = {}												-- list of typref (external or internal or primitive typeref)
	}
end

return M
