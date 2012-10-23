#!/usr/bin/lua
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

-- Fetch libraries form current plugin
package.path = '../lib/?.lua;../lib/external/?.lua;../lib/external/?.luac;' .. package.path

local apimodelbuilder      = require 'models.apimodelbuilder'
local modeltransformations = require 'modeltransformations'
local templateengine       = require 'templateengine'
for key, value in pairs(require 'template.utils') do
	templateengine.env[key] = value
end
if #arg < 1 then
	print 'No file to serialize.'
	return
end

for k = 1, #arg do

	-- Load source to serialize
	local filename = arg[k]
	
	-- Load file
	local luafile = io.open(filename, 'r')
	local luasource = luafile:read('*a')
	luafile:close()

	-- Generate AST
	local ast, errormessage = getast( luasource )
	if not ast then
		return nil, string.format('Unable to generate AST for %s.\n%s', filename, errormessage)
	end

	--Generate  API model
	local apimodel = apimodelbuilder.createmoduleapi(ast)
	
	-- Generate html form API Model
	local htmlcode, errormessage = templateengine.applytemplate(apimodel)
	if not htmlcode then
		print( string.format('Unable to generate html for %s.\%s', luasourcepath, errormessage) )
	end

	-- Generate html form API Model
	local htmlcode, errormessage = templateengine.applytemplate(apimodel)
	if not htmlcode then
		print( string.format('Unable to generate html for %s.\%s', luasourcepath, errormessage) )
	end
	
	local htmlfilename = filename:gsub('([%w%-_/\]+)%.lua', '%1.html')

	-- Save serialized model
	local htmlfile = io.open(htmlfilename, 'w')
	htmlfile:write( htmlcode )
	htmlfile:close()

	-- This a success
	print( string.format('%s serialized to %s.', filename, htmlfilename) )
end
