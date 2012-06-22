-------------------------------------------------------------------------------
-- Copyright (c) 2011, 2012 Sierra Wireless and others.
-- All rights reserved. This program and the accompanying materials
-- are made available under the terms of the Eclipse Public License v1.0
-- which accompanies this distribution, and is available at
-- http://www.eclipse.org/legal/epl-v10.html
--
-- Contributors:
--     Sierra Wireless - initial API and implementation
-------------------------------------------------------------------------------

---
-- Uses Metalua capabilities to indent code and provide source code offset
-- semantic depth
--
-- @module luaformatter
local M = {}
require 'metalua.compiler'

---
--  calculate all ident level
-- @param Source code to analyze
-- @return #table {linenumber = identationlevel}
-- @usage local depth = format.indentLevel("local var")
local function getindentlevel(source,indenttable)

	local function getfirstline(node)
		-- Regular node
		local offsets = node[1].lineinfo
		local first
		local offset
		-- Consider previous comments as part of current chunk
		-- WARNING: This is NOT the default in Metalua
		if offsets.first.comments then
			first = offsets.first.comments.lineinfo.first.line
			offset = offsets.first.comments.lineinfo.first.offset
		else
			first = offsets.first.line
			offset = offsets.first.offset
		end
		return first, offset
	end

	local function getlastline(node)
		-- Regular node
		local offsets = node[#node].lineinfo
		local last
		-- Same for block end comments
		if offsets.last.comments then
			last = offsets.last.comments.lineinfo.last.line
		else
			last = offsets.last.line
		end
		return last
	end

	--
	-- Define AST walker
	--
	local linetodepth = {}
	local walker = {
		block = {},
		expr = {},
		depth = 0,     -- Current depth while walking
	}

	function walker.block.down(node, parent,...)
		--ignore empty node
		if #node == 0 then
			return end
		-- get first line of the block
		local startline,startoffset = getfirstline(node)
		local endline = getlastline(node)
		if source:sub(1,startoffset-1):find("[\r\n]%s*$") then
			for i=startline, endline do
				linetodepth[i]=walker.depth
			end
		else
			for i=startline+1, endline do
				linetodepth[i]=walker.depth
			end
		end
		walker.depth = walker.depth + 1
	end

	function walker.block.up(node, ...)
		if #node == 0 then
			return end
		walker.depth = walker.depth - 1
	end

	function walker.expr.down(node, parent, ...)
		if indenttable and node.tag == 'Table' then
			if #node == 0 then
				return end
			local startline,startoffset = getfirstline(node)
			local endline = getlastline(node)
			if source:sub(1,startoffset-1):find("[\r\n]%s*$") then
				for i=startline, endline do
					linetodepth[i]=walker.depth
				end
			else
				for i=startline+1, endline do
					linetodepth[i]=walker.depth
				end
			end
			walker.depth = walker.depth + 1
		elseif node.tag =='String' then
			local firstline = node.lineinfo.first.line
			local lastline = node.lineinfo.last.line
			for i=firstline+1, lastline do
				linetodepth[i]=false
			end
		end
	end

	function walker.expr.up(node, parent, ...)
		if indenttable and node.tag == 'Table' then
			if #node == 0 then
				return end
			walker.depth = walker.depth - 1
		end
	end

	-- Walk through AST to build linetodepth
	local ast = mlc.luastring_to_ast(source)
	require 'metalua.walk'
	walk.block(walker, ast)

	return linetodepth
end

---
-- Trim white spaces before and after given string
--
-- @usage local trimmedstr = trim('          foo')
-- @param #string string to trim
-- @return #string string trimmed
local function trim(string)
	local pattern = "^(%s*)(.*)"
	local _, strip =  string:match(pattern)
	if not strip then return string end
	local restrip
	_, restrip = strip:reverse():match(pattern)
	return restrip and restrip:reverse() or strip
end

---
-- Indent Lua Source Code.
-- @function [parent=#luaformatter] indentCode
-- @param source source code to format
-- @param delimiter line delimiter to use
-- @param indenttable true if you want to ident in table
-- @return #string formatted code
-- @usage indentCode('local var', '\n', true, '\t',)
-- @usage indentCode('local var', '\n', true, --[[tabulationSize]]4, --[[indentationSize]]2)
function M.indentcode(source, delimiter,indenttable, ...)
	--
	-- Create function which will generate indentation
	--
	local tabulation
	if select('#', ...) > 1 then
		local tabSize = select(1, ...)
		local indentationSize = select(2, ...)
		-- When tabulation size and indentation size is given, tabulation is
		-- composed of tabulation and spaces
		tabulation = function(depth)
			local range = depth * indentationSize
			local tabCount = range / tabSize
			local spaceCount = range % tabSize
			local tab = '\t'
			local space = ' '
			return tab:rep(tabCount) .. space:rep(spaceCount)
		end
	else
		local char = select(1, ...)
		-- When tabulation character is given, this character will be ducplicated
		-- according to length
		tabulation = function (depth) return char:rep(depth) end
	end

	-- Delimiter position table
	-- Initialisation represent string start offset
	local delimiterLength = delimiter:len()
	local positions = {1-delimiterLength}

	--
	-- Seek for delimiters
	--
	local i = 1
	local delimiterPosition = nil
	repeat
		delimiterPosition = source:find(delimiter, i, true)
		if delimiterPosition then
			positions[#positions + 1] = delimiterPosition
			i = delimiterPosition + 1
		end
	until not delimiterPosition
	-- No need for indentation, while no delimiters has been found
	if #positions < 2 then
		return source
	end

	-- calculate indentation
	local linetodepth = getindentlevel(source,indenttable)


	-- Concatenate string with right identation
	local indented = {}
	for  position=1, #positions do
		-- Extract source code line
		local offset = positions[position]
		-- Get the interval between two positions
		local rawline
		if positions[position + 1] then
			rawline = source:sub(offset + delimiterLength, positions[position + 1] -1)
		else
			-- From current prosition to end of line
			rawline = source:sub(offset + delimiterLength)
		end

		-- Trim white spaces
		local indentcount = linetodepth[position]
		if not indentcount then
			indented[#indented+1] = rawline
		else
			local line = trim(rawline)
			-- Append right indentation
			-- Indent only when there is code on the line
			if line:len() > 0 then
				-- Compute next real depth related offset
				-- As is offset is pointing a white space before first statement of block,
				-- We will work with parent node depth
				indented[#indented+1] = tabulation( indentcount)
				-- Append timmed source code
				indented[#indented+1] = line
			end
		end
		-- Append carriage return
		-- While on last character append carriage return only if at end of original source
		if position < #positions or source:sub(source:len()-delimiterLength, source:len()) == delimiter then
			indented[#indented+1] = delimiter
		end
	end

	return table.concat(indented)
end

return M
