--------------------------------------------------------------------------------
--  Copyright (c) 2011-2012 Sierra Wireless.
--  All rights reserved. This program and the accompanying materials
--  are made available under the terms of the Eclipse Public License v1.0
--  which accompanies this distribution, and is available at
--  http://www.eclipse.org/legal/epl-v10.html
--
--  Contributors:
--       Kevin KIN-FOO <kkinfoo@sierrawireless.com>
--           - initial API and implementation and initial documentation
--------------------------------------------------------------------------------
---
-- This library provide html description of elements from the externalapi
local M = {}

-- Load template engine
local pltemplate = require 'pl.template'

-- Markdown handling
local markdown = require 'markdown'

-- apply template to the given element
function M.applytemplate(elem,templatetype)
	-- define environment
	local env = M.getenv(elem,templatetype)

	-- load template
	local template = M.gettemplate(elem,templatetype)
	if not template then
		templatetype = templatetype and ' "'.. templatetype.. '"' or ''
		local elementname = ' for '.. (elem.tag or 'untagged element')
		error('Unable to load'..templatetype..' template'..elementname)
	end

	-- apply template
	local str, err = pltemplate.substitute(template, env)

	--manage errors
	if not str then
		local templateerror = templatetype and ' parsing "'.. templatetype ..'" template ' or ''
		error('An error occured' ..templateerror ..' for "'..elem.tag..'"\n'..err)
	end
	return str
end

-- get the a new environment for this element
function M.getenv(elem)
	local currentenv ={}
	for k,v in pairs(M.env) do currentenv[k] = v end
	if elem and elem.tag then
		currentenv['_'..elem.tag]= elem
	end
	return currentenv
end

-- get the template for this element
function M.gettemplate(elem,templatetype)
	local tag = elem and elem.tag
	if tag then
		if templatetype then
			return require ("template." .. templatetype.. "." .. tag)
		else
			return require ("template." .. tag)
		end
	end
end


---
-- Allow user to format text in descriptions.
-- Default implementation replaces @{---} tags with links and apply markdown.
-- @return #string 
local function format(string)
	-- Allow to replace encountered tags with valid links
	local replace = function(found)
		local apiref = M.env.getelement(found)
		if apiref then
			local href = M.env.linkto( apiref )
			if href then
				return [[<a href="]]..href..[[">]]..M.env.prettyname(apiref)..[[</a>]]
			end
		end
		return found
	end
	string = string:gsub('@{%s*(.-)%s*}', replace)
	-- Trim markdown results from auto generated <p></p>
	return M.env.markdown( string ):gsub('^%s*<p>(.+)</p>%s*$','%1')
end
---
-- Provide a full link to an element using `prettyname` and `linkto`.
-- Default implementation is for HTML.
local function fulllinkto(o)
	local ref		= M.env.linkto(o)
	local name	= M.env.prettyname(o)
	if not ref then
		return name
	end
	return [[<a href="]]..ref..[[">]]..name..[[</a>]]
end
--
-- Define default template environnement
--
local defaultenv = {
	table					= table,
	ipairs				= ipairs,
	pairs					= pairs,
	markdown			= markdown,
	applytemplate	= M.applytemplate,
	format				= format,
	linkto				= function(str) return str end,
	fulllinkto		= fulllinkto,
	prettyname		= function(s) return s end,
	getelement		= function(s)	return nil end
}

-- this is the global env accessible in the templates
-- env should be redefine by docgenerator user to add functions or redefine it.
M.env = defaultenv
return M
