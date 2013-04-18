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

-- Fetch libraries form same plugin
package.path = './?.lua;../lib/?.lua;../../../libraries/metalua/?.lua;../../../libraries/modelsbuilder/?.lua;../../../libraries/luaformatter/?.lua;'
package.mpath = '../../../libraries/metalua/?.mlua;../../../libraries/modelsbuilder/?.mlua;'
require 'metalua.package'

--
-- Generate serialized lua Internal models files next to given file.
--
local internalmodelbuilder = require 'models.internalmodelbuilder'
local modeltransformations = require 'modeltransformations'
if #arg < 1 then
	print 'No file to serialize.'
	return
end
for k = 1, #arg do

	-- Load source to serialize
	local filename = arg[k]
	local status, error = modeltransformations.codetoserialisedmodel(
		filename,
		'serialized.lua',
		internalmodelbuilder.createinternalcontent
	)
	if not status then
		print( error )
	end
end
