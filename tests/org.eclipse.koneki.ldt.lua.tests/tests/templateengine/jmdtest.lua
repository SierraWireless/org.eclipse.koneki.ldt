--------------------------------------------------------------------------------
-- Copyright (c) 2012-2013 Sierra Wireless and others.
-- All rights reserved. This program and the accompanying materials
-- are made available under the terms of the Eclipse Public License v1.0
-- which accompanies this distribution, and is available at
-- http://www.eclipse.org/legal/epl-v10.html
--
-- Contributors:
--     Sierra Wireless - initial API and implementation
--------------------------------------------------------------------------------
require 'metalua.package'
local apimodelbuilder = require 'models.apimodelbuilder'
local tablecompare = require 'tablecompare'
local domhandler = require 'domhandler'
local testutil = require 'testutil'
local mlc = require 'metalua.compiler'.new()
local xml = require 'xml'

local templateengine = require 'templateengine'
for k, v in pairs(require 'template.utils') do
	templateengine.env[k] = v
end

local M = {}

function M.test(luasourcepath, referencepath)

	--
	-- Load provided model
	--
	local inputstring = testutil.loadfile(luasourcepath)

	-- Generate AST
	local ast = mlc:src_to_ast( inputstring )

	-- Generate API model
	local apimodel = apimodelbuilder.createmoduleapi(ast)

	-- Generate html
	templateengine.env.markdown = jmdmarkdown
	local inputhtml = templateengine.applytemplate(apimodel)

	-- Create parser for input html
	local htmltable, errormessage = testutil.parsehtml(inputhtml, "Generated HTML")
	if not htmltable then
		return nil, errormessage
	end

	--
	-- Load provided reference
	--
	local referencehtml = testutil.loadfile(referencepath)

	-- Parse html from reference
	local htmlreferencetable = testutil.parsehtml(referencehtml, referencepath)
	if not htmlreferencetable then
		return nil, errormessage
	end

	-- Check that they are equivalent
	return testutil.comparehtml(htmltable,htmlreferencetable, inputhtml,referencehtml)
end

return M
