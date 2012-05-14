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
-- @module format
-- @alias M
local M = {}
require 'metalua.compiler'
---
--  Provide semantic depth of a source code offset
--
-- @param Source code to analyze
-- @param Source offset of depth to compute
-- @param Flush previously computed AST
-- @param Indicates if code in table definitions should be indented
-- @result Semantic depth of source at given offset
-- @usage local depth = format.indentLevel("local var", 3)
local parsedSources = {}
function M.indentLevel(source, offset, flush, indenttable)
	---
	-- Indicates whether an offset is included in node offsets
	-- @param node Metalua node
	-- @param offset Source position to check
	-- @return boolean True if offset is node boundaries, False else way
	local function isIncluded(node, offset)
		-- Empty block
		local first, last = 0, 0
		if node.lineinfo then
			-- Regular node
			local offsets = node.lineinfo
			-- Consider previous comments as part of current chunk
			-- WARNING: This is NOT the default in Metalua
			if offsets.first.comments then
				first = offsets.first.comments.lineinfo.first.offset
			else
				first = offsets.first.offset
			end
			-- Same for block end comments
			if offsets.last.comments then
				last = offsets.last.comments.lineinfo.last.offset
			else
				last = offsets.last.offset
			end
		elseif #node > 0 then
			-- Regular block
			local nop
			first, nop = isIncluded(node[1], offset)
			nop, last =  isIncluded(node[#node], offset)
		end
		return offset >= first and last >= offset
	end
	--
	-- Define AST walker
	--
	flush = flush or false
	local walker = {
	block       = {},
	expr		= {},
	depth       = 0,     -- Current depth while walking
	nodeDepth   = 0,     -- Depth of node at required offset
	offset      = offset -- Sought offset
	}
	function walker.block.down(node, ...)
		walker.depth = walker.depth + 1
		if isIncluded(node, walker.offset) then
			walker.nodeDepth = walker.depth
		end
	end
	function walker.block.up(node, ...)
		walker.depth = walker.depth - 1
	end
	function walker.expr.down(node, parent, ...)
		if indenttable and parent and parent.tag == 'Table' then
			walker.depth = walker.depth + 1
			if isIncluded(node, walker.offset) then
				walker.nodeDepth = walker.depth
			end
		end
	end
	function walker.expr.up(node, parent, ...)
		if indenttable and parent and parent.tag == 'Table' then
			walker.depth = walker.depth - 1
		end
	end
	-- Fetch previous ast from those sources
	if not parsedSources[source] or flush then
		-- Generate AST when needed or asked
		parsedSources[source] = mlc.luastring_to_ast(source)
	end
	-- Walk through AST
	local ast = parsedSources[source]
	require 'metalua.walk'
	walk.block(walker, ast)
	return walker.nodeDepth > 0 and walker.nodeDepth - 1 or 0
end
---
-- Forces indentation while in a block, highly experimental
--
-- @param source Source code to analyze
-- @param delimiter End of line delimiter (ex: '\n' for Unix )
-- @param tabulation String inserted before statement (ex: '\t' or several spaces)
function M.formatCode(source, delimiter, tabulation)
	local formatted = {}
	local ast = mlc.luastring_to_ast(source)
	local previousIdent =  identLevel(ast, 1)
	for i=1,#source do
		local char = source:sub(i, i)
		local currentIdent = identLevel(ast, i)
		if currentIdent ~= previousIdent then
			formatted[#formatted+1] = delimiter .. string.rep(tabulation, currentIdent)
			previousIdent = currentIdent
		end
		formatted[#formatted + 1]= char
	end
	return table.concat(formatted)
end
---
-- Provide position of first non white space character of string
--
-- @param s String to analyze
-- @return Position of first valuable character
-- @usage local str = '          foo' str:validOffset()
function string.validOffset(s)
	local spaces, letters  = s:match('(%s*)(.*)')
	return spaces:len()
end
---
-- Trim white spaces before and after given string
--
-- @usage local str = '          foo' str:trim()
-- @param string to trim
-- @return String trimmed
function string.trim(string)
	local pattern = "^(%s*)(.*)"
	local _, strip =  string:match(pattern)
	if not strip then return string end
	local restrip
	_, restrip = strip:reverse():match(pattern)
	return restrip and restrip:reverse() or strip
end
---
-- Correct Lua source code indentation
--
-- Will work on delimiter ( '\n' on Unix ) positions. The algorythm is to
-- 1. comptute sub strings between delimiters
-- 2. Trim them
-- 3. Indent them when there not empty
-- There is and exception, start of string is handled as a delimiter to comply
-- whith algorythm.
-- @usage indentCode('local var', '\n', '\t', 0)
-- @usage indentCode('local var', '\n', --[[indentationSize]]2, --[[tabulationSize]]4, 0)
function M.indentCode(source, delimiter, ...)
	--
	-- Create function which will generate indentation
	--
	local tabulation, initialDepth
	local indenttable
	if select('#', ...) > 3 then
		local tabSize = select(1, ...)
		local indentationSize = select(2, ...)
		initialDepth = select(3, ...)
		indenttable = select(4, ...)
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
		initialDepth = select(2, ...)
		indenttable = select(3, ...)
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
	-- Concatenate string with right identation
	local indented = {}
	local ast = mlc.luastring_to_ast(source)
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
		local line = rawline:trim()
		-- Append right indentation
		-- Indent only when there is code on the line
		local indentation
		if line:len() > 0 then
			-- Compute next real depth related offset
			-- As is offset is pointing a white space before first statement of block,
			-- We will work with parent node depth
			local depthoffset = offset + delimiterLength + rawline:validOffset()
			local indentCount = M.indentLevel(source, depthoffset, false, indenttable) + initialDepth
			indented[#indented+1] = tabulation( indentCount )
		end
		-- Append timmed source code
		indented[#indented+1] = line
		-- Append carriage return
		-- While on last character append carriage return only if at end of original source
		if position < #positions or source:sub(source:len()-delimiterLength, source:len()) == delimiter then
			indented[#indented+1] = delimiter
		end
	end
	return table.concat(indented)
end
-- Just allow to play with current library from commend line
if arg then
	for k=2,#arg,2 do
		local argument = arg[k]
		local offset = arg[k+1]
		print(argument)
		print(string.rep(' ', tonumber(offset)-1)..'^')
		table.print(ast, 'nohash', 1)
		print ('Offset '..offset..' has depth '..tostring(indentLevel(argument, tonumber(offset))))
		print ('Indented code:')
		print (indentCode(argument, "\n", "    ", 0))
	end
end
return M
