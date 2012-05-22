--------------------------------------------------------------------------------
--  Copyright (c) 2012 Sierra Wireless.
--  All rights reserved. This program and the accompanying materials
--  are made available under the terms of the Eclipse Public License v1.0
--  which accompanies this distribution, and is available at
--  http://www.eclipse.org/legal/epl-v10.html
--
--  Contributors:
--       Kevin KIN-FOO <kkinfoo@sierrawireless.com>
--           - initial API and implementation and initial documentation
--------------------------------------------------------------------------------
local apimodel = require 'models.apimodel'

---
-- @module docutils
-- Handles link generation, node quick description.
--
-- Provides:
--	* link generation
--	* anchor generation
--	* node quick description
local M = {}

function M.isempty(map)
	local f = pairs(map)
	return f(map) == nil
end
---
-- Provide a handling function for all supported anchor types
M.anchortypes = {
	file = function(o) return string.format('%s.html', o.name) end,
	internaltyperef = function(o) return string.format('#(%s)', o.typename) end,
	recordtypedef = function (o) return string.format('#(%s)', o.name) end,
	item = function(modelobject)
		-- Handle items referencing globals
		if not modelobject.parent or modelobject.parent.tag == 'file' then
			return modelobject.name
		else
			-- Prefix item name with parent anchor
			local parent = modelobject.parent
			local parentanchor = M.anchor(parent)
			if not parentanchor then
				return nil, string.format('Unable to generate anchor for `%s parent.',
					parent and parent.tag or 'nil')
			end
			return string.format('%s.%s', parentanchor, modelobject.name)
		end
	end
}
---
-- Provides anchor string for an object of API mode
--
-- @function [parent = #docutils] anchor
-- @param modelobject Object form API model
-- @result #string Anchor for an API model object, this function __may rise an error__
-- @usage # -- In a template
-- # local anchorname = anchor(someobject)
-- <a id="$(anchorname)" />
function M.anchor( modelobject )
	local tag = modelobject.tag
	if M.anchortypes[ tag ] then
		return M.anchortypes[ tag ](modelobject)
	end
	return nil, string.format('No anchor available for `%s', tag)
end
M.linktypes = {
	internaltyperef	= function(o) return string.format('##(%s)', o.typename) end,
	externaltyperef	= function(o) return string.format('%s.html##(%s)', o.modulename, o.typename) end,
	index = function() return 'index.html' end,
	recordtypedef = function(o)
		local anchor = M.anchor(o)
		return anchor or nil, 'Unable to generate anchor for `recordtypedef.'
	end,
	item = function( apiobject )

		-- This item may be related to ...
		if apiobject.parent.tag == 'recordtypedef' then

			-- A type
			
			-- Fetch parent type
			local parent = apiobject.parent
			local parentanchor = M.anchor( parent )
			if not parentanchor then
				return nil, string.format('Unable to generate anchor for `%s parent.',
					parentanchor and parentanchor.tag or 'nil')
			end
			
			-- Fetch file containing parent type
			local parentfile = apiobject.parent.parent
			if parentfile then
				local parentfileanchor = M.anchor( parentfile )
				if not parentfileanchor then
					return nil, string.format('Unable to generate anchor for `%s parent.',
						parentfile.tag or 'nil')
				end
				return string.format('%s#%s.%s', parentfileanchor, parentanchor, apiobject.name)
			end
			return string.format('#%s.%s', parentanchor, apiobject.name)
		end

		-- Create anchor for current object
		local currentapiobjectanchor = M.anchor( apiobject )
		if not currentapiobjectanchor then
			return nil, string.format('Unable to generate anchor for `%s object.', apiobject.tag)
		end

		-- This item may be related to ...
		if apiobject.parent and apiobject.parent.tag == 'file' then

			-- A global defined in another module
			local linktoparent = M.linkto( apiobject.parent )

			-- Check if it is possible to create a link
			if not linktoparent then
				return nil, string.format('Unable to generate link for `%s parent.', apiobject.parent.tag)
			end

			-- Create actual link
			return string.format('%s#%s', linktoparent, currentapiobjectanchor)
		end

		-- This item reference a global definition
		return string.format('#%s', currentapiobjectanchor)
	end
}
---
-- Generates text for HTML links from API model element
--
-- @function [parent = #docutils]
-- @param modelobject Object form API model
-- @result #string Links text for an API model element, this function __may rise an error__.
-- @usage # -- In a template
-- <a href="$( linkto(api) )">Some text</a>
M.linktypes.file = M.linktypes.recordtypedef
function M.linkto( apiobject )
	local tag = apiobject.tag
	if M.linktypes[ tag ] then
		return M.linktypes[tag](apiobject)
	end
	if not tag then
		return nil, 'Link generation is impossible as no tag has been provided.'
	end
	return nil, string.format('No link generation available for `%s.', tag)
end
M.prettynametypes = {
	primitivetyperef = function(o) return string.format('#%s', o.typename) end,
	externaltyperef = function(o) return string.format('%s#%s', o.modulename, o.typename) end,
	file = function(o) return o.name end,
	item = function( apiobject )

		-- Retrieve referenced type definition
		local parent = apiobject.parent
		local global = parent and parent.tag == 'file'
		local typefield = parent and parent.tag == 'recordtypedef'
		if not apiobject.type then

			-- Current item doesn't have any type reference enabling to math it
			-- with a type, but we can still prefix it with its parent name
			local prefix = ( typefield and parent.name ) and parent.name.. '.' or '' 
			return prefix .. apiobject.name

		end

		--
		-- Fetch item definition
		--
		local file 
		local definition
		if global then
			definition = parent.types[ apiobject.type.typename ]
		elseif typefield then
			-- Get definition container
			file = parent.parent
			if not file then
				-- This type has no container,
				-- it may be created from a shorcut notation
				return string.format('#%s.%s', parent.name, apiobject.name)
			end
			definition = file.types[ apiobject.type.typename ]
		end

		-- When type is not available, just provide item name prefixed with parent
		if not definition then
			local prefix = ( typefield and parent.name ) and parent.name.. '.' or '' 
			return string.format('%s%s', prefix, apiobject.name)
		elseif definition.tag == 'recordtypedef' then
			-- In case of record return item name prefixed with module name if available
			if global then
				return apiobject.name
			else
				return string.format('%s.%s', parent.name, apiobject.name)
			end
		else
			--
			-- Dealing with a function
			--

			-- Build parameter list
			local paramlist = {}
			local hasfirstself = false
			for position, param in ipairs(definition.params) do
				-- When first parameter is 'self', it will not be part of listed parameters
				if position == 1 and param.name == 'self' then
					hasfirstself = true
				else
					paramlist[#paramlist + 1] = param.name
					if position ~= #definition.params then
						paramlist[#paramlist + 1] =  ', '
					end
				end
			end
			-- Compose function prefix,
			-- ':' if 'self' is first parameter, '.' else way
			local fname = ''
			if not global then
				fname = fname .. parent.name ..( hasfirstself and ':' or '.' )
			end
			-- Append function parameters
			return string.format('%s%s(%s)', fname, apiobject.name, table.concat(paramlist))
		end
	end
}
M.prettynametypes.index = M.prettynametypes.file
M.prettynametypes.internaltyperef = M.prettynametypes.primitivetyperef
---
-- Provide human readable overview from an API model element
--
-- Resolve all element needed to summurize nicely an element form API model.
-- @usage $ print( prettyname(item) )
--	module:somefunction(secondparameter)
-- @function [parent = #docutils]
-- @param apiobject Object form API model
-- @result #string Human readable description of given element.
-- @result #nil, #string In case of error.
function M.prettyname( apiobject )
	local tag = apiobject.tag
	if M.prettynametypes[tag] then
		return M.prettynametypes[tag](apiobject)
	elseif not tag then
		return nil, 'No pretty name available as no tag has been provided.'
	end
	return nil, string.format('No pretty name for `%s.', tag)
end
---
-- Just make a string print table in HTML.
-- @function [parent = #docutils] securechevrons
-- @param #string String to convert.
-- @usage securechevrons('<markup>') => '&lt;markup&gt;'
-- @return #string Converted string.
function M.securechevrons( str )
	if not str then return nil, 'String expected.' end
	return string.gsub(str:gsub('<', '&lt;'), '>', '&gt;')
end

-------------------------------------------------------------------------------
-- Handling format of @{some#type} tag.
-- Following functions enable to recognize several type of references between
-- "{}".
-------------------------------------------------------------------------------

---
-- Provide API Model elements from string describing global elements
-- such as:
-- * `global#foo`
-- * `foo#global.bar`
local globals = function(str)
	-- Handling globals from modules
	for modulename, fieldname in str:gmatch('([%a%.%d_]+)#global%.([%a%.%d_]+)') do
		local item = apimodel._item(fieldname)
		local file = apimodel._file()
		file.name = modulename
		file:addglobalvar( item )
		return item
	end
	-- Handling other globals
	for name in str:gmatch('global#([%a%.%d_]+)') do
		--	print("globale", name)
		return apimodel._externaltypref('global', name)
	end
	return nil
end

---
-- Transform a string like `module#(type).field` in an API Model item
local field = function( str )
	for mod, typename, fieldname in str:gmatch('([%a%.%d_]*)#%(?([%a%.%d_]+)%)?%.([%a%%d_]+)') do
		local modulefield = apimodel._item( fieldname )
		local moduletype = apimodel._recordtypedef(typename)
		moduletype:addfield( modulefield )
		local typeref
		if #mod > 0 then
			local modulefile = apimodel._file()
			modulefile.name = mod
			modulefile:addtype( moduletype )
			typeref = apimodel._externaltypref(mod, typename)
		else
			typeref = apimodel._internaltyperef(typename)
		end
		modulefield.type = typeref
		return modulefield
	end
	return nil
end

---
-- Build an API internal reference from a string like: `#typeref`
local internal = function ( typestring )
	for name in typestring:gmatch('#([%a%.%d_]+)') do
		-- Do not handle this name is it starts with reserved name "global"
		if name:find("global.") == 1 then return nil end
		return apimodel._internaltyperef(name)
	end
	return nil
end

---
-- Build an API external reference from a string like: `mod.ule#type`
local extern = function (type)
	for _, modulename, typename in type:gmatch('(([%a%.%d_]+)#([%a%.%d_]+))') do
		return apimodel._externaltypref(modulename, typename)
	end
	return nil
end

---
-- Provide API Model element from a string
-- @usage local externaltyperef = getelement("somemodule#somefield")
function M.getelement( str )

	-- Order matters, more restrictive are at begin of table
	local extractors = {
		globals,
		field,
		extern,
		internal
	}
	-- Loop over extractors.
	-- First valid result is used
	for _, extractor in ipairs( extractors ) do
		local result = extractor( str )
		if result then return result end
	end
	return nil
end

--------------------------------------------------------------------------------
-- Iterator that iterates on the table in key ascending order.
--
-- @function [parent=#utils.table] sortedPairs
-- @param t table to iterate.
-- @return iterator function.
--
function M.sortedpairs(t)
	local a = {}
	local insert = table.insert
	for n in pairs(t) do insert(a, n) end
	table.sort(a)
	local i = 0
	return function()
		i = i + 1
		return a[i], t[a[i]]
	end
end
return M
