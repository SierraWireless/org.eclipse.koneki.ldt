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

--- @module xmlformatter
local M = {}

local nodetostring ={}-- a map (key=NODE_TYPE, value=conversion function)

-- convert xml to table
local function xmltotable(domtable,result,indent,indentlevel)
	local f = nodetostring[domtable._type]
	if f then return f(domtable,result,indent,indentlevel) end
	return nil
end

-- print ROOT in table
local function roottotable(domtable,result,indent,indentlevel)
	for i,child in ipairs(domtable._children) do
		xmltotable(child,result,indent,indentlevel)
	end
	return result
end

-- print ELEMENT in table
local function elementtotable(domtable,result,indent,indentlevel)
	-- detect if this element has children
	local havechildren = domtable._children and (#(domtable._children) > 0)

	-- indent if needed
	if indent then
		result[#result+1] = "\n"
		result[#result+1] = string.rep("   ",indentlevel)
	end

	-- open  element
	result[#result+1] = "<"
	result[#result+1] = domtable._name

	-- print attributes
	if domtable._attr then
		for id, val in pairs(domtable._attr) do
			result[#result+1] = " "
			result[#result+1] = id
			result[#result+1] = "=\""
			result[#result+1] = val
			result[#result+1] = "\""
		end
	end

	-- auto close balise if needed
	if havechildren then
		result[#result+1] = ">"
	else
		result[#result+1] = "/>"
	end

	-- do not indent in a pre balise
	indent = indent and domtable._name ~= "pre"
	local indentnext = true
	
	-- print children
	if havechildren then
		for i,child in ipairs(domtable._children) do
			xmltotable(child,result,indent and indentnext,indentlevel+1)
			indentnext = child._type ~= "TEXT"
		end
	end

	-- close element
	if havechildren then
		if indent and indentnext then
			result[#result+1] = "\n"
			result[#result+1] = string.rep("   ",indentlevel)
		end
		result[#result+1] = "</"
		result[#result+1] = domtable._name
		result[#result+1] = ">"
	end

	return result
end

-- print TEXT in table
local function texttotable(domtable,result,indent,indentlevel)
	-- just print the text
	result[#result+1] =  domtable._text

	return result
end


nodetostring.ROOT=roottotable
nodetostring.ELEMENT=elementtotable
nodetostring.TEXT=texttotable

----------------------------------------------
-- @function [parent=#xmlformatter] xmltostring
-- @param #table domtable return by domhandler
function M.xmltostring(domtable)
	local result = xmltotable(domtable,{},true,0)
	return table.concat(result);
end

return M

