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

	-- Build AST
	local ast = mlc:src_to_ast( source )
	local root = javamodelfactory.newsourceroot(#source)

	-- Check if an error occurred
	local status, astvalid, errormsg, positions = pcall(compiler.check_ast, ast)


	-- Report problem
	if not astvalid then
		local msg = errormsg or 'Unable to determine error'
		if positions then
			local line = positions.line and positions.line - 1 or 0
			local column = positions.column and positions.column - 1 or 0
			local offset = positions.offset and positions.offset - 1 or 0
			javamodelfactory.setproblem(root, line, column, offset, msg)
		else
			javamodelfactory.setproblem(root, 0, 0, 0, msg)
		end
		return root
	end

	-- Create api model
	local apimodelbuilder = require 'models.apimodelbuilder'
	local _file, comment2apiobj = apimodelbuilder.createmoduleapi(ast)

	-- create internal model
	local internalmodelbuilder = require "models.internalmodelbuilder"
	local _internalcontent = internalmodelbuilder.createinternalcontent(ast,_file,comment2apiobj)

	-- Converting api model to java
	local javaapimodelbuilder = require 'javaapimodelbuilder'
	local jfile, handledexpr = javaapimodelbuilder._file(_file)

	-- Converting internal model to java
	local javainternalmodelbuilder = require 'javainternalmodelbuilder'
	local jinternalcontent = javainternalmodelbuilder._internalcontent(_internalcontent,_file, handledexpr)

	-- Append information from documentation
	javamodelfactory.addcontent(root,jfile,jinternalcontent)

	local handledcomments={}
	return root
end

return M
