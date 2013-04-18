#!/usr/bin/lua
-------------------------------------------------------------------------------
-- Copyright (c) 2013 Sierra Wireless and others.
-- All rights reserved. This program and the accompanying materials
-- are made available under the terms of the Eclipse Public License v1.0
-- which accompanies this distribution, and is available at
-- http://www.eclipse.org/legal/epl-v10.html
--
-- Contributors:
--     Sierra Wireless - initial API and implementation
-------------------------------------------------------------------------------

-- Fetch libraries form current plugin
package.path = './?.lua;../lib/?.lua;../../../libraries/metalua/?.lua;../../../libraries/modelsbuilder/?.lua;../../../libraries/luaformatter/?.lua;'
package.mpath = '../../../libraries/metalua/?.mlua;../../../libraries/modelsbuilder/?.mlua;'
require 'metalua.package'

--
-- Generate serialized lua API models files next to given file.
--
local apimodelbuilder = require 'models.apimodelbuilder'
local internalmodelbuilder = require 'models.internalmodelbuilder'
local modeltransformations = require 'modeltransformations'
local tablecompare = require 'tablecompare'

if #arg < 1 then
	print 'No file to serialize.'
	return
end
for k = 1, #arg do

	-- Load source to serialize
	local filename = arg[k]
	local status, error = modeltransformations.codetoserialisedmodel(
		filename,
		'serialized',
		function (ast)
			--
			-- Generate API model
			--
			local apimodel, comment2apiobj = apimodelbuilder.createmoduleapi(ast)
		
			--
			-- Generate internal model
			--
			local internalmodel = internalmodelbuilder.createinternalcontent(ast,apimodel,comment2apiobj)
		
			--
			-- create table with the two models
			-- 
			local luasourceroot = {}
			luasourceroot.fileapi=apimodel
			luasourceroot.internalcontent=internalmodel
			
			return luasourceroot
		end
	)
	if not status then
		print( error )
	end
end
