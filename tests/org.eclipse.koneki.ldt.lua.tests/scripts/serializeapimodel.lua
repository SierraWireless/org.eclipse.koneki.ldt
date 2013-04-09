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
local arch = "32" --64
package.path = '../lib/?.lua;../../../libraries/modelsbuilder/src/?.lua;../../../libraries/modelsbuilder/'..arch..'/?.luac;../../../plugins/org.eclipse.koneki.ldt.metalua.'..arch..'bits/lib/?.luac;../../../plugins/org.eclipse.koneki.ldt.metalua.'..arch..'bits/lib/?.lua;../lib/external/?.lua;' .. package.path

--
-- Generate serialized lua API models files next to given file.
--
local apimodelbuiler = require 'models.apimodelbuilder'
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
		'serialized',
		apimodelbuiler.createmoduleapi
	)
	if not status then
		print( error )
	end
end
