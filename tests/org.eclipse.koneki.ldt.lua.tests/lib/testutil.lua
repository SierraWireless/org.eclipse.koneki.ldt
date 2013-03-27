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
-- A module containing generic function for test
-- @module testutil
local apimodelbuilder = require 'models.apimodelbuilder'
local domhandler      = require 'domhandler'
local tablecompare    = require 'tablecompare'
local templateengine  = require 'templateengine'
local xml = require 'xml'

--
-- Loading template engine environment
--
for key, value in pairs(require 'template.utils') do
	templateengine.env[key] = value
end

local M = {}

local errorhandling = function (filename)
	local errorbuffer = {}
	return function (err, offset)
		local message = string.format(
			"An error occured while parsing html for %s at offset %d:%s\n",
			filename,
			offset,
			err
		)
		table.insert(errorbuffer, message)
	end, errorbuffer
end

---
-- Load a file in a string
-- @function [parent = #testutil] loadfile
-- @param filepath file absolute path
-- @return #string file content
function M.loadfile(filepath)

	local luafile, errormessage = io.open(filepath, 'r')
	assert(
		luafile,
		string.format('Unable to read from %s.\n%s', filepath, errormessage or '')
	)
	local filestring = luafile:read('*a')
	luafile:close()
	
	return filestring
end

---
-- Parse HTML in a table
-- @function [parent = #testutil] parsehtml
-- @param #string htmlstring html
-- @param #string filename File name to be display in case of a parsing error
-- @return table
-- @return status, errormessage in case of failure
function M.parsehtml(htmlstring, filename)
	
	-- Create parser for input html
	local handler = domhandler.createhandler()
	local xmlparser = xml.newparser(handler)
	xmlparser.options.stripWS = false
	local errorhandlingfunction, errormessages = errorhandling(filename)
	xmlparser.options.errorHandler = errorhandlingfunction
	
	-- Actual html parsing
	local status, pcallerror = pcall( function() 
		xmlparser:parse(htmlstring)
	end)
	
	-- throw error with all message
	assert(status, string.format("%s\n%s\n%s",table.concat(errormessages), tostring(pcallerror),htmlstring))
	
	--throw failure
	if #errormessages ~= 0 then
		return nil, string.format("%s\n%s",table.concat(errormessages),htmlstring)
	end
	return handler.root
end

---
-- Compare HTML and return the result with a complete error message
-- @function [parent = #testutil] comparehtml
-- @param generatedtable Table to compare
-- @param referencetable Table expected
-- @param generatedhtml Html to compare
-- @parm referencehtml Html expected
-- @return status, formattederrormessage
function M.comparehtml(generatedtable, referencetable, generatedhtml, referencehtml)
	-- Check that they are equivalent
	local equivalent = tablecompare.compare(generatedtable, referencetable)
	if #equivalent > 0 then
	
		-- Compute which keys differs
		local differentkeys = tablecompare.diff(generatedtable, referencetable)
		local differentkeysstring = table.tostring(differentkeys)
		
		-- Convert table in formatted string
		local xmlformatter = require("xmlformatter")
		local htmlformattedstring= xmlformatter.xmltostring(generatedtable)
		local htmlformattedreferencestring = xmlformatter.xmltostring(referencetable)
		
		-- Create the diff
		local diffclass = java.require("diff.match.patch.diff_match_patch")
		local diff = diffclass:new()
		local differences = diff:diff_main(htmlformattedstring,htmlformattedreferencestring)
		diff:diff_cleanupSemantic(differences)
		
		-- Prettify the result
		local diffutil = java.require("org.eclipse.koneki.ldt.lua.tests.internal.utils.DiffUtil")
		local prettydiff = diffutil:diff_pretty_diff(differences)
		
		-- Formalise first table output
		local _ = '_'
		local line = _:rep(80)
		local stringdiff = string.format('%s\nString Diff \n%s\n%s', line, line, prettydiff)
		local generatedhtml   = string.format('%s\nGenerated HTML\n%s\n%s', line, line, generatedhtml)
		local referencehtml  = string.format('%s\nReference HTML\n%s\n%s', line, line, referencehtml)
		local tablediff = string.format('%s\nTable Diff \n%s\n%s', line, line, differentkeysstring)
		local generatedtable   = string.format('%s\nGenerated table\n%s\n%s', line, line, table.tostring(generatedtable, 1))
		local referencetable  = string.format('%s\nReference table\n%s\n%s', line, line, table.tostring(referencetable, 1))
		return nil, string.format('The generated HTML is not the same as the reference:\n%s\n%s\n%s\n%s\n%s\n%s',stringdiff, generatedhtml, referencehtml, tablediff, generatedtable, referencetable)
	end
	return true
end

return M
