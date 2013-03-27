--------------------------------------------------------------------------------
--  Copyright (c) 2012-2013 Sierra Wireless.
--  All rights reserved. This program and the accompanying materials
--  are made available under the terms of the Eclipse Public License v1.0
--  which accompanies this distribution, and is available at
--  http://www.eclipse.org/legal/epl-v10.html
--
--  Contributors:
--       Kevin KIN-FOO <kkinfoo@sierrawireless.com>
--           - initial API and implementation and initial documentation
--------------------------------------------------------------------------------

require 'metalua.package'
local compiler = require 'metalua.compiler'
local mlc = compiler.new()

local javamodelfactory = require 'javamodelfactory'

-- Just redefining classic print, as there is a flush problem calling it from Java
local print = function(...) print(...) io.flush() end

local M = {}

---
-- Build Java Model from source code
--
-- @param	source Code to parse
-- @return	LuaSourceRoot, DLTK node, root of DLTK AST
function M.build(source)

	--  initialize
	handledcomments={}

	-- Build AST
	local ast = mlc:src_to_ast( source )
	local root = javamodelfactory.newsourceroot(#source)

	-- Check if an error occurred
	local status, astvalid, errormsg, positions = pcall(compiler.check_ast, ast)


	-- Report problem
	if not astvalid then
		local msg = errormsg or 'Unable to determine error'
		if positions then
			local offset = positions.offset and positions.offset - 1 or 0
			javamodelfactory.setproblem(root, positions.line, positions.column, offset, msg)
		else
			javamodelfactory.setproblem(root, 1, 1, 0, msg)
		end
		return root
	end

	-- Create api model
	local apimodelbuilder = require 'models.apimodelbuilder'
	local _file = apimodelbuilder.createmoduleapi(ast)

	-- Converting api model to java
	local javaapimodelbuilder = require 'javaapimodelbuilder'
	local jfile = javaapimodelbuilder._file(_file)

	-- create internal model
	local internalmodelbuilder = require 'models.internalmodelbuilder'
	local _internalcontent = internalmodelbuilder.createinternalcontent(ast)

	-- Converting internal model to java
	local javainternalmodelbuilder = require 'javainternalmodelbuilder'
	local jinternalcontent = javainternalmodelbuilder._internalcontent(_internalcontent)

	-- Append information from documentation
	javamodelfactory.addcontent(root,jfile,jinternalcontent)

	local handledcomments={}
	return root
end

return M
